package com.sentrysoftware.matrix.converter.state.monitors.collect.valueTable;

import java.io.IOException;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.sentrysoftware.matrix.converter.AbstractConnectorPropertyConverterTest;

class CpuCollectValueTableTest  extends AbstractConnectorPropertyConverterTest{
	
	@Override
	protected String getResourcePath() {
		return "src/test/resources/test-files/monitors/collect/valueTable/cpu";
	}

	@Test
	@Disabled("Until CpuValueTable processor is up")
	void test() throws IOException {
		testAll();
	}
}