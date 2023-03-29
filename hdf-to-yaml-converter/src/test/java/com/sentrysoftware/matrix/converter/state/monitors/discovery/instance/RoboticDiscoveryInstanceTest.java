package com.sentrysoftware.matrix.converter.state.monitors.discovery.instance;

import java.io.IOException;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.sentrysoftware.matrix.converter.AbstractConnectorPropertyConverterTest;

class RoboticDiscoveryInstanceTest extends AbstractConnectorPropertyConverterTest {

	@Override
	protected String getResourcePath() {
		return "src/test/resources/test-files/monitors/discovery/instance/robotic";
	}

	@Test
	@Disabled("Until RoboticInstance processor is up")
	void test() throws IOException {
		testAll();
	}
}
