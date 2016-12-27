package ua.com.platinumbank.util;

import static ua.com.platinumbank.model.Address.getEmptyAddress;
import static ua.com.platinumbank.util.JSONUtil.addressListToJSONString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;

import ua.com.platinumbank.model.Address;

/**
 * Utility class to deal with Elasticsearch search results.
 */
public class SearchUtil {

    private SearchUtil() {
        // There is no point to instantiate utility class with static methods
    }

    /**
     * Parses {@link SearchResponse} and returns {@code JSON} {@link String} with all concatenated
     * corresponding {@link Address} objects.
     *
     * @param searchResponse
     *            {@link SearchResponse} from Elasticsearch
     * @return {@code JSON} {@link String} with parsed {@link Address} objects.
     */
    public static String searchResponseToString(SearchResponse searchResponse) {

        String resultString;

        List<Address> addresses = searchResponseToList(searchResponse);

        resultString = addressListToJSONString(addresses);

        return resultString;
    }

    /**
     * Parses {@link SearchResponse} and returns {@link List} filled with corresponding
     * {@link Address} objects.
     *
     * @param searchResponse
     *            {@link SearchResponse} from Elasticsearch
     * @return {@link List} with {@link Address} objects.
     */
    public static List<Address> searchResponseToList(SearchResponse searchResponse) {

        List<Address> resultList;

        SearchHit[] hits = searchResponse.getHits()
                                         .getHits();

        // Check, if no results from Elasticsearch simply return empty address
        if (hits.length == 0 || hits == null) {
            resultList = new ArrayList<>(1);

            resultList.add(0, getEmptyAddress());
        } else {
            resultList = new ArrayList<>(hits.length);

            for (int i = 0; i < hits.length; i++) {

                Map resultSourceMap = hits[i].sourceAsMap();

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
        }

        return resultList;
    }

}
