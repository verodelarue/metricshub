package com.sentrysoftware.matrix.converter;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access =  AccessLevel.PRIVATE)
public class ConverterConstants {

	public static final String EMPTY_STRING = "";
	public static final String COMMA = ",";
	public static final String SEMICOLON = ";";
	public static final String COLON = ":";
	public static final String SPACE = " ";
	public static final String COLON_SPACE = COLON + SPACE;
	public static final String DOT = ".";
	public static final String OPENING_PARENTHESIS = "(";
	public static final String CLOSING_PARENTHESIS = ")";

	public static final String TRUE = "true";
	public static final String ONE = "1";
	public static final String PERCENT = "%";

	public static final String DOUBLE_QUOTES_REGEX_REPLACEMENT = "^\\s*\"(.*)\"\\s*$";
	public static final String SOURCE_REFERENCE_REGEX_REPLACEMENT = "^\\s*%(.*)%\\s*$";

	public static final String INTEGER_REGEX = "^[1-9]\\d*$";
	public static final String EMBEDDED_FILE_REGEX = "^\\s*embeddedfile\\(([1-9]\\d*)\\)\\s*$";

	public static final String CONNECTOR = "connector";

	public static final String DETECTION = "detection";
	public static final String CRITERIA = "criteria";

	public static final String DISCOVERY = "discovery";
	public static final String COLLECT = "collect";

	public static final String MONO_INSTANCE = "monoinstance";
	public static final String MULTI_INSTANCE = "multiinstance";

	public static final String TYPE = "type";
	public static final String VALUE_TABLE = "valuetable";

	public static final String DETECTION_DOT_CRITERIA = "detection.criteria";
	public static final String DOT_COMPUTE = ".compute";

	public static final String SET_COLUMN = "setColumn";
	public static final String SET_EXPECTED_RESULT = "setExpectedResult";
	public static final String SET_ERROR_MESSAGE = "setErrorMessage";
	public static final String SET_WBEM_QUERY = "setWbemQuery";
	public static final String SET_WBEM_NAMESPACE = "setWbemNamespace";
	public static final String SET_TIMEOUT = "setTimeout";
	public static final String SET_TEXT = "setText";

	public static final String DEFAULT = "default";

	public static final String IPMI_TOOL = "ipmitool";

	public static final String SOURCES = "sources";
	public static final String MONITORS = "monitors";
	public static final String COMPUTES = "computes";
	public static final String MAPPING = "mapping";
	public static final String ATTRIBUTES = "attributes";
	public static final String SOURCE = "source";

}