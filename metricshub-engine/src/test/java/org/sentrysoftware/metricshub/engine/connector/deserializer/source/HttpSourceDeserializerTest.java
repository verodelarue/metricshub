package org.sentrysoftware.metricshub.engine.connector.deserializer.source;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.sentrysoftware.metricshub.engine.connector.model.common.HttpMethod.GET;
import static org.sentrysoftware.metricshub.engine.connector.model.common.HttpMethod.POST;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.sentrysoftware.metricshub.engine.connector.deserializer.DeserializerTest;
import org.sentrysoftware.metricshub.engine.connector.model.Connector;
import org.sentrysoftware.metricshub.engine.connector.model.common.EntryConcatMethod;
import org.sentrysoftware.metricshub.engine.connector.model.common.ExecuteForEachEntryOf;
import org.sentrysoftware.metricshub.engine.connector.model.monitor.task.source.HttpSource;
import org.sentrysoftware.metricshub.engine.connector.model.monitor.task.source.Source;

class HttpSourceDeserializerTest extends DeserializerTest {

	@Override
	public String getResourcePath() {
		return "src/test/resources/test-files/source/http/";
	}

	@Test
	void testDeserializeHttpSource() throws IOException {
		final Connector connector = getConnector("http");

		final Map<String, Source> expected = new LinkedHashMap<>(
			Map.of(
				"testHttpSource",
				HttpSource
					.builder()
					.key("${source::pre.testHttpSource}")
					.type("http")
					.url("/testUrl/")
					.method(POST)
					.body("test\nbody")
					.build()
			)
		);

		assertEquals(expected, connector.getPre());
	}

	@Test
	void testDeserializeHttpSourceWithExecuteForEachEntryOf() throws IOException {
		final Connector connector = getConnector("httpExecuteForEachEntryOf");

		final Map<String, Source> expected = new LinkedHashMap<>(
			Map.of(
				"devices",
				HttpSource.builder().key("${source::pre.devices}").type("http").url("/devices").method(GET).build(),
				"detailsOfEachDevice",
				HttpSource
					.builder()
					.key("${source::pre.detailsOfEachDevice}")
					.type("http")
					.url("/device-detail/$entry.column(1)$")
					.method(GET)
					.executeForEachEntryOf(new ExecuteForEachEntryOf("${source::pre.devices}", EntryConcatMethod.LIST))
					.build()
			)
		);

		assertEquals(expected, connector.getPre());
	}
}
