package org.sentrysoftware.metricshub.engine.strategy.detection;

/*-
 * ╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲
 * MetricsHub Engine
 * ჻჻჻჻჻჻
 * Copyright 2023 - 2024 Sentry Software
 * ჻჻჻჻჻჻
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * ╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱
 */

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.sentrysoftware.metricshub.engine.client.ClientsExecutor;
import org.sentrysoftware.metricshub.engine.configuration.HostConfiguration;
import org.sentrysoftware.metricshub.engine.connector.model.Connector;
import org.sentrysoftware.metricshub.engine.connector.model.ConnectorStore;
import org.sentrysoftware.metricshub.engine.telemetry.TelemetryManager;

@Slf4j
@NoArgsConstructor
public class ConnectorSelection extends AbstractConnectorProcessor {

	public ConnectorSelection(
		@NonNull final TelemetryManager telemetryManager,
		@NonNull final ClientsExecutor clientsExecutor
	) {
		super(telemetryManager, clientsExecutor);
	}

	@Override
	public List<ConnectorTestResult> run() {
		final HostConfiguration hostConfiguration = telemetryManager.getHostConfiguration();
		if (hostConfiguration == null) {
			log.error("Empty host configuration, aborting detection job.");
			return Collections.emptyList();
		}

		final String hostname = hostConfiguration.getHostname();
		log.debug("Hostname {} - Start connector selection.", hostname);

		final ConnectorStore telemetryManagerConnectorStore = telemetryManager.getConnectorStore();
		if (telemetryManagerConnectorStore == null) {
			log.error("Hostname {} - No connectorStore found. Stopping detection operation.", hostname);
			return Collections.emptyList();
		}

		final Map<String, Connector> connectorStore = telemetryManagerConnectorStore.getStore();
		if (connectorStore == null) {
			log.error("Hostname {} - No connectorStore found. Stopping detection operation.", hostname);
			return Collections.emptyList();
		}

		final Set<String> caseInsensitiveSelectedConnectors = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		final Set<String> caseSensitiveSelectedConnectors = hostConfiguration.getSelectedConnectors();

		if (caseSensitiveSelectedConnectors != null) {
			caseInsensitiveSelectedConnectors.addAll(caseSensitiveSelectedConnectors);
		}

		if (caseInsensitiveSelectedConnectors == null || caseInsensitiveSelectedConnectors.isEmpty()) {
			log.error(
				"Hostname {} - No connectors have been selected for the detection. Stopping discovery operation.",
				hostname
			);
			return Collections.emptyList();
		}

		return runAllConnectorsDetectionCriteria(
			connectorStore
				.values()
				.stream()
				.filter(connector -> isConnectorContainedInSet(connector, caseInsensitiveSelectedConnectors)),
			hostConfiguration
		)
			.collect(Collectors.toList()); //NOSONAR
	}
}
