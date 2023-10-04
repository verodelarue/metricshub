package com.sentrysoftware.matrix.sustainability;

import com.sentrysoftware.matrix.telemetry.TelemetryManager;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class NetworkPowerAndEnergyEstimator extends HardwarePowerAndEnergyEstimator {

	public NetworkPowerAndEnergyEstimator(final TelemetryManager telemetryManager) {
		super(telemetryManager);
	}

	/**
	 * Estimates the power consumption of the Network monitor
	 * @return Double
	 */
	@Override
	Double estimatePower() {
		// TODO
		return null;
	}

	/**
	 * Estimates the energy consumption of the Network monitor
	 * @return Double
	 */
	@Override
	Double estimateEnergy() {
		// TODO
		return null;
	}
}
