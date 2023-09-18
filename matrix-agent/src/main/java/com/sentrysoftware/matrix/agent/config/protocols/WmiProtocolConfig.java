package com.sentrysoftware.matrix.agent.config.protocols;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sentrysoftware.matrix.agent.deserialization.TimeDeserializer;
import com.sentrysoftware.matrix.configuration.IConfiguration;
import com.sentrysoftware.matrix.configuration.WmiConfiguration;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class WmiProtocolConfig extends AbstractProtocolConfig {

	private String username;

	private char[] password;

	private String namespace;

	@Default
	@JsonDeserialize(using = TimeDeserializer.class)
	private Long timeout = 120L;

	/**
	 * Create a new {@link WmiConfiguration} instance based on the current members
	 *
	 * @return The {@link WmiConfiguration} instance
	 */
	@Override
	public IConfiguration toProtocol() {
		return WmiConfiguration
			.builder()
			.namespace(namespace)
			.username(username)
			.password(super.decrypt(password))
			.timeout(timeout)
			.build();
	}

	@Override
	public String toString() {
		String desc = "WMI";
		if (username != null) {
			desc = desc + " as " + username;
		}
		return desc;
	}
}
