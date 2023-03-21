package com.sentrysoftware.matrix.converter.state.computes;

import java.io.IOException;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.sentrysoftware.matrix.converter.AbstractConnectorPropertyConverterTest;

class AddComputeTest extends AbstractConnectorPropertyConverterTest {

	@Override
	protected String getResourcePath() {
		return "src/test/resources/test-files/connector/computes/add";
	}
	
	@Test
	@Disabled("until Add compute processor is up")
	void test() throws IOException {
		testConversion("discovery");
		testConversion("collect");
		
		testAll();
	}
}
