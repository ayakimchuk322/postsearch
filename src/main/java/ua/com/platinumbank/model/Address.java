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

		this.region = region != null ? region : "";
		return this;
	}

	public String getDistrict() {

		return district;
	}

	public Address setDistrict(String district) {

		this.district = district != null ? district : "";
		return this;
	}

	public String getCityType() {

		return cityType;
	}

	public Address setCityType(String cityType) {

		this.cityType = cityType != null ? cityType : "";
		return this;
	}

	public String getCity() {

		return city;
	}

	public Address setCity(String city) {

		this.city = city != null ? city : "";
		return this;
	}

	public String getStreetType() {

		return streetType;
	}

	public Address setStreetType(String streetType) {

		this.streetType = streetType != null ? streetType : "";
		return this;
	}

	public String getStreet() {

		return street;
	}

	public Address setStreet(String street) {

		this.street = street != null ? street : "";
		return this;
	}

	public String getHouse() {

		return house;
	}

	public Address setHouse(String house) {

		this.house = house != null ? house : "";
		return this;
	}

	public String getPostIndex() {

		return postIndex;
	}

	public Address setPostIndex(String postIndex) {

		this.postIndex = postIndex != null ? postIndex : "";
		return this;
	}

	@Override
	public String toString() {

		// @formatter:off
        return "Address{" +
            "region='" + region + '\'' +
            ", district='" + district + '\'' +
            ", cityType='" + cityType + '\'' +
            ", city='" + city + '\'' +
            ", streetType='" + streetType + '\'' +
            ", street='" + street + '\'' +
            ", house='" + house + '\'' +
            ", postIndex='" + postIndex + '\'' +
            '}';
        // @formatter:on
	}
}
