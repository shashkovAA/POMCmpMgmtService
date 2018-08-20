package ru.step;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.logging.log4j.Logger;

import ru.step.objects.GetCampaignJobs;
import ru.step.objects.MyLogger;
import ru.step.objects.RunCampaign;
import ru.step.objects.StopCampaign;
import ru.step.objects.StopJob;


public class POMCmpMgmtClient {
	private String actionServiceName;
	private final static Properties properties = new Properties();
	private ArrayList<String> params = new ArrayList<String>();
	private String webServiceResponse;
	public POMCmpMgmtClient(String[] args) {
		
		MyLogger.initLogger();
		
		getProgramAttributes(args);
		
		switch (actionServiceName) {
		case "GetCampaignJobs": 
								GetCampaignJobs getCampaignJobs = new GetCampaignJobs(params);
								//webServiceResponse = getCampaignJobs.getResponseString();
								webServiceResponse = getCampaignJobs.getResponseResult();
								break;
		case "RunCampaign": 
								RunCampaign runCampaign = new RunCampaign(params);
								//webServiceResponse = runCampaign.getResponseString();
								webServiceResponse = runCampaign.getResponseResult();
								break;
		case "StopJob": 	
								StopJob stopJob = new StopJob(params);
								//webServiceResponse = stopJob.getResponseString();
								webServiceResponse = stopJob.getResponseResult();
								break;
		case "StopCampaign": 	
								StopCampaign stopCampaign = new StopCampaign(params);
								//webServiceResponse = stopCampaign.getResponseString();
								webServiceResponse = stopCampaign.getResponseResult();								
								break;
		}
		System.out.println(webServiceResponse);
	}
		
	private void getProgramAttributes(String[] args) {
		
		if (args.length >= 1) 
			loadProperties(args[0]);  	
        else {
        	getLogger().error("Missing first mandatory program attributes: <epm_config_filename>.");
        	getLogger().error("Note: Program attributes is <epm_config_filename> <service_method> <params> [params]");
        	System.exit(0);
        }
		
		if (args.length >= 2)
			actionServiceName = args[1];
        else {
        	getLogger().warn("Missing second mandatory program attributes: <service_method>.");
        	System.exit(0);
        	
        }
        if (args.length >= 3) {
        	        	
        	for (int i=2; i< args.length; i++)
        		params.add(args[i]);	     	
        } 
        
	}
	
	
	private void loadProperties(String fileName) {   	

		try{
	   	  	  FileInputStream fis = new FileInputStream(fileName);
		      properties.load( fis );
		      getLogger().debug("Getting properties from file [" + fileName + "]:");
		      
		      for (Enumeration names = properties.propertyNames(); names.hasMoreElements();) {
		      	String key = (String) names.nextElement();
		      	getLogger().debug("key =" + key + " value =" + properties.getProperty(key, ""));
		      	System.setProperty(key, properties.getProperty(key, ""));	            
		      }
		      fis.close();
	   	  } catch (FileNotFoundException e) {
	   	      getLogger().error("Failed to load properties file: exception=" + e.getMessage());
	   	      System.exit(0);
	   	  } catch (IOException e) {
	   	      getLogger().error("Failed to load properties file: exception=" + e.getMessage());
	   	      System.exit(0);
	   	  }	      
	   }
	
	
	
	private Logger getLogger() {		
	    return MyLogger.log;
	}
	
	private String printErrorStackTrace(Exception except) {
		StringWriter sw = new StringWriter();
    	except.printStackTrace(new PrintWriter(sw));
    	return sw.toString();
	}
	
	public static void main(String[] args) {
		
		POMCmpMgmtClient client = new POMCmpMgmtClient(args);
		
	}

}
