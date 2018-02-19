package org.crypto.util;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.JsonObject;

public class CryptoDetailsMessageCodec implements MessageCodec<CryptoDetails, CryptoDetails> {
	
	@Override
	public CryptoDetails decodeFromWire(int position, Buffer buffer) {
		int _pos = position;
		int length = buffer.getInt(_pos);
		
		String jsonStr = buffer.getString(_pos+=4, _pos+=length);
		JsonObject contentJson = new JsonObject(jsonStr);
		
		String cryptoName = contentJson.getString("cryptoName");
	    float currencyRatio = contentJson.getFloat("currencyRatio");
	    double maxPrice = contentJson.getDouble("maxPrice");
	    double minPrice = contentJson.getDouble("minPrice");
	   
	    return new CryptoDetails(cryptoName, currencyRatio,maxPrice, minPrice);
		
	}
	
	@Override
	public String name() {
		return this.getClass().getSimpleName();
	}
	@Override
	public byte systemCodecID() {
		return -1;
	}	
	@Override
	public CryptoDetails transform(CryptoDetails objCryptoDetails) {
		return objCryptoDetails;
	}
	@Override
	public void encodeToWire(Buffer buffer, CryptoDetails objCryptoDetails) {
		 JsonObject jsonToEncode = new JsonObject();
		    jsonToEncode.put("cryptoName", objCryptoDetails.getCryptoName());
		    jsonToEncode.put("currencyRatio", objCryptoDetails.getCurrencyRatio());
		    jsonToEncode.put("maxPrice", objCryptoDetails.getMaxPrice());
		    jsonToEncode.put("minPrice", objCryptoDetails.getMinPrice());
		  
		    String jsonToStr = jsonToEncode.encode();	    
		    int length = jsonToStr.getBytes().length;	   
		    buffer.appendInt(length);
		    buffer.appendString(jsonToStr);
		
	}
}
