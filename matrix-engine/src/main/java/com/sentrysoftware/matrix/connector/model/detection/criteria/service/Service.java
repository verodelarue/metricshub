package com.sentrysoftware.matrix.connector.model.detection.criteria.service;

import com.sentrysoftware.matrix.connector.model.detection.criteria.Criterion;
import com.sentrysoftware.matrix.engine.strategy.detection.CriterionTestResult;
import com.sentrysoftware.matrix.engine.strategy.detection.ICriterionVisitor;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Service extends Criterion {

	private static final long serialVersionUID = -6173571823803344096L;

	private String serviceName;

	@Builder
	public Service(boolean forceSerialization, String serviceName, int index) {

		super(forceSerialization, index);
		this.serviceName = serviceName;
	}

	@Override
	public CriterionTestResult accept(final ICriterionVisitor criterionVisitor) {
		return criterionVisitor.visit(this);
	}

	@Override
	public String toString() {
		return "- Service: " + serviceName;
	}

}
