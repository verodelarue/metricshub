package com.sentrysoftware.matrix.common.meta.parameter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class MetaParameter {

	private String name;
	private String unit;
	private ParameterType type;
	private boolean basicCollect;
}
