package com.sentrysoftware.matrix.converter.state.detection.http;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sentrysoftware.matrix.converter.PreConnector;
import com.sentrysoftware.matrix.converter.state.AbstractStateConverter;

public class BodyProcessor extends AbstractStateConverter {

	private static final Pattern BODY_KEY_PATTERN = Pattern.compile(
		"^\\s*detection\\.criteria\\(([1-9]\\d*)\\)\\.body\\s*$",
		Pattern.CASE_INSENSITIVE
	);

	@Override
	public void convert(String key, String value, JsonNode connector, PreConnector preConnector) {
		((ObjectNode) getLastCriterion(key, connector))
			.set("body", JsonNodeFactory.instance.textNode(value));
	}

	@Override
	protected Matcher getMatcher(String key) {
		return BODY_KEY_PATTERN.matcher(key);
	}

}
