package ua.com.platinumbank.util;

import java.io.File;
import java.io.IOException;

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

	public static void addressToJSONFile(Address address) {

		ObjectMapper objectMapper = new ObjectMapper();
		ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();

		try {
			// TODO change write logic
			objectWriter.writeValue(new File("c:\\temp\\file.json"), address);
		} catch (IOException e) {
			// TODO replace with logging
			e.printStackTrace();
		}
	}

}
