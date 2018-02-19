package org.crypto;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Properties;
import java.util.logging.Logger;

import org.crypto.scheduler.CryptoCurrencyReceiver;
import org.crypto.scheduler.CryptoCurrencyScheduler;
import org.crypto.services.CryptoCurrencyPrice;
import org.crypto.services.CryptoRestServices;
import org.crypto.util.CryptoDetails;
import org.crypto.util.CryptoDetailsMessageCodec;
import io.vertx.core.Vertx;

public class VertxApp {
	
	private static final Logger log = Logger.getLogger(VertxApp.class.getName());
	
	public static void main(String[] args) {
		
		Properties prop = new Properties();
		Hashtable<String, String> objHashTableProp = new Hashtable();
		try(InputStream input = new FileInputStream("..\\CryptoCompare\\src\\main\\resources\\config.properties")) {
			prop.load(input);
			objHashTableProp.put("CURRENCIES", prop.getProperty("CURRENCIES"));
			objHashTableProp.put("HOST", prop.getProperty("HOST"));
			objHashTableProp.put("PRICE_URI", prop.getProperty("PRICE_URI"));
		} catch (IOException ex) {
			ex.printStackTrace();
		} 
		
		Vertx vertx = Vertx.vertx();				
		//vertx.eventBus().registerCodec(new CryptoDetailsMessageCodec());		
		CryptoDetailsMessageCodec objMyCodec =  new CryptoDetailsMessageCodec();
		vertx.eventBus().registerDefaultCodec(CryptoDetails.class , objMyCodec);			
		vertx.deployVerticle(new CryptoCurrencyPrice(objHashTableProp));
		vertx.deployVerticle(new CryptoCurrencyScheduler(objHashTableProp));
		vertx.deployVerticle(new CryptoCurrencyReceiver());
		vertx.deployVerticle(new CryptoRestServices(objHashTableProp));
	}
}
