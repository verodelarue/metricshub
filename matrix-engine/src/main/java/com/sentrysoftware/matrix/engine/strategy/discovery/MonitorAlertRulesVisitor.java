package com.sentrysoftware.matrix.engine.strategy.discovery;

import static com.sentrysoftware.matrix.common.helpers.HardwareConstants.ALARM_THRESHOLD;
import static com.sentrysoftware.matrix.common.helpers.HardwareConstants.AVAILABLE_PATH_COUNT_PARAMETER;
import static com.sentrysoftware.matrix.common.helpers.HardwareConstants.AVAILABLE_PATH_WARNING;
import static com.sentrysoftware.matrix.common.helpers.HardwareConstants.CORRECTED_ERROR_ALARM_THRESHOLD;
import static com.sentrysoftware.matrix.common.helpers.HardwareConstants.CORRECTED_ERROR_COUNT_PARAMETER;
import static com.sentrysoftware.matrix.common.helpers.HardwareConstants.CORRECTED_ERROR_WARNING_THRESHOLD;
import static com.sentrysoftware.matrix.common.helpers.HardwareConstants.ERROR_COUNT_ALARM_THRESHOLD;
import static com.sentrysoftware.matrix.common.helpers.HardwareConstants.ERROR_COUNT_PARAMETER;
import static com.sentrysoftware.matrix.common.helpers.HardwareConstants.ERROR_COUNT_WARNING_THRESHOLD;
import static com.sentrysoftware.matrix.common.helpers.HardwareConstants.ERROR_PERCENT_ALARM_THRESHOLD;
import static com.sentrysoftware.matrix.common.helpers.HardwareConstants.ERROR_PERCENT_PARAMETER;
import static com.sentrysoftware.matrix.common.helpers.HardwareConstants.ERROR_PERCENT_WARNING_THRESHOLD;
import static com.sentrysoftware.matrix.common.helpers.HardwareConstants.LOWER_THRESHOLD;
import static com.sentrysoftware.matrix.common.helpers.HardwareConstants.PERCENT_ALARM_THRESHOLD;
import static com.sentrysoftware.matrix.common.helpers.HardwareConstants.PERCENT_WARNING_THRESHOLD;
import static com.sentrysoftware.matrix.common.helpers.HardwareConstants.SPEED_PARAMETER;
import static com.sentrysoftware.matrix.common.helpers.HardwareConstants.SPEED_PERCENT_PARAMETER;
import static com.sentrysoftware.matrix.common.helpers.HardwareConstants.TEMPERATURE_PARAMETER;
import static com.sentrysoftware.matrix.common.helpers.HardwareConstants.UPPER_THRESHOLD;
import static com.sentrysoftware.matrix.common.helpers.HardwareConstants.USAGE_COUNT_ALARM_THRESHOLD;
import static com.sentrysoftware.matrix.common.helpers.HardwareConstants.USAGE_COUNT_PARAMETER;
import static com.sentrysoftware.matrix.common.helpers.HardwareConstants.USAGE_COUNT_WARNING_THRESHOLD;
import static com.sentrysoftware.matrix.common.helpers.HardwareConstants.VALUE_ALARM_THRESHOLD;
import static com.sentrysoftware.matrix.common.helpers.HardwareConstants.VALUE_PARAMETER;
import static com.sentrysoftware.matrix.common.helpers.HardwareConstants.VALUE_WARNING_THRESHOLD;
import static com.sentrysoftware.matrix.common.helpers.HardwareConstants.VOLTAGE_PARAMETER;
import static com.sentrysoftware.matrix.common.helpers.HardwareConstants.WARNING_THRESHOLD;
import static com.sentrysoftware.matrix.engine.strategy.discovery.MonitorDiscoveryVisitor.METADATA_CANNOT_BE_NULL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.springframework.util.Assert;

import com.sentrysoftware.matrix.common.helpers.NumberHelper;
import com.sentrysoftware.matrix.common.meta.monitor.Battery;
import com.sentrysoftware.matrix.common.meta.monitor.Blade;
import com.sentrysoftware.matrix.common.meta.monitor.Cpu;
import com.sentrysoftware.matrix.common.meta.monitor.CpuCore;
import com.sentrysoftware.matrix.common.meta.monitor.DiskController;
import com.sentrysoftware.matrix.common.meta.monitor.Enclosure;
import com.sentrysoftware.matrix.common.meta.monitor.Fan;
import com.sentrysoftware.matrix.common.meta.monitor.Gpu;
import com.sentrysoftware.matrix.common.meta.monitor.IMetaMonitor;
import com.sentrysoftware.matrix.common.meta.monitor.Led;
import com.sentrysoftware.matrix.common.meta.monitor.LogicalDisk;
import com.sentrysoftware.matrix.common.meta.monitor.Lun;
import com.sentrysoftware.matrix.common.meta.monitor.Memory;
import com.sentrysoftware.matrix.common.meta.monitor.MetaConnector;
import com.sentrysoftware.matrix.common.meta.monitor.NetworkCard;
import com.sentrysoftware.matrix.common.meta.monitor.OtherDevice;
import com.sentrysoftware.matrix.common.meta.monitor.PhysicalDisk;
import com.sentrysoftware.matrix.common.meta.monitor.PowerSupply;
import com.sentrysoftware.matrix.common.meta.monitor.Robotics;
import com.sentrysoftware.matrix.common.meta.monitor.TapeDrive;
import com.sentrysoftware.matrix.common.meta.monitor.Host;
import com.sentrysoftware.matrix.common.meta.monitor.Temperature;
import com.sentrysoftware.matrix.common.meta.monitor.Vm;
import com.sentrysoftware.matrix.common.meta.monitor.Voltage;
import com.sentrysoftware.matrix.engine.strategy.IMonitorVisitor;
import com.sentrysoftware.matrix.model.alert.AlertCondition;
import com.sentrysoftware.matrix.model.alert.AlertConditionsBuilder;
import com.sentrysoftware.matrix.model.alert.AlertDetails;
import com.sentrysoftware.matrix.model.alert.AlertRule;
import com.sentrysoftware.matrix.model.alert.AlertRule.AlertRuleType;
import com.sentrysoftware.matrix.model.alert.Severity;
import com.sentrysoftware.matrix.model.monitor.Monitor;

import lombok.NonNull;

public class MonitorAlertRulesVisitor implements IMonitorVisitor {

	private Monitor monitor;

	public MonitorAlertRulesVisitor(@NonNull Monitor monitor) {
		this.monitor = monitor;
		Assert.isTrue(monitor.getMetadata() != null, METADATA_CANNOT_BE_NULL);
	}

	@Override
	public void visit(MetaConnector metaConnector) {

		// Process the static alert rules
		processStaticAlertRules(monitor, metaConnector);
	}

	@Override
	public void visit(Host host) {

		// Process the static alert rules
		processStaticAlertRules(monitor, host);
	}

	@Override
	public void visit(Battery battery) {

		// Process the static alert rules
		processStaticAlertRules(monitor, battery);
	}

	@Override
	public void visit(Blade blade) {

		// Process the static alert rules
		processStaticAlertRules(monitor, blade);
	}

	@Override
	public void visit(Cpu cpu) {

		// Process the CPU Instance Alert Rules
		final Set<String> parametersToSkip = processCpuInstanceAlertRules(monitor);

		// Process the static alert rules
		processStaticAlertRules(monitor, cpu, parametersToSkip);
	}

	@Override
	public void visit(CpuCore cpuCore) {

		// Process the static alert rules
		processStaticAlertRules(monitor, cpuCore);
	}

	@Override
	public void visit(DiskController diskController) {

		// Process the static alert rules
		processStaticAlertRules(monitor, diskController);
	}

	@Override
	public void visit(Enclosure enclosure) {

		// Last step, for the enclosure we set the static alert rules.
		// We don't have instance (dynamic) threshold from the connector
		processStaticAlertRules(monitor, enclosure);
	}

	@Override
	public void visit(Fan fan) {

		// Process the Fan Instance Alert Rules
		final Set<String> parametersToSkip = processFanInstanceAlertRules(monitor);

		// Static alert rules processing
		processStaticAlertRules(monitor, fan, parametersToSkip);
	}

	@Override
	public void visit(Led led) {

		// Static alert rules processing
		processStaticAlertRules(monitor, led);
	}

	@Override
	public void visit(LogicalDisk logicalDisk) {

		// Process instance alert rules
		final Set<String> parametersToSkip = processErrorCountAlertRules(monitor,
				LogicalDisk::checkErrorCountCondition,
				LogicalDisk::checkHighErrorCountCondition);

		// Process static alert rules
		processStaticAlertRules(monitor, logicalDisk, parametersToSkip);
	}

	@Override
	public void visit(Lun lun) {

		// Process instance alert rules
		final Set<String> parametersToSkip = processLunInstanceAlertRules(monitor);

		// Process static alert rules
		processStaticAlertRules(monitor, lun, parametersToSkip);
	}

	@Override
	public void visit(Memory memory) {

		// Process instance alert rules
		final Set<String> parametersToSkip = processErrorCountAlertRules(monitor,
				Memory::checkErrorCountCondition,
				Memory::checkHighErrorCountCondition);

		// Process static alert rules
		processStaticAlertRules(monitor, memory, parametersToSkip);
	}

	@Override
	public void visit(NetworkCard networkCard) {

		// Process instance alert rules
		final Set<String> parametersToSkip = processNetworkCardInstanceAlertRules(monitor);

		// Process static alert rules
		processStaticAlertRules(monitor, networkCard, parametersToSkip);
	}

	@Override
	public void visit(OtherDevice otherDevice) {

		// Process instance alert rules
		final Set<String> parametersToSkip = processOtherDeviceInstanceAlertRules(monitor);

		// Process static alert rules
		processStaticAlertRules(monitor, otherDevice, parametersToSkip);
	}

	@Override
	public void visit(PhysicalDisk physicalDisk) {

		// Process instance alert rules
		final Set<String> parametersToSkip = processErrorCountAlertRules(monitor,
				(mo, conditions) -> PhysicalDisk.checkErrorCountCondition(mo, ERROR_COUNT_PARAMETER, conditions),
				(mo, conditions) -> PhysicalDisk.checkHighErrorCountCondition(mo, ERROR_COUNT_PARAMETER, conditions));

		// Process static alert rules
		processStaticAlertRules(monitor, physicalDisk, parametersToSkip);
	}

	@Override
	public void visit(PowerSupply powerSupply) {

		// Process static alert rules
		processStaticAlertRules(monitor, powerSupply);
	}

	@Override
	public void visit(TapeDrive tapeDrive) {

		// Process instance alert rules
		final Set<String> parametersToSkip = processErrorCountAlertRules(monitor,
				TapeDrive::checkErrorCountCondition,
				TapeDrive::checkHighErrorCountCondition);

		// Process static alert rules
		processStaticAlertRules(monitor, tapeDrive, parametersToSkip);
	}

	@Override
	public void visit(Temperature temperature) {

		// Process instance alert rules
		final Set<String> parametersToSkip = processTemperatureAlertRules(monitor);

		// Process static alert rules
		processStaticAlertRules(monitor, temperature, parametersToSkip);
	}

	@Override
	public void visit(Voltage voltage) {

		// Process instance alert rules
		final Set<String> parametersToSkip = processVoltageAlertRules(monitor);

		// Process static alert rules
		processStaticAlertRules(monitor, voltage, parametersToSkip);
	}

	@Override
	public void visit(Robotics robotics) {

		// Process instance alert rules
		final Set<String> parametersToSkip = processErrorCountAlertRules(monitor,
				Robotics::checkErrorCountCondition,
				Robotics::checkHighErrorCountCondition);

		// Process static alert rules
		processStaticAlertRules(monitor, robotics, parametersToSkip);
	}

	@Override
	public void visit(Vm vm) {

		// Process the static alert rules
		processStaticAlertRules(monitor, vm);
	}

	@Override
	public void visit(Gpu gpu) {

		// Process the GPU Instance Alert Rules
		final Set<String> parametersToSkip = processGpuInstanceAlertRules(monitor);

		// Process the static alert rules
		processStaticAlertRules(monitor, gpu, parametersToSkip);
	}

	/**
	 * Process the alert rules for the given monitor, if some alert rules are already defined for a parameter, means they are top priority so we
	 * will skip them
	 *
	 * @param monitor          The monitor on which we set the parameter alert rules
	 * @param metaMonitor      The meta monitor instance, e.g. {@link Fan} instance, from which we want to extract the static alert rules
	 * @param parametersToSkip The parameters to skip (priority parameters)
	 */
	void processStaticAlertRules(final Monitor monitor, final IMetaMonitor metaMonitor, final Set<String> parametersToSkip) {

		metaMonitor.getStaticAlertRules().entrySet()
			.stream()
			.filter(entry -> !parametersToSkip.contains(entry.getKey()))
			.forEach(entry -> monitor.addAlertRules(entry.getKey(), entry.getValue()
					.stream()
					.map(AlertRule::copy)
					.collect(Collectors.toCollection(ArrayList::new))));

	}

	/**
	 * Process the alert rules for the given monitor
	 *
	 * @param monitor     The monitor on which we set the parameter alert rules
	 * @param metaMonitor The meta monitor instance, e.g. {@link Fan} instance, from which we want to extract the static alert rules
	 */
	void processStaticAlertRules(final Monitor monitor, final IMetaMonitor metaMonitor) {
		processStaticAlertRules(monitor, metaMonitor, Collections.emptySet());
	}

	/**
	 * Process the voltage instance alert rules
	 *
	 * @param monitor The monitor we wish to process the alert rules
	 * @return set of parameters with alert rules otherwise empty list
	 */
	Set<String> processVoltageAlertRules(Monitor monitor) {

		final Map<String, String> metadata = monitor.getMetadata();

		// warning threshold and alarm threshold on the error count
		final Double upperThreshold = NumberHelper.parseDouble(metadata.get(UPPER_THRESHOLD), null);
		final Double lowerThreshold = NumberHelper.parseDouble(metadata.get(LOWER_THRESHOLD), null);

		return updateVoltageInstanceAlertRules(monitor, lowerThreshold, upperThreshold);

	}

	/**
	 * Build the Fan instance alert rules
	 *
	 * @param monitor          The monitor we wish to process the alert rules
	 * @param parameterName    The name of the parameter we wish to build the alert rules
	 * @param warningThreshold The warning threshold
	 * @param alarmThreshold   The alarm threshold
	 * @return Singleton set of the updated parameter or empty
	 */
	Set<String> updateFanInstanceSpeedAlertRules(final Monitor monitor, final String parameterName, Double warningThreshold, Double alarmThreshold) {

		final BiFunction<Monitor, Set<AlertCondition>, AlertDetails> outOfRangeSpeedChecker =
				(mo, conditions) -> Fan.checkOutOfRangeSpeedCondition(mo, parameterName, conditions);
		final BiFunction<Monitor, Set<AlertCondition>, AlertDetails> lowSpeedChecker =
				(mo, conditions) -> Fan.checkLowSpeedCondition(mo, parameterName, conditions);
		final BiFunction<Monitor, Set<AlertCondition>, AlertDetails> zeroSpeedConditionChecker =
				(mo, conditions) -> Fan.checkZeroSpeedCondition(mo, parameterName, conditions);

		if (warningThreshold != null && alarmThreshold != null) {

			// Check that warning is above alarm
			if (warningThreshold < alarmThreshold) {
				var swap = warningThreshold;
				warningThreshold = alarmThreshold;
				alarmThreshold = swap;
			}

		} else if (alarmThreshold != null) {
			// Only alarm threshold is provided. Warning threshold will be 110% of alarm threshold
			warningThreshold = alarmThreshold * 1.1;
		} else if (warningThreshold != null) {
			// Only Warning thresholds is provided. Alarm threshold will be 90% of warning threshold
			alarmThreshold = warningThreshold * 0.9;
		} else {
			// Means the static rules will take over
			return Collections.emptySet();
		}

		final Set<AlertCondition> alarmConditions = AlertConditionsBuilder.newInstance()
				.gte(0D)
				.lte(alarmThreshold)
				.build();

		final Set<AlertCondition> warningConditions = AlertConditionsBuilder.newInstance()
				.lte(warningThreshold)
				.build();


		// Get the good checker producing the consistent problem, consequence and recommended action
		final var alarmChecker = alarmThreshold > 0 ? lowSpeedChecker : zeroSpeedConditionChecker;

		// Create the alert rule
		final AlertRule alarmAlertRule = new AlertRule(alarmChecker, alarmConditions, Severity.ALARM, AlertRuleType.INSTANCE);
		final AlertRule warnAlertRule = new AlertRule(outOfRangeSpeedChecker, warningConditions, Severity.WARN, AlertRuleType.INSTANCE);

		// Add them to the monitor, they will be inserted only if they are updated.
		monitor.addAlertRules(parameterName, new ArrayList<>(Arrays.asList(warnAlertRule, alarmAlertRule)));

		return Collections.singleton(parameterName);
	}

	/**
	 * Build the voltage instance alert rules
	 *
	 * @param monitor          The monitor we wish to process the alert rules
	 * @param lowerThreshold   The voltage lower threshold
	 * @param upperThreshold   The voltage upper threshold
	 * @return Singleton list of the updated parameter or empty
	 */
	Set<String> updateVoltageInstanceAlertRules(final Monitor monitor, Double lowerThreshold, Double upperThreshold) {

		final AlertRule alertRule1;
		final AlertRule alertRule2;
		if (lowerThreshold != null && upperThreshold != null) {

			// Check that warning is above alarm
			if (lowerThreshold > upperThreshold) {
				var swap = lowerThreshold;
				lowerThreshold = upperThreshold;
				upperThreshold = swap;
			}

			final Set<AlertCondition> alarm1Conditions = AlertConditionsBuilder.newInstance()
					.lte(lowerThreshold)
					.build();
			final Set<AlertCondition> alarm2Conditions = AlertConditionsBuilder.newInstance()
					.gte(upperThreshold)
					.build();

			// Create the alert rule
			alertRule1 = new AlertRule(Voltage::checkVoltageOutOfRangeCondition, alarm1Conditions, Severity.ALARM, AlertRuleType.INSTANCE);
			alertRule2 = new AlertRule(Voltage::checkVoltageOutOfRangeCondition, alarm2Conditions, Severity.ALARM, AlertRuleType.INSTANCE);

		} else if (upperThreshold != null) {
			// The upper threshold becomes a warning
			double warningThreshold = upperThreshold;
			double alarmThreshold = upperThreshold * 1.1;
			final Set<AlertCondition> warningConditions;
			final Set<AlertCondition> alarmConditions;

			if (warningThreshold > 0) {
				warningConditions = AlertConditionsBuilder.newInstance()
						.gte(warningThreshold)
						.build();
				alarmConditions = AlertConditionsBuilder.newInstance()
						.gte(alarmThreshold)
						.build();
			} else {
				warningConditions = AlertConditionsBuilder.newInstance()
						.lte(warningThreshold)
						.build();
				alarmConditions = AlertConditionsBuilder.newInstance()
						.lte(alarmThreshold)
						.build();
			}

			// Create the alert rule
			alertRule1 = new AlertRule(Voltage::checkVoltageOutOfRangeCondition, warningConditions, Severity.WARN, AlertRuleType.INSTANCE);
			alertRule2 = new AlertRule(Voltage::checkVoltageOutOfRangeCondition, alarmConditions, Severity.ALARM, AlertRuleType.INSTANCE);

		} else if (lowerThreshold != null) {
			// The upper threshold becomes a warning
			double warningThreshold = lowerThreshold;
			double alarmThreshold = lowerThreshold * 0.9;

			final Set<AlertCondition> warningConditions;
			final Set<AlertCondition> alarmConditions;

			if (warningThreshold > 0) {
				warningConditions = AlertConditionsBuilder.newInstance()
						.lte(warningThreshold)
						.gte(0D)
						.build();
				alarmConditions = AlertConditionsBuilder.newInstance()
						.lte(alarmThreshold)
						.gte(0D)
						.build();
			} else {
				warningConditions = AlertConditionsBuilder.newInstance()
						.gte(warningThreshold)
						.lte(0D)
						.build();
				alarmConditions = AlertConditionsBuilder.newInstance()
						.gte(alarmThreshold)
						.lte(0D)
						.build();
			}

			// Create the alert rule
			alertRule1 = new AlertRule(Voltage::checkVoltageOutOfRangeCondition, warningConditions, Severity.WARN, AlertRuleType.INSTANCE);
			alertRule2 = new AlertRule(Voltage::checkVoltageOutOfRangeCondition, alarmConditions, Severity.ALARM, AlertRuleType.INSTANCE);

		} else {
			// Means the static rules will take over
			return Collections.emptySet();
		}

		// Add them to the monitor, they will be inserted only if they are updated.
		monitor.addAlertRules(VOLTAGE_PARAMETER, new ArrayList<>(Arrays.asList(alertRule1, alertRule2)));

		return Collections.singleton(VOLTAGE_PARAMETER);
	}

	/**
	 * Process the temperature instance alert rules
	 *
	 * @param monitor The monitor we wish to process the alert rules
	 * @return list of parameters with alert rules otherwise empty list
	 */
	Set<String> processTemperatureAlertRules(Monitor monitor) {

		final Map<String, String> metadata = monitor.getMetadata();

		// warning threshold and alarm threshold on the error count
		final Double warningThreshold = NumberHelper.parseDouble(metadata.get(WARNING_THRESHOLD), null);
		final Double alarmThreshold = NumberHelper.parseDouble(metadata.get(ALARM_THRESHOLD), null);

		return updateWarningToAlarmAlertRules(
				monitor,
				TEMPERATURE_PARAMETER,
				warningThreshold,
				alarmThreshold,
				Temperature::checkTemperatureAbnormallyHighCondition,
				Temperature::checkTemperatureCriticallyHighCondition);
	}

	/**
	 * Process the error count instance alert rules
	 *
	 * @param monitor The monitor we wish to process the alert rules
	 * @return list of parameters with alert rules otherwise empty list
	 */
	Set<String> processErrorCountAlertRules(Monitor monitor,
			final BiFunction<Monitor, Set<AlertCondition>, AlertDetails> warnConditionsChecker,
			final BiFunction<Monitor, Set<AlertCondition>, AlertDetails> alarmConditionsChecker) {

		final Map<String, String> metadata = monitor.getMetadata();

		// warning threshold and alarm threshold on the error count
		final Double warningThreshold = NumberHelper.parseDouble(metadata.get(ERROR_COUNT_WARNING_THRESHOLD), null);
		final Double alarmThreshold = NumberHelper.parseDouble(metadata.get(ERROR_COUNT_ALARM_THRESHOLD), null);

		return updateWarningToAlarmAlertRules(
				monitor,
				ERROR_COUNT_PARAMETER,
				warningThreshold,
				alarmThreshold,
				warnConditionsChecker,
				alarmConditionsChecker);
	}

	/**
	 * Process the Cpu instance alert rules set by the connector
	 *
	 * @param monitor The CPU monitor from which we extract the warning and alarm threshold
	 * @return list of parameters with alert rules otherwise empty list
	 */
	Set<String> processCpuInstanceAlertRules(Monitor monitor) {

		final Map<String, String> metadata = monitor.getMetadata();

		final Double correctedErrorWarningThreshold = NumberHelper.parseDouble(metadata.get(CORRECTED_ERROR_WARNING_THRESHOLD), null);
		final Double correctedErrorAlarmThreshold = NumberHelper.parseDouble(metadata.get(CORRECTED_ERROR_ALARM_THRESHOLD), null);

		return updateWarningToAlarmAlertRules(monitor,
				CORRECTED_ERROR_COUNT_PARAMETER,
				correctedErrorWarningThreshold,
				correctedErrorAlarmThreshold,
				Cpu::checkCorrectedFiewErrorCountCondition,
				Cpu::checkCorrectedLargeErrorCountCondition);
	}

	/**
	 * Process the LUN instance alert rules set by the connector
	 *
	 * @param monitor The LUN monitor from which we extract the warning or the alarm threshold
	 * @return set of parameters with alert rules otherwise empty list
	 */
	Set<String> processLunInstanceAlertRules(Monitor monitor) {

		final Map<String, String> metadata = monitor.getMetadata();

		final Double availablePathWarning = NumberHelper.parseDouble(metadata.get(AVAILABLE_PATH_WARNING), null);

		if (availablePathWarning != null && availablePathWarning > 0) {
			final Set<AlertCondition> warningConditions = AlertConditionsBuilder.newInstance()
					.lte(availablePathWarning)
					.build();

			final AlertRule warnAlertRule = new AlertRule(Lun::checkLowerAvailablePathCountCondition, warningConditions, Severity.WARN, AlertRuleType.INSTANCE);

			// Add to the monitor, it will be inserted only if updated.
			monitor.addAlertRules(AVAILABLE_PATH_COUNT_PARAMETER, new ArrayList<>(Collections.singletonList(warnAlertRule)));

			return Collections.singleton(AVAILABLE_PATH_COUNT_PARAMETER);
		}

		return Collections.emptySet();
	}

	/**
	 * Process the network card instance alert rules set by the connector
	 *
	 * @param monitor The network card monitor from which we extract the warning and the alarm threshold
	 * @return list of parameters with alert rules otherwise empty list
	 */
	Set<String> processNetworkCardInstanceAlertRules(Monitor monitor) {

		final Map<String, String> metadata = monitor.getMetadata();

		Double errorPercentWarningThreshold = NumberHelper.parseDouble(metadata.get(ERROR_PERCENT_WARNING_THRESHOLD), null);
		Double errorPercentAlarmThreshold = NumberHelper.parseDouble(metadata.get(ERROR_PERCENT_ALARM_THRESHOLD), null);

		if (!isValidErrorPercentThresholdValue(errorPercentWarningThreshold) || !isValidErrorPercentThresholdValue(errorPercentAlarmThreshold)) {
			errorPercentWarningThreshold = 20D;
			errorPercentAlarmThreshold = 30D;
		}

		return updateWarningToAlarmAlertRules(monitor,
				ERROR_PERCENT_PARAMETER,
				errorPercentWarningThreshold,
				errorPercentAlarmThreshold,
				NetworkCard::checkErrorPercentWarnCondition,
				NetworkCard::checkErrorPercentAlarmCondition);
	}

	/**
	 * Process the OtherDevice instance alert rules set by the connector
	 *
	 * @param monitor The OtherDevice monitor from which we extract the warning and the alarm threshold
	 * @return set of parameters with alert rules otherwise empty list
	 */
	Set<String> processOtherDeviceInstanceAlertRules(Monitor monitor) {

		final Map<String, String> metadata = monitor.getMetadata();

		final Double valueWarningThreshold = NumberHelper.parseDouble(metadata.get(VALUE_WARNING_THRESHOLD), null);
		final Double valueAlarmThreshold = NumberHelper.parseDouble(metadata.get(VALUE_ALARM_THRESHOLD), null);

		final Set<String> parametersWithAlertRules = new HashSet<>();
		parametersWithAlertRules.addAll(updateWarningToAlarmEnhancedAlertRules(monitor,
				VALUE_PARAMETER,
				valueWarningThreshold, valueAlarmThreshold,
				OtherDevice::checkValueWarnCondition,
				OtherDevice::checkValueAlarmCondition));

		final Double usageCountWarningThreshold = NumberHelper.parseDouble(metadata.get(USAGE_COUNT_WARNING_THRESHOLD), null);
		final Double usageCountAlarmThreshold = NumberHelper.parseDouble(metadata.get(USAGE_COUNT_ALARM_THRESHOLD), null);

		parametersWithAlertRules.addAll(updateWarningToAlarmEnhancedAlertRules(monitor,
				USAGE_COUNT_PARAMETER,
				usageCountWarningThreshold, usageCountAlarmThreshold,
				OtherDevice::checkUsageCountWarnCondition,
				OtherDevice::checkUsageCountAlarmCondition));

		return parametersWithAlertRules;
	}

	/**
	 * Process the GPU instance alert rules set by the connector
	 *
	 * @param monitor The GPU monitor from which we extract the warning and alarm threshold
	 * @return list of parameters with alert rules otherwise empty list
	 */
	Set<String> processGpuInstanceAlertRules(Monitor monitor) {

		return Collections.emptySet();
	}

	/**
	 * Update the warning to alarm enhanced instance alert rules
	 *
	 * @param monitor          The monitor we wish to process the alert rules
	 * @param parameterName    The name of the parameter we wish to build the alert rules
	 * @param warningThreshold The warning threshold
	 * @param alarmThreshold   The alarm threshold
	 * @return Singleton list of the updated parameter or empty
	 */
	Set<String> updateWarningToAlarmEnhancedAlertRules(final Monitor monitor, final String parameterName,
			Double warningThreshold, Double alarmThreshold,
			final BiFunction<Monitor, Set<AlertCondition>, AlertDetails> warnConditionsChecker,
			final BiFunction<Monitor, Set<AlertCondition>, AlertDetails> alarmConditionsChecker) {

		if (warningThreshold != null && alarmThreshold != null) {

			// Check that warning is above alarm
			if (warningThreshold > alarmThreshold) {
				var swap = warningThreshold;
				warningThreshold = alarmThreshold;
				alarmThreshold = swap;
			}

		} else if (alarmThreshold != null) {
			// Only alarm threshold is provided. Warning threshold will be 90% of alarm threshold
			warningThreshold = alarmThreshold * 0.9;
		} else if (warningThreshold != null) {
			// Only warning thresholds is provided. Alarm threshold will be 110% of warning threshold
			alarmThreshold = warningThreshold * 1.1;
		} else {
			// Means the static rules will take over
			return Collections.emptySet();
		}

		final Set<AlertCondition> warningConditions = AlertConditionsBuilder.newInstance()
				.gte(warningThreshold)
				.build();
		final Set<AlertCondition> alarmConditions = AlertConditionsBuilder.newInstance()
				.gte(alarmThreshold)
				.build();

		// Create the alert rule
		final AlertRule alarmAlertRule = new AlertRule(alarmConditionsChecker, alarmConditions, Severity.ALARM, AlertRuleType.INSTANCE);
		final AlertRule warnAlertRule = new AlertRule(warnConditionsChecker, warningConditions, Severity.WARN, AlertRuleType.INSTANCE);

		// Add them to the monitor, they will be inserted only if they are updated.
		monitor.addAlertRules(parameterName, new ArrayList<>(Arrays.asList(warnAlertRule, alarmAlertRule)));

		return Collections.singleton(parameterName);
	}

	/**
	 * Check if the given threshold value is valid
	 *
	 * @param value The value we wish to check
	 * @return <code>true</code> if the value is valid otherwise false
	 */
	static boolean isValidErrorPercentThresholdValue(Double value) {
		// Value is null means there is no threshold, so it a valid value
		if (value == null) {
			return true;
		}

		return value >= 0;
	}

	/**
	 * Build alert rules <em>WARN = (value >= warningThreshold & ALARM < alarmThreshold)</em> and <em>ALARM = (value >= alarmThreshold)</em>
	 *
	 * @param monitor                The monitor we wish to build the alert rules
	 * @param parameterName          The name of the parameter we wish to build the alert rules
	 * @param warningThreshold       The warning threshold value
	 * @param alarmThreshold         The alarm threshold value
	 * @param warnConditionsChecker  The warning conditions checker function
	 * @param alarmConditionsChecker The alarm conditions checker function
	 * @return Singleton set of the updated parameter
	 */
	Set<String> updateWarningToAlarmAlertRules(final Monitor monitor, final String parameterName,
			Double warningThreshold, Double alarmThreshold,
			final BiFunction<Monitor, Set<AlertCondition>, AlertDetails> warnConditionsChecker,
			final BiFunction<Monitor, Set<AlertCondition>, AlertDetails> alarmConditionsChecker) {

		if (warningThreshold != null && alarmThreshold != null) {

			// Check that warning is lower than alarm
			if (warningThreshold > alarmThreshold) {
				var swap = warningThreshold;
				warningThreshold = alarmThreshold;
				alarmThreshold = swap;
			}

			final Set<AlertCondition> warningConditions = AlertConditionsBuilder.newInstance()
					.gte(warningThreshold)
					.build();
			final Set<AlertCondition> alarmConditions = AlertConditionsBuilder.newInstance()
					.gte(alarmThreshold)
					.build();

			final AlertRule warnAlertRule = new AlertRule(warnConditionsChecker, warningConditions, Severity.WARN, AlertRuleType.INSTANCE);
			final AlertRule alarmAlertRule = new AlertRule(alarmConditionsChecker, alarmConditions, Severity.ALARM, AlertRuleType.INSTANCE);

			// Add them to the monitor, they will be inserted only if they are updated.
			monitor.addAlertRules(parameterName, new ArrayList<>(Arrays.asList(warnAlertRule, alarmAlertRule)));

			return Collections.singleton(parameterName);

		} else if (warningThreshold != null) {

			final Set<AlertCondition> warningConditions = AlertConditionsBuilder.newInstance()
					.gte(warningThreshold)
					.build();
			final AlertRule warnAlertRule = new AlertRule(warnConditionsChecker, warningConditions, Severity.WARN, AlertRuleType.INSTANCE);

			// Add to the monitor, it will be inserted only if updated.
			monitor.addAlertRules(parameterName, new ArrayList<>(Collections.singletonList(warnAlertRule)));

			return Collections.singleton(parameterName);

		} else if (alarmThreshold != null) {

			final Set<AlertCondition> alarmConditions = AlertConditionsBuilder.newInstance()
					.gte(alarmThreshold)
					.build();
			final AlertRule alarmAlertRule = new AlertRule(alarmConditionsChecker, alarmConditions, Severity.ALARM, AlertRuleType.INSTANCE);

			// Add to the monitor, it will be inserted only if updated.
			monitor.addAlertRules(parameterName, new ArrayList<>(Collections.singletonList(alarmAlertRule)));

			return Collections.singleton(parameterName);
		}

		return Collections.emptySet();
	}

	/**
	 * Process Fan alert rules using the monitor instance metadata
	 *
	 * @param monitor The monitor we wish to process
	 * @return list of parameters with alert rules otherwise empty list
	 */
	Set<String> processFanInstanceAlertRules(final Monitor monitor) {

		final Map<String, String> metadata = monitor.getMetadata();

		// warning threshold and alarm threshold on speed
		final Double warningThreshold = NumberHelper.parseDouble(metadata.get(WARNING_THRESHOLD), null);
		final Double alarmThreshold = NumberHelper.parseDouble(metadata.get(ALARM_THRESHOLD), null);

		final Set<String> parametersWithAlertRules = new HashSet<>();
		parametersWithAlertRules.addAll(updateFanInstanceSpeedAlertRules(monitor, SPEED_PARAMETER, warningThreshold, alarmThreshold));

		// percent warning threshold and percent alarm threshold on speed percentage
		final Double percentWarningThreshold = NumberHelper.parseDouble(metadata.get(PERCENT_WARNING_THRESHOLD), null);
		final Double percentAlarmThreshold = NumberHelper.parseDouble(metadata.get(PERCENT_ALARM_THRESHOLD), null);
		parametersWithAlertRules.addAll(updateFanInstanceSpeedAlertRules(monitor, SPEED_PERCENT_PARAMETER, percentWarningThreshold, percentAlarmThreshold));

		return parametersWithAlertRules;
	}

}
