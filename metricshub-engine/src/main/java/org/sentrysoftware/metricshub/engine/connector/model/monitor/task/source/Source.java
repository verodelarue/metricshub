package org.sentrysoftware.metricshub.engine.connector.model.monitor.task.source;

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

import static com.fasterxml.jackson.annotation.Nulls.SKIP;
import static org.sentrysoftware.metricshub.engine.common.helpers.MetricsHubConstants.EMPTY;
import static org.sentrysoftware.metricshub.engine.common.helpers.MetricsHubConstants.NEW_LINE;
import static org.sentrysoftware.metricshub.engine.common.helpers.StringHelper.addNonNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.UnaryOperator;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.sentrysoftware.metricshub.engine.connector.model.common.EntryConcatMethod;
import org.sentrysoftware.metricshub.engine.connector.model.common.ExecuteForEachEntryOf;
import org.sentrysoftware.metricshub.engine.connector.model.common.IEntryConcatMethod;
import org.sentrysoftware.metricshub.engine.connector.model.monitor.task.source.compute.Compute;
import org.sentrysoftware.metricshub.engine.strategy.source.ISourceProcessor;
import org.sentrysoftware.metricshub.engine.strategy.source.SourceTable;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", visible = true)
@JsonSubTypes(
	{
		@JsonSubTypes.Type(value = CopySource.class, name = "copy"),
		@JsonSubTypes.Type(value = HttpSource.class, name = "http"),
		@JsonSubTypes.Type(value = IpmiSource.class, name = "ipmi"),
		@JsonSubTypes.Type(value = OsCommandSource.class, name = "osCommand"),
		@JsonSubTypes.Type(value = SnmpGetSource.class, name = "snmpGet"),
		@JsonSubTypes.Type(value = SnmpTableSource.class, name = "snmpTable"),
		@JsonSubTypes.Type(value = StaticSource.class, name = "static"),
		@JsonSubTypes.Type(value = TableJoinSource.class, name = "tableJoin"),
		@JsonSubTypes.Type(value = TableUnionSource.class, name = "tableUnion"),
		@JsonSubTypes.Type(value = WbemSource.class, name = "wbem"),
		@JsonSubTypes.Type(value = WmiSource.class, name = "wmi")
	}
)
@Data
@NoArgsConstructor
public abstract class Source implements Serializable {

	private static final long serialVersionUID = 1L;

	protected String type;

	@JsonSetter(nulls = SKIP)
	private List<Compute> computes = new ArrayList<>();

	protected boolean forceSerialization;

	@JsonIgnore
	protected String key;

	protected ExecuteForEachEntryOf executeForEachEntryOf;
	private Set<String> references = new HashSet<>();

	protected Source(
		String type,
		List<Compute> computes,
		boolean forceSerialization,
		String key,
		ExecuteForEachEntryOf executeForEachEntryOf
	) {
		this.type = type;
		this.computes = computes == null ? new ArrayList<>() : computes;
		this.forceSerialization = forceSerialization;
		this.key = key;
		this.executeForEachEntryOf = executeForEachEntryOf;
		this.references = new HashSet<>();
	}

	public abstract Source copy();

	public abstract void update(UnaryOperator<String> updater);

	public abstract SourceTable accept(ISourceProcessor sourceProcessor);

	@Override
	public String toString() {
		final StringJoiner stringJoiner = new StringJoiner(NEW_LINE);

		stringJoiner.add(new StringBuilder("- ").append(key).append(".type=").append(this.getClass().getSimpleName()));

		addNonNull(stringJoiner, "- forceSerialization=", forceSerialization);
		// A small trick here because the executeForEachEntryOf.toString value is already
		// formatted that's why we don't need a prefix for the string value of the nested executeForEachEntryOf
		addNonNull(stringJoiner, EMPTY, executeForEachEntryOf != null ? executeForEachEntryOf.toString() : null);

		return stringJoiner.toString();
	}

	/**
	 * Whether the {@link ExecuteForEachEntryOf} is present in the {@link Source} or
	 * not
	 *
	 * @return <code>true</code> if {@link ExecuteForEachEntryOf} is present otherwise
	 *         <code>false</code>
	 */
	public boolean isExecuteForEachEntryOf() {
		// CHECKSTYLE:OFF
		return (
			executeForEachEntryOf != null &&
			executeForEachEntryOf.getSource() != null &&
			!executeForEachEntryOf.getSource().isBlank()
		);
		// CHECKSTYLE:ON
	}

	/**
	 * Get the {@link EntryConcatMethod} value
	 *
	 * @return {@link EntryConcatMethod} enum value
	 */
	public IEntryConcatMethod getEntryConcatMethod() {
		return executeForEachEntryOf != null ? executeForEachEntryOf.getConcatMethod() : null;
	}

	/**
	 * Get the executeForEachEntryOf string value
	 *
	 * @return String value
	 */
	public String getExecuteForEachEntryOf() {
		return executeForEachEntryOf != null ? executeForEachEntryOf.getSource() : null;
	}
}
