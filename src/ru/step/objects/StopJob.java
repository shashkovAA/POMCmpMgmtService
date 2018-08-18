package ru.step.objects;

import java.util.ArrayList;

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

public class StopJob extends POMWebServiceOperation{
	private final String actionServiceName = "StopJob";;
	private SOAPMessage response;
	private ArrayList<String> params;
	
	public 	StopJob(ArrayList<String> params){
		this.params = params;
		getLogger().info("Run request for POM WebService:" + actionServiceName);
		
		if (isParamsSufficient())			
		 response = callPOMWebService(actionServiceName);
		else {
			getLogger().error("Number parameters for StopJob is insufficient!");
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
        	soapBodyElem.addChildElement("JobID", namespace).addTextNode(params.get(0));
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
	
	public String getResponseString() {
		return convertSOAPMessageToString(response); 
	}

}
