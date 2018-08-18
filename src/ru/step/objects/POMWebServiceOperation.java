package ru.step.objects;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.Logger;

public abstract class POMWebServiceOperation {
	
	public String namespaceURI = "http://services.pim.avaya.com/CmpMgmt/";
	public String soapUrl = "https://" + getProperty("epm.ip") + "/axis2/services/VP_POMCmpMgmtService/";
	public String namespace = "tns";
	
	
	abstract public void createSoapEnvelope(SOAPMessage soapMessage);
	
	
	public SOAPMessage callPOMWebService(String actionServiceName) {
    	SOAPConnectionFactory soapFactory  = null;
    	SOAPConnection        soapConnect  = null;
    	SOAPMessage           soapRequest  = null;
    	SOAPMessage           soapResponse = null;
        try {
            // Создание SOAP Connection
        	soapFactory = SOAPConnectionFactory.newInstance();
            soapConnect = soapFactory.createConnection();

            // Создание SOAP Message Request
            soapRequest  = createSOAPRequest(actionServiceName);
            
            getLogger().info("Request SOAP Message for " + actionServiceName + ":");
            getLogger().info(convertSOAPMessageToString(soapRequest));
            
            // Получение SOAP Message Response
            soapResponse = soapConnect.call(soapRequest, soapUrl);
            
            getLogger().info("Response SOAP Message for " + actionServiceName + ":");
            getLogger().info(convertSOAPMessageToString(soapResponse));
                                   		
            soapConnect.close();
            
        } catch (Exception except) {
        	getLogger().error("\nError occurred while sending SOAP Request to Server!\n"
            		         + "Make sure you have the correct endpoint URL and SOAPAction!\n");
        	getLogger().error(except.getMessage());
        	getLogger().debug(printErrorStackTrace(except));
        	
        }
        return soapResponse;
    }
	
	
	private SOAPMessage createSOAPRequest(String actionServiceName) throws Exception
	{
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();

        createSoapEnvelope(soapMessage);
        
        String soapAction = namespaceURI + "/" + actionServiceName;
        
        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", soapAction);
        
        // Определение авторизации сервиса
        String loginPassword = getProperty("epm.login") + ":" + getProperty("epm.password");
        Base64 codec = new Base64();
        String auth = new String(codec.encodeBase64(loginPassword.getBytes()));
        getLogger().debug(" Authentication string for Base64 format [" + auth + "]");
        
        headers.addHeader("Authorization", "Basic " + auth);

        soapMessage.saveChanges();

        return soapMessage;
    }
	
	
	public String convertSOAPMessageToString (SOAPMessage soapResponse){
        TransformerFactory transformerFactory;
        Transformer        transformer;
        StringWriter stringResult = new StringWriter();
        try {
            // Создание XSLT-процессора
            transformerFactory = TransformerFactory.newInstance();
            transformer = transformerFactory.newTransformer();
            
            // Получение содержимого ответа
            Source content;
            content = soapResponse.getSOAPPart().getContent();
            
            // Определение выходного потока в stringResult
            StreamResult result = new StreamResult(stringResult);
            transformer.transform(content, result);

        } catch (Exception except) {
        	getLogger().error(except.getMessage());
        	getLogger().debug(printErrorStackTrace(except));
        }
        return stringResult.toString().replace('"', '|');
	}
	
	public String printErrorStackTrace(Exception except) {
		StringWriter sw = new StringWriter();
    	except.printStackTrace(new PrintWriter(sw));
    	return sw.toString();
	}
	
	
	public String getProperty(String propName) {
		String prop = null;
		try {
		prop = System.getProperty(propName);
			if (prop.isEmpty()) {
				getLogger().error("Property name [" + propName + "] is EMPTY in configuration file. Check this and try again ..." );
				System.exit(0);
			}
		}
		catch (NullPointerException except) {
			getLogger().error("Property name [" + propName + "] is INVALID or MISSING in configuration file. Check this and try again ..." );
			System.exit(0);
		}
		return prop;		
	}
	
	public Logger getLogger() {
	    return MyLogger.log;
	}

}
