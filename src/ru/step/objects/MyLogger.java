package ru.step.objects;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class MyLogger {
	public static Logger log;

	public static void initLogger() {
		log = LogManager.getLogger("POMWebService");
	}

	

}
