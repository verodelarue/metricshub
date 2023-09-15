package com.sentrysoftware.matrix.converter.state.monitors.collect.valuetable;

import com.sentrysoftware.matrix.converter.AbstractConnectorPropertyConverterTest;
import java.io.IOException;
import org.junit.jupiter.api.Test;

class GpuCollectValueTableTest extends AbstractConnectorPropertyConverterTest {

	@Override
	protected String getResourcePath() {
		return "src/test/resources/test-files/monitors/collect/valueTable/gpu";
	}

	@Test
	void test() throws IOException {
		testAll();
	}
}
