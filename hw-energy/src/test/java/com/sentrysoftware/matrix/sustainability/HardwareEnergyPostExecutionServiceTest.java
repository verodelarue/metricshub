package com.sentrysoftware.matrix.sustainability;

import static com.sentrysoftware.matrix.common.Constants.DISK_CONTROLLER_ENERGY_METRIC;
import static com.sentrysoftware.matrix.common.Constants.DISK_CONTROLLER_POWER_METRIC;
import static com.sentrysoftware.matrix.common.Constants.FAN_ENERGY_METRIC;
import static com.sentrysoftware.matrix.common.Constants.FAN_POWER_METRIC;
import static com.sentrysoftware.matrix.common.Constants.FAN_SPEED_METRIC;
import static com.sentrysoftware.matrix.common.Constants.LOCALHOST;
import static com.sentrysoftware.matrix.common.Constants.MEMORY_ENERGY_METRIC;
import static com.sentrysoftware.matrix.common.Constants.MEMORY_POWER_METRIC;
import static com.sentrysoftware.matrix.common.Constants.ROBOTICS_ENERGY_METRIC;
import static com.sentrysoftware.matrix.common.Constants.ROBOTICS_MOVE_COUNT_METRIC;
import static com.sentrysoftware.matrix.common.Constants.ROBOTICS_POWER_METRIC;
import static com.sentrysoftware.matrix.common.Constants.TAPE_DRIVE_ENERGY_METRIC;
import static com.sentrysoftware.matrix.common.Constants.TAPE_DRIVE_MOUNT_COUNT_METRIC;
import static com.sentrysoftware.matrix.common.Constants.TAPE_DRIVE_POWER_METRIC;
import static com.sentrysoftware.matrix.common.Constants.TAPE_DRIVE_UNMOUNT_COUNT_METRIC;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.sentrysoftware.matrix.HardwareEnergyPostExecutionService;
import com.sentrysoftware.matrix.common.helpers.KnownMonitorType;
import com.sentrysoftware.matrix.configuration.HostConfiguration;
import com.sentrysoftware.matrix.telemetry.Monitor;
import com.sentrysoftware.matrix.telemetry.TelemetryManager;
import com.sentrysoftware.matrix.telemetry.metric.NumberMetric;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HardwareEnergyPostExecutionServiceTest {

	private HardwareEnergyPostExecutionService hardwareEnergyPostExecutionService;

	private TelemetryManager telemetryManager = null;

	private static final String DISK_CONTROLLER = KnownMonitorType.DISK_CONTROLLER.getKey();
	private static final String FAN = KnownMonitorType.FAN.getKey();
	private static final String MEMORY = KnownMonitorType.MEMORY.getKey();
	private static final String ROBOTICS = KnownMonitorType.ROBOTICS.getKey();
	private static final String TAPE_DRIVE = KnownMonitorType.TAPE_DRIVE.getKey();

	@BeforeEach
	void init() {
		telemetryManager =
			TelemetryManager
				.builder()
				.strategyTime(1696597422644L)
				.hostConfiguration(HostConfiguration.builder().hostname(LOCALHOST).build())
				.build();
	}

	@Test
	void testRunWithFanMonitor() {
		// Create a fan monitor
		final Monitor fanMonitor = Monitor
			.builder()
			.type(FAN)
			.metrics(new HashMap<>(Map.of(FAN_SPEED_METRIC, NumberMetric.builder().value(0.7).build())))
			.build();

		// Set the previously created fan monitor in telemetryManager
		final Map<String, Monitor> fanMonitors = new HashMap<>(Map.of("monitor1", fanMonitor));
		telemetryManager.setMonitors(new HashMap<>(Map.of(FAN, fanMonitors)));

		// Call run method in HardwareEnergyPostExecutionService
		hardwareEnergyPostExecutionService = new HardwareEnergyPostExecutionService(telemetryManager);
		hardwareEnergyPostExecutionService.run();

		// Check the computed and collected power metric
		assertNotNull(fanMonitor.getMetric(FAN_POWER_METRIC, NumberMetric.class));

		// Check the computed and collected energy metric
		assertNotNull(fanMonitor.getMetric(FAN_ENERGY_METRIC, NumberMetric.class));
	}

	@Test
	void testRunWithRoboticsMonitor() {
		// Create a robotics monitor
		final Monitor roboticsMonitor = Monitor
			.builder()
			.type(ROBOTICS)
			.metrics(new HashMap<>(Map.of(ROBOTICS_MOVE_COUNT_METRIC, NumberMetric.builder().value(0.7).build())))
			.build();

		// Set the previously created robotics monitor in telemetryManager
		final Map<String, Monitor> roboticsMonitors = new HashMap<>(Map.of("monitor2", roboticsMonitor));
		telemetryManager.setMonitors(new HashMap<>(Map.of(ROBOTICS, roboticsMonitors)));

		// Call run method in HardwareEnergyPostExecutionService
		hardwareEnergyPostExecutionService = new HardwareEnergyPostExecutionService(telemetryManager);
		hardwareEnergyPostExecutionService.run();

		// Check the computed and collected power metric
		assertNotNull(roboticsMonitor.getMetric(ROBOTICS_POWER_METRIC, NumberMetric.class));

		// Check the computed and collected energy metric
		assertNotNull(roboticsMonitor.getMetric(ROBOTICS_ENERGY_METRIC, NumberMetric.class));
	}

	@Test
	void testRunWithTapeDriveMonitor() {
		// Create a tape drive monitor
		final Monitor tapeDriveMonitor = Monitor
			.builder()
			.type(TAPE_DRIVE)
			.metrics(
				new HashMap<>(
					Map.of(
						TAPE_DRIVE_MOUNT_COUNT_METRIC,
						NumberMetric.builder().value(0.7).build(),
						TAPE_DRIVE_UNMOUNT_COUNT_METRIC,
						NumberMetric.builder().value(0.1).build()
					)
				)
			)
			.attributes(new HashMap<>(Map.of("name", "lto123")))
			.build();

		// Set the previously created tape drive monitor in telemetryManager
		final Map<String, Monitor> tapeDriveMonitors = new HashMap<>(Map.of("monitor3", tapeDriveMonitor));
		telemetryManager.setMonitors(new HashMap<>(Map.of(TAPE_DRIVE, tapeDriveMonitors)));

		// Call run method in HardwareEnergyPostExecutionService
		hardwareEnergyPostExecutionService = new HardwareEnergyPostExecutionService(telemetryManager);
		hardwareEnergyPostExecutionService.run();

		// Check the computed and collected power metric
		assertNotNull(tapeDriveMonitor.getMetric(TAPE_DRIVE_POWER_METRIC, NumberMetric.class));

		// Check the computed and collected energy metric
		assertNotNull(tapeDriveMonitor.getMetric(TAPE_DRIVE_ENERGY_METRIC, NumberMetric.class));
	}

	@Test
	void testRunWithDiskControllerMonitor() {
		// Create a disk controller monitor
		final Monitor diskControllerMonitor = Monitor.builder().type(DISK_CONTROLLER).build();

		// Set the previously created disk controller monitor in telemetryManager
		final Map<String, Monitor> diskControllerMonitors = new HashMap<>(Map.of("monitor4", diskControllerMonitor));
		telemetryManager.setMonitors(new HashMap<>(Map.of(DISK_CONTROLLER, diskControllerMonitors)));

		// Call run method in HardwareEnergyPostExecutionService
		hardwareEnergyPostExecutionService = new HardwareEnergyPostExecutionService(telemetryManager);
		hardwareEnergyPostExecutionService.run();

		// Check the computed and collected power metric
		assertNotNull(diskControllerMonitor.getMetric(DISK_CONTROLLER_POWER_METRIC, NumberMetric.class));

		// Check the computed and collected energy metric
		assertNotNull(diskControllerMonitor.getMetric(DISK_CONTROLLER_ENERGY_METRIC, NumberMetric.class));
	}

	@Test
	void testRunWithMemoryMonitor() {
		// Create a fan monitor
		final Monitor monitor = Monitor.builder().type(MEMORY).build();

		// Set the previously created monitor in telemetryManager
		final Map<String, Monitor> monitors = new HashMap<>(Map.of("monitor1", monitor));
		telemetryManager.setMonitors(new HashMap<>(Map.of(MEMORY, monitors)));

		// Call run method in HardwareEnergyPostExecutionService
		hardwareEnergyPostExecutionService = new HardwareEnergyPostExecutionService(telemetryManager);
		hardwareEnergyPostExecutionService.run();

		// Check the computed and collected power metric
		assertNotNull(monitor.getMetric(MEMORY_POWER_METRIC, NumberMetric.class));

		// Check the computed and collected energy metric
		assertNotNull(monitor.getMetric(MEMORY_ENERGY_METRIC, NumberMetric.class));
	}
}
