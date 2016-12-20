package ua.com.platinumbank.controller;

import static ua.com.platinumbank.util.JSONUtil.addressToJSON;

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

	@RequestMapping(value = "/req", method = RequestMethod.GET)
	public @ResponseBody String getSearchResultsFromES(HttpServletRequest request,
			Address address) {

		String region = request.getParameter("region");
		String district = request.getParameter("district");
		String cityType = request.getParameter("cityType");
		String city = request.getParameter("city");
		String streetType = request.getParameter("streetType");
		String street = request.getParameter("street");
		String house = request.getParameter("house");
		String postIndex = request.getParameter("postIndex");

		// Prepare response for html output
		String response = queryMatch(region, district, cityType, city, streetType, street, house,
				postIndex);
		response = "<pre>" + response.replaceAll("<", "&lt;") + "</pre>";

		return response;
	}

	private static String queryMatch(String region, String district, String cityType, String city,
			String streetType, String street, String house, String postIndex) {

		TransportClient transportClient;
		SearchRequestBuilder searchRequestBuilder;
		SearchResponse searchResponse = null;

		try {
			transportClient = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(
					new InetSocketTransportAddress(InetAddress.getByName(inetAddress), 9300));

			searchRequestBuilder = transportClient.prepareSearch("post").setTypes("address");

			if (region != null) {
				QueryBuilder regionQb = QueryBuilders.matchQuery("region", region);
				searchRequestBuilder.setQuery(regionQb);
			}

			if (district != null) {
				QueryBuilder districtQb = QueryBuilders.matchQuery("district", district);
				searchRequestBuilder.setQuery(districtQb);
			}

			if (cityType != null) {
				QueryBuilder cityTypeQb = QueryBuilders.matchQuery("city_type", cityType);
				searchRequestBuilder.setQuery(cityTypeQb);
			}

			if (city != null) {
				QueryBuilder cityQb = QueryBuilders.matchQuery("city", city);
				searchRequestBuilder.setQuery(cityQb);
			}

			if (streetType != null) {
				QueryBuilder streetTypeQb = QueryBuilders.matchQuery("street_type", streetType);
				searchRequestBuilder.setQuery(streetTypeQb);
			}

			if (street != null) {
				QueryBuilder streetQb = QueryBuilders.matchQuery("street", street);
				searchRequestBuilder.setQuery(streetQb);
			}

			if (house != null) {
				QueryBuilder houseQb = QueryBuilders.matchQuery("house", house);
				searchRequestBuilder.setQuery(houseQb);
			}

			if (postIndex != null) {
				QueryBuilder postIndexQb = QueryBuilders.matchQuery("post_index", postIndex);
				searchRequestBuilder.setQuery(postIndexQb);
			}

			searchResponse = searchRequestBuilder.execute().actionGet();

		} catch (UnknownHostException e) {
			// TODO replace with logging
			e.printStackTrace();
		}

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
			String cityType = (String) resultSourceMap.get("cityType");
			String city = (String) resultSourceMap.get("city");
			String streetType = (String) resultSourceMap.get("street_type");
			String street = (String) resultSourceMap.get("street");
			String house = (String) resultSourceMap.get("house");
			String postIndex = (String) resultSourceMap.get("post_index");

			address.setRegion(region);
			address.setDistrict(district);
			address.setCityType(cityType);
			address.setCity(city);
			address.setStreetType(streetType);
			address.setStreet(street);
			address.setHouse(house);
			address.setPostIndex(postIndex);

			resultList.add(i, address);
		}

		for (Address a : resultList) {
			addressToJSON(a);
		}

		return resultList;
	}

}
