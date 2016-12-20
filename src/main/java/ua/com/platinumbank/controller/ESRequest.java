package ua.com.platinumbank.controller;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.servlet.http.HttpServletRequest;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
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

	private static String queryMatch(String region, String district, String cityType, String city,
			String streetType, String street, String house, String postIndex) {

		StringBuilder result = new StringBuilder();

		final TransportClient client;

		try {
			client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(
					new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));

			SearchResponse searchResponse = client.prepareSearch("post").setTypes("address")
					.execute().actionGet();

			SearchHit[] hits = searchResponse.getHits().getHits();

			for (int i = 0; i < hits.length; i++) {
				result.append(hits[i].getId());
				result.append("\n");
				result.append(hits[i].getSourceAsString());
				result.append("\n");
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		return result.toString();
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

		// address.setRegion(region);
		// address.setDistrict(district);
		// address.setCityType(cityType);
		// address.setCity(city);
		// address.setStreetType(streetType);
		// address.setStreet(street);
		// address.setHouse(house);
		// address.setPostIndex(postIndex);

		return queryMatch(region, district, cityType, city, streetType, street, house, postIndex);
	}
}
