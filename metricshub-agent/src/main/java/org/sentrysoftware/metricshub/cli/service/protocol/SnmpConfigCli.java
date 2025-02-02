package org.sentrysoftware.metricshub.cli.service.protocol;

/*-
 * ╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲
 * MetricsHub Agent
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

import lombok.Data;
import org.sentrysoftware.metricshub.cli.service.converter.SnmpPrivacyConverter;
import org.sentrysoftware.metricshub.cli.service.converter.SnmpVersionConverter;
import org.sentrysoftware.metricshub.engine.configuration.IConfiguration;
import org.sentrysoftware.metricshub.engine.configuration.SnmpConfiguration;
import picocli.CommandLine.Option;

/**
 * This class is used by MetricsHubCliService to configure Snmp protocol when using the MetricsHub CLI.
 * It create the engine's {@link SnmpConfiguration} object that is used to monitor a specific resource.
 */
@Data
public class SnmpConfigCli implements IProtocolConfigCli {

	/**
	 * Default timeout in seconds for an SNMP operation
	 */
	public static final int DEFAULT_TIMEOUT = 30;

	@Option(
		names = "--snmp",
		order = 1,
		defaultValue = "1",
		paramLabel = "VERSION",
		description = "Enables SNMP protocol version: 1, 2, 3-md5, 3-sha or 3-noauth",
		converter = SnmpVersionConverter.class
	)
	SnmpConfiguration.SnmpVersion snmpVersion;

	@Option(
		names = { "--snmp-community", "--community" },
		order = 2,
		paramLabel = "COMMUNITY",
		defaultValue = "public",
		description = "Community string for SNMP version 1 and 2 (default: ${DEFAULT-VALUE})"
	)
	String community;

	@Option(
		names = "--snmp-username",
		order = 3,
		paramLabel = "USER",
		description = "Username for SNMP version 3 with MD5 or SHA"
	)
	String username;

	@Option(
		names = "--snmp-password",
		order = 4,
		paramLabel = "P4SSW0RD",
		description = "Password for SNMP version 3 with MD5 or SHA",
		interactive = true,
		arity = "0..1"
	)
	char[] password;

	@Option(
		names = "--snmp-privacy",
		order = 5,
		paramLabel = "DES|AES",
		description = "Privacy (encryption type) for SNMP version 3 (DES, AES, or none)",
		converter = SnmpPrivacyConverter.class
	)
	SnmpConfiguration.Privacy privacy;

	@Option(
		names = "--snmp-privacy-password",
		order = 6,
		paramLabel = "P4SSW0RD",
		description = "Privacy (encryption) password",
		interactive = true,
		arity = "0..1"
	)
	char[] privacyPassword;

	@Option(
		names = "--snmp-port",
		order = 7,
		paramLabel = "PORT",
		defaultValue = "161",
		description = "Port of the SNMP agent (default: ${DEFAULT-VALUE})"
	)
	int port;

	@Option(
		names = "--snmp-timeout",
		order = 8,
		paramLabel = "TIMEOUT",
		defaultValue = "" + DEFAULT_TIMEOUT,
		description = "Timeout in seconds for SNMP operations (default: ${DEFAULT-VALUE} s)"
	)
	long timeout;

	@Option(
		names = { "--context-name" },
		order = 9,
		paramLabel = "CONTEXT_NAME",
		description = "Snmp V3 protocol context name"
	)
	String contextName;

	/**
	 * This method creates an {@link SnmpConfiguration} for a given username and a given password
	 *
	 * @param defaultUsername Username specified at the top level of the CLI (with the --username option)
	 * @param defaultPassword Password specified at the top level of the CLI (with the --password option)
	 * @return a {@link SnmpConfiguration} instance corresponding to the options specified by the user in the CLI
	 */
	@Override
	public IConfiguration toProtocol(final String defaultUsername, final char[] defaultPassword) {
		return SnmpConfiguration
			.builder()
			.version(snmpVersion)
			.community(community)
			.username(username == null ? defaultUsername : username)
			.password(username == null ? defaultPassword : password)
			.privacy(privacy)
			.privacyPassword(privacyPassword)
			.port(port)
			.timeout(timeout)
			.contextName(contextName)
			.build();
	}
}
