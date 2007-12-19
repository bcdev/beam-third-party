/*
 * $Id: SpectrumTest.java,v 1.2 2005/02/18 14:20:19 meris Exp $
 *
 * Copyright (C) 2002 by Brockmann Consult (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation. This program is distributed in the hope it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.esa.beam.processor.baer.auxdata;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class SpectrumTest extends TestCase {

    private Spectrum _spectrum;

    public SpectrumTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(SpectrumTest.class);
    }

    /**
     * Initializes the test environment.
     */
    protected void setUp() {
        _spectrum = new Spectrum();
        assertNotNull(_spectrum);
    }

    /**
     * Tests that the object initializes correctly.
     */
    public void testDefaultValues() {
        assertEquals(0, Spectrum.MIN_BAND_IDX);
        assertEquals(14, Spectrum.MAX_BAND_IDX);

        assertEquals("", _spectrum.getShortName());
        assertEquals("", _spectrum.getDescription());
        assertEquals("", _spectrum.getGroundType());

        for (int n = Spectrum.MIN_BAND_IDX; n <= Spectrum.MAX_BAND_IDX; n++) {
            assertEquals(0.0, _spectrum.getValueAt(n), 1e-6);
        }
    }

    /**
     * Tests the correct functionality of the shortName accessor methods
     */
    public void testSetGetShortName() {
        String expName_1 = "nasenmann";
        String expName_2 = "gulp";

        // null not allowed as argument
        try {
            _spectrum.setShortName(null);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
        }

        _spectrum.setShortName(expName_1);
        assertEquals(expName_1, _spectrum.getShortName());

        _spectrum.setShortName(expName_2);
        assertEquals(expName_2, _spectrum.getShortName());
    }

    /**
     * Tests the correct functionality of the description accessor methods.
     */
    public void testSetGetDescription() {
        String expDesc_1 = "a test description for this case";
        String expDesc_2 = "Another very stupid description thingy";

        // null not allowed as argument
        try {
            _spectrum.setDescription(null);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
        }

        _spectrum.setDescription(expDesc_1);
        assertEquals(expDesc_1, _spectrum.getDescription());

        _spectrum.setDescription(expDesc_2);
        assertEquals(expDesc_2, _spectrum.getDescription());
    }

    /**
     * Tests the correct functionality of the values accessors
     */
    public void testSetGetValues() {
        // out of range indices not allowed
        try {
            _spectrum.getValueAt(-3);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
        }

        try {
            _spectrum.getValueAt(15);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
        }

        // set all to theoir index and the re-read
        for (int n = Spectrum.MIN_BAND_IDX; n <= Spectrum.MAX_BAND_IDX; n++) {
            _spectrum.setValueAt(n, (double) n);
        }
        for (int n = Spectrum.MIN_BAND_IDX; n <= Spectrum.MAX_BAND_IDX; n++) {
            assertEquals((double) n, _spectrum.getValueAt(n), 1e-6);
        }
    }

    /**
     * Tests the correct functionality of the ground type accessors
     */
    public void testSetGetGroundType() {
        String expType_1 = "ground";
        String expType_2 = "type";

        // null not allowed as argument
        try {
            _spectrum.setGroundType(null);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
        }

        _spectrum.setGroundType(expType_1);
        assertEquals(expType_1, _spectrum.getGroundType());

        _spectrum.setGroundType(expType_2);
        assertEquals(expType_2, _spectrum.getGroundType());
    }
}
