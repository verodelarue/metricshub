package com.sentrysoftware.matrix.converter.state.mapping;

import static com.sentrysoftware.matrix.converter.ConverterConstants.ATTRIBUTES;
import static com.sentrysoftware.matrix.converter.ConverterConstants.HDF_DEVICE_ID;
import static com.sentrysoftware.matrix.converter.ConverterConstants.HDF_DISPLAY_ID;
import static com.sentrysoftware.matrix.converter.ConverterConstants.HDF_LOWER_THRESHOLD;
import static com.sentrysoftware.matrix.converter.ConverterConstants.HDF_STATUS;
import static com.sentrysoftware.matrix.converter.ConverterConstants.HDF_STATUS_INFORMATION;
import static com.sentrysoftware.matrix.converter.ConverterConstants.HDF_UPPER_THRESHOLD;
import static com.sentrysoftware.matrix.converter.ConverterConstants.HDF_VOLTAGE_TYPE;
import static com.sentrysoftware.matrix.converter.ConverterConstants.HDF_VOLTAGE_VALUE;
import static com.sentrysoftware.matrix.converter.ConverterConstants.LEGACY_TEXT_PARAMETERS;
import static com.sentrysoftware.matrix.converter.ConverterConstants.METRICS;
import static com.sentrysoftware.matrix.converter.ConverterConstants.YAML_DISPLAY_ID;
import static com.sentrysoftware.matrix.converter.ConverterConstants.YAML_ID;
import static com.sentrysoftware.matrix.converter.ConverterConstants.YAML_NAME;
import static com.sentrysoftware.matrix.converter.ConverterConstants.YAML_STATUS_INFORMATION;
import static com.sentrysoftware.matrix.converter.ConverterConstants.YAML_VOLTAGE_HIGH_DEGRADED;
import static com.sentrysoftware.matrix.converter.ConverterConstants.YAML_VOLTAGE_LOW_CRITICAL;
import static com.sentrysoftware.matrix.converter.ConverterConstants.YAML_VOLTAGE_STATUS;
import static com.sentrysoftware.matrix.converter.ConverterConstants.YAML_VOLTAGE_TYPE;
import static com.sentrysoftware.matrix.converter.ConverterConstants.YAML_VOLTAGE_VALUE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

public class VoltageConverter extends AbstractMappingConverter {

	private static final Map<String, Entry<String, IMappingKey>> ONE_TO_ONE_ATTRIBUTES_MAPPING;
	static {
		final Map<String, Entry<String, IMappingKey>> attributesMap = new HashMap<>();
		attributesMap.put(HDF_DEVICE_ID, IMappingKey.of(ATTRIBUTES, YAML_ID));
		attributesMap.put(HDF_DISPLAY_ID, IMappingKey.of(ATTRIBUTES, YAML_DISPLAY_ID));
		attributesMap.put(HDF_VOLTAGE_TYPE, IMappingKey.of(ATTRIBUTES, YAML_VOLTAGE_TYPE));
		attributesMap.put(HDF_LOWER_THRESHOLD, IMappingKey.of(METRICS, YAML_VOLTAGE_LOW_CRITICAL));
		attributesMap.put(HDF_UPPER_THRESHOLD, IMappingKey.of(METRICS, YAML_VOLTAGE_HIGH_DEGRADED));
		ONE_TO_ONE_ATTRIBUTES_MAPPING = Collections.unmodifiableMap(attributesMap);
	}

	private static final Map<String, Entry<String, IMappingKey>> ONE_TO_ONE_METRICS_MAPPING;
	static {

		final Map<String, Entry<String, IMappingKey>> metricsMap = new HashMap<>();
		metricsMap.put(HDF_STATUS, IMappingKey.of(METRICS, YAML_VOLTAGE_STATUS));
		metricsMap.put(HDF_STATUS_INFORMATION, IMappingKey.of(LEGACY_TEXT_PARAMETERS, YAML_STATUS_INFORMATION));
		metricsMap.put(HDF_VOLTAGE_VALUE, IMappingKey.of(METRICS, YAML_VOLTAGE_VALUE));
		ONE_TO_ONE_METRICS_MAPPING = Collections.unmodifiableMap(metricsMap);
	}

	@Override
	protected Map<String, Entry<String, IMappingKey>> getOneToOneAttributesMapping() {
		return ONE_TO_ONE_ATTRIBUTES_MAPPING;
	}

	@Override
	protected void convertAttributesSpecific(JsonNode mapping, ObjectNode existingAttributes, ObjectNode newAttributes) {
		// No specific attributes to convert
	}

	@Override
	protected void setName(ObjectNode existingAttributes, ObjectNode newAttributes) {
		final JsonNode deviceId = existingAttributes.get(HDF_DEVICE_ID);
		if (deviceId == null) {
			throw new IllegalStateException(String.format("%s cannot be null.", HDF_DEVICE_ID));
		}

		final JsonNode displayId = existingAttributes.get(HDF_DISPLAY_ID);
		JsonNode firstDisplayArgument = deviceId;
		if (displayId != null) {
			firstDisplayArgument = displayId;
		}

		final JsonNode voltageType = existingAttributes.get(HDF_VOLTAGE_TYPE);

		newAttributes.set(
			YAML_NAME,
			new TextNode(
				buildNameValue(firstDisplayArgument, voltageType)
			)
		);
	}

	/**
	 * Joins the given non-empty text nodes to build the voltage name value
	 *
	 * @param firstDisplayArgument {@link JsonNode} representing the display name
	 * @param voltageTypeNode       {@link JsonNode} voltage type
	 *
	 * @return {@link String} Joined text nodes
	 */
	private String buildNameValue(final JsonNode firstDisplayArgument, final JsonNode voltageTypeNode) {

		final String firstArg = firstDisplayArgument.asText();
		if (voltageTypeNode == null) {
			return firstArg;
		}

		// Create the function with the arguments
		final StringBuilder format = new StringBuilder("sprintf(\"%s (%s)");

		// Build the list of arguments non null
		final List<String> sprintfArgs = new ArrayList<>();
		sprintfArgs.add(firstArg);
		sprintfArgs.add(voltageTypeNode.asText());

		// Join the arguments: $column(1), $column(2)) 
		// append the result to our format variable in order to get something like
		// sprint("%s (%s)", $column(1), $column(2))
		return format
			.append("\", ") // Here we will have a string like sprintf("%s (%s)", 
			.append(
				sprintfArgs
					.stream()
					.map(this::getFunctionArgument)
					.collect(Collectors.joining(", ", "", ")"))
			)
			.toString();
		
		
	}

	@Override
	protected Map<String, Entry<String, IMappingKey>> getOneToOneMetricsMapping() {
		return ONE_TO_ONE_METRICS_MAPPING;
	}

	@Override
	public void convertCollectProperty(final String key, final String value, final JsonNode node) {
		convertOneToOneMetrics(key, value, (ObjectNode) node);
	}

}
