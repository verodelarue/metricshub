package com.sentrysoftware.hardware.cli.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.sentrysoftware.hardware.cli.component.cli.protocols.SNMPCredentials;
import com.sentrysoftware.matrix.engine.protocol.SNMPProtocol;
import com.sentrysoftware.matrix.engine.protocol.SNMPProtocol.Privacy;
import com.sentrysoftware.matrix.engine.protocol.SNMPProtocol.SNMPVersion;

public class EngineServiceTest {

	EngineService engine = new EngineService();

	@Test
	public void getSelectedConnectorsTest() {
		
		Set<String> allHdfs = new HashSet<>(Arrays.asList("aa.hdfs", "bb.hdfs", "cc.hdfs", "dd.hdfs", "ee.hdfs"));
		Set<String> hdfs = new HashSet<>(Arrays.asList("aa.hdfs", "bb.hdfs", "cc.hdfs"));
		Set<String> hdfsToConnectors = new HashSet<>(Arrays.asList("aa.connector", "bb.connector", "cc.connector"));
		Set<String> hdfsExclusion = new HashSet<>(Arrays.asList("aa.hdfs", "bb.hdfs"));
		Set<String> hdfsExclusionConnectors = new HashSet<>(Arrays.asList("cc.connector", "dd.connector", "ee.connector"));
		
		Set<String> selectedHdfs = engine.getSelectedConnectors(allHdfs, hdfs, hdfsExclusion);
		assertTrue(hdfsToConnectors.size() == selectedHdfs.size() && hdfsToConnectors.containsAll(selectedHdfs) && selectedHdfs.containsAll(hdfsToConnectors));
		
		Set<String> selectedHdfs2 = engine.getSelectedConnectors(allHdfs, hdfs, null);
		assertTrue(hdfsToConnectors.size() == selectedHdfs2.size() && hdfsToConnectors.containsAll(selectedHdfs2) && selectedHdfs2.containsAll(hdfsToConnectors));
		
		Set<String> excludeHdfs = engine.getSelectedConnectors(allHdfs, null, hdfsExclusion);
		assertTrue(excludeHdfs.size() == hdfsExclusionConnectors.size() && excludeHdfs.containsAll(hdfsExclusionConnectors) && hdfsExclusionConnectors.containsAll(excludeHdfs));
		
		Set<String> noSelectedHdfs = engine.getSelectedConnectors(allHdfs, null, null);
		assertEquals(Collections.emptySet(), noSelectedHdfs);
		noSelectedHdfs = engine.getSelectedConnectors(null, null, null);
		assertEquals(Collections.emptySet(), noSelectedHdfs);
		
	}
	
	@Test
	public void getSNMPCredentialsTest() {
	
		SNMPCredentials snmpCred = new SNMPCredentials();
		snmpCred.setCommunity("comm1");
		snmpCred.setPassword("pwd1");
		snmpCred.setPort(110);
		snmpCred.setPrivacy(Privacy.AES);
		snmpCred.setPrivacyPassword("privPwd");
		snmpCred.setSnmpVersion(SNMPVersion.V1);
		snmpCred.setTimeout(10);
		snmpCred.setUsername("user");
		SNMPProtocol snmpProtocol = engine.getSNMPCredentials(snmpCred);
		assertEquals("comm1", snmpProtocol.getCommunity());
		assertEquals("pwd1", snmpProtocol.getPassword());
		assertEquals(110, snmpProtocol.getPort());
		assertEquals(Privacy.AES, snmpProtocol.getPrivacy());
		assertEquals("privPwd", snmpProtocol.getPrivacyPassword());
		assertEquals(SNMPVersion.V1, snmpProtocol.getVersion());
		assertEquals(600000, snmpProtocol.getTimeout());
		assertEquals("user", snmpProtocol.getUsername());
	}
}
