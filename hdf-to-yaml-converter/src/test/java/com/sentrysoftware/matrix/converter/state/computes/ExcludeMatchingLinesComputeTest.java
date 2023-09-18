package com.sentrysoftware.matrix.converter.state.computes;

import com.sentrysoftware.matrix.converter.AbstractConnectorPropertyConverterTest;
import java.io.IOException;
import org.junit.jupiter.api.Test;

class ExcludeMatchingLinesComputeTest extends AbstractConnectorPropertyConverterTest {

	@Override
	protected String getResourcePath() {
		return "src/test/resources/test-files/connector/computes/excludeMatchingLines";
	}

	@Test
	void test() throws IOException {
		testConversion("discovery");
		testConversion("collect");

		testAll();
	}
}
