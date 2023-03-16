package com.sentrysoftware.matrix.converter.state.detection.wmi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sentrysoftware.matrix.common.helpers.JsonHelper;
import com.sentrysoftware.matrix.converter.ConnectorConverter;
import com.sentrysoftware.matrix.converter.PreConnector;

class ConnectorWmiPropertyTest {

	@Test
	@Disabled("Until Wmi Converter is up!")
	void getAvailableTest() throws IOException {
		String input = """
				// WMI criterion comment
				Detection.Criteria(1).Type="WMI"
				Detection.Criteria(1).WbemQuery="query"
				Detection.Criteria(1).WbemNamespace="namespace"
				Detection.Criteria(1).ExpectedResult="result"
				Detection.Criteria(1).ErrorMessage="error"
			""";

		PreConnector preConnector = new PreConnector();
		preConnector.load(new ByteArrayInputStream(input.getBytes()));
		ConnectorConverter connectorConverter = new ConnectorConverter(preConnector);
		JsonNode connector = connectorConverter.convert();
		String yaml = """
				connector:
				  detection:
				    criteria:
				    - _comment: "WMI criterion comment"
				      type: wmi
				      query: query
				      namespace: namespace
				      expectedResult: result
				      errorMessage: error
				""";
		ObjectMapper mapper = JsonHelper.buildYamlMapper();
		JsonNode expected = mapper.readTree(yaml);
		assertEquals(expected, connector);
	}

	@Test
	@Disabled("Until Wmi Converter is up!")
	void testMany() throws IOException {
		String input = """
				// First WMI criterion comment
				Detection.Criteria(1).Type="WMI"
				Detection.Criteria(1).WbemQuery="query1"
				Detection.Criteria(1).WbemNamespace="namespace1"
				Detection.Criteria(1).ExpectedResult="result1"
				Detection.Criteria(1).ErrorMessage="error1"

				// Second WMI criterion comment
				Detection.Criteria(2).Type="WMI"
				Detection.Criteria(2).WbemQuery="query2"
				Detection.Criteria(2).WbemNamespace="namespace2"
				Detection.Criteria(2).ExpectedResult="result2"
				Detection.Criteria(2).ErrorMessage="error2"
				""";

		PreConnector preConnector = new PreConnector();
		preConnector.load(new ByteArrayInputStream(input.getBytes()));
		ConnectorConverter connectorConverter = new ConnectorConverter(preConnector);
		JsonNode connector = connectorConverter.convert();
		String yaml = """
				connector:
				  detection:
				    criteria:
				    - _comment: "First WMI criterion comment"
				      type: wmi
				      query: query1
				      namespace: namespace1
				      expectedResult: result1
				      errorMessage: error1
				    - _comment: "Second WMI criterion comment"
				      type: wmi
				      query: query2
				      namespace: namespace2
				      expectedResult: result2
				      errorMessage: error2
				""";
		ObjectMapper mapper = JsonHelper.buildYamlMapper();
		JsonNode expected = mapper.readTree(yaml);
		assertEquals(expected, connector);
	}
}
