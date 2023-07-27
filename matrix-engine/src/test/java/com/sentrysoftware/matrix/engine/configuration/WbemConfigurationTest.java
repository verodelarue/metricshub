package com.sentrysoftware.matrix.engine.configuration;

import org.junit.jupiter.api.Test;

import static com.sentrysoftware.matrix.constants.Constants.PASSWORD;
import static com.sentrysoftware.matrix.constants.Constants.USERNAME;
import static com.sentrysoftware.matrix.constants.Constants.WBEM_CONFIGURATION_TO_STRING;
import static com.sentrysoftware.matrix.constants.Constants.WBEM_HTTPS;
import static com.sentrysoftware.matrix.constants.Constants.WBEM_NAMESPACE;
import static com.sentrysoftware.matrix.constants.Constants.WBEM_VCENTER;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test of {@link WbemConfiguration}
 */
public class WbemConfigurationTest {

	@Test
	public void testToString() {
		final WbemConfiguration wbemConfiguration = new WbemConfiguration();

		// When the userName is NOT null, it's appended to the result
		wbemConfiguration.setUsername(USERNAME);
		wbemConfiguration.setPassword(PASSWORD.toCharArray());
		wbemConfiguration.setNamespace(WBEM_NAMESPACE);
		wbemConfiguration.setVCenter(WBEM_VCENTER);
		assertEquals(WBEM_CONFIGURATION_TO_STRING, wbemConfiguration.toString());

		// When the userName is null, it's not appended to the result
		wbemConfiguration.setUsername(null);
		assertEquals(WBEM_HTTPS, wbemConfiguration.toString());
	}
}
