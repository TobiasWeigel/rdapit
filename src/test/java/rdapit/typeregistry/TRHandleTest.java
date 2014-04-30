/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package rdapit.typeregistry;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 *
 * @author thomz
 */
@RunWith(JUnit4.class)
public class TRHandleTest {
    
    public TRHandleTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of read method, of class TRHandle.
     */
    @Test
    public void testRead() {
        System.out.println("Testing the read method ...");
        String id = "11314.2/0aecc18032a27d5e0b242fcd31cc2e72";
        TRHandle instance = new TRHandle("http://typeregistry.org/registrar/records/");
        String expResult = "success";
        TRType result = instance.read(id);
        assertEquals("Correct result", expResult, result.getStatus());
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    
}
