package ru.step.objects;

import java.util.ArrayList;

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

public class GetContactListNames extends POMWebServiceOperation{
	
	private final String actionServiceName = "GetContactListNames";;
	private SOAPMessage response;
	private ArrayList<String> params;
	
	public 	GetContactListNames(ArrayList<String> params){
		this.params = params;
				
		if (isParamsSufficient()) {
		 getLogger().info("Run request for POM WebService:" + actionServiceName);
		 response = callPOMWebService(actionServiceName);
		}
		else {
			getLogger().error("Number parameters for "+ actionServiceName +" is insufficient!");
			getLogger().error("POMWebService not called!");
			System.exit(0);
		}
			
	}
		
	private boolean isParamsSufficient() {
		if (params.size() >= 1)
			return true;
		else 		
			return false;
	}

	
	/* Example of Request
	<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:cmp="http://services.pim.avaya.com/CmpMgmt/">
   	<soapenv:Header/>
   		<soapenv:Body>
      		<cmp:GetContactListNames>
         		<cmp:CampaignName>b1122_campaign</cmp:CampaignName>
      		</cmp:GetContactListNames>
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
		} catch (SOAPException except) {
			getLogger().error(except.getMessage());
			getLogger().debug(printErrorStackTrace(except));
		}
  
		/* Example of Response
		<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
   			<soapenv:Body>
      			<ns1:GetContactListNamesResponse xmlns:ns1="http://services.pim.avaya.com/CmpMgmt/">
         			<ns1:ListNames>rshb_test1</ns1:ListNames>
      			</ns1:GetContactListNamesResponse>
   			</soapenv:Body>
		</soapenv:Envelope>  
		*/
    }
	
	public String getResponseString() {
		return convertSOAPMessageToString(response); 
	}

	public String getResponseResult() {
		return getSOAPResponseTagText(response, "ListNames");
	}
}