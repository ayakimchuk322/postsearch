package ua.com.platinumbank.util;

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

		// XXX for testing
		System.out.println(jsonAddress);

		return jsonAddress;
	}

}
