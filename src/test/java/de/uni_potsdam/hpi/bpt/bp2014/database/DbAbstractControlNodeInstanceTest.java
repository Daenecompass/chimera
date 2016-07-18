package de.uni_potsdam.hpi.bpt.bp2014.database;

import de.uni_potsdam.hpi.bpt.bp2014.AbstractDatabaseDependentTest;
import de.uni_potsdam.hpi.bpt.bp2014.database.controlnodes.DbControlNodeInstance;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class DbAbstractControlNodeInstanceTest extends AbstractDatabaseDependentTest {
    /**
     *
     */
    @Test
    public void testExistControlNodeInstance(){
        DbControlNodeInstance dbControlNodeInstance = new DbControlNodeInstance();
        assertTrue(dbControlNodeInstance.existControlNodeInstance(4, 126));
        assertFalse(dbControlNodeInstance.existControlNodeInstance(4, 100));
        assertFalse(dbControlNodeInstance.existControlNodeInstance(999, 100));
        assertFalse(dbControlNodeInstance.existControlNodeInstance(3, 999));
        assertFalse(dbControlNodeInstance.existControlNodeInstance(999, 999));
    }

    /**
     *
     */
    @Test
    public void testGetControlNodeInstanceID() {
        DbControlNodeInstance dbControlNodeInstance = new DbControlNodeInstance();
        assertEquals(178, dbControlNodeInstance.getControlNodeInstanceId(4, 168));
    }

    /**
     *
     */
    @Test
    public void testGetActivitiesForFragmentInstanceID() {
        DbControlNodeInstance dbControlNodeInstance = new DbControlNodeInstance();
        assertEquals(2, (int)dbControlNodeInstance.getActivitiesForFragmentInstanceId(83).get(0));
        assertEquals(5, (int)dbControlNodeInstance.getActivitiesForFragmentInstanceId(83).get(1));
    }
}
