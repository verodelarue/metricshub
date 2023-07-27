package com.sentrysoftware.matrix.engine.configuration;

import org.junit.Test;

import static com.sentrysoftware.matrix.constants.Constants.BMC_KEY;
import static com.sentrysoftware.matrix.constants.Constants.IMPI_CONFIGURATION_TO_STRING;
import static com.sentrysoftware.matrix.constants.Constants.IPMI;
import static com.sentrysoftware.matrix.constants.Constants.PASSWORD;
import static com.sentrysoftware.matrix.constants.Constants.USERNAME;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test of {@link IpmiConfiguration}
 */
public class IpmiConfigurationTest {
	@Test
	public void testToString() {
		final IpmiConfiguration ipmiConfiguration = new IpmiConfiguration();

		// When the userName is NOT null, it's appended to the result
		ipmiConfiguration.setUsername(USERNAME);
		ipmiConfiguration.setPassword(PASSWORD.toCharArray());
		ipmiConfiguration.setBmcKey(BMC_KEY);
		ipmiConfiguration.setSkipAuth(false);
		assertEquals(IMPI_CONFIGURATION_TO_STRING, ipmiConfiguration.toString());

		// When the userName is null, it's not appended to the result
		ipmiConfiguration.setUsername(null);
		assertEquals(IPMI, ipmiConfiguration.toString());
	}
}
