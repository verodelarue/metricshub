package org.sentrysoftware.metricshub.agent.helper;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.sentrysoftware.metricshub.agent.helper.AgentConstants.DEFAULT_OTEL_CRT_FILENAME;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.sentrysoftware.metricshub.agent.config.AgentConfig;
import org.sentrysoftware.metricshub.agent.config.exporter.ExporterConfig;
import org.sentrysoftware.metricshub.agent.config.exporter.OtlpExporterConfig;

class OtelConfigHelperTest {

	private static final String OTEL_EXPORTER_OTLP_HEADERS_PROPERTY = "otel.exporter.otlp.headers";

	private static final String TEST_SECURITY_PATH = "src/test/resources/security";

	private static final String HTTP_GRPC_ENDPOINT = "http://localhost:4317";
	private static final String HTTPS_GRPC_ENDPOINT = "https://localhost:4317";

	@Test
	void testGetSecurityFilePath() {
		// Null certificate file configured, the default is loaded
		assertTrue(
			OtelConfigHelper
				.getSecurityFilePath(TEST_SECURITY_PATH, DEFAULT_OTEL_CRT_FILENAME, null, HTTPS_GRPC_ENDPOINT)
				.isPresent()
		);

		// Empty certificate file configured, the default is loaded
		assertTrue(
			OtelConfigHelper
				.getSecurityFilePath(TEST_SECURITY_PATH, DEFAULT_OTEL_CRT_FILENAME, "", HTTPS_GRPC_ENDPOINT)
				.isPresent()
		);

		// Blank certificate file, the default is loaded
		assertTrue(
			OtelConfigHelper
				.getSecurityFilePath(TEST_SECURITY_PATH, DEFAULT_OTEL_CRT_FILENAME, " ", HTTPS_GRPC_ENDPOINT)
				.isPresent()
		);

		// Certificate file not found, it cannot be loaded
		final String randomFilename = UUID.randomUUID().toString();
		assertFalse(
			OtelConfigHelper
				.getSecurityFilePath(TEST_SECURITY_PATH, DEFAULT_OTEL_CRT_FILENAME, randomFilename, HTTPS_GRPC_ENDPOINT)
				.isPresent()
		);

		// HTTP instead of HTTPS, no certificate to load
		assertTrue(
			OtelConfigHelper
				.getSecurityFilePath(TEST_SECURITY_PATH, DEFAULT_OTEL_CRT_FILENAME, null, HTTP_GRPC_ENDPOINT)
				.isEmpty()
		);

		// The certificate file is found
		assertFalse(
			OtelConfigHelper
				.getSecurityFilePath(
					TEST_SECURITY_PATH,
					DEFAULT_OTEL_CRT_FILENAME,
					TEST_SECURITY_PATH + "/" + DEFAULT_OTEL_CRT_FILENAME,
					HTTPS_GRPC_ENDPOINT
				)
				.isEmpty()
		);
	}

	@Test
	void testBuildOtelSdkConfiguration() {
		assertDoesNotThrow(() -> OtelConfigHelper.buildOtelSdkConfiguration(new AgentConfig()));
	}

	@Test
	void testBuildOtelSdkConfigurationHeaders() {
		{
			final Map<String, String> sdkConfig = OtelConfigHelper.buildOtelSdkConfiguration(AgentConfig.empty());
			assertTrue(sdkConfig.containsKey(OTEL_EXPORTER_OTLP_HEADERS_PROPERTY));
		}

		{
			final AgentConfig agentConfig = AgentConfig
				.builder()
				.exporter(ExporterConfig.builder().otlp(OtlpExporterConfig.builder().headers(null).build()).build())
				.build();
			final Map<String, String> sdkConfig = OtelConfigHelper.buildOtelSdkConfiguration(agentConfig);
			assertFalse(sdkConfig.containsKey(OTEL_EXPORTER_OTLP_HEADERS_PROPERTY));
		}

		{
			final AgentConfig agentConfig = AgentConfig
				.builder()
				.exporter(
					ExporterConfig.builder().otlp(OtlpExporterConfig.builder().headers(Collections.emptyMap()).build()).build()
				)
				.build();
			final Map<String, String> sdkConfig = OtelConfigHelper.buildOtelSdkConfiguration(agentConfig);
			assertFalse(sdkConfig.containsKey(OTEL_EXPORTER_OTLP_HEADERS_PROPERTY));
		}

		{
			final AgentConfig agentConfig = AgentConfig
				.builder()
				.exporter(ExporterConfig.builder().otlp(null).build())
				.build();
			final Map<String, String> sdkConfig = OtelConfigHelper.buildOtelSdkConfiguration(agentConfig);
			assertFalse(sdkConfig.containsKey(OTEL_EXPORTER_OTLP_HEADERS_PROPERTY));
		}

		{
			final AgentConfig agentConfig = AgentConfig.builder().exporter(null).build();
			final Map<String, String> sdkConfig = OtelConfigHelper.buildOtelSdkConfiguration(agentConfig);
			assertFalse(sdkConfig.containsKey(OTEL_EXPORTER_OTLP_HEADERS_PROPERTY));
		}
	}
}
