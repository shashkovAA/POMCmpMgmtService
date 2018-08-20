package ru.step.objects;

import java.util.ArrayList;

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.w3c.dom.NodeList;

public class StopCampaign extends POMWebServiceOperation{
	private String actionServiceName;
	private SOAPMessage response;
	private ArrayList<String> params;
	private String activeJobID;
	private String stopJobResult = "-1";
	private boolean isSetProceedStopCampaignFlag = false;
	
	public 	StopCampaign(ArrayList<String> params){
		this.params = params;
		actionServiceName = "GetCampaignJobs";		
		getLogger().info("Run request for POM WebService:" + actionServiceName);
		
		if (isParamsSufficient())			
		 response = callPOMWebService(actionServiceName);
		else {
			getLogger().error("Number parameters for StopActiveJob is insufficient!");
			getLogger().error("POMWebService not called!");
			System.exit(0);
		}
		//activeJobID = parseResponseForGetCampaignJobs(response);
		activeJobID = getSOAPResponseTagText(response, "JobID");
		
		if (activeJobID.equals("-1"))
        	getLogger().info("Not found active JobId for campaign : " + params.get(0));
        else
        	if (isIdFieldContainsOnlyDigits(activeJobID)) {
        		isSetProceedStopCampaignFlag = true;
        		getLogger().info("Get JobId = " + activeJobID);
        	} else {
				getLogger().error("An error has occurred on getting JobId number");
				getLogger().error("JobId = " + activeJobID);			
			} 
        
        if (isSetProceedStopCampaignFlag) {
        	getLogger().info("Run request for POM WebService: StopJob");
        	actionServiceName  = "StopJob";	
        	response = callPOMWebService(actionServiceName);		
    		
    		//stopJobResult = parseResponseForStopJob(response);
    		stopJobResult = getSOAPResponseTagText(response, "IsStopped");
    		
    		if (stopJobResult.equals("true"))
    			getLogger().info("Request for POM WebService [StopJob] return [true]");
    		else {
    			getLogger().warn("Request for POM WebService [StopJob] return [fault]. Check response manually.");
    			getLogger().warn(stopJobResult);
    		}	
        }    
	}
		
	private boolean isParamsSufficient() {
		if (params.size() >= 1)
			return true;
		else 		
			return false;
	}
	@Override
	public void createSoapEnvelope(SOAPMessage soapMessage) {
		SOAPPart soapPart = soapMessage.getSOAPPart();

        // SOAP Envelope
        SOAPEnvelope envelope = null;
        SOAPBody soapBody;
        SOAPElement soapBodyElem;
        
		try {
			envelope = soapPart.getEnvelope();
			envelope.addNamespaceDeclaration(namespace, namespaceURI);
			soapBody = envelope.getBody();
			    		
    		soapBodyElem = soapBody.addChildElement(actionServiceName, namespace);
    		
    		if (!isSetProceedStopCampaignFlag) {
    			soapBodyElem.addChildElement("CampaignName", namespace).addTextNode(params.get(0));
    			soapBodyElem.addChildElement("JobState", namespace).addTextNode("JOB_ACTIVE");
    		} else
    			soapBodyElem.addChildElement("JobID", namespace).addTextNode(activeJobID);
		} catch (SOAPException except) {
			getLogger().error(except.getMessage());
			getLogger().debug(printErrorStackTrace(except));
		}
  
	/* Example of Request
	<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:cmp="http://services.pim.avaya.com/CmpMgmt/">
    <soapenv:Header/>
    <soapenv:Body>
      <cmp:StopJob>
         <cmp:JobID>117</cmp:JobID>
      </cmp:StopJob>
    </soapenv:Body>
	</soapenv:Envelope>
		*/
    }	
	/*private String parseResponseForGetCampaignJobs(SOAPMessage soapResponse) {
    	
    	SOAPBody body = null;
		
    	try {
			body = soapResponse.getSOAPBody();
		} catch (SOAPException except) {
			getLogger().error(except.getMessage());
		}
    	NodeList list = null;
    	String jobId = "-1";
    	String soapResponseString = convertSOAPMessageToString(soapResponse);
    	
    	String partResponse = soapResponseString.substring(0,soapResponseString.indexOf(namespaceURI));
    	String responseNamespace = partResponse.substring(partResponse.lastIndexOf(':') + 1, partResponse.lastIndexOf('='));
    	
    	//Find namespace from Response. Example: It is [ns1] from  xmlns:ns1="http://services.pim.avaya.com/CmpMgmt/"
    	list = body.getElementsByTagName(responseNamespace + ":JobID");
    	
    	if (list.getLength()!=0)
    		try {
    			jobId = list.item(0).getTextContent();
    		} catch (Exception except){
    			getLogger().error(except.getMessage());
    			getLogger().debug(printErrorStackTrace(except));
    		}
    	
    	return jobId;
	}
	
	private String parseResponseForStopJob(SOAPMessage soapResponse) {
		
		SOAPBody body = null;
		
    	try {
			body = soapResponse.getSOAPBody();
		} catch (SOAPException except) {
			getLogger().warn(except.getMessage());
		}
    	NodeList list;
    	
    	String soapResponseString = convertSOAPMessageToString(soapResponse);
    	String result = soapResponseString;
    	
    	String partResponse = soapResponseString.substring(0,soapResponseString.indexOf(namespaceURI));
    	String responseNamespace = partResponse.substring(partResponse.lastIndexOf(':') + 1, partResponse.lastIndexOf('='));
    	
    	//Find namespace from Response. Example: It is [ns1] from  xmlns:ns1="http://services.pim.avaya.com/CmpMgmt/"
    	list = body.getElementsByTagName(responseNamespace + ":IsStopped");
    	
    	try {
    		result = list.item(0).getTextContent();
    	} catch (Exception except){
    		getLogger().error(except.getMessage());
    		getLogger().debug(printErrorStackTrace(except));
    	}
    	
    	return result;
	}
*/
	private boolean isIdFieldContainsOnlyDigits (String id) {
		String regex = "[\\d]+";
		if (id.matches(regex))
			return true;
		else 
			return false;
	}

	public String getResponseString() {
		return convertSOAPMessageToString(response); 
	}

	public String getResponseResult() {	
		return stopJobResult + "," + activeJobID;
	}
	
}
