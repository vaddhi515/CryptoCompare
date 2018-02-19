package org.crypto.scheduler;

import java.util.Hashtable;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.crypto.util.PropertyConfig;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;

public class CryptoCurrencyScheduler extends AbstractVerticle {
	
	private static final Logger log = Logger.getLogger(CryptoCurrencyScheduler.class.getName());
	
	private Hashtable<String, String> objHashTableProp = null;
	PropertiesConfiguration configure ;
	Properties objProp;
	HttpClientOptions httpClientOptions = null;
	HttpClient httpClient;
	StringBuffer uri;
	String uriCurrencies;
	
	public CryptoCurrencyScheduler(Hashtable objHashTableProp) {
		this.objHashTableProp = objHashTableProp;
		
		if(null != this.objHashTableProp) {
    		httpClientOptions = new HttpClientOptions().setDefaultHost(this.objHashTableProp.get("HOST"))
					.setSsl(true).setTrustAll(true).setDefaultPort(443);
			uri = new StringBuffer(this.objHashTableProp.get("PRICE_URI").replace("%", this.objHashTableProp.get("CURRENCIES")));
		}
	}
	
    public void start(Future<Void> startFuture) {   	
    	httpClient = vertx.createHttpClient(httpClientOptions);
    	
    	long timerID = vertx.setPeriodic(5000, new Handler<Long>() {
		    @Override
		    public void handle(Long aLong) {		    	
		    	vertx.eventBus().consumer("NewCurrencyAdded", message -> {
			        try {
						configure = new PropertiesConfiguration(PropertyConfig.CONFIG_FILE_PATH);
					} catch (ConfigurationException e) {
						e.printStackTrace();
					}
			        int length = configure.getProperty("CURRENCIES").toString().length();
			        uriCurrencies  = configure.getProperty("CURRENCIES").toString().substring(1, length-1);
			        httpClient = vertx.createHttpClient(httpClientOptions);
			        uri = null;
			    	uri = new StringBuffer(objHashTableProp.get("PRICE_URI").toString().replace("%", uriCurrencies));
		        });
		    	httpClient.getNow(uri.toString(), resp -> {
		    		if(resp.statusCode() == 200) {
					         resp.bodyHandler(  body -> {
					        	 vertx.eventBus().publish("CurrencyRate", body);
					         });
		    			  }
				    });		    	 
		         };		    
		});   	       
    }
    
    @Override
	public void stop(Future stopFuture) throws Exception {
		log.info("MyVerticle stopped!");
	}
    
}
