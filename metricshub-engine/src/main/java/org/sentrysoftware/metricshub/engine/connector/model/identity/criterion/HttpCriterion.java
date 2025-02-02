package org.sentrysoftware.metricshub.engine.connector.model.identity.criterion;

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

import static com.fasterxml.jackson.annotation.Nulls.FAIL;
import static com.fasterxml.jackson.annotation.Nulls.SKIP;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import org.sentrysoftware.metricshub.engine.connector.model.common.HttpMethod;
import org.sentrysoftware.metricshub.engine.connector.model.common.ResultContent;
import org.sentrysoftware.metricshub.engine.strategy.detection.CriterionTestResult;
import org.sentrysoftware.metricshub.engine.strategy.detection.ICriterionProcessor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class HttpCriterion extends Criterion {

	private static final long serialVersionUID = 1L;

	@JsonSetter(nulls = SKIP)
	private HttpMethod method = HttpMethod.GET;

	@NonNull
	@JsonSetter(nulls = FAIL)
	private String url;

	// String or EmbeddedFile reference
	private String header;
	private String body;
	private String expectedResult;
	private String errorMessage;

	@JsonSetter(nulls = SKIP)
	private ResultContent resultContent = ResultContent.BODY;

	private String authenticationToken;

	@Builder
	@JsonCreator
	public HttpCriterion(
		@JsonProperty("type") String type,
		@JsonProperty("forceSerialization") boolean forceSerialization,
		@JsonProperty("method") HttpMethod method,
		@JsonProperty(value = "url", required = true) @NonNull String url,
		@JsonProperty("header") String header,
		@JsonProperty("body") String body,
		@JsonProperty("expectedResult") String expectedResult,
		@JsonProperty("errorMessage") String errorMessage,
		@JsonProperty("resultContent") ResultContent resultContent,
		@JsonProperty("authenticationToken") String authenticationToken
	) {
		super(type, forceSerialization);
		this.method = method == null ? HttpMethod.GET : method;
		this.url = url;
		this.header = header;
		this.body = body;
		this.expectedResult = expectedResult;
		this.errorMessage = errorMessage;
		this.resultContent = resultContent == null ? ResultContent.BODY : resultContent;
		this.authenticationToken = authenticationToken;
	}

	@Override
	public CriterionTestResult accept(ICriterionProcessor criterionProcessor) {
		return criterionProcessor.process(this);
	}
}
