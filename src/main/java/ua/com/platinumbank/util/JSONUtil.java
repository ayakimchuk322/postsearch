package ua.com.platinumbank.util;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import ua.com.platinumbank.model.Address;

// TODO add javadoc
public class JSONUtil {

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

    // TODO add javadoc
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
