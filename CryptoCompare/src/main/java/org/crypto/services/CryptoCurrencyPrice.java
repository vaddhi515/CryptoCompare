package org.crypto.services;

import java.util.Hashtable;
import java.util.logging.Logger;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpClientOptions;

public class CryptoCurrencyPrice extends AbstractVerticle {
	
	private static final Logger log = Logger.getLogger(CryptoCurrencyPrice.class.getName());
	
	private Hashtable<String, String> objHashTableProp = null;
	HttpClientOptions httpClientOptions = null;
	HttpClientOptions options = null;
	StringBuffer uri;
	
	public CryptoCurrencyPrice(Hashtable objHashTableProp) {
		this.objHashTableProp = objHashTableProp;
	}
	
	@Override
	public void start(Future<Void> startFuture) {		
		if(null != objHashTableProp) {
			options = new HttpClientOptions().setDefaultHost(objHashTableProp.get("HOST"))
					.setSsl(true).setTrustAll(true).setDefaultPort(443);
			uri = new StringBuffer(objHashTableProp.get("PRICE_URI").replace("%", objHashTableProp.get("CURRENCIES")));
			vertx.deployVerticle(new CryptoCurrencyService(options, uri));			
		}		
	}

	@Override
	public void stop(Future stopFuture) throws Exception {
		log.info("MyVerticle stopped!");
	}
}
