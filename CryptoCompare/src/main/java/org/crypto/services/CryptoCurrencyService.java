package org.crypto.services;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

import org.crypto.util.CryptoDetails;
import org.crypto.util.CryptoDetailsMessageCodec;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

public class CryptoCurrencyService extends AbstractVerticle {
	
	private static final Logger log = Logger.getLogger(CryptoCurrencyService.class.getName());
	
	private HttpClientOptions httpClientOptions;
	Map<String, Object> objCryptoDetails = new HashMap();
	private final ConcurrentMap<String, MessageCodec> userCodecMap = new ConcurrentHashMap<>();
	CryptoDetails objCrypto;
	JsonObject jsonObj;
	StringBuffer uri ;
	Double price = 0.0;
	Double maxPrice= 0.0;
	Double minPrice= 0.0;
	
	public CryptoCurrencyService(HttpClientOptions options, StringBuffer uri) {
		this.httpClientOptions = options;
		this.uri = uri;
	}
	
	public void start(Future<Void> startFuture) {		
		HttpClient httpClient = vertx.createHttpClient(this.httpClientOptions);	
		CryptoDetailsMessageCodec objCodec = new CryptoDetailsMessageCodec();
		httpClient.getNow(uri.toString(), resp -> {	
			if(resp.statusCode() == 200) {
							StringJoiner currencyJoiner = new StringJoiner("/");								
							resp.bodyHandler(
			  						(body) -> {
			  							jsonObj = body.toJsonObject();	
			  							Map<String, Object> objmap = Json.decodeValue(body, Map.class);
			  							objmap.forEach( 
			  									(k,v) -> { 	
			  										objCryptoDetails =(HashMap)objmap.get(k);	
			  										objCryptoDetails.forEach(
			  												(k1,v1)-> {			  													
			  														if(k1.equalsIgnoreCase("USD")) {
			  															currencyJoiner.add(k);
			  															price = Double.parseDouble(objCryptoDetails.get(k1).toString());
			  															log.info("price  "+ price);
			  															if(price > maxPrice) {
			  																maxPrice = price;
			  															}else {
			  																minPrice = price;
			  															}
			  														}			  														
			  													}
			  												);  
			  											log.info("currencyJoiner  "+currencyJoiner);
			  											objCrypto = new CryptoDetails();
			  											objCrypto.setCryptoName(currencyJoiner.toString());
			  											objCrypto.setMinPrice(minPrice);
			  											objCrypto.setMaxPrice(maxPrice);
			  											objCrypto.setCurrencyRatio(0.0f);
			  											if(minPrice>0) {
			  												float ratio = (float)(maxPrice/minPrice);
			  												objCrypto.setCurrencyRatio(ratio);
			  											}			 			  											
			  									});		  		
			  							vertx.eventBus().publish("CurrencyRatio", objCrypto);  
			  						});
							startFuture.complete();						
					}
			});
		}
	
	@Override
	public void stop(Future stopFuture) throws Exception {
		log.info("MyVerticle stopped!");
	}
}
