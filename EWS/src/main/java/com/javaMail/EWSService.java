package com.javaMail;
import java.io.File;
import java.io.FileNotFoundException;
/**
 * 
 *  ESWService
 * 	@author lauchiulam
 */
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.enumeration.property.BasePropertySet;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.enumeration.search.LogicalOperator;
import microsoft.exchange.webservices.data.core.enumeration.search.SortDirection;
import microsoft.exchange.webservices.data.core.enumeration.service.ConflictResolutionMode;
import microsoft.exchange.webservices.data.core.exception.service.local.ServiceLocalException;
import microsoft.exchange.webservices.data.core.service.item.Item;
import microsoft.exchange.webservices.data.core.service.folder.Folder;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.core.service.schema.FolderSchema;
import microsoft.exchange.webservices.data.core.service.schema.ItemSchema;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.property.complex.Attachment;
import microsoft.exchange.webservices.data.property.complex.AttachmentCollection;
import microsoft.exchange.webservices.data.property.complex.FileAttachment;
import microsoft.exchange.webservices.data.property.complex.FolderId;
import microsoft.exchange.webservices.data.property.complex.ItemId;
import microsoft.exchange.webservices.data.property.complex.MimeContent;
import microsoft.exchange.webservices.data.search.FindFoldersResults;
import microsoft.exchange.webservices.data.search.FindItemsResults;
import microsoft.exchange.webservices.data.search.FolderView;
import microsoft.exchange.webservices.data.search.ItemView;
import microsoft.exchange.webservices.data.search.filter.SearchFilter;
import microsoft.exchange.webservices.data.search.filter.SearchFilter.SearchFilterCollection;

@Service
public class EWSService {

	private Logger logger = LogManager.getLogger(this.getClass());
	/**
	 *   The Exchange Web Services Injection
	 */
	private static ExchangeService service;
	
    public static String getFilePath() {
		return FILE_PATH;
	}

	public static String getAttachmentFileStoragePath() {
		return ATTACHMENT_FILE_STORAGE_PATH;
	}

	public static String getRootFilePath() {
		return ROOT_FILE_PATH;
	}

	private static final String FILE_PATH = "/Users/lauchiulam/Downloads/emailAttachment/test.pdf";
    private static final String ATTACHMENT_FILE_STORAGE_PATH = "/Users/lauchiulam/Downloads/emailAttachment/";
    private static final String ROOT_FILE_PATH = "/Users/lauchiulam/Downloads/emailAttachment/";
	
    private Set<String> listOfFilesName;
    
    
	// Constructor
	public EWSService() {}
	
	@PostConstruct
	public void init() {
		logger.debug("EWSSerivce init");
		System.out.println("EWSSerivce init called");
		ExchangeVersion exchangeVersion = Enum.valueOf(ExchangeVersion.class, "Exchange2010_SP2");

		// Setup Exchange Web Service version and connection
		service = new ExchangeService(exchangeVersion);
		service.setCredentials(new WebCredentials("ac", "pw", "domain.com"));
		
		listOfFileInDir(ATTACHMENT_FILE_STORAGE_PATH);
		
		getListOfFilesName();
		
		System.out.println("listOfFilesName size" +listOfFilesName.size());
		
		System.out.println("listOfFilesName toString" +listOfFilesName.toString());
		
		try {
			service.setUrl(new URI("https://donmain.com/EWS/Exchange.asmx"));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 	https://github.com/OfficeDev/ews-java-api/wiki/Getting-Started-Guide#maven
	 * 	The EWS JAVA API defines a class hierarchy of items. 
	 *  Each class in the hierarchy maps to a given item type in Exchange. 
	 *  For example, the EmailMessage class represents email messages 
	 *  and the Appointment class represents calendar events and meetings.
	 * @throws Exception 
	 */
	// Function for display the list of folder under Inbox
	public void findFolder() throws Exception {
		FindFoldersResults findResults = service.findFolders(WellKnownFolderName.Inbox, new FolderView(Integer.MAX_VALUE));
		System.out.println(findResults.getTotalCount());
		System.out.println(findResults.getFolders().size());
		
		for ( Folder folder : findResults) {
			System.out.println(folder.getChildFolderCount());
			System.out.println(folder.getDisplayName());
		}
	}
		
	// Function for create Email Folder By Given Name
	public Folder createEmailFolderByGivenName ( String folderName) throws Exception {
		// check the folder is exiting or not
		FindFoldersResults findResults = service.findFolders(WellKnownFolderName.Inbox, new FolderView(Integer.MAX_VALUE));
		boolean folderIsExisted = false;
		for ( Folder folderExisted : findResults) {
			System.out.println(folderExisted.getDisplayName());
			System.out.println("folder wanna craeted: " + folderName);
			if( folderExisted.getDisplayName().toString() == folderName) {
				folderIsExisted = true;		
			}
			System.out.println("Is the folder existing? "+ folderIsExisted);
		}
		if(folderIsExisted == false) {
			Folder folder = new Folder(service);
			folder.setDisplayName(folderName);
			// creates the folder as a child of the Inbox folder.
			folder.save(WellKnownFolderName.Inbox);
		return folder;
		}else {
			System.out.println("folder is existing !");
			return null;
		}
	}
	
	public Folder getEmailFolderIdByName(String folderName) throws Exception {
		PropertySet properties = new PropertySet(BasePropertySet.IdOnly);
		properties.add(FolderSchema.DisplayName);
		FolderView view = new FolderView(100);
		view.setPropertySet(properties);
		FindFoldersResults foldersResults = service.findFolders(WellKnownFolderName.Inbox, view);
		for (Folder folder : foldersResults) {
			if (folderName.equals(folder.getDisplayName())) {
				System.out.println("This is the folder you get : " + folder.getDisplayName());
				System.out.println("This is the folder you get : " + folder.getId());
				return folder;
			}
		}
		return null;
	}
	// for testing - not proof yet
	/*public List<EmailMessage> readFolder( SearchFilterCollection serachFilterCollection ){
		List<EmailMessage> emailMessages = new ArrayList<EmailMessage>();
		return emailMessages;
	}*/
	
	/**
	 * read the email messages
	 * @param folderName - specific folder want to read
	 * @param searchFilterCollection - the collection of filter set
	 * @return
	 */
	public List<EmailMessage>readMessagesWithSerachFilterCollection( SearchFilterCollection searchFilterCollection) {
		
		List<EmailMessage> emailMessages = new ArrayList<EmailMessage>();
		
		try {
			// Folder to access by binding to an existing folder
			// Default- Folder folder = Folder.bind(service, WellKnownFolderName.Inbox);
			Folder folder = Folder.bind(service, WellKnownFolderName.Inbox);
			System.out.println( "Folder called:" + folder.getDisplayName());
			
			// Obtains the items
			// 1.set the list of items view, e.g. limited to 10	
			ItemView view = new ItemView(10);
			view.getOrderBy().add(ItemSchema.DateTimeReceived, SortDirection.Ascending);
			view.setPropertySet(new PropertySet( BasePropertySet.IdOnly, ItemSchema.Subject, ItemSchema.DateTimeReceived));
			
			// 2. Find the items
			FindItemsResults<Item> findResults = service.findItems(folder.getId(), searchFilterCollection ,view);
			//FindItemsResults<Item> findResults = service.findItems(WellKnownFolderName.Inbox, new SearchFilter.SearchFilterCollection( LogicalOperator.Or ,new SearchFilter.ContainsSubstring(ItemSchema.Subject,"This is the test for downloading mail attachment") ),view);
			
			//MOOOOOOST IMPORTANT: load items properties, before
			if(!findResults.getItems().isEmpty()) {
				service.loadPropertiesForItems(findResults, PropertySet.FirstClassProperties);
			}else {
				System.out.println("findResults.getItems().isEmpty() " + findResults.getItems().isEmpty() );
			}
			System.out.println("Total number of items found: " + findResults.getTotalCount());
			
			System.out.println("items.getItems().size()" + findResults.getItems().size());
			
			// 4. Insert items get from findResults to List of Email Messages
			if( findResults.getItems().size() > 0) {
				for ( Item item : findResults) {
					//System.out.println("item.getBody().toString()" + item.getBody().toString());
					System.out.println("item.getSubject" + item.getSubject());
					emailMessages.add(readEmailById(item.getId()));
					//System.out.println("emailMessages.size()" + emailMessages.size());
				}
			}
		} catch( Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("List of emailMessages" + emailMessages);
		return emailMessages;
	}
	// read the email by its id
	public EmailMessage readEmailById( ItemId itemid ) {
		EmailMessage emailMessage = null;
		try {
			/**
			 * PropertySet - 
			 * Represents a set of item or folder property. Property sets are used to
			 * indicate what property of an item or folder should be loaded when binding
			 * to an existing item or folder or when loading an item or folder's property.
			 */
			Item item = Item.bind(service, itemid, PropertySet.FirstClassProperties);
			emailMessage = EmailMessage.bind(service, item.getId());
			
			// **mark the email as read once it has been accessed
			// markAsRead(emailMessage, true);
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return emailMessage;
	} 
	
	public EmailMessage markAsRead( EmailMessage emailMessage, boolean read) {
		try {
			emailMessage.setIsRead(read);			
			emailMessage.update(ConflictResolutionMode.AlwaysOverwrite);
		}catch( Exception e) {
			e.printStackTrace();
		}
		return emailMessage;
	}
	/**
	 *  function for downloading Attachment from an email Message
	 * @param folderNameToMove - the folder name that message moving to after attachment download
	 * @param emailMessages	- an email message to be checked
	 * @throws Exception
	 */
	public void downloadAttachmentFromEmailMessage(EmailMessage emailMessage, String folderNameToMove) throws Exception {
		getListOfFilesName();
		System.out.println("listOfFilesName size" +listOfFilesName.size());
		System.out.println("listOfFilesName toString" +listOfFilesName.toString());
		
		// Email Subject
		System.out.println("Email Subject: " + emailMessage.getSubject());
		// How many attachments
		System.out.println("Number of attachments: " + emailMessage.getAttachments().getCount());
		// List the attachments
		for (Attachment att : emailMessage.getAttachments()) {
			// attachment name
			System.out.println("Attachment Name: " + att.getName());
			// attachment content type
			System.out.println("getContentType" + att.getContentType());

			AttachmentCollection attachments = emailMessage.getAttachments();
			for (Attachment a : attachments) {
				if (a instanceof FileAttachment) {
					if (!getListOfFilesName().contains(a.getName().toString())) {
						System.out.println("Attachment name is not existed");
						try (FileOutputStream stream = new FileOutputStream(ATTACHMENT_FILE_STORAGE_PATH + a.getName());) {
							FileAttachment fileAttachment = (FileAttachment) a;
							fileAttachment.load(stream);
						}
					} else {
						System.out.println("Attachment name is already existed");
						Date now = new Date();
						String addFileDate = DateTimeHelper.formatDate( now , DateTimeHelper.DATE_FORMAT_yyyyMMdd_HHmmss);
								
						try (FileOutputStream stream = new FileOutputStream(ATTACHMENT_FILE_STORAGE_PATH + addFileDate + "_" + a.getName());) {
							FileAttachment fileAttachment = (FileAttachment) a;
							fileAttachment.load(stream);
						}
					}
				}
			}

		}
		emailMessage.move(getEmailFolderIdByName(folderNameToMove).getId());
	}
	/**
	 *  function for downloading Attachment from List of Messages
	 * @param folderNameToMove - the folder name that message moving to after attachment download
	 * @param emailMessages	- the list of email messages to be checked
	 * @throws Exception
	 */
	public void downloadAttachmentFromListOfMessages(List<EmailMessage> emailMessages, String folderNameToMove) throws Exception {
		System.out.println( "emailMessages.size()" + emailMessages.size());
		if( !emailMessages.isEmpty() && emailMessages.size() > 0 ) {	
			for (EmailMessage email : emailMessages) {
				// Email Subject
				System.out.println("Email Subject: " +email.getSubject());
				// How many attachments
				System.out.println("Number of attachments: " + email.getAttachments().getCount());
				// List the attachments
				for (Attachment att : email.getAttachments()) {
					// attachment name
					System.out.println("Attachment Name: " + att.getName());
					// attachment content type
					System.out.println("getContentType" + att.getContentType());
					
					AttachmentCollection attachments = email.getAttachments();
					for (Attachment a : attachments) {
						if (a instanceof FileAttachment) {
							if (!getListOfFilesName().contains(a.getName().toString())) {
								System.out.println("Attachment name is not existed");
								try (FileOutputStream stream = new FileOutputStream(ATTACHMENT_FILE_STORAGE_PATH + a.getName());) {
									FileAttachment fileAttachment = (FileAttachment) a;
									fileAttachment.load(stream);
								}
							} else {
								System.out.println("Attachment name is already existed");
								Date now = new Date();
								String addFileDate = DateTimeHelper.formatDate( now , DateTimeHelper.DATE_FORMAT_yyyyMMdd_HHmmss);
									
								try (FileOutputStream stream = new FileOutputStream(ATTACHMENT_FILE_STORAGE_PATH + addFileDate + "_" + a.getName());) {
									FileAttachment fileAttachment = (FileAttachment) a;
									fileAttachment.load(stream);
								}
							}

						}
					}
	
				}
				email.move(getEmailFolderIdByName(folderNameToMove).getId());
			}
		}else {
			System.out.println("no emailMessage existed");
		}
		listOfFileInDir(ATTACHMENT_FILE_STORAGE_PATH);
	}
	
	// For checking the file is existing or not 
	public void listOfFileInDir( String path) {
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();	
		Set<String> listOfFilesName = new HashSet<>();
		System.out.println("listOfFiles.length: " + listOfFiles.length);
		for (int i = 0; i < listOfFiles.length; i++) {
		  if (listOfFiles[i].isFile()) { 
			listOfFilesName.add( listOfFiles[i].getName());		 
		    //System.out.println("File " + listOfFiles[i].getName());
		    //System.out.println("listOfFilesName " + listOfFilesName );
		  } else if (listOfFiles[i].isDirectory()) {
		    System.out.println("Directory " + listOfFiles[i].getName());
		  }
		}
		setListOfFileInDir(listOfFilesName);
	}
	
	public void setListOfFileInDir( Set<String> listOfFilesName ) {
		this.listOfFilesName = listOfFilesName;
	}
	
	public Set<String> getListOfFilesName(){
		return listOfFilesName;
	}
	
	// For getting the file folder root
	public File getAttachmentByFileName(String fileName) throws FileNotFoundException {
		File file = new File(ROOT_FILE_PATH + fileName +".pdf");
		if (!file.exists()){
            throw new FileNotFoundException("file with path: " + ROOT_FILE_PATH +  fileName + " was not found.");
        }
		return file;
	}
	
	// For getting the specific file
	public File getFileByPath() throws FileNotFoundException {
        File file = new File(FILE_PATH);
        if (!file.exists()){
            throw new FileNotFoundException("file with path: " + FILE_PATH + " was not found.");
        }
        return file;
    }
	

}
