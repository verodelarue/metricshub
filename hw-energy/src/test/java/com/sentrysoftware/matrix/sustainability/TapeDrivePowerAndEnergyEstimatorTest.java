package com.sentrysoftware.matrix.sustainability;

import static com.sentrysoftware.matrix.common.Constants.TAPE_DRIVE_MOUNT_COUNT_METRIC;
import static com.sentrysoftware.matrix.common.Constants.TAPE_DRIVE_POWER_METRIC;
import static com.sentrysoftware.matrix.common.Constants.TAPE_DRIVE_UNMOUNT_COUNT_METRIC;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.sentrysoftware.matrix.configuration.HostConfiguration;
import com.sentrysoftware.matrix.telemetry.MetricFactory;
import com.sentrysoftware.matrix.telemetry.Monitor;
import com.sentrysoftware.matrix.telemetry.TelemetryManager;
import com.sentrysoftware.matrix.telemetry.metric.NumberMetric;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

class TapeDrivePowerAndEnergyEstimatorTest {

	@InjectMocks
	private TapeDrivePowerAndEnergyEstimator tapeDrivePowerAndEnergyEstimator;

	private Monitor monitor = null;
	private TelemetryManager telemetryManager = null;

	@BeforeEach
	void init() {
		monitor =
			Monitor
				.builder()
				.metrics(
					new HashMap<>(
						Map.of(
							TAPE_DRIVE_MOUNT_COUNT_METRIC,
							NumberMetric.builder().value(7.0).build(),
							TAPE_DRIVE_UNMOUNT_COUNT_METRIC,
							NumberMetric.builder().value(0.2).build()
						)
					)
				)
				.attributes(new HashMap<>(Map.of("name", "lto123")))
				.build();
		telemetryManager =
			TelemetryManager
				.builder()
				.strategyTime(1696597422644L)
				.hostConfiguration(HostConfiguration.builder().hostname("localhost").build())
				.build();
		tapeDrivePowerAndEnergyEstimator = new TapeDrivePowerAndEnergyEstimator(monitor, telemetryManager);
	}

	@Test
	void testEstimatePower() {
		assertEquals(46, tapeDrivePowerAndEnergyEstimator.estimatePower());
	}

	@Test
	void testEstimateEnergy() {
		// Estimate energy consumption, no previous collect time
		assertNull(tapeDrivePowerAndEnergyEstimator.estimateEnergy());

		// Estimate power consumption
		Double estimatedPower = tapeDrivePowerAndEnergyEstimator.estimatePower();

		// Create metricFactory and collect power
		final MetricFactory metricFactory = new MetricFactory(telemetryManager.getHostname());
		final NumberMetric collectedPowerMetric = metricFactory.collectNumberMetric(
			monitor,
			TAPE_DRIVE_POWER_METRIC,
			estimatedPower,
			telemetryManager.getStrategyTime()
		);

		// Save the collected power metric
		collectedPowerMetric.save();

		// Estimate power consumption again
		estimatedPower = tapeDrivePowerAndEnergyEstimator.estimatePower();

		// Collect the new power consumption metric
		metricFactory.collectNumberMetric(
			monitor,
			TAPE_DRIVE_POWER_METRIC,
			estimatedPower,
			telemetryManager.getStrategyTime()
		);

		// Estimate the energy
		assertEquals(46, tapeDrivePowerAndEnergyEstimator.estimateEnergy());
	}
}
