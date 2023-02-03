package com.sentrysoftware.matrix.connector.deserializer;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.InvalidNullException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.sentrysoftware.matrix.connector.model.Connector;
import com.sentrysoftware.matrix.connector.model.identity.criterion.Criterion;
import com.sentrysoftware.matrix.connector.model.identity.criterion.Wbem;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

class CriteriaWbemDeserializerTest extends DeserializerTest {

	@Override
	public String getResourcePath() {
		return "src/test/resources/test-files/connector/detection/criteria/wbem/";
	}

	@Test
	/**
	 * Checks that the criteria type is wbem and that the attributes match
	 *
	 * @throws IOException
	 */
	void testDeserializeWbemCriterion() throws IOException {
		final Connector connector = getConnector("wbemCriterion");

		final List<Criterion> expected = new ArrayList<>();

		final Wbem wbem = Wbem
			.builder()
			.type("wbem")
			.query("testQuery")
			.namespace("testNamespace")
			.expectedResult("testExpectedResult")
			.errorMessage("testErrorMessage")
			.forceSerialization(true)
			.build();

		expected.add(wbem);

		compareCriterion("wbemCriterion", connector, expected);
	}

	@Test
	/**
	 * Checks that the namespace field gets assigned the proper default value
	 *
	 * @throws IOException
	 */
	void testWbemDefaultNamespace() throws IOException { // NOSONAR compareCriterion performs assertion
		final Connector connector = getConnector("wbemCriterionDefaultNamespace");

		final List<Criterion> expected = new ArrayList<>();

		final Wbem wbem = Wbem.builder().type("wbem").query("testQuery").build();

		expected.add(wbem);

		compareCriterion("wbemCriterionDefaultNamespace", connector, expected);
	}

	@Test
	/**
	 * Checks that the query field throws an error when it is null or missing
	 *
	 * @throws IOException
	 */
	void testWbemMissingOrNullQueryNotAccepted() throws IOException {
		{
			try {
				getConnector("wbemCriterionMissingQuery");
				Assert.fail("Expected an MismatchedInputException to be thrown");
			} catch (MismatchedInputException e) {
				final String message = "Missing required creator property 'query' (index 2)";
				checkMessage(e, message);
			}
		}

		{
			try {
				getConnector("wbemCriterionNullQuery");
				Assert.fail("Expected an MismatchedInputException to be thrown");
			} catch (InvalidNullException e) {
				final String message = "Invalid `null` value encountered for property \"query\"";
				checkMessage(e, message);
			}
		}
	}

	@Test
	/**
	 * Checks that the query field throws an error when it is blank
	 *
	 * @throws IOException
	 */
	void testWbemBlankQueryNotAccepted() throws IOException {
		try {
			getConnector("wbemCriterionBlankQuery");
			Assert.fail("Expected an InvalidFormatException to be thrown.");
		} catch (InvalidFormatException e) {
			String message = "Invalid blank value encountered for property 'query'.";
			checkMessage(e, message);
		}
	}

	@Test
	/**
	 * Checks that the namespace field throws an error when it is blank
	 *
	 * @throws IOException
	 */
	void testWbemBlankNamespaceNotAccepted() throws IOException {
		try {
			getConnector("wbemCriterionBlankNamespace");
			Assert.fail("Expected an InvalidFormatException to be thrown.");
		} catch (InvalidFormatException e) {
			String message = "Invalid blank value encountered for property 'namespace'.";
			checkMessage(e, message);
		}
	}
}
