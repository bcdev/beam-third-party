/*
 * $Id: BaerAlgorithmTest.java,v 1.2 2005/02/18 14:20:19 meris Exp $
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
package org.esa.beam.processor.baer.algorithm;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.esa.beam.processor.baer.auxdata.*;

public class BaerAlgorithmTest extends TestCase {

    private BaerAlgorithm _algo;

    public BaerAlgorithmTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(BaerAlgorithmTest.class);
    }

    /**
     * Initializes the test environment
     */
    protected void setUp() {
        _algo = new BaerAlgorithm();
        assertNotNull(_algo);
    }

    // @todo - implement more tests once the functionality is clearer ...
    /**
     * Tests the correct functionality of the default constructor.
     */
    public void testDefaultConstruction() {

    }

    /**
     * Tests the functionality of the aerosol phase access interface injection method
     */
    public void testAerPhaseAccesSetting() {
        // null not allowed as argument
        try {
            _algo.setAerPhaseAccess(null);
            fail ("Exception expected");
        } catch(IllegalArgumentException e) {
        }

        // shall accep a valid argument
        try {
            _algo.setAerPhaseAccess(new AerPhaseLoader());
        } catch(IllegalArgumentException e) {
            fail ("No exception expected");
        }
    }

    /**
     * Tests the functionality of the relative phase access interface injection method
     */
    public void testRelPhaseAccesSetting() {
        // null not allowed as argument
        try {
            _algo.setRelAerPhaseAccess(null);
            fail ("Exception expected");
        } catch(IllegalArgumentException e) {
        }

        // shall accep a valid argument
        try {
            _algo.setRelAerPhaseAccess(new RelAerPhaseLoader());
        } catch(IllegalArgumentException e) {
            fail ("No exception expected");
        }
    }

    /**
     * Tests the functionality of the ndvi interface injection method
     */
    public void testNdviAccessSetting() {
        // null not allowed as argument
        try {
            _algo.setNdviAccess(null);
            fail ("Exception expected");
        } catch(IllegalArgumentException e) {
        }

        // shall accep a valid argument
        try {
            _algo.setNdviAccess(new NdviLoader());
        } catch(IllegalArgumentException e) {
            fail ("No exception expected");
        }
    }

    /**
     * Tests the functionality of the ground reflectances interface injection method
     */
    public void testGroundReflectanceAccessSetting() {
        // null not allowed as argument
        try {
            _algo.setGroundReflectanceAccess(null);
            fail ("Exception expected");
        } catch(IllegalArgumentException e) {
        }

        // shall accep a valid argument
        try {
            _algo.setGroundReflectanceAccess(new GroundReflectanceLoader());
        } catch(IllegalArgumentException e) {
            fail ("No exception expected");
        }
    }


     /**
     * Tests the functionality of the soil fraction interface injection method
     */
    public void testSoilFractionAccessSetting() {
        // null not allowed as argument
        try {
            _algo.setSoilFractionAccess(null);
            fail ("Exception expected");
        } catch(IllegalArgumentException e) {
        }

        // shall accep a valid argument
        try {
            _algo.setSoilFractionAccess(new SoilFractionLoader());
        } catch(IllegalArgumentException e) {
            fail ("No exception expected");
        }
    }

     /**
     * Tests the functionality of the F_Tuning interface injection method
     */
    public void testFTuningAccessSetting() {
        // null not allowed as argument
        try {
            _algo.setGroundReflectanceAccess(null);
            fail ("Exception expected");
        } catch(IllegalArgumentException e) {
        }

        // shall accep a valid argument
        try {
            _algo.setF_TuningAccess(new F_TuningLoader());
        } catch(IllegalArgumentException e) {
            fail ("No exception expected");
        }
    }

     /**
     * Tests the functionality of the Aerosol diffuse transmission interface injection method
     */
    public void testAerDiffTransmAccessSetting() {
        // null not allowed as argument
        try {
            _algo.setAerDiffTransmAccess(null);
            fail ("Exception expected");
        } catch(IllegalArgumentException e) {
        }

        // shall accep a valid argument
        try {
            _algo.setAerDiffTransmAccess(new AerDiffTransmLoader());
        } catch(IllegalArgumentException e) {
            fail ("No exception expected");
        }
    }

    /**
     * Tests the functionality of the hemispheric reflectances interface injection method
     */
    public void testHemisphReflecAccessSetting() {
        // null not allowed as argument
        try {
            _algo.setHemisphReflecAccess(null);
            fail ("Exception expected");
        } catch(IllegalArgumentException e) {
        }

        // shall accep a valid argument
        try {
            _algo.setHemisphReflecAccess(new HemisphReflecLoader());
        } catch(IllegalArgumentException e) {
            fail ("No exception expected");
        }
    }

}
