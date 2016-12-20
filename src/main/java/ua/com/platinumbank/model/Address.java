package ua.com.platinumbank.model;

public class Address {

	private String region;
	private String district;
	private String cityType;
	private String city;
	private String streetType;
	private String street;
	private String house;
	private String postIndex;

	public Address() {

	}

	public String getRegion() {

		return region;
	}

	public Address setRegion(String region) {

		this.region = region;
		return this;
	}

	public String getDistrict() {

		return district;
	}

	public Address setDistrict(String district) {

		this.district = district;
		return this;
	}

	public String getCityType() {

		return cityType;
	}

	public Address setCityType(String cityType) {

		this.cityType = cityType;
		return this;
	}

	public String getCity() {

		return city;
	}

	public Address setCity(String city) {

		this.city = city;
		return this;
	}

	public String getStreetType() {

		return streetType;
	}

	public Address setStreetType(String streetType) {

		this.streetType = streetType;
		return this;
	}

	public String getStreet() {

		return street;
	}

	public Address setStreet(String street) {

		this.street = street;
		return this;
	}

	public String getHouse() {

		return house;
	}

	public Address setHouse(String house) {

		this.house = house;
		return this;
	}

	public String getPostIndex() {

		return postIndex;
	}

	public Address setPostIndex(String postIndex) {

		this.postIndex = postIndex;
		return this;
	}

}
