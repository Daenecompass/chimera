package de.uni_potsdam.hpi.bpt.bp2014;

/**
 * Created by jaspar.mang on 14.01.15.
 */

import de.uni_potsdam.hpi.bpt.bp2014.database.DbGatewayInstance;
import org.junit.Test;

import static org.junit.Assert.*;

public class DbGatewayInstanceTest {
    @Test
    public void testGetType(){
        DbGatewayInstance gatewayInstance = new DbGatewayInstance();
        assertEquals("AND", gatewayInstance.getType(100));
    }
    @Test
    public void testGetState(){
        DbGatewayInstance gatewayInstance = new DbGatewayInstance();
        assertEquals("Init", gatewayInstance.getState(100));
    }
}
