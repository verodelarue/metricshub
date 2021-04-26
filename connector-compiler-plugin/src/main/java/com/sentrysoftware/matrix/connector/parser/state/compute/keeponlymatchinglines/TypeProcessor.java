package com.sentrysoftware.matrix.connector.parser.state.compute.keeponlymatchinglines;

import com.sentrysoftware.matrix.connector.model.Connector;
import com.sentrysoftware.matrix.connector.model.monitor.job.source.Source;
import com.sentrysoftware.matrix.connector.model.monitor.job.source.compute.KeepOnlyMatchingLines;
import com.sentrysoftware.matrix.connector.parser.ConnectorParserConstants;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.util.Assert.isTrue;

public class TypeProcessor extends KeepOnlyMatchingLinesProcessor {

	private static final Pattern TYPE_KEY_PATTERN = Pattern.compile(
			"^\\s*(.*)\\.(discovery|collect)\\.source\\(([1-9]\\d*)\\)\\.compute\\(([1-9]\\d*)\\)\\.type\\s*$",
			Pattern.CASE_INSENSITIVE
	);

	@Override
	protected Matcher getMatcher(String key) {
		return TYPE_KEY_PATTERN.matcher(key);
	}

	@Override
	public void parse(final String key, final String value, final Connector connector) {

		super.parse(key, value, connector);

		isTrue(
				KEEP_ONLY_MATCHING_LINES_TYPE_VALUE.equalsIgnoreCase(
						value.replaceAll(ConnectorParserConstants.DOUBLE_QUOTES_REGEX_REPLACEMENT, "$1")
				),
				"Invalid Compute type: " + value
		);

		Matcher matcher = getMatcher(key);
		isTrue(matcher.matches(), "Invalid key: " + key + ConnectorParserConstants.DOT);

		Source source = getSource(matcher, connector);

		// TODO: When all source types have been implemented,
		// remove this if statement and uncomment the notNull check
		if (source == null) {
			return;
		}
		//notNull(source, "Could not find any source for the following key: " + key + ConnectorParserConstants.DOT);

		if (source.getComputes() == null) {
			source.setComputes(new ArrayList<>());
		}

		KeepOnlyMatchingLines keepOnlyMatchingLines = new KeepOnlyMatchingLines();
		keepOnlyMatchingLines.setIndex(getComputeIndex(matcher));

		source
				.getComputes()
				.add(keepOnlyMatchingLines);
	}
}
