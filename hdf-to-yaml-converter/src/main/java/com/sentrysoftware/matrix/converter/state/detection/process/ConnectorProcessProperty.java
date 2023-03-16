package com.sentrysoftware.matrix.converter.state.detection.process;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.sentrysoftware.matrix.converter.state.IConnectorStateConverter;
import com.sentrysoftware.matrix.converter.state.detection.common.ForceSerializationProcessor;
import com.sentrysoftware.matrix.converter.state.detection.common.TypeProcessor;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConnectorProcessProperty {

	private static final String PROCESS_HDF_TYPE_VALUE = "KMVersion";
	private static final String PROCESS_YAML_TYPE_VALUE = "productRequirements";

	public static Set<IConnectorStateConverter> getConnectorProperties() {

		return Stream.of(
				new TypeProcessor(PROCESS_HDF_TYPE_VALUE, PROCESS_YAML_TYPE_VALUE),
				new ForceSerializationProcessor(),
				new ProcessCommandLineProcessor())
				.collect(Collectors.toSet());
	}
}