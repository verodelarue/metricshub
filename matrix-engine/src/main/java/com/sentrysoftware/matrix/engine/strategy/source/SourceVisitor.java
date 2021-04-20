package com.sentrysoftware.matrix.engine.strategy.source;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sentrysoftware.matrix.connector.model.monitor.job.source.type.http.HTTPSource;
import com.sentrysoftware.matrix.connector.model.monitor.job.source.type.ipmi.IPMI;
import com.sentrysoftware.matrix.connector.model.monitor.job.source.type.oscommand.OSCommandSource;
import com.sentrysoftware.matrix.connector.model.monitor.job.source.type.reference.ReferenceSource;
import com.sentrysoftware.matrix.connector.model.monitor.job.source.type.snmp.SNMPGetSource;
import com.sentrysoftware.matrix.connector.model.monitor.job.source.type.snmp.SNMPGetTableSource;
import com.sentrysoftware.matrix.connector.model.monitor.job.source.type.tablejoin.TableJoinSource;
import com.sentrysoftware.matrix.connector.model.monitor.job.source.type.tableunion.TableUnionSource;
import com.sentrysoftware.matrix.connector.model.monitor.job.source.type.telnet.TelnetInteractiveSource;
import com.sentrysoftware.matrix.connector.model.monitor.job.source.type.ucs.UCSSource;
import com.sentrysoftware.matrix.connector.model.monitor.job.source.type.wbem.WBEMSource;
import com.sentrysoftware.matrix.connector.model.monitor.job.source.type.wmi.WMISource;
import com.sentrysoftware.matrix.engine.protocol.IProtocolConfiguration;
import com.sentrysoftware.matrix.engine.protocol.SNMPProtocol;
import com.sentrysoftware.matrix.engine.strategy.MatsyaClientsExecutor;
import com.sentrysoftware.matrix.engine.strategy.MatsyaListResult;
import com.sentrysoftware.matrix.engine.strategy.StrategyConfig;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SourceVisitor implements ISourceVisitor {
	

	@Autowired
	private StrategyConfig strategyConfig;

	@Autowired
	private MatsyaClientsExecutor matsyaClientsExecutor;

	@Override
	public SourceTable visit(HTTPSource httpSource) {
		return SourceTable.empty();
	}

	@Override
	public SourceTable visit(IPMI ipmi) {
		return SourceTable.empty();
	}

	@Override
	public SourceTable visit(OSCommandSource osCommandSource) {
		return SourceTable.empty();
	}

	@Override
	public SourceTable visit(ReferenceSource referenceSource) {
		return SourceTable.empty();
	}

	@Override
	public SourceTable visit(SNMPGetSource snmpGetSource) {
		return SourceTable.empty();
	}

	@Override
	public SourceTable visit(SNMPGetTableSource snmpGetTableSource) {
		if( null == snmpGetTableSource || null == snmpGetTableSource.getOid()) {
			return SourceTable.empty();
		}
		// run Matsya in order to execute the snmpTable
		// receives a CSV structure to be transformed into a List. Delimiters are "\n" and ","
		SourceTable sourceTable = new SourceTable();
		List<String> selectedColumns = snmpGetTableSource.getSnmpTableSelectColumns();
		if(null == selectedColumns) {
			return SourceTable.empty();
		}
		String[] selectColumnArray = new String[selectedColumns.size()];
		selectColumnArray = selectedColumns.toArray(selectColumnArray);
		
		final Optional<IProtocolConfiguration> snmpProtocolOpt = strategyConfig.getEngineConfiguration()
				.getProtocolConfigurations().stream().filter(SNMPProtocol.class::isInstance).findFirst();

		if (!snmpProtocolOpt.isPresent()) {
			return SourceTable.empty();
		}

		final SNMPProtocol protocol = (SNMPProtocol) snmpProtocolOpt.get();
		final String hostname = strategyConfig.getEngineConfiguration().getTarget().getHostname();

		try {

			MatsyaListResult matsyaListResult = (MatsyaListResult) matsyaClientsExecutor.executeSNMPTable(
					snmpGetTableSource.getOid(),
					selectColumnArray,
					protocol,
					hostname,
					true);
			final List<List<String>> result = matsyaListResult == null ? null : matsyaListResult.getData();

			sourceTable.setHeaders(selectedColumns);
			sourceTable.setTable(result);

			return sourceTable;

		} catch (Exception e) {
			final String message = String.format(
					"SNMP Test Failed - SNMP Table of %s on %s was unsuccessful due to an exception. Message: %s.",
					snmpGetTableSource.getOid(), hostname, e.getMessage());
			log.debug(message, e);
			return SourceTable.empty();
		}
	}

	@Override
	public SourceTable visit(TableJoinSource tableJoinSource) {
		return SourceTable.empty();
	}

	@Override
	public SourceTable visit(TableUnionSource tableUnionSource) {
		return SourceTable.empty();
	}

	@Override
	public SourceTable visit(TelnetInteractiveSource telnetInteractiveSource) {
		return SourceTable.empty();
	}

	@Override
	public SourceTable visit(UCSSource ucsSource) {
		return SourceTable.empty();
	}

	@Override
	public SourceTable visit(WBEMSource wbemSource) {
		return SourceTable.empty();
	}

	@Override
	public SourceTable visit(WMISource wmiSource) {
		return SourceTable.empty();
	}

	
}
