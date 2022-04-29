package com.sentrysoftware.hardware.agent.configuration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.sentrysoftware.hardware.agent.dto.ErrorCode;
import com.sentrysoftware.hardware.agent.dto.HardwareTargetDTO;
import com.sentrysoftware.hardware.agent.dto.HostConfigurationDTO;
import com.sentrysoftware.hardware.agent.dto.MultiHostsConfigurationDTO;
import com.sentrysoftware.hardware.agent.dto.protocol.IProtocolConfigDTO;
import com.sentrysoftware.hardware.agent.dto.protocol.SnmpProtocolDTO;
import com.sentrysoftware.hardware.agent.exception.BusinessException;
import com.sentrysoftware.hardware.agent.security.PasswordEncrypt;
import com.sentrysoftware.matrix.common.helpers.NetworkHelper;
import com.sentrysoftware.matrix.common.helpers.ResourceHelper;
import com.sentrysoftware.matrix.connector.ConnectorStore;
import com.sentrysoftware.matrix.connector.model.Connector;
import com.sentrysoftware.matrix.connector.model.monitor.job.source.Source;
import com.sentrysoftware.matrix.engine.EngineConfiguration;
import com.sentrysoftware.matrix.engine.protocol.IProtocolConfiguration;
import com.sentrysoftware.matrix.engine.target.TargetType;
import com.sentrysoftware.matrix.model.monitoring.HostMonitoringFactory;
import com.sentrysoftware.matrix.model.monitoring.IHostMonitoring;
import com.sentrysoftware.matrix.security.SecurityManager;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class ConfigHelper {

	public static final Path DEFAULT_OUTPUT_DIRECTORY = getSubDirectory("logs", true);
	private static final String TIMEOUT_ERROR = "Timeout value is invalid for hostname %s for protocol %s. Host will not be monitored.";
	private static final String PORT_ERROR = "Invalid port configured for hostname %s for protocol %s. Host will not be monitored.";
	private static final String USERNAME_ERROR = "No username configured for hostname %s for protocol %s. Host will not be monitored.";
	private static final Predicate<String> INVALID_STRING_CHECKER = attr -> attr == null || attr.isBlank();
	private static final Predicate<Integer> INVALID_PORT_CHECKER = attr -> attr == null || attr < 1 || attr > 65535;
	private static final Predicate<Long> INVALID_TIMEOUT_CHECKER = attr -> attr == null || attr < 0L;
	private static final String COMMAND_ERROR = "Command invalid for hostname: %s";
	
	/**
	 * Deserialize YAML configuration file.
	 * 
	 * @param <T>
	 *
	 * @param file YAML file
	 * @param type the value type to return
	 *
	 * @return new instance of type T
	 * 
	 * @throws IOException
	 *
	 */
	static <T> T deserializeYamlFile(final File file, final Class<T> type) throws IOException {

		// Since 2.13 use JsonMapper.builder().enable(...)
		return JsonMapper.builder(new YAMLFactory()).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false)
				.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS).build().readValue(file, type);

	}

	/**
	 * Validate the attribute against a predetermined test
	 * 
	 * @param attribute       value getting compared
	 * @param errorChecker    logic test comparing our value
	 * @param messageSupplier error message being generated
	 * @param errorCode       error code being generated
	 * @throws BusinessException
	 */
	private static <T> void validateAttribute(final T attribute, final Predicate<T> errorChecker,
			final Supplier<String> messageSupplier, final ErrorCode errorCode) throws BusinessException {

		if (errorChecker.test(attribute)) {
			log.error(messageSupplier.get());
			throw new BusinessException(errorCode, messageSupplier.get());
		}
	}

	/**
	 * Validate the given target information (hostname and targetType)
	 *
	 * @param targetType type of the target
	 * @param hostname   hostname of the target
	 * @throws BusinessException
	 */
	static void validateTarget(final TargetType targetType, final String hostname) throws BusinessException {

		validateAttribute(hostname, 
				INVALID_STRING_CHECKER, 
				() -> String.format("Invalid hostname: %s", hostname),
				ErrorCode.INVALID_HOSTNAME);

		validateAttribute(targetType, 
				Objects::isNull,
				() -> String.format("No target type configured for hostname: %s", hostname), 
				ErrorCode.NO_TARGET_TYPE);
	}

	/**
	 * Validate that the given protocol specified is valid for the selected
	 * connectors.
	 * 
	 * @param engineConfiguration
	 * @param connectorStore
	 */
	static void validateEngineConfiguration(@NonNull final EngineConfiguration engineConfiguration,
			@NonNull final Collection<Connector> connectorStore) throws BusinessException {
		if (engineConfiguration.getSelectedConnectors().isEmpty()) {
			return;
		}
		final Set<Connector> connectors = connectorStore.stream().filter(
				connector -> engineConfiguration.getSelectedConnectors().contains(connector.getCompiledFilename()))
				.collect(Collectors.toSet());

		final String hostname = engineConfiguration.getTarget().getHostname();
		final Set<Class<? extends Source>> acceptedSources = engineConfiguration
				.determineAcceptedSources(NetworkHelper.isLocalhost(hostname));

		for (Connector connector : connectors) {
			if (acceptedSources.stream().noneMatch(source -> connector.getSourceTypes().contains(source))) {
				String message = String.format(
						"Hostname %s - Selected connector %s couldn't be processed due to unsupported protocol",
						hostname, connector.getCompiledFilename());
				log.error(message);
				throw new BusinessException(ErrorCode.UNSUPPORTED_PROTOCOL, message);
			}
		}
	}

	/**
	 * Validate the given SNMP information (hostname, community, port and timeout)
	 *
	 * @param hostname    hostname of the target
	 * @param snmp   	  Snmp object of the target (configuration)
	 * @throws BusinessException
	 */
	static void validateSnmpInfo(final String hostname, SnmpProtocolDTO snmpDto)
			throws BusinessException {
		
		final String displayName = snmpDto.getVersion().getDisplayName();
		final int intVersion = snmpDto.getVersion().getIntVersion();
		
		if (intVersion != 3) {
			validateAttribute(snmpDto.getCommunity(), 
				attr -> attr == null || attr.length == 0, 
				() -> String.format(
						"No community string configured for hostname %s for protocol %s. Host will not be monitored.",
						hostname, displayName), 
				ErrorCode.NO_COMMUNITY_STRING);
		}
		
		validateAttribute(snmpDto.getPort(), 
				INVALID_PORT_CHECKER,
				() -> String.format(PORT_ERROR, hostname, displayName), 
				ErrorCode.INVALID_PORT);

		validateAttribute(snmpDto.getTimeout(), 
				INVALID_TIMEOUT_CHECKER,
				() -> String.format(TIMEOUT_ERROR, hostname, displayName), 
				ErrorCode.INVALID_TIMEOUT);

		if (intVersion == 3 && snmpDto.getVersion().getAuthType() != null) {
			validateAttribute(snmpDto.getUsername(), 
					INVALID_STRING_CHECKER, 
					() -> String.format(USERNAME_ERROR, hostname, displayName),
					ErrorCode.NO_USERNAME);
		}
	}

	/**
	 * Validate the given IPMI information (hostname, username, password and
	 * timeout)
	 *
	 * @param hostname hostname of the target
	 * @param username username of the target
	 * @param password password of the target
	 * @param timeout  timeout of the target
	 * @throws BusinessException
	 */
	static void validateIpmiInfo(final String hostname, final String username, final Long timeout)
			throws BusinessException {
		
		final String protocol = "IPMI";

		validateAttribute(username, 
				INVALID_STRING_CHECKER, 
				() -> String.format(USERNAME_ERROR, hostname, protocol),
				ErrorCode.NO_USERNAME);

		validateAttribute(timeout, 
				INVALID_TIMEOUT_CHECKER,
				() -> String.format(TIMEOUT_ERROR, hostname, protocol), 
				ErrorCode.INVALID_TIMEOUT);
	}

	/**
	 * Validate the given SSH information (hostname, username, timeout and
	 * sudoCommand)
	 *
	 * @param hostname    hostname of the target
	 * @param username    username of the target
	 * @param timeout     timeout of the target
	 * @param sudoCommand sudo command of the target
	 * @throws BusinessException
	 */
	static void validateSshInfo(final String hostname, final String username, final Long timeout)
			throws BusinessException {
		
		final String protocol = "SSH";

		validateAttribute(username, 
				INVALID_STRING_CHECKER, 
				() -> String.format(USERNAME_ERROR, hostname, protocol),
				ErrorCode.NO_USERNAME);

		validateAttribute(timeout, 
				INVALID_TIMEOUT_CHECKER,
				() -> String.format(TIMEOUT_ERROR, hostname, protocol), 
				ErrorCode.INVALID_TIMEOUT);
	}

	/**
	 * Validate the given WBEM information (timeout and port)
	 *
	 * @param hostname hostname of the target
	 * @param username username of the target
	 * @param timeout  timeout of the target
	 * @param port     port of the target
	 * @throws BusinessException
	 */
	static void validateWbemInfo(final String hostname, final String username, final Long timeout, final Integer port)
			throws BusinessException {

		final String protocol = "WBEM";
		
		validateAttribute(timeout, 
				INVALID_TIMEOUT_CHECKER,
				() -> String.format(TIMEOUT_ERROR, hostname, protocol), 
				ErrorCode.INVALID_TIMEOUT);

		validateAttribute(port, 
				INVALID_PORT_CHECKER,
				() -> String.format(PORT_ERROR, hostname, protocol), 
				ErrorCode.INVALID_PORT);
	
		validateAttribute(username, 
				INVALID_STRING_CHECKER, 
				() -> String.format(USERNAME_ERROR, hostname, protocol),
				ErrorCode.NO_USERNAME);	
	}
	
	/**
	 * Validate the given WMI information (username and namespace)
	 *
	 * @param hostname hostname of the target
	 * @param timeout  timeout of the target
	 * @throws BusinessException
	 */
	static void validateWmiInfo(final String hostname, final Long timeout) throws BusinessException {

		final String protocol = "WMI";
		
		validateAttribute(timeout, 
				INVALID_TIMEOUT_CHECKER,
				() -> String.format(TIMEOUT_ERROR, hostname, protocol), 
				ErrorCode.INVALID_TIMEOUT);
	}

	/**
	 * Validate the given HTTP information (hostname, timeout and port)
	 *
	 * @param hostname hostname of the target
	 * @param timeout  timeout of the target
	 * @param port     port of the target
	 * @throws BusinessException
	 */
	static void validateHttpInfo(final String hostname, final Long timeout, final Integer port)
			throws BusinessException {

		final String protocol = "HTTP";
		
		validateAttribute(timeout, 
				INVALID_TIMEOUT_CHECKER,
				() -> String.format(TIMEOUT_ERROR, hostname, protocol), 
				ErrorCode.INVALID_TIMEOUT);

		validateAttribute(port, 
				INVALID_PORT_CHECKER,
				() -> String.format(PORT_ERROR, hostname, protocol), 
				ErrorCode.INVALID_PORT);
	}

	/**
	 * Validate the given OS Command information (timeout and sudoCommand)
	 *
	 * @param hostname    hostname of the target
	 * @param timeout     timeout of the target
	 * @param sudoCommand sudo command of the target
	 * @throws BusinessException
	 */
	static void validateOsCommandInfo(final String hostname, final Long timeout) throws BusinessException {

		final String protocol = "OSCommand";
		
		validateAttribute(timeout, 
				INVALID_TIMEOUT_CHECKER,
				() -> String.format(TIMEOUT_ERROR, hostname, protocol), 
				ErrorCode.INVALID_TIMEOUT);
	}

	/**
	 * Build the {@link EngineConfiguration} instance from the given
	 * {@link HostConfigurationDTO}
	 *
	 * @param hostConfigurationDto User's configuration
	 * @param selectedConnectors   The connector names, the matrix engine should run
	 * @param excludedConnectors   The connector names, the matrix engine should skip
	 *
	 * @return The built {@link EngineConfiguration}.
	 */
	static EngineConfiguration buildEngineConfiguration(final HostConfigurationDTO hostConfigurationDto,
			final Set<String> selectedConnectors, final Set<String> excludedConnectors) {

		final HardwareTargetDTO target = hostConfigurationDto.getTarget();

		final Map<Class<? extends IProtocolConfiguration>, IProtocolConfiguration> protocolConfigurations = 
				new HashMap<>(
						Stream.of(
								hostConfigurationDto.getSnmp(),
								hostConfigurationDto.getSsh(),
								hostConfigurationDto.getHttp(),
								hostConfigurationDto.getWbem(),
								hostConfigurationDto.getWmi(),
								hostConfigurationDto.getOsCommand(),
								hostConfigurationDto.getIpmi(),
								hostConfigurationDto.getWinRM())
						.filter(Objects::nonNull)
						.map(IProtocolConfigDTO::toProtocol)
						.filter(Objects::nonNull)
						.collect(Collectors.toMap(IProtocolConfiguration::getClass, Function.identity())));

		return EngineConfiguration
			.builder()
			.operationTimeout(hostConfigurationDto.getOperationTimeout())
			.protocolConfigurations(protocolConfigurations)
			.selectedConnectors(selectedConnectors)
			.excludedConnectors(excludedConnectors)
			.target(target.toHardwareTarget())
			.sequential(Boolean.TRUE.equals(hostConfigurationDto.getSequential()))
			.build();
	}

	/**
	 * Return configured connector names. This method throws a BusinessException if
	 * we encounter an unknown connector
	 *
	 * @param acceptedConnectorNames Known connector names (connector compiled file names)
	 * @param configConnectors       user's selected or excluded connectors
	 * @param hostname               target hostname
	 *
	 * @return {@link Set} containing the selected connector names
	 * @throws BusinessException
	 */
	static Set<String> validateAndGetConnectors(final @NonNull Set<String> acceptedConnectorNames,
			final Set<String> configConnectors, final String hostname, final boolean isExcluded) throws BusinessException {

		if (configConnectors == null || configConnectors.isEmpty()) {
			return Collections.emptySet();
		}

		// Get unknown connectors
		final Set<String> unknownConnectors = configConnectors
				.stream()
				.filter(compiledFileName -> !acceptedConnectorNames.contains(compiledFileName))
				.collect(Collectors.toSet());

		// Check unknown connectors
		if (unknownConnectors.isEmpty()) {
			return configConnectors;
		}
		
		// Throw the bad configuration exception
		
		if (isExcluded) {
			String message = String.format(
					"Configured unknown selected connector(s): %s. Hostname: %s. This target will be monitored, but the unknown connectors will be ignored.",
					String.join(", ", unknownConnectors),
					hostname
			);
	
			log.error(message);
			configConnectors.removeAll(unknownConnectors);
			return configConnectors;
		} else {
			String message = String.format(
					"Configured unknown selected connector(s): %s. Hostname: %s. This target will not be monitored.",
					String.join(", ", unknownConnectors),
					hostname
			);
	
			log.error(message);
	
			throw new BusinessException(ErrorCode.BAD_CONNECTOR_CONFIGURATION, message);
		}
		

	}

	/**
	 * Reads the user's configuration.
	 *
	 * @param configFile the configuration file
	 *
	 * @return A {@link MultiHostsConfigurationDTO} instance.
	 *
	 * @throws BusinessException if a read error occurred.
	 */
	public static MultiHostsConfigurationDTO readConfigurationSafe(final File configFile) {
		try {
			final MultiHostsConfigurationDTO multiHostsConfig = deserializeYamlFile(configFile, MultiHostsConfigurationDTO.class);

			multiHostsConfig.getTargets().forEach(configDto -> {
				HardwareTargetDTO target = configDto.getTarget();
				// Make sure the target id is always set
				if (target.getId() == null) {
					target.setId(target.getHostname());
				}

				// Set global collect period if there is no specific collect period on the target
				if (configDto.getCollectPeriod() == null) {
					configDto.setCollectPeriod(multiHostsConfig.getCollectPeriod());
				}

				// Set global collect period if there is no specific collect period on the target
				if (configDto.getDiscoveryCycle() == null) {
					configDto.setDiscoveryCycle(multiHostsConfig.getDiscoveryCycle());
				}

				// Set the global level in the target log level.
				// Always the global logger settings wins as the matrix logger
				// 'com.sentrysoftware', is created only once and handles the Level globally for
				// all the targets.
				configDto.setLoggerLevel(multiHostsConfig.getLoggerLevel());
				configDto.setOutputDirectory(multiHostsConfig.getOutputDirectory());

				// Set global sequential flag in the target configuration if this target doesn't define the sequential flag
				// It is more practical to set the flag only once when the requirement is that each target must run the network calls in serial mode
				if (configDto.getSequential() == null) {
					configDto.setSequential(multiHostsConfig.isSequential());
				}
			});

			return multiHostsConfig;
		} catch (Exception e) {
			log.info("Cannot read the configuration file {}", configFile.getAbsoluteFile());
			log.debug("Exception: ", e);
			return MultiHostsConfigurationDTO.empty();

		}
	}

	/**
	 * Build the {@link IHostMonitoring} map. Each entry is index by the targetId
	 * 
	 * @param multiHostsConfigurationDto DTO that wraps the agent configuration for all the targets
	 * @param acceptedConnectorNames     set of accepted compiled connector names
	 * @return Map of {@link IHostMonitoring} instances indexed by the target id
	 */
	public static Map<String, IHostMonitoring> buildHostMonitoringMap(final MultiHostsConfigurationDTO multiHostsConfigurationDto,
			final Set<String> acceptedConnectorNames) {

		final Map<String, IHostMonitoring> hostMonitoringMap = new HashMap<>();

		multiHostsConfigurationDto
			.getTargets()
			.forEach(hostConfigurationDto -> 
				fillHostMonitoringMap(hostMonitoringMap, acceptedConnectorNames, hostConfigurationDto));

		return hostMonitoringMap;

	}

	/**
	 * Create a new {@link IHostMonitoring} instance for the given
	 * {@link HostConfigurationDTO} and update the host monitoring map
	 * 
	 * @param hostMonitoringMap      Map of {@link IHostMonitoring} instances indexed by the targetId
	 * @param acceptedConnectorNames set of accepted compiled connector names
	 * @param hostConfigurationDto   the host configuration we wish to process in order to build
	 *                               the {@link IHostMonitoring} instance
	 */
	public static void fillHostMonitoringMap(final Map<String, IHostMonitoring> hostMonitoringMap,
			final Set<String> acceptedConnectorNames, final HostConfigurationDTO hostConfigurationDto) {

		final String hostname = hostConfigurationDto.getTarget().getHostname();

		try {
			validateTarget(hostConfigurationDto.getTarget().getType(), hostname);
			
//			if (hostConfigurationDto.getOsCommand() != null) {
//				WinRMProtocolDTO winRMDTO = hostConfigurationDto.getWinRM();
//				 validateWinRM(hostname,
//						winRMDTO.getPort(),
//						winRMDTO.getTimeout(),
//						winRMDTO.getUsername(),
//						winRMDTO.getCommand());
//			}

			if (hostConfigurationDto.getSnmp() != null)
				validateSnmpInfo(hostname, 
						hostConfigurationDto.getSnmp());

			if (hostConfigurationDto.getIpmi() != null)
				validateIpmiInfo(hostname, 
						hostConfigurationDto.getIpmi().getUsername(),
						hostConfigurationDto.getIpmi().getTimeout());

			if (hostConfigurationDto.getSsh() != null)
				validateSshInfo(hostname, 
						hostConfigurationDto.getSsh().getUsername(),
						hostConfigurationDto.getSsh().getTimeout());

			if (hostConfigurationDto.getWbem() != null)
				validateWbemInfo(hostname, 
						hostConfigurationDto.getWbem().getUsername(),
						hostConfigurationDto.getWbem().getTimeout(),
						hostConfigurationDto.getWbem().getPort());

			if (hostConfigurationDto.getWmi() != null)
				validateWmiInfo(hostname, 
						hostConfigurationDto.getWmi().getTimeout());

			if (hostConfigurationDto.getHttp() != null)
				validateHttpInfo(hostname, 
						hostConfigurationDto.getHttp().getTimeout(),
						hostConfigurationDto.getHttp().getPort());

			if (hostConfigurationDto.getOsCommand() != null)
				validateOsCommandInfo(hostname, 
						hostConfigurationDto.getOsCommand().getTimeout());

			final Set<String> selectedConnectors = validateAndGetConnectors(acceptedConnectorNames,
					hostConfigurationDto.getSelectedConnectors(), hostname, false);
			final Set<String> excludedConnectors = validateAndGetConnectors(acceptedConnectorNames,
					hostConfigurationDto.getExcludedConnectors(), hostname, true);

			final EngineConfiguration engineConfiguration = buildEngineConfiguration(hostConfigurationDto,
					selectedConnectors, excludedConnectors);

			final Map<String, Connector> connectors = ConnectorStore.getInstance().getConnectors();

			validateEngineConfiguration(engineConfiguration, connectors.values());

			// targetId can never be null here
			final String targetId = hostConfigurationDto.getTarget().getId();

			hostMonitoringMap.putIfAbsent(
					targetId,
					HostMonitoringFactory.getInstance().createHostMonitoring(targetId, engineConfiguration));

		} catch (Exception e) {

			log.warn("The given target has been staged as invalid. Target: {}", hostConfigurationDto);

		}
	}

	/**
	 * Get the directory path of the given file
	 * 
	 * @param file
	 * @return {@link Path} instance
	 */
	public static Path getDirectoryPath(final File file) {
		return file.getAbsoluteFile().toPath().getParent();
	}

	/**
	 * @param dir    the directory assumed under the product directory. E.g. logs
	 *               assumed under /usr/local/bin/hws-otel-collector
	 * @param create indicate if we should create the sub directory or not
	 * @return The absolute path of the sub directory
	 */
	public static Path getSubDirectory(@NonNull final String dir, boolean create) {

		Path subDirectory = getSubPath(dir);
		if (!create) {
			return subDirectory;
		}

		try {
			return Files.createDirectories(subDirectory).toRealPath();
		} catch (IOException e) {
			throw new IllegalStateException("Could not create " + dir + " directory " + subDirectory, e);
		}
	}

	/**
	 * Get the sub path under the home directory. E.g.
	 * <em>/usr/local/bin/hws-otel-collector/lib/../config</em>
	 * 
	 * @param subPath sub path to the directory or the file
	 * @return {@link Path} instance
	 */
	public static Path getSubPath(@NonNull final String subPath) {
		File me = getExecutableDir();

		final Path path = me.getAbsoluteFile().toPath();

		Path parentLibPath = path.getParent();

		// No parent? let's work with the current directory
		if (parentLibPath == null) {
			parentLibPath = path;
		}

		return parentLibPath.resolve("../" + subPath);
	}

	/**
	 * Get the directory of the current executable jar.
	 * 
	 * @return {@link File} instance
	 */
	public static File getExecutableDir() {
		final File me;
		try {
			me = ResourceHelper.findSource(ConfigHelper.class);
		} catch (Exception e) {
			throw new IllegalStateException("Error detected when getting local source file", e);
		}

		if (me == null) {
			throw new IllegalStateException("Could not get the local source file.");
		}
		return me;
	}

	/**
	 * Decrypt the given crypted password.
	 * 
	 * @param crypted
	 * @return char array
	 */
	public static char[] decrypt(final char[] crypted) {
		try {
			return SecurityManager.decrypt(crypted, PasswordEncrypt.getKeyStoreFile(false));
		} catch (Exception e) {
			// This is a real problem, let's log the error
			log.error("Could not decrypt password: {}", e.getMessage());
			log.debug("Exception", e);
			return crypted;
		}
	}
	
	/**
	 * Validate the given WinRM information (hostname, port, timeout, username and command)
	 *
	 * @param hostname  hostname of the target
	 * @param port      port of the target
	 * @param timeout   timeout of the target
	 * @param username	username used to authenticate to the target
	 * @param command	wql command to execute
	 * @throws BusinessException
	 */
	static void validateWinRM(final String hostname, final Integer port, final Long timeout, final String username, final String command)
			throws BusinessException {
		if (port == null || port < 1 || port > 65535) {
			String message = String.format(PORT_ERROR, hostname);
			log.error(message);
			throw new BusinessException(ErrorCode.INVALID_PORT, message);
		}

		if (timeout == null || timeout < 0L) {
			String message = String.format(TIMEOUT_ERROR, hostname);
			log.error(message);
			throw new BusinessException(ErrorCode.INVALID_TIMEOUT, message);
		}

		if (username == null || username.isEmpty()) {
			String message = String.format(USERNAME_ERROR, hostname);
			log.error(message);
			throw new BusinessException(ErrorCode.NO_USERNAME, message);
		}

		if (command == null || command.isEmpty()) {
			String message = String.format(COMMAND_ERROR, hostname);
			log.error(message);
			throw new BusinessException(ErrorCode.NO_COMMAND, message);
		}
	}

}
