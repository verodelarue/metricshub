package com.sentrysoftware.metricshub.engine.awk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class AwkTest {

	@Test
	void UnitTestPresence() throws URISyntaxException {

		// Do we have our unit tests?
		assertTrue(listUnitTests().size() > 0);
	}

	@ParameterizedTest
	@MethodSource("listUnitTests")
	void testAwkAgainstNAwk(String testName) throws URISyntaxException, RuntimeException, ParseException, AwkException {

		// Get the script, the input to process, and the expected result
		String script = getResourceAsString("/scripts/" + testName + ".awk");
		String input = getResourceAsString("/inputs/" + testName + ".INP.txt");
		String expectedResult = getResourceAsString("/expected-results/" + testName + ".EXP.txt");

		// Do we have everything?
		assertNotNull(script, "Script " + testName + " is null");
		assertNotNull(input, "Input " + testName + " is null");
		assertNotNull(expectedResult, "Expected Result " + testName + " is null");

		// Let's do the actual test
		String result = AwkExecutor.executeAwk(script, input);
		assertEquals(expectedResult, result, "Results don't match for " + testName);
		AwkExecutor.resetCache();

	}

	/**
	 * Lists the Awk unit tests in the `scripts` resource directory
	 * @return The list of unit tests
	 * @throws URISyntaxException when something is wrong with the URL
	 */
	public static ArrayList<String> listUnitTests() throws URISyntaxException {

		ArrayList<String> unitTestList = new ArrayList<String>();

		// Get the scripts resource directory
		URL scriptsUrl = AwkTest.class.getResource("/scripts");
		if (scriptsUrl != null) {
			File scriptsDir = new File(scriptsUrl.toURI());

			if (scriptsDir.isDirectory()) {
				// List the Awk script files in this directory
				for (File scriptFile : scriptsDir.listFiles()) {
					String scriptName = scriptFile.getName();
					if (scriptName.toLowerCase().endsWith(".awk")) {
						unitTestList.add(scriptName.substring(0, scriptName.length() - 4));
					}
				}
			}
		}

		return unitTestList;
	}

	/**
	 * Reads the specified resource file and returns its content as a String
	 *
	 * @param path Path to the resource file
	 * @return The content of the resource file as a String
	 */
	private static String getResourceAsString(String path) {

		BufferedReader reader = new BufferedReader(new InputStreamReader(AwkTest.class.getResourceAsStream(path)));
		StringBuilder builder = new StringBuilder();
		String l;
		try {
			while ((l = reader.readLine()) != null) {
				builder.append(l).append('\n');
			}
		} catch (IOException e) {
			return null;
		}

		return builder.toString();
	}

	public static void main(String args[]) {

		try {
			String testName = "MS_HW_DiskPart.LogicalDisk.Collect.Source(1).Compute(1).EF2";
			// Get the script, the input to process, and the expected result
			String script = getResourceAsString("/scripts/" + testName + ".awk");
			System.out.println("Script:\n" + script + "\n--------------------\n");
			String input = getResourceAsString("/inputs/" + testName + ".INP.txt");
			System.out.println("Input:\n" + input + "\n-------------------\n");
			String expectedResult = getResourceAsString("/expected-results/" + testName + ".EXP.txt");
			System.out.println("Expected result:\n" + expectedResult + "\n-------------------\n");

			// Let's do the actual test
			String result = Awk.interpret(input, script);
			System.out.println("Actual result:\n" + result + "\n-------------------\n");
			if (!result.equals(expectedResult)) {
				System.err.println("Different results!");
			} else {
				System.out.println("Success!");
			}

			// Performance Test
//			int period = 10;
//			long endTime = System.currentTimeMillis() + period * 1000;
//			int count = 0;
//			while (System.currentTimeMillis() < endTime) {
//				AwkIntermediateCode ic = MatsyaAwk.getIntermediateCode(script);
//				String result = MatsyaAwk.interpret(input, ic);
//				if (!expectedResult.equals(result)) {
//					System.err.println("Failed because of different result");
//					break;
//				}
//				count++;
//			}
//			System.out.println("Performed " + (count / period) + " AWK parse-interpret per second");
//
//			endTime = System.currentTimeMillis() + period * 1000;
//			count = 0;
//			AwkIntermediateCode ic = MatsyaAwk.getIntermediateCode(script);
//			while (System.currentTimeMillis() < endTime) {
//				String result = MatsyaAwk.interpret(input, ic);
//				if (!expectedResult.equals(result)) {
//					System.err.println("Failed because of different result");
//					break;
//				}
//				count++;
//			}
//			System.out.println("Performed " + (count / period) + " AWK interpret per second");
//

		} catch (RuntimeException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}