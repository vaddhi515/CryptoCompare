package org.crypto.services;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.StringJoiner;
import java.util.logging.Logger;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.crypto.util.CryptoDetails;
import org.crypto.util.PropertyConfig;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class CryptoRestServices extends AbstractVerticle {
	
	private static final Logger log = Logger.getLogger(CryptoRestServices.class.getName());
	
	private Hashtable<String, String> objHashTableProp;
	Properties objProp = PropertyConfig.getInstance().getConfigure();
	PropertiesConfiguration configure;
	private HttpServer httpServer;
	HttpServerResponse response;
	boolean checkDuplicate = false;	
	CryptoDetails objCrypto;
	HttpClientOptions httpClientOptions;
	HttpClientOptions options;
	StringBuffer uri;

	public CryptoRestServices(Hashtable objHashTableProp) {
		this.objHashTableProp = 	objHashTableProp;
	}
	
	@Override
	public void start(Future<Void> fut) throws Exception {
		
		log.info(" deployment ids  "+vertx.deploymentIDs());
		
		try(InputStream input = new FileInputStream(PropertyConfig.CONFIG_FILE_PATH)) {
			objProp = new Properties();
			objProp.load(input);
		}catch (IOException ex) {
			ex.printStackTrace();
		} 
		
		Router router = Router.router(vertx);
		
		getVertx().createHttpServer().requestHandler(router::accept).listen(
				config().getInteger("http.port", 8080),  result -> {
		           if (result.succeeded()) {
		        	 fut.complete();
		           } else {
		             fut.fail(result.cause());
		           }
		         });
		
		router.route("/cryptos").handler(this::cryptos);
		router.get("/cryptos/:ccy1/:ccy2").handler(this::cryptos);
		
	}
	
	private void cryptos(RoutingContext routingContext) {
		
		HttpServerRequest request = routingContext.request();
		HttpServerResponse response = routingContext.response();
		
		log.info("request.method() "+request.method());
		
		if (request.method() == HttpMethod.POST) {
			addCurrencies(request, response);
		}else if(request.method() == HttpMethod.GET) {
			getCurrencyRatio(request);
		}		
	}
	
	private void addCurrencies(HttpServerRequest request, HttpServerResponse response) {
		try {
			configure = new PropertiesConfiguration(PropertyConfig.CONFIG_FILE_PATH);
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
		
		request.bodyHandler(reqBody -> {
			Map<Object, Object> objmap = Json.decodeValue(reqBody, Hashtable.class);
			objmap.forEach(								
					(k,v)->{								
						if(!configure.getProperty("CURRENCIES").toString().contains(objmap.get(k)+"")) {								
							objProp.setProperty("CURRENCIES", objProp.getProperty("CURRENCIES")+","+objmap.get(k));
						}									
					});
			try(FileOutputStream out = new FileOutputStream(PropertyConfig.CONFIG_FILE_PATH)){
				objProp.store(out, null);
				out.flush();
			}catch (IOException ex) {
				ex.printStackTrace();
			}
		});
		getVertx().eventBus().publish("NewCurrencyAdded", "New Currency Added");            
		response.putHeader("content-type", "application/json; charset=utf-8");		
		response.setStatusCode(201);
		response.end(" Currencies added to the Property file ");	
	
	}
	
	private void getCurrencyRatio(HttpServerRequest request) {
		request.bodyHandler(reqBody -> {
			try {
				StringJoiner currencyJoiner = new StringJoiner(",");	
				currencyJoiner.add(request.getParam("ccy1"));
				currencyJoiner.add(request.getParam("ccy2"));
				
				if(null != objProp) {
					options = new HttpClientOptions().setDefaultHost(objProp.getProperty("HOST").toString())
							.setSsl(true).setTrustAll(true).setDefaultPort(443);
					uri = new StringBuffer(objHashTableProp.get("PRICE_URI").replace("%", currencyJoiner.toString()));
					getVertx().deployVerticle(new CryptoCurrencyService(options, uri));			
				}	
				HttpServerResponse resp = request.response();
				
				getVertx().eventBus().consumer("CurrencyRatio", message -> {
					objCrypto =   (CryptoDetails) message.body();
					if(!resp.closed()) {
						resp.putHeader("content-type", "application/json");		
						resp.setStatusCode(201);
			    		resp.end(objCrypto.toString());
					}
				});
				
				
			}catch(Exception ex) {
				ex.printStackTrace();
			}
		}); 
		
	}
	
	@Override
	public void stop(Future stopFuture) throws Exception {
		log.info(" CryptoRestServices  stopped! ");
	}
}
