package ua.com.platinumbank.util;

import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import ua.com.platinumbank.model.Address;

// TODO add javadoc
public class JSONUtil {

	public static void addressToJSON(Address address) {

		ObjectMapper objectMapper = new ObjectMapper();

		ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();

		try {
			// TODO change write logic
			objectWriter.writeValue(new File("c:\\temp\\file.json"), address);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
