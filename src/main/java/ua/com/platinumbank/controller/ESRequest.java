package ua.com.platinumbank.controller;

// XXX for testing

import static ua.com.platinumbank.util.SearchUtil.searchResponseToList;
import static ua.com.platinumbank.util.SearchUtil.searchResponseToString;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ua.com.platinumbank.model.Address;

@RestController
@RequestMapping(value = "/es")
public class ESRequest {

	private static Properties properties;

	private static String inetAddress;

	// Load properties file with connection specific information
	static {
		properties = new Properties();

		try (InputStream propertiesIn = ESRequest.class.getClassLoader()
				.getResourceAsStream("properties.properties")) {

			properties.load(propertiesIn);

			inetAddress = properties.getProperty("inetaddress");
		} catch (IOException e) {
			// TODO replace with logging
			e.printStackTrace();
		}
	}

	// TODO add javadoc
	@RequestMapping(value = "/match", method = RequestMethod.GET)
	public @ResponseBody String getMatchSearchResultsFromES(HttpServletRequest request,
			Address address) {

		String region = request.getParameter("region");
		String district = request.getParameter("district");
		String city = request.getParameter("city");
		String postIndex = request.getParameter("postIndex");
		String street = request.getParameter("street");
		String house = request.getParameter("house");

		String response = queryMatch(region, district, city, postIndex, street, house);

		// Prepare response for html output
		response = "<pre>" + response.replaceAll("<", "&lt;") + "</pre>";

		return response;
	}

	// TODO add javadoc
	@RequestMapping(value = "/term", method = RequestMethod.GET)
	public @ResponseBody String getTermSearchResultsFromES(HttpServletRequest request,
			Address address) {

		String region = request.getParameter("region");
		String district = request.getParameter("district");
		String city = request.getParameter("city");
		String postIndex = request.getParameter("postIndex");
		String street = request.getParameter("street");
		String house = request.getParameter("house");

		String response = queryTerm(region, district, city, postIndex, street, house);

		// Prepare response for html output
		response = "<pre>" + response.replaceAll("<", "&lt;") + "</pre>";

		return response;
	}

	// TODO add javadoc
	private static String queryMatch(String region, String district, String city, String postIndex,
			String street, String house) {

		TransportClient transportClient;
		SearchRequestBuilder searchRequestBuilder;
		SearchResponse searchResponse = null;

		try {
			transportClient = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(
					new InetSocketTransportAddress(InetAddress.getByName(inetAddress), 9300));

			searchRequestBuilder = transportClient.prepareSearch("logstash-post")
					.setTypes("address");

			if (region != null && !region.isEmpty()) {
				QueryBuilder regionQb = QueryBuilders.matchQuery("region", region);
				searchRequestBuilder = searchRequestBuilder.setQuery(regionQb);
			}

			if (district != null && !district.isEmpty()) {
				QueryBuilder districtQb = QueryBuilders.matchQuery("district", district);
				searchRequestBuilder = searchRequestBuilder.setQuery(districtQb);
			}

			if (city != null && !city.isEmpty()) {
				QueryBuilder cityQb = QueryBuilders.matchQuery("city", city);
				searchRequestBuilder = searchRequestBuilder.setQuery(cityQb);
			}

			if (postIndex != null && !postIndex.isEmpty()) {
				QueryBuilder postIndexQb = QueryBuilders.matchQuery("post_index", postIndex);
				searchRequestBuilder = searchRequestBuilder.setQuery(postIndexQb);
			}

			if (street != null && !street.isEmpty()) {
				QueryBuilder streetQb = QueryBuilders.matchQuery("street", street);
				searchRequestBuilder = searchRequestBuilder.setQuery(streetQb);
			}

			if (house != null && !house.isEmpty()) {
				QueryBuilder houseQb = QueryBuilders.matchQuery("house", house);
				searchRequestBuilder = searchRequestBuilder.setQuery(houseQb);
			}

			searchResponse = searchRequestBuilder.execute().actionGet();

		} catch (UnknownHostException e) {
			// TODO replace with logging
			e.printStackTrace();
		}

		// XXX for testing
		searchResponseToList(searchResponse);

		return searchResponseToString(searchResponse);
	}

	// TODO add javadoc
	private static String queryTerm(String region, String district, String city, String postIndex,
			String street, String house) {

		TransportClient transportClient;
		SearchRequestBuilder searchRequestBuilder;
		SearchResponse searchResponse = null;

		try {
			transportClient = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(
					new InetSocketTransportAddress(InetAddress.getByName(inetAddress), 9300));

			searchRequestBuilder = transportClient.prepareSearch("logstash-post")
					.setTypes("address");

			if (region != null && !region.isEmpty()) {
				QueryBuilder regionQb = QueryBuilders.termQuery("region.keyword", region);
				searchRequestBuilder = searchRequestBuilder.setQuery(regionQb);
			}

			if (district != null && !district.isEmpty()) {
				QueryBuilder districtQb = QueryBuilders.termQuery("district.keyword", district);
				searchRequestBuilder = searchRequestBuilder.setQuery(districtQb);
			}

			if (city != null && !city.isEmpty()) {
				QueryBuilder cityQb = QueryBuilders.termQuery("city.keyword", city);
				searchRequestBuilder = searchRequestBuilder.setQuery(cityQb);
			}

			if (postIndex != null && !postIndex.isEmpty()) {
				QueryBuilder postIndexQb = QueryBuilders.termQuery("post_index.keyword", postIndex);
				searchRequestBuilder = searchRequestBuilder.setQuery(postIndexQb);
			}

			if (street != null && !street.isEmpty()) {
				QueryBuilder streetQb = QueryBuilders.termQuery("street.keyword", street);
				searchRequestBuilder = searchRequestBuilder.setQuery(streetQb);
			}

			if (house != null && !house.isEmpty()) {
				QueryBuilder houseQb = QueryBuilders.termQuery("house.keyword", house);
				searchRequestBuilder = searchRequestBuilder.setQuery(houseQb);
			}

			searchResponse = searchRequestBuilder.execute().actionGet();

		} catch (UnknownHostException e) {
			// TODO replace with logging
			e.printStackTrace();
		}

		// XXX for testing
		searchResponseToList(searchResponse);

		return searchResponseToString(searchResponse);
	}

}
