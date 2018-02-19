package org.crypto.scheduler;

import java.util.logging.Logger;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

public class CryptoCurrencyReceiver extends AbstractVerticle {
	
	private static final Logger log = Logger.getLogger(CryptoCurrencyReceiver.class.getName());
	
	@Override
	public void start(Future<Void> startFuture) {
        vertx.eventBus().consumer("CurrencyRate", message -> {
        	log.info(" message received: " +  message.body().toString());
        });
    }
	
	@Override
	public void stop(Future stopFuture) throws Exception {
		log.info("MyVerticle stopped!");
	}
    
}
