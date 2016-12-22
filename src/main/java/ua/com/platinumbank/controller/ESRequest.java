package ua.com.platinumbank.controller;

// XXX for testing

import static ua.com.platinumbank.util.JSONUtil.addressToJSONString;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
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

			if (!region.isEmpty() && region != null) {
				QueryBuilder regionQb = QueryBuilders.matchQuery("region", region);
				searchRequestBuilder = searchRequestBuilder.setQuery(regionQb);
			}

			if (!district.isEmpty() && district != null) {
				QueryBuilder districtQb = QueryBuilders.matchQuery("district", district);
				searchRequestBuilder = searchRequestBuilder.setQuery(districtQb);
			}

			if (!city.isEmpty() && city != null) {
				QueryBuilder cityQb = QueryBuilders.matchQuery("city", city);
				searchRequestBuilder = searchRequestBuilder.setQuery(cityQb);
			}

			if (!postIndex.isEmpty() && postIndex != null) {
				QueryBuilder postIndexQb = QueryBuilders.matchQuery("post_index", postIndex);
				searchRequestBuilder = searchRequestBuilder.setQuery(postIndexQb);
			}

			if (!street.isEmpty() && street != null) {
				QueryBuilder streetQb = QueryBuilders.matchQuery("street", street);
				searchRequestBuilder = searchRequestBuilder.setQuery(streetQb);
			}

			if (!house.isEmpty() && house != null) {
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

			if (!region.isEmpty() && region != null) {
				QueryBuilder regionQb = QueryBuilders.termQuery("region.keyword", region);
				searchRequestBuilder = searchRequestBuilder.setQuery(regionQb);
			}

			if (!district.isEmpty() && district != null) {
				QueryBuilder districtQb = QueryBuilders.termQuery("district.keyword", district);
				searchRequestBuilder = searchRequestBuilder.setQuery(districtQb);
			}

			if (!city.isEmpty() && city != null) {
				QueryBuilder cityQb = QueryBuilders.termQuery("city.keyword", city);
				searchRequestBuilder = searchRequestBuilder.setQuery(cityQb);
			}

			if (!postIndex.isEmpty() && postIndex != null) {
				QueryBuilder postIndexQb = QueryBuilders.termQuery("post_index.keyword", postIndex);
				searchRequestBuilder = searchRequestBuilder.setQuery(postIndexQb);
			}

			if (!street.isEmpty() && street != null) {
				QueryBuilder streetQb = QueryBuilders.termQuery("street.keyword", street);
				searchRequestBuilder = searchRequestBuilder.setQuery(streetQb);
			}

			if (!house.isEmpty() && house != null) {
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

	/**
	 * Prints all search results as is.
	 * 
	 * @param searchResponse
	 *            {@link SearchResponse} from ElasticSearch
	 * @return {@link String} representing this search response
	 */
	private static String searchResponseToString(SearchResponse searchResponse) {

		StringBuilder result = new StringBuilder();

		SearchHit[] hits = searchResponse.getHits().getHits();

		for (int i = 0; i < hits.length; i++) {
			result.append(hits[i].getId());
			result.append("\n");
			result.append(hits[i].getSourceAsString());
			result.append("\n");
		}

		return result.toString();
	}

	/**
	 * Parses {@link SearchResponse} and returns {@link List} filled with
	 * corresponding {@link Address} objects.
	 * 
	 * @param searchResponse
	 *            {@link SearchResponse} from ElasticSearch
	 * @return {@link List} with {@link Address} objects
	 */
	private static List<Address> searchResponseToList(SearchResponse searchResponse) {

		List<Address> resultList;
		Map resultSourceMap;

		SearchHit[] hits = searchResponse.getHits().getHits();

		resultList = new ArrayList(hits.length);

		for (int i = 0; i < hits.length; i++) {

			resultSourceMap = hits[i].sourceAsMap();

			Address address = new Address();

			String region = (String) resultSourceMap.get("region");
			String district = (String) resultSourceMap.get("district");
			String city = (String) resultSourceMap.get("city");
			String postIndex = (String) resultSourceMap.get("post_index");
			String street = (String) resultSourceMap.get("street");
			String house = (String) resultSourceMap.get("house");

			address.setRegion(region);
			address.setDistrict(district);
			address.setCity(city);
			address.setPostIndex(postIndex);
			address.setStreet(street);
			address.setHouse(house);

			resultList.add(i, address);
		}

		// XXX for testing
		for (Address a : resultList) {
			addressToJSONString(a);
		}

		return resultList;
	}

}
