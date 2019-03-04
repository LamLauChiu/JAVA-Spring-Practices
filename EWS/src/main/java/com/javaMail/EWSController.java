package com.javaMail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import microsoft.exchange.webservices.data.core.enumeration.search.LogicalOperator;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.core.service.schema.EmailMessageSchema;
import microsoft.exchange.webservices.data.core.service.schema.ItemSchema;
import microsoft.exchange.webservices.data.search.filter.SearchFilter;
import microsoft.exchange.webservices.data.search.filter.SearchFilter.SearchFilterCollection;

@RestController
@RequestMapping( value =("ews"))
public class EWSController {

	@Autowired
	private EWSService ewsService;
	
	
	@RequestMapping( value="findFolder")
	public void findFolder()
	{
		try {
			ewsService.findFolder();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("static-access")
	@RequestMapping ( value="ews" )
	public void ews() {
		try {
						
			SearchFilterCollection searchFilterCollection = new SearchFilterCollection(LogicalOperator.And);
			
			//new SearchFilter.ContainsSubstring(ItemSchema.Subject,"This is the test for downloading CMS mail attachment") ),view);
			
			//searchFilterCollection.add(new SearchFilter.ContainsSubstring(EmailMessageSchema.Subject, "This is the test for downloading CMS mail attachment"));
			
		
			
			Date dateTo = new Date();
			Date dateFrom = DateTimeHelper.addDays(dateTo, -3);
			System.out.println("dateFrom: "+ dateFrom);
			System.out.println("dateTo:" + dateTo);
			
			searchFilterCollection.add(new SearchFilter.IsGreaterThan(ItemSchema.DateTimeReceived,dateFrom));
			searchFilterCollection.add(new SearchFilter.IsLessThan(ItemSchema.DateTimeReceived,dateTo));
			
			searchFilterCollection.add(new SearchFilter.ContainsSubstring(ItemSchema.Subject, "This is the test for downloading CMS mail attachment"));
			searchFilterCollection.add(new SearchFilter.IsEqualTo( ItemSchema.HasAttachments, true));
			//FindItemsResults<Item> findResults = service.findItems(WellKnownFolderName.Inbox, new SearchFilter.SearchFilterCollection( LogicalOperator.Or ,new SearchFilter.ContainsSubstring(ItemSchema.Subject,"This is the test for downloading CMS mail attachment") ),view);
			
			
			
			List<EmailMessage> emailMessages = ewsService.readMessagesWithSerachFilterCollection(searchFilterCollection);
			
			
			//ewsService.downloadAttachmentFromListOfMessages(emailMessages, "CubeTestReport");
			
			
			for( EmailMessage emailMessage : emailMessages) {
				
				ewsService.downloadAttachmentFromEmailMessage(emailMessage, "CubeTestReport");
				System.out.println(emailMessage.getSubject().toString());
				
			}
			ewsService.listOfFileInDir(ewsService.getAttachmentFileStoragePath());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    private static final String APPLICATION_PDF = "application/pdf";
	
	
    @RequestMapping(value = "/a", method = RequestMethod.GET, produces = APPLICATION_PDF)
    public @ResponseBody void downloadA(HttpServletResponse response) throws IOException {
        
    	File file = ewsService.getFileByPath();
        InputStream in = new FileInputStream(file);

        response.setContentType(APPLICATION_PDF);
        response.setHeader("Content-Disposition", "attachment; filename=" + file.getName());
        response.setHeader("Content-Length", String.valueOf(file.length()));
        FileCopyUtils.copy(in, response.getOutputStream());
    }

    @RequestMapping(value = "/b", method = RequestMethod.GET, produces = APPLICATION_PDF)
    public @ResponseBody HttpEntity<byte[]> downloadB() throws IOException {
        File file = ewsService.getFileByPath();
        byte[] document = FileCopyUtils.copyToByteArray(file);

        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "pdf"));
        header.set("Content-Disposition", "inline; filename=" + file.getName());
        header.setContentLength(document.length);

        return new HttpEntity<byte[]>(document, header);
    }

    @RequestMapping(value = "/c", method = RequestMethod.GET, produces = APPLICATION_PDF)
    public @ResponseBody Resource downloadC(HttpServletResponse response) throws FileNotFoundException {
        File file = ewsService.getFileByPath();
        response.setContentType(APPLICATION_PDF);
        response.setHeader("Content-Disposition", "inline; filename=" + file.getName());
        response.setHeader("Content-Length", String.valueOf(file.length()));
        return new FileSystemResource(file);
    }
    // Prefer to use a, it downloads file directly
    @RequestMapping(value = "/a/{fileName}", method = RequestMethod.GET, produces = APPLICATION_PDF)
    public @ResponseBody void downloadAWithFileName(@PathVariable @NotEmpty String fileName,HttpServletResponse response) throws IOException {
        
    	File file = ewsService.getAttachmentByFileName(fileName);
        InputStream in = new FileInputStream(file);

        response.setContentType(APPLICATION_PDF);
        response.setHeader("Content-Disposition", "attachment; filename=" + file.getName());
        response.setHeader("Content-Length", String.valueOf(file.length()));
        FileCopyUtils.copy(in, response.getOutputStream());
    }

    @RequestMapping(value = "/b/{fileName}", method = RequestMethod.GET, produces = APPLICATION_PDF)
    public @ResponseBody HttpEntity<byte[]> downloadBWithFileName(@PathVariable @NotEmpty String fileName) throws IOException {
    	File file = ewsService.getAttachmentByFileName(fileName);
        byte[] document = FileCopyUtils.copyToByteArray(file);

        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "pdf"));
        header.set("Content-Disposition", "inline; filename=" + file.getName());
        header.setContentLength(document.length);

        return new HttpEntity<byte[]>(document, header);
    }
    
    @RequestMapping(value = "/c/{fileName}", method = RequestMethod.GET, produces = APPLICATION_PDF)
    public @ResponseBody Resource downloadCWithFileName(@PathVariable @NotEmpty String fileName, HttpServletResponse response) throws FileNotFoundException {
        File file = ewsService.getAttachmentByFileName(fileName);
        response.setContentType(APPLICATION_PDF);
        response.setHeader("Content-Disposition", "inline; filename=" + file.getName());
        response.setHeader("Content-Length", String.valueOf(file.length()));
        return new FileSystemResource(file);
    }

}
