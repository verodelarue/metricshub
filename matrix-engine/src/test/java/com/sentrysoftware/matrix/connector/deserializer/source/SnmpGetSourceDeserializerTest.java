package com.sentrysoftware.matrix.connector.deserializer.source;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.sentrysoftware.matrix.connector.deserializer.DeserializerTest;
import com.sentrysoftware.matrix.connector.model.Connector;
import com.sentrysoftware.matrix.connector.model.monitor.task.source.SnmpGetSource;
import com.sentrysoftware.matrix.connector.model.monitor.task.source.Source;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class SnmpGetSourceDeserializerTest extends DeserializerTest {

	@Override
	public String getResourcePath() {
		return "src/test/resources/test-files/source/snmpGet/";
	}

	@Test
	void testDeserializeTableJoin() throws IOException {
		final String testResource = "snmpGet";

		final Connector connector = getConnector(testResource);
		Map<String, Source> expected = new LinkedHashMap<>();
		expected.put(
			"testSnmpGetSource",
			SnmpGetSource
				.builder()
				.key("${source::pre.testSnmpGetSource}")
				.type("snmpGet")
				.oid("testOidString")
				.forceSerialization(true)
				.computes(Collections.emptyList())
				.build()
		);

		assertEquals(expected, connector.getPre());
	}
}
