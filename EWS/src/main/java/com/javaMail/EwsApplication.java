package com.javaMail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EwsApplication {
	
	@Autowired
	private EWSService ewsSerivce;

	public static void main(String[] args) {
		SpringApplication.run(EwsApplication.class, args);
		
		try {
			EWSService ewsSerivce = new EWSService();
			//ewsSerivce.getEmailFolderIdByName("CubeTestReport");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
