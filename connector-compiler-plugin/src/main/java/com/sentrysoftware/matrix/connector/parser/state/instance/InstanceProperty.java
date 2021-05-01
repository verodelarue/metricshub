package com.sentrysoftware.matrix.connector.parser.state.instance;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import com.sentrysoftware.matrix.connector.model.Connector;
import com.sentrysoftware.matrix.connector.parser.state.IConnectorStateParser;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InstanceProperty {

	INSTANCE_TABLE(new InstanceTableProcessor()),
	INSTANCE(new InstanceProcessor());

	private final IConnectorStateParser connectorStateProcessor;

	public boolean detect(final String key, final String value, final Connector connector) {

		return connectorStateProcessor.detect(key, value, connector);
	}

	public void parse(final String key, final String value, final Connector connector) {

		connectorStateProcessor.parse(key, value, connector);
	}

	public static Set<InstanceProperty> getConnectorProperties() {

		return Arrays.stream(InstanceProperty.values()).collect(Collectors.toSet());
	}
}
