package ru.step.objects;

import java.util.ArrayList;

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;


	public class GetCampaignJobs extends POMWebServiceOperation{
	
	private final String actionServiceName = "GetCampaignJobs";;
	private SOAPMessage response;
	private ArrayList<String> params;
	
	public 	GetCampaignJobs(ArrayList<String> params){
		this.params = params;
				
		if (isParamsSufficient()) {
		 getLogger().info("Run request for POM WebService:" + actionServiceName);
		 response = callPOMWebService(actionServiceName);
		}
		else {
			getLogger().error("Number parameters for GetCampaignJobs is insufficient!");
			getLogger().error("POMWebService not called!");
			System.exit(0);
		}
			
	}
		
	private boolean isParamsSufficient() {
		if (params.size() >= 2)
			return true;
		else 		
			return false;
	}

	/*private Logger getLogger() {
	    return MyLogger.log;
	}*/

	/* Example of Request
	<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:cmp="http://services.pim.avaya.com/CmpMgmt/">
   		<soapenv:Header/>
   		<soapenv:Body>
      		<cmp:GetCampaignJobs>
         		<cmp:CampaignName>b1122_infinite</cmp:CampaignName>
         	<!--Zero or more repetitions:-->
         		<cmp:JobState>JOB_COMPLETED</cmp:JobState>
      		</cmp:GetCampaignJobs>
   		</soapenv:Body>
	</soapenv:Envelope>   
	*/

	@Override
	public void createSoapEnvelope(SOAPMessage soapMessage){
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
        	soapBodyElem.addChildElement("CampaignName", namespace).addTextNode(params.get(0));
        	soapBodyElem.addChildElement("JobState", namespace).addTextNode(params.get(1));
		} catch (SOAPException except) {
			getLogger().error(except.getMessage());
			getLogger().debug(printErrorStackTrace(except));
		}
  
		/* Example of Request
		<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:cmp="http://services.pim.avaya.com/CmpMgmt/">
	   		<soapenv:Header/>
	   		<soapenv:Body>
	      		<cmp:GetCampaignJobs>
	         		<cmp:CampaignName>b1122_infinite</cmp:CampaignName>
	         	<!--Zero or more repetitions:-->
	         		<cmp:JobState>JOB_COMPLETED</cmp:JobState>
	      		</cmp:GetCampaignJobs>
	   		</soapenv:Body>
		</soapenv:Envelope>   
		*/
    }
	
	public String getResponseString() {
		return convertSOAPMessageToString(response); 
	}

	public String getResponseResult() {
		return getSOAPResponseTagText(response, "JobID");
	}
}
