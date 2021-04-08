package com.sentrysoftware.matrix.connector.parser.state;

import com.sentrysoftware.matrix.connector.model.Connector;
import com.sentrysoftware.matrix.connector.model.detection.Detection;
import com.sentrysoftware.matrix.connector.model.detection.criteria.Criterion;
import com.sentrysoftware.matrix.connector.model.detection.criteria.snmp.SNMPGetNext;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static com.sentrysoftware.matrix.connector.parser.state.ConnectorState.CONNECTOR_SIMPLE_PROPERTY;
import static com.sentrysoftware.matrix.connector.parser.state.ConnectorState.CONNECTOR_SNMP_DETECTION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConnectorStateTest {

    private final Connector connector = new Connector();

    private static final String DISPLAY_NAME_KEY = "hdf.displayname";
    private static final String OID_KEY = "detection.criteria(1).snmpgetnext";
    private static final String FOO = "FOO";

    @Test
    void testDetect() {

        assertFalse(CONNECTOR_SIMPLE_PROPERTY.detect(null, null, null));
        assertTrue(CONNECTOR_SIMPLE_PROPERTY.detect(DISPLAY_NAME_KEY, null, null));

        assertFalse(CONNECTOR_SNMP_DETECTION.detect(null, null, null));
        assertTrue(CONNECTOR_SNMP_DETECTION.detect(OID_KEY, FOO, null));
    }

    @Test
    void testParse() {

        CONNECTOR_SIMPLE_PROPERTY.parse(DISPLAY_NAME_KEY, null, connector);
        assertNull(connector.getDisplayName());

        CONNECTOR_SIMPLE_PROPERTY.parse(DISPLAY_NAME_KEY, FOO, connector);
        assertEquals(FOO, connector.getDisplayName());

        CONNECTOR_SNMP_DETECTION.parse(OID_KEY, null, connector);
        assertNull(connector.getDetection());

        CONNECTOR_SNMP_DETECTION.parse(OID_KEY, FOO, connector);
        Detection detection = connector.getDetection();
        assertNotNull(detection);
        List<Criterion> criteria = detection.getCriteria();
        assertEquals(1, criteria.size());
        Criterion criterion = criteria.get(0);
        assertTrue(criterion instanceof SNMPGetNext);
        SNMPGetNext snmpGetNextCriterion = (SNMPGetNext) criterion;
        assertEquals(FOO, snmpGetNextCriterion.getOid());
    }

    @Test
    void testGetConnectorStates() {

        Set<ConnectorState> connectorStates = ConnectorState.getConnectorStates();

        assertNotNull(connectorStates);
        assertEquals(2, connectorStates.size());
        assertTrue(connectorStates.contains(CONNECTOR_SIMPLE_PROPERTY));
        assertTrue(connectorStates.contains(CONNECTOR_SNMP_DETECTION));
    }
}