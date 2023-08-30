package com.sentrysoftware.matrix.connector.parser;

import static com.sentrysoftware.matrix.common.helpers.MatrixConstants.YAML_EXTENDS_KEY;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.sentrysoftware.matrix.common.helpers.JsonHelper;
import com.sentrysoftware.matrix.connector.deserializer.ConnectorDeserializer;
import com.sentrysoftware.matrix.connector.deserializer.PostDeserializeHelper;
import com.sentrysoftware.matrix.connector.model.Connector;
import com.sentrysoftware.matrix.connector.update.AvailableSourceUpdate;
import com.sentrysoftware.matrix.connector.update.CompiledFilenameUpdate;
import com.sentrysoftware.matrix.connector.update.ConnectorUpdateChain;
import com.sentrysoftware.matrix.connector.update.MonitorTaskSourceDepUpdate;
import com.sentrysoftware.matrix.connector.update.PreSourceDepUpdate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class ConnectorParser {

	private ConnectorDeserializer deserializer;
	private NodeProcessor processor;
	private ConnectorUpdateChain connectorUpdateChain;

	/**
	 * Parse the given connector file
	 * 
	 * @param file
	 * @return new {@link Connector} object
	 * @throws IOException
	 */
	public Connector parse(final File file) throws IOException {

		JsonNode node = deserializer.getMapper().readTree(file);

		// PRE-Processing
		if (processor != null) {
			final Map<Path, JsonNode> parents = new HashMap<>();
			final Path connectorDirectory = file.toPath().getParent();
			resolveParents(node, connectorDirectory, parents);

			node = processor.process(node);

			new EmbeddedFilesResolver(node, connectorDirectory, parents.keySet()).internalize();
		}

		// POST-Processing
		final Connector connector = deserializer.deserialize(node);

		// Run the update chain
		if (connectorUpdateChain != null) {
			connectorUpdateChain.update(connector);
		}

		// Update the compiled filename
		new CompiledFilenameUpdate(file.getName()).update(connector);

		return connector;
	}

	/**
	 * Creates a new {@link ConnectorParser} with extends and constants
	 * {@link NodeProcessor}
	 * 
	 * @param connectorDirectory
	 * @return new instance of {@link ConnectorParser}
	 */
	public static ConnectorParser withNodeProcessor(final Path connectorDirectory) {
		final ObjectMapper mapper = JsonHelper.buildYamlMapper();

		PostDeserializeHelper.addPostDeserializeSupport(mapper);

		return ConnectorParser.builder()
			.deserializer(new ConnectorDeserializer(mapper))
			.processor(NodeProcessorHelper.withExtendsAndConstantsProcessor(connectorDirectory, mapper))
			.build();
	}

	/**
	 * Creates a new {@link ConnectorParser} with extends and constants
	 * {@link NodeProcessor} and with a {@link ConnectorUpdateChain}
	 * 
	 * @param connectorDirectory
	 * @return new instance of {@link ConnectorParser}
	 */
	public static ConnectorParser withNodeProcessorAndUpdateChain(final Path connectorDirectory) {
		final ConnectorParser connectorParser = withNodeProcessor(connectorDirectory);

		// Create the update objects
		final ConnectorUpdateChain availableSource = new AvailableSourceUpdate();
		final ConnectorUpdateChain preSourceDepUpdate = new PreSourceDepUpdate();
		final ConnectorUpdateChain monitorTaskSourceDepUpdate = new MonitorTaskSourceDepUpdate();

		// Create the chain
		availableSource.setNextUpdateChain(preSourceDepUpdate);
		preSourceDepUpdate.setNextUpdateChain(monitorTaskSourceDepUpdate);

		// Set the first update chain
		connectorParser.setConnectorUpdateChain(availableSource);

		return connectorParser;
	}

	/**
	 * Resolve connector parent paths
	 * 
	 * @param connector    The connector object as {@link JsonNode}
	 * @param connectorDir The connector directory
	 * @param parents      The parents map to resolve
	 * @throws IOException
	 */
	private void resolveParents(final JsonNode connector, final Path connectorDir, final Map<Path, JsonNode> parents)
		throws IOException {

		final ArrayNode extended = (ArrayNode) connector.get(YAML_EXTENDS_KEY);
		if (extended == null || extended.isNull() || extended.isEmpty()) {
			return;
		}

		final List<Entry<Path, JsonNode>> nextEntries = new ArrayList<>();
		Entry<Path, JsonNode> parentEntry;
		for (final JsonNode extendedNode : extended) {
			parentEntry = getConnectorParentEntry(connectorDir, extendedNode.asText());
			nextEntries.add(parentEntry);
			parents.put(parentEntry.getKey(), parentEntry.getValue());
		}

		for (Entry<Path, JsonNode> entry : nextEntries) {
			resolveParents(entry.getValue(), entry.getKey(), parents);
		}
	}

	/**
	 * Get a connector entry where the entry key is the connector path and the value
	 * is the connector as {@link JsonNode}
	 * 
	 * @param connectorCurrentDir   The current directory of the connector which extends the parent
	 * @param connectorRelativePath The relative path of the connector parent
	 * @return a Map entry defining the path as key and the {@link JsonNode} parent connector as value
	 * @throws IOException
	 */
	private Entry<Path, JsonNode> getConnectorParentEntry(
		final Path connectorCurrentDir,
		final String connectorRelativePath) throws IOException {
		final Path connectorPath = connectorCurrentDir.resolve(connectorRelativePath + ".yaml");
		if (!Files.exists(connectorPath)) {
			throw new IllegalStateException("Cannot find extended connector " + connectorPath.toString());
		}
		return new AbstractMap.SimpleEntry<>(
			connectorPath.getParent(),
			deserializer.getMapper().readTree(connectorPath.toFile()));
	}
}
