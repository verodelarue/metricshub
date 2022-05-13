package com.sentrysoftware.matrix.connector.parser.state.source.http;

import static com.sentrysoftware.matrix.connector.parser.state.detection.http.HttpProcessor.HTTP_TYPE_VALUE;

import java.util.Set;

import com.sentrysoftware.matrix.connector.model.monitor.job.source.type.http.HttpSource;
import com.sentrysoftware.matrix.connector.parser.state.IConnectorStateParser;
import com.sentrysoftware.matrix.connector.parser.state.source.common.EntryConcatEndProcessor;
import com.sentrysoftware.matrix.connector.parser.state.source.common.EntryConcatMethodProcessor;
import com.sentrysoftware.matrix.connector.parser.state.source.common.EntryConcatStartProcessor;
import com.sentrysoftware.matrix.connector.parser.state.source.common.ExecuteForEachEntryOfProcessor;
import com.sentrysoftware.matrix.connector.parser.state.source.common.ForceSerializationProcessor;
import com.sentrysoftware.matrix.connector.parser.state.source.common.TypeProcessor;

public class ConnectorHttpProperty {
	private ConnectorHttpProperty() {
	}

	public static Set<IConnectorStateParser> getConnectorProperties() {

		return Set.of(
				new TypeProcessor(HttpSource.class, HTTP_TYPE_VALUE),
				new ForceSerializationProcessor(HttpSource.class, HTTP_TYPE_VALUE),
				new MethodProcessor(),
				new UrlProcessor(),
				new HeaderProcessor(),
				new BodyProcessor(),
				new AuthenticationTokenProcessor(),
				new ResultContentProcessor(),
				new ExecuteForEachEntryOfProcessor(HttpSource.class, HTTP_TYPE_VALUE),
				new EntryConcatMethodProcessor(HttpSource.class, HTTP_TYPE_VALUE),
				new EntryConcatStartProcessor(HttpSource.class, HTTP_TYPE_VALUE),
				new EntryConcatEndProcessor(HttpSource.class, HTTP_TYPE_VALUE));
	}
}
