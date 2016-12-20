package ua.com.platinumbank;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

public class TestQueryAll {

	private static String queryAll(String region, String district, String cityType, String city,
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

}
