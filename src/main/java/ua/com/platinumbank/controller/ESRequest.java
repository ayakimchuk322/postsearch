package ua.com.platinumbank.controller;

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
import org.elasticsearch.index.query.BoolQueryBuilder;
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
                                                       .getResourceAsStream(
                                                           "properties.properties")) {

            properties.load(propertiesIn);

            inetAddress = properties.getProperty("inetaddress");
        } catch (IOException e) {
            // TODO replace with logging
            e.printStackTrace();
        }
    }

    // TODO add javadoc
    @RequestMapping(value = "/getmatch", method = RequestMethod.GET)
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

    /**
     * Convenient method to call same match query with POST request.
     *
     * @param request
     *            {@link HttpServletRequest}
     * @param address
     *            {@link Address}
     * @return // TODO add return text
     */
    @RequestMapping(value = "/postmatch", method = RequestMethod.POST)
    public @ResponseBody String postMatchSearchResultsFromES(HttpServletRequest request,
        Address address) {

        return getMatchSearchResultsFromES(request, address);
    }

    // TODO add javadoc
    @RequestMapping(value = "/getterm", method = RequestMethod.GET)
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

    /**
     * Convenient method to call same term query with POST request.
     *
     * @param request
     *            {@link HttpServletRequest}
     * @param address
     *            {@link Address}
     * @return // TODO add return text
     */
    @RequestMapping(value = "/postterm", method = RequestMethod.POST)
    public @ResponseBody String postTermSearchResultsFromES(HttpServletRequest request,
        Address address) {

        return getTermSearchResultsFromES(request, address);
    }

    // TODO add javadoc
    private static String queryMatch(String region, String district, String city, String postIndex,
        String street, String house) {

        SearchResponse searchResponse = null;

        TransportClient transportClient;
        SearchRequestBuilder searchRequestBuilder;
        BoolQueryBuilder boolQueryBuilder;

        try {
            transportClient = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(
                new InetSocketTransportAddress(InetAddress.getByName(inetAddress), 9300));

            searchRequestBuilder = transportClient.prepareSearch("logstash-post")
                                                  .setTypes("address");

            boolQueryBuilder = new BoolQueryBuilder();

            if (region != null && !region.isEmpty()) {
                QueryBuilder regionQb = QueryBuilders.matchQuery("region", region)
                                                     .boost(6.0f);

                boolQueryBuilder = boolQueryBuilder.must(regionQb);
            }

            if (district != null && !district.isEmpty()) {
                QueryBuilder districtQb = QueryBuilders.matchQuery("district", district)
                                                       .boost(5.0f);

                boolQueryBuilder = boolQueryBuilder.must(districtQb);
            }

            if (city != null && !city.isEmpty()) {
                QueryBuilder cityQb = QueryBuilders.matchQuery("city", city)
                                                   .boost(4.0f);

                boolQueryBuilder = boolQueryBuilder.must(cityQb);
            }

            if (postIndex != null && !postIndex.isEmpty()) {
                QueryBuilder postIndexQb = QueryBuilders.matchQuery("post_index", postIndex)
                                                        .boost(3.0f);

                boolQueryBuilder = boolQueryBuilder.must(postIndexQb);
            }

            if (street != null && !street.isEmpty()) {
                QueryBuilder streetQb = QueryBuilders.matchQuery("street", street)
                                                     .boost(2.0f);

                boolQueryBuilder = boolQueryBuilder.must(streetQb);
            }

            // For house field use wild card query instead of term because house
            // numbers stored all in one field separated by commas
            if (house != null && !house.isEmpty()) {
                QueryBuilder houseQb = QueryBuilders.wildcardQuery("house", "*" + house + "*");

                boolQueryBuilder = boolQueryBuilder.must(houseQb);
            }

            searchRequestBuilder = searchRequestBuilder.setQuery(boolQueryBuilder);

            searchResponse = searchRequestBuilder.execute()
                                                 .actionGet();

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

        SearchResponse searchResponse = null;

        TransportClient transportClient;
        SearchRequestBuilder searchRequestBuilder;
        BoolQueryBuilder boolQueryBuilder;

        try {
            transportClient = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(
                new InetSocketTransportAddress(InetAddress.getByName(inetAddress), 9300));

            searchRequestBuilder = transportClient.prepareSearch("logstash-post")
                                                  .setTypes("address");

            boolQueryBuilder = new BoolQueryBuilder();

            if (region != null && !region.isEmpty()) {
                QueryBuilder regionQb = QueryBuilders.termQuery("region.keyword", region)
                                                     .boost(6.0f);

                boolQueryBuilder = boolQueryBuilder.must(regionQb);
            }

            if (district != null && !district.isEmpty()) {
                QueryBuilder districtQb = QueryBuilders.termQuery("district.keyword", district)
                                                       .boost(5.0f);

                boolQueryBuilder = boolQueryBuilder.must(districtQb);
            }

            if (city != null && !city.isEmpty()) {
                QueryBuilder cityQb = QueryBuilders.termQuery("city.keyword", city)
                                                   .boost(4.0f);

                boolQueryBuilder = boolQueryBuilder.must(cityQb);
            }

            if (postIndex != null && !postIndex.isEmpty()) {
                QueryBuilder postIndexQb = QueryBuilders.termQuery("post_index.keyword", postIndex)
                                                        .boost(3.0f);

                boolQueryBuilder = boolQueryBuilder.must(postIndexQb);
            }

            if (street != null && !street.isEmpty()) {
                QueryBuilder streetQb = QueryBuilders.termQuery("street.keyword", street)
                                                     .boost(2.0f);

                boolQueryBuilder = boolQueryBuilder.must(streetQb);
            }

            // For house field use wild card query instead of term because house
            // numbers stored all in one field separated by commas
            if (house != null && !house.isEmpty()) {
                QueryBuilder houseQb = QueryBuilders.wildcardQuery("house", "*" + house + "*");

                boolQueryBuilder = boolQueryBuilder.must(houseQb);
            }

            searchRequestBuilder = searchRequestBuilder.setQuery(boolQueryBuilder);

            searchResponse = searchRequestBuilder.execute()
                                                 .actionGet();

        } catch (UnknownHostException e) {
            // TODO replace with logging
            e.printStackTrace();
        }

        // XXX for testing
        searchResponseToList(searchResponse);

        return searchResponseToString(searchResponse);
    }

}
