package ua.com.platinumbank.util;

import static ua.com.platinumbank.model.Address.getEmptyAddress;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import ua.com.platinumbank.model.Address;

/**
 * Utility class to deal with {@code JSON} and searches.
 */
public class JSONUtil {

    // Logger for JSONUtil class
    private static final Logger logger = LogManager.getLogger(JSONUtil.class);

    private JSONUtil() {
        // There is no point to instantiate utility class with static methods
    }

    /**
     * Transforms single {@link Address} into {@code JSON} String.
     *
     * @param address
     *            {@link Address} instance
     * @return {@code JSON} {@link String} representing single {@link Address} instance.
     */
    public static String addressToJSONString(Address address) {

        StringBuilder jsonAddress = new StringBuilder();

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();

        try {
            jsonAddress.append("{")
                       .append(System.lineSeparator())
                       .append("\"addresses\" : ")
                       .append(System.lineSeparator())
                       .append(objectWriter.writeValueAsString(address))
                       .append(System.lineSeparator())
                       .append("}");
        } catch (JsonProcessingException e) {
            if (logger.isErrorEnabled()) {
                logger.error(
                    "Error occurred during serializing address - \"{}\" - into string - {}",
                    address.toString(), e.getMessage());
            }

            // If current address can not be serialized, simply return string from empty address
            return getEmptyAddress().toString();
        }

        return jsonAddress.toString();
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
            if (logger.isErrorEnabled()) {
                logger.error("Error occurred during serializing addresses list into string - {}",
                    e.getMessage());
            }

            // If current list can not be serialized, simply return string from empty address
            return getEmptyAddress().toString();
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
            if (logger.isErrorEnabled()) {
                logger.error("Error occurred during reading JSON request - \"{}\" - {}", jsonString,
                    e.getMessage());
            }

            // If JSON request can not be read, return empty address object
            // In this case, search should not be done
            return getEmptyAddress();
        }

        return addressRequest;
    }

}
