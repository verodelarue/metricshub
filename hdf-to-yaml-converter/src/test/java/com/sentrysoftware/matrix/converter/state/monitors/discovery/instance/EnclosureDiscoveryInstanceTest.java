package com.sentrysoftware.matrix.converter.state.monitors.discovery.instance;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.sentrysoftware.matrix.converter.AbstractConnectorPropertyConverterTest;

public class EnclosureDiscoveryInstanceTest extends AbstractConnectorPropertyConverterTest {

	@Override
	protected String getResourcePath() {
		return "src/test/resources/test-files/monitors/discovery/instance/enclosure";
	}

	@Test
	void test() throws IOException {
		testAll();
	}
}
