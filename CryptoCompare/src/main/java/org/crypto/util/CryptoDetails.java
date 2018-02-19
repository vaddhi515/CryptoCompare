package org.crypto.util;

public class CryptoDetails {

	private String cryptoName;
	private Float currencyRatio;
	private Double maxPrice = 0.0;
	private Double minPrice = 0.0;
	
	public CryptoDetails() {
		
	}
	
	public CryptoDetails(String cryptoName, Float currencyRatio,  Double maxPrice, Double minPrice) {
	    this.cryptoName = cryptoName;
	    this.currencyRatio = currencyRatio;
	    this.maxPrice = maxPrice;
	    this.minPrice = minPrice;
	}
	
	public String getCryptoName() {
		return cryptoName;
	}

	public void setCryptoName(String cryptoName) {
		this.cryptoName = cryptoName;
	}

	public Float getCurrencyRatio() {
		return currencyRatio;
	}

	public void setCurrencyRatio(Float currencyRatio) {
		this.currencyRatio = currencyRatio;
	}

	public void setMaxPrice(Double maxPrice) {
		this.maxPrice = maxPrice;
	}

	public Double getMaxPrice() {
		return maxPrice;
	}

	public void setMinPrice(Double minPrice) {
		this.minPrice = minPrice;
	}

	public Double getMinPrice() {
		return minPrice;
	}	

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("CryptoDetails{");
		sb.append("cryptoName=").append(cryptoName);
		sb.append(", currencyRatio='").append(currencyRatio);
		sb.append(", maxPrice='").append(maxPrice);
		sb.append(", minPrice='").append(minPrice);		
		sb.append('}');
		return sb.toString();
	}

}
