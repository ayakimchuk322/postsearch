package ua.com.platinumbank.util;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import ua.com.platinumbank.model.Address;

/**
 * Utility class to deal with {@code JSON} and searches.
 */
public class JSONUtil {

    /**
     * Transforms single {@link Address} into {@code JSON} String.
     * 
     * @param address
     *            {@link Address} instance
     * @return {@code JSON} {@link String} representing single {@link Address} instance.
     */
    public static String addressToJSONString(Address address) {

        String jsonAddress = null;

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();

        try {
            jsonAddress = objectWriter.writeValueAsString(address);
        } catch (JsonProcessingException e) {
            // TODO replace with logging
            e.printStackTrace();
        }

        return jsonAddress;
    }

    /**
     * Transforms {@link List} with {@link Address} objects into {@code JSON} String.
     * 
     * @param addresses
     *            {@link List} with {@link Address} objects
     * @return {@code JSON} {@link String} representing these {@link Address} objects.
     */
    public static String addressListToJSONString(List<Address> addresses) {

        StringBuilder jsonAddresses = new StringBuilder();

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();

        try {
            jsonAddresses.append("{")
                         .append(System.lineSeparator())
                         .append("\"addresses\" : ")
                         .append(System.lineSeparator())
                         .append(objectWriter.writeValueAsString(addresses))
                         .append(System.lineSeparator())
                         .append("}");
        } catch (JsonProcessingException e) {
            // TODO replace with logging
            e.printStackTrace();
        }

        return jsonAddresses.toString();
    }

    /**
     * Parses {@code JSON} string from caller into single {@link Address} object.
     * 
     * @param jsonString
     *            {@code JSON} request {@link String} from caller
     * @return {@link Address} object representing this {@code JSON} request.
     */
    public static Address parseJsonString(String jsonString) {

        Address addressRequest = null;

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            addressRequest = objectMapper.readValue(jsonString, Address.class);
        } catch (IOException e) {
            // TODO replace with logging
            e.printStackTrace();
        }

        return addressRequest;
    }

}
