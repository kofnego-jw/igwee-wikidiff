package at.ac.uibk.igwee.lucene.json;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * ObjectMapper using Jackson library.
 * @author Joseph
 *
 */
public class JsonObjectMapper {
	
	/**
	 * ObjectMapper, should be thread safe.
	 */
	private static final ObjectMapper OBJECT_MAPPER;
	static {
		OBJECT_MAPPER = new ObjectMapper();
		OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
	
	private static final Logger LOGGER = LoggerFactory.getLogger(JsonObjectMapper.class);
	
	/**
	 * 
	 * @param jsonString
	 * @return a JsonSearchRequest object or null, if any exception happened.
	 */
	public static JsonSearchRequest jsonToSearchRequest(String jsonString) {
		try {
			return OBJECT_MAPPER.readValue(jsonString, JsonSearchRequest.class);
		} catch (Exception e) {
			LOGGER.error("Cannot deserialize String to JsonSearchRequest: " + jsonString, e);
			return null;
		}
	}
	
	/**
	 * 
	 * @param sr
	 * @return a String (no escaping special characters), JSON representation of the JsonSearchRequest
	 */
	public static String searchRequestToJson(JsonSearchRequest sr) {
		try {
			return OBJECT_MAPPER.writeValueAsString(sr);
		} catch (Exception e) {
			LOGGER.error("Cannot serialize JsonSearchRequest to String.", e);
			return null;
		}
	}

}
