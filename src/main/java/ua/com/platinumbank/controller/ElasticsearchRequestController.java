package ua.com.platinumbank.controller;

import static ua.com.platinumbank.model.Address.getEmptyAddress;
import static ua.com.platinumbank.util.JSONUtil.addressToJSONString;
import static ua.com.platinumbank.util.JSONUtil.parseJsonString;
import static ua.com.platinumbank.util.SearchUtil.searchResponseToString;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ua.com.platinumbank.model.Address;

/**
 * This class controls searching addresses in Elasticsearch.
 */
@RestController
@RequestMapping(value = "/es")
public class ElasticsearchRequestController {

    // Logger for ElasticsearchRequestController class
    private static final Logger logger = LogManager.getLogger(ElasticsearchRequestController.class);

    private static final Properties PROPERTIES;

    // Elasticsearch host ip
    private static String INETADDRESS;
    // Elasticsearch host port
    private static int PORT;

    // Load properties file with connection specific information
    static {
        PROPERTIES = new Properties();

        try (InputStream propertiesIn = ElasticsearchRequestController.class.getClassLoader()
                                                                            .getResourceAsStream(
                                                                                "properties.properties")) {

            PROPERTIES.load(propertiesIn);

            INETADDRESS = PROPERTIES.getProperty("inetaddress");
            PORT = Integer.valueOf(PROPERTIES.getProperty("port"));
        } catch (IOException e) {
            if (logger.isErrorEnabled()) {
                logger.error("Error occurred during properties static initializing - {}",
                    e.getMessage());
            }
        }

    }

    /**
     * Calls match query with parameters obtained from {@code GET} request.
     *
     * @param request
     *            {@link HttpServletRequest} from caller
     * @return {@code JSON} {@link String} with searched {@link Address} objects. In case of any
     *         {@code Exception} returns {@code JSON} {@code String} with empty {@link Address}.
     */
    @RequestMapping(value = "/getmatch", method = RequestMethod.GET)
    public @ResponseBody String getMatchSearchResultsFromES(HttpServletRequest request) {

        String response;

        try {
            String region = request.getParameter("region");
            String district = request.getParameter("district");
            String city = request.getParameter("city");
            String postIndex = request.getParameter("postIndex");
            String street = request.getParameter("street");
            String house = request.getParameter("house");

            response = queryMatch(region, district, city, postIndex, street, house);
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("Error occurred during handling search request - {}", e.getMessage());
            }

            // In case of any exception return to caller empty json address
            response = addressToJSONString(getEmptyAddress());
        }

        return response;
    }

    /**
     * Convenient method to call match query with {@code POST} request.
     *
     * @param request
     *            {@link HttpServletRequest} from caller
     * @return {@code JSON} {@link String} with searched {@link Address} objects. In case of any
     *         {@code Exception} returns {@code JSON} {@code String} with empty {@link Address}.
     */
    @RequestMapping(value = "/postmatch", method = RequestMethod.POST)
    public @ResponseBody String postMatchSearchResultsFromES(HttpServletRequest request) {

        return getMatchSearchResultsFromES(request);
    }

    /**
     * Calls term query with parameters obtained from {@code GET} request.
     *
     * @param request
     *            {@link HttpServletRequest} from caller
     * @return {@code JSON} {@link String} with searched {@link Address} objects. In case of any
     *         {@code Exception} returns {@code JSON} {@code String} with empty {@link Address}.
     */
    @RequestMapping(value = "/getterm", method = RequestMethod.GET)
    public @ResponseBody String getTermSearchResultsFromES(HttpServletRequest request) {

        String response;

        try {
            String region = request.getParameter("region");
            String district = request.getParameter("district");
            String city = request.getParameter("city");
            String postIndex = request.getParameter("postIndex");
            String street = request.getParameter("street");
            String house = request.getParameter("house");

            response = queryTerm(region, district, city, postIndex, street, house);
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("Error occurred during handling search request - {}", e.getMessage());
            }

            // In case of any exception return to caller empty json address
            response = addressToJSONString(getEmptyAddress());
        }

        return response;
    }

    /**
     * Convenient method to call term query with {@code POST} request.
     *
     * @param request
     *            {@link HttpServletRequest} from caller
     * @return {@code JSON} {@link String} with searched {@link Address} objects. In case of any
     *         {@code Exception} returns {@code JSON} {@code String} with empty {@link Address}.
     */
    @RequestMapping(value = "/postterm", method = RequestMethod.POST)
    public @ResponseBody String postTermSearchResultsFromES(HttpServletRequest request) {

        return getTermSearchResultsFromES(request);
    }

    /**
     * Calls match query with {@code JSON} request obtained from {@code POST} request.
     *
     * @param jsonRequest
     *            {@code JSON} {@link String} with request from caller
     * @return {@code JSON} {@link String} with searched {@link Address} objects. In case of any
     *         {@code Exception} returns {@code JSON} {@code String} with empty {@link Address}.
     */
    @RequestMapping(value = "/jsonpostmatch", method = RequestMethod.POST, headers = "Content-Type=application/json")
    public @ResponseBody String jsonPostMatchSearchResultsFromES(@RequestBody String jsonRequest) {

        String response;

        try {
            Address addressRequest = parseJsonString(jsonRequest);

            String region = addressRequest.getRegion();
            String district = addressRequest.getDistrict();
            String city = addressRequest.getCity();
            String postIndex = addressRequest.getPostIndex();
            String street = addressRequest.getStreet();
            String house = addressRequest.getHouseRequest();

            response = queryMatch(region, district, city, postIndex, street, house);
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("Error occurred during handling JSON search request - {}",
                    e.getMessage());
            }

            // In case of any exception return to caller empty json address
            response = addressToJSONString(getEmptyAddress());
        }

        return response;
    }

    /**
     * Calls term query with {@code JSON} request obtained from {@code POST} request.
     *
     * @param jsonRequest
     *            {@code JSON} {@link String} with request from caller
     * @return {@code JSON} {@link String} with searched {@link Address} objects. In case of any
     *         {@code Exception} returns {@code JSON} {@code String} with empty {@link Address}.
     */
    @RequestMapping(value = "/jsonpostterm", method = RequestMethod.POST, headers = "Content-Type=application/json")
    public @ResponseBody String jsonPostTermSearchResultsFromES(@RequestBody String jsonRequest) {

        String response;

        try {
            Address addressRequest = parseJsonString(jsonRequest);

            String region = addressRequest.getRegion();
            String district = addressRequest.getDistrict();
            String city = addressRequest.getCity();
            String postIndex = addressRequest.getPostIndex();
            String street = addressRequest.getStreet();
            String house = addressRequest.getHouseRequest();

            response = queryTerm(region, district, city, postIndex, street, house);
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("Error occurred during handling JSON search request - {}",
                    e.getMessage());
            }

            // In case of any exception return to caller empty json address
            response = addressToJSONString(getEmptyAddress());
        }

        return response;
    }

    /**
     * Executes match query with specified parameters. Each parameter gets score boost depending on
     * it's scale in address. If any parameter is empty or {@code null} it won't be queried by.
     *
     * @param region
     * @param district
     * @param city
     * @param postIndex
     * @param street
     * @param house
     * @return {@code JSON} {@link String} with searched {@link Address} objects.
     */
    private static String queryMatch(String region, String district, String city, String postIndex,
        String street, String house) {

        SearchResponse searchResponse = null;

        TransportClient transportClient;
        SearchRequestBuilder searchRequestBuilder;
        BoolQueryBuilder boolQueryBuilder;

        try {
            transportClient = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(
                new InetSocketTransportAddress(InetAddress.getByName(INETADDRESS), PORT));

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

            // For house field use wild card query instead of term because house numbers stored
            // all in one field separated by commas
            if (house != null && !house.isEmpty()) {
                QueryBuilder houseQb = QueryBuilders.wildcardQuery("house", "*" + house + "*");

                boolQueryBuilder = boolQueryBuilder.must(houseQb);
            }

            // If there are no search parameters - (empty) request, - there is no need to call
            // Elasticsearch
            if (boolQueryBuilder.hasClauses()) {
                searchRequestBuilder = searchRequestBuilder.setQuery(boolQueryBuilder);

                searchResponse = searchRequestBuilder.execute()
                                                     .actionGet();
            }

        } catch (UnknownHostException e) {
            if (logger.isErrorEnabled()) {
                logger.error("Error occurred during communicating with Elasticsearch server - {}",
                    e.getMessage());
            }
        }

        return searchResponseToString(searchResponse);
    }

    /**
     * Executes term query with specified parameters. Each parameter gets score boost depending on
     * it's scale in address. If any parameter is empty or {@code null} it won't be queried by.
     *
     * @param region
     * @param district
     * @param city
     * @param postIndex
     * @param street
     * @param house
     * @return {@code JSON} {@link String} with searched {@link Address} objects.
     */
    private static String queryTerm(String region, String district, String city, String postIndex,
        String street, String house) {

        SearchResponse searchResponse = null;

        TransportClient transportClient;
        SearchRequestBuilder searchRequestBuilder;
        BoolQueryBuilder boolQueryBuilder;

        try {
            transportClient = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(
                new InetSocketTransportAddress(InetAddress.getByName(INETADDRESS), PORT));

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

            // For house field use wild card query instead of term because house numbers stored
            // all in one field separated by commas
            if (house != null && !house.isEmpty()) {
                QueryBuilder houseQb = QueryBuilders.wildcardQuery("house", "*" + house + "*");

                boolQueryBuilder = boolQueryBuilder.must(houseQb);
            }

            // If there are no search parameters - (empty) request, - there is no need to call
            // Elasticsearch
            if (boolQueryBuilder.hasClauses()) {
                searchRequestBuilder = searchRequestBuilder.setQuery(boolQueryBuilder);

                searchResponse = searchRequestBuilder.execute()
                                                     .actionGet();
            }

        } catch (UnknownHostException e) {
            if (logger.isErrorEnabled()) {
                logger.error("Error occurred during communicating with Elasticsearch server - {}",
                    e.getMessage());
            }
        }

        return searchResponseToString(searchResponse);
    }

}
