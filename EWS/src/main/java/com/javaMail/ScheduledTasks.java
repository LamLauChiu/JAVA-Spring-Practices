package com.javaMail;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import microsoft.exchange.webservices.data.core.enumeration.search.LogicalOperator;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.core.service.schema.ItemSchema;
import microsoft.exchange.webservices.data.search.filter.SearchFilter;
import microsoft.exchange.webservices.data.search.filter.SearchFilter.SearchFilterCollection;

@Component
public class ScheduledTasks {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	@Autowired
	private EWSService ewsService;
	
    
    @Scheduled(fixedRate = 1000*40)
    public void reportCurrentTime() {
        log.info("The time is now {}", dateFormat.format(new Date()));
        
        SearchFilterCollection searchFilterCollection = new SearchFilterCollection(LogicalOperator.And);
		
		//new SearchFilter.ContainsSubstring(ItemSchema.Subject,"This is the test for downloading mail attachment") ),view);
		
		//searchFilterCollection.add(new SearchFilter.ContainsSubstring(EmailMessageSchema.Subject, "This is the test for downloading mail attachment"));
		
	
		
		Date dateTo = new Date();
		Date dateFrom = DateTimeHelper.addDays(dateTo, -3);
		System.out.println("dateFrom: "+ dateFrom);
		System.out.println("dateTo:" + dateTo);
		
		searchFilterCollection.add(new SearchFilter.IsGreaterThan(ItemSchema.DateTimeReceived,dateFrom));
		searchFilterCollection.add(new SearchFilter.IsLessThan(ItemSchema.DateTimeReceived,dateTo));
		
		searchFilterCollection.add(new SearchFilter.ContainsSubstring(ItemSchema.Subject, "This is the test for downloading mail attachment"));
		searchFilterCollection.add(new SearchFilter.IsEqualTo( ItemSchema.HasAttachments, true));
		//FindItemsResults<Item> findResults = service.findItems(WellKnownFolderName.Inbox, new SearchFilter.SearchFilterCollection( LogicalOperator.Or ,new SearchFilter.ContainsSubstring(ItemSchema.Subject,"This is the test for downloading mail attachment") ),view);
		
		
		
		List<EmailMessage> emailMessages = ewsService.readMessagesWithSerachFilterCollection(searchFilterCollection);
		
		
		try {
			ewsService.downloadAttachmentFromListOfMessages(emailMessages, "XXXXREPORT");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        
    }
}
