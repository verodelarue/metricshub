package com.sentrysoftware.matrix.connector.parser.state;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.sentrysoftware.matrix.connector.model.Connector;
import com.sentrysoftware.matrix.connector.model.common.OsType;
import com.sentrysoftware.matrix.connector.model.monitor.MonitorType;
import com.sentrysoftware.matrix.connector.parser.ConnectorParserConstants;

public class ConnectorSimpleProperty {

	private ConnectorSimpleProperty() {}

	public static Set<IConnectorStateParser> getConnectorProperties() {

		return Stream
				.of(new DisplayNameProcessor(),
					new TypicalPlatformProcessor(),
					new ReliesOnProcessor(),
					new VersionProcessor(),
					new RemoteSupportProcessor(),
					new LocalSupportProcessor(),
					new AppliesToOsProcessor(),
					new SupersedesProcessor(),
					new CommentsProcessor(),
					new NoAutoDetectionProcessor(),
					new OnLastResortProcessor())
				.collect(Collectors.toSet());
	}

	private static boolean detect(final String key, final String value, final String property) {
		return value != null && key != null && key.toLowerCase().startsWith(property.toLowerCase());
	}

	public static class DisplayNameProcessor implements IConnectorStateParser {

		@Override
		public boolean detect(final String key, final String value, final Connector connector) {
			return ConnectorSimpleProperty.detect(key, value, "hdf.displayname");
		}

		@Override
		public void parse(final String key, final String value, final Connector connector) {
			if (connector != null && value != null) {
				connector.setDisplayName(value.trim());
			}
		}
	}

	public static class SupersedesProcessor implements IConnectorStateParser {

		@Override
		public boolean detect(final String key, final String value, final Connector connector) {
			return ConnectorSimpleProperty.detect(key, value, "hdf.supersedes");
		}

		@Override
		public void parse(final String key, final String value, final Connector connector) {
			if (connector != null && value != null) {
				Set<String> supersedes = new HashSet<>();

				Arrays.stream(value.split(ConnectorParserConstants.COMMA))
					.forEach(supersedesStr -> supersedes.add(supersedesStr.trim()));

				connector.setSupersedes(supersedes);
			}
		}
	}

	public static class AppliesToOsProcessor implements IConnectorStateParser {

		@Override
		public boolean detect(final String key, final String value, final Connector connector) {
			return ConnectorSimpleProperty.detect(key, value, "hdf.appliestoos");
		}

		@Override
		public void parse(final String key, final String value, final Connector connector) {
			if (connector != null && value != null) {
				Set<OsType> osTypes = new HashSet<>();

				Arrays.stream(value.split(ConnectorParserConstants.COMMA))
					.forEach(osTypeStr -> osTypes.add(OsType.valueOf(osTypeStr.trim().toUpperCase())));

				connector.setAppliesToOS(osTypes);
			}
		}
	}

	public static class LocalSupportProcessor implements IConnectorStateParser {

		@Override
		public boolean detect(final String key, final String value, final Connector connector) {
			return ConnectorSimpleProperty.detect(key, value, "hdf.localsupport");
		}

		@Override
		public void parse(final String key, final String value, final Connector connector) {
			if (connector != null && value != null) {
				connector.setLocalSupport(Boolean.parseBoolean(value.trim().toLowerCase()));
			}
		}
	}

	public static class RemoteSupportProcessor implements IConnectorStateParser {

		@Override
		public boolean detect(final String key, final String value, final Connector connector) {
			return ConnectorSimpleProperty.detect(key, value, "hdf.remotesupport");
		}

		@Override
		public void parse(final String key, final String value, final Connector connector) {
			if (connector != null && value != null) {
				connector.setRemoteSupport(Boolean.parseBoolean(value.trim().toLowerCase()));
			}
		}
	}

	public static class TypicalPlatformProcessor implements IConnectorStateParser {

		@Override
		public boolean detect(final String key, final String value, final Connector connector) {
			return ConnectorSimpleProperty.detect(key, value, "hdf.typicalplatform");
		}

		@Override
		public void parse(final String key, final String value, final Connector connector) {
			if (connector != null && value != null) {
				connector.setTypicalPlatform(value.trim());
			}
		}
	}

	public static class ReliesOnProcessor implements IConnectorStateParser {

		@Override
		public boolean detect(final String key, final String value, final Connector connector) {
			return ConnectorSimpleProperty.detect(key, value, "hdf.relieson");
		}

		@Override
		public void parse(final String key, final String value, final Connector connector) {
			if (connector != null && value != null) {
				connector.setReliesOn(value.trim());
			}
		}
	}

	public static class VersionProcessor implements IConnectorStateParser {

		@Override
		public boolean detect(final String key, final String value, final Connector connector) {
			return ConnectorSimpleProperty.detect(key, value, "hdf.version");
		}

		@Override
		public void parse(final String key, final String value, final Connector connector) {
			if (connector != null && value != null) {
				connector.setVersion(value.trim());
			}
		}
	}

	public static class CommentsProcessor implements IConnectorStateParser {

		@Override
		public boolean detect(final String key, final String value, final Connector connector) {
			return ConnectorSimpleProperty.detect(key, value, "hdf.comments");
		}

		@Override
		public void parse(final String key, final String value, final Connector connector) {
			if (connector != null && value != null) {
				connector.setComments(value.trim());
			}
		}
	}

	public static class NoAutoDetectionProcessor implements IConnectorStateParser {

		@Override
		public boolean detect(final String key, final String value, final Connector connector) {
			return ConnectorSimpleProperty.detect(key, value, "hdf.noautodetection");
		}

		@Override
		public void parse(final String key, final String value, final Connector connector) {

			if (connector != null) {
				connector.setNoAutoDetection(Boolean.valueOf(value));
			}
		}
	}
	
	public static class OnLastResortProcessor implements IConnectorStateParser {

		@Override
		public boolean detect(final String key, final String value, final Connector connector) {
			return ConnectorSimpleProperty.detect(key, value, "hdf.onLastResort");
		}

		@Override
		public void parse(final String key, final String value, final Connector connector) {
			if (connector != null && value != null) {
				Optional<MonitorType> monitorTypeOpt = MonitorType.getByNameInConnectorOptional(value.trim());
				if (monitorTypeOpt.isPresent()) {
					connector.setOnLastResort(monitorTypeOpt.get());
				}
			}
		}
	}
}