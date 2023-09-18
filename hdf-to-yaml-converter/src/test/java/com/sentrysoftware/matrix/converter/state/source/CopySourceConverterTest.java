package com.sentrysoftware.matrix.converter.state.source;

import com.sentrysoftware.matrix.converter.AbstractConnectorPropertyConverterTest;
import java.io.IOException;
import org.junit.jupiter.api.Test;

class CopySourceConverterTest extends AbstractConnectorPropertyConverterTest {

	@Override
	protected String getResourcePath() {
		return "src/test/resources/test-files/monitors/source/copy";
	}

	@Test
	void test() throws IOException {
		testConversion("discovery");
		testConversion("collect");

		testConversion("discoveryFromCollect");

		testAll();
	}
}
