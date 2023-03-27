package com.sentrysoftware.matrix.converter.state.computes.substring;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JsonNode;
import com.sentrysoftware.matrix.converter.PreConnector;
import com.sentrysoftware.matrix.converter.state.AbstractStateConverter;
import com.sentrysoftware.matrix.converter.state.ConversionHelper;

public class SubstringStartProcessor extends AbstractStateConverter {

	private static final Pattern PATTERN = Pattern.compile(
			ConversionHelper.buildComputeKeyRegex("start"),
			Pattern.CASE_INSENSITIVE
		);

	@Override
	public void convert(String key, String value, JsonNode connector, PreConnector preConnector) {
		createComputeTextNode(key, value, connector, "start");
	}

	@Override
	protected Matcher getMatcher(String key) {
		return PATTERN.matcher(key);
	}

}