/*
 * $Id: ToaVegRequestElementFactoryTest.java,v 1.4 2006/03/24 08:09:15 meris Exp $
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
package org.esa.beam.processor.toa;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.esa.beam.framework.param.Parameter;
import org.esa.beam.framework.processor.ProductRef;
import org.esa.beam.framework.processor.RequestElementFactoryException;
import org.esa.beam.processor.common.VegRequestElementFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class ToaVegRequestElementFactoryTest extends TestCase {

    private VegRequestElementFactory _factory;
    private File _file;

    public ToaVegRequestElementFactoryTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(ToaVegRequestElementFactoryTest.class);
    }

    /**
     * Sets up the test environment.
     */
    protected void setUp() {
        _factory = VegRequestElementFactory.getToaVegInstance();
        assertNotNull(_factory);


			_file = new File(  "r:/away_dir/strange_file_not_there.inv");
    }

    /**
     * Tests the correct functionality of the singleton interface.
     */
    public void testSingletonInterface() {
        VegRequestElementFactory ref_1;
        VegRequestElementFactory ref_2;

        // must return something
        ref_1 = VegRequestElementFactory.getToaVegInstance();
        assertNotNull(ref_1);

        // must return something
        ref_2 = VegRequestElementFactory.getToaVegInstance();
        assertNotNull(ref_2);

        // both references must point to the same object
        assertSame(ref_1, ref_2);
    }

    /**
     * Tests the createInputProductRef() method for expected behaviour
     */
    public void testInputProductErrors() {
        String fileFormat = "VegFileFormat";
        String typeId = "VegTypeID";

        // must throw exception when no url is set
        try {
            _factory.createInputProductRef(null, fileFormat, typeId);
            fail("IllegalArgumentException expected");
        } catch (RequestElementFactoryException e) {
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
        }

        // other parameter may be null
        try {
            _factory.createInputProductRef(_file, null, typeId);
        } catch (RequestElementFactoryException e) {
            fail("No Exception expected");
        } catch (IllegalArgumentException e) {
            fail("No Exception expected expected");
        }

        try {
            _factory.createInputProductRef(_file, fileFormat, null);
        } catch (RequestElementFactoryException e) {
            fail("No Exception expected");
        } catch (IllegalArgumentException e) {
            fail("No Exception expected expected");
        }
    }

    /**
     * Tests that createInputProductRef() returns the correct values when fed with an url and two null parameters
     */
    public void testInputProductResults_url_null_null() {
        ProductRef prod = null;
        File file;

        try {
            prod = _factory.createInputProductRef(_file, null, null);
            // we must get something in return
            assertNotNull(prod);
            // prod must give the correct url
            file = prod.getFile();
            assertNotNull(file);
            // compare url as string - PERFORMANCE!
            assertEquals(_file.toString(), file.toString());
            // fileFormat must be null
            assertNull(prod.getFileFormat());
            // typeID must be null
            assertNull(prod.getTypeId());
        } catch (RequestElementFactoryException e) {
            fail("No Exception expected");
        } catch (IllegalArgumentException e) {
            fail("No Exception expected");
        }
    }

    /**
     * Tests that createInputProductRef() returns the correct values when fed with an url and a file format
     */
    public void testInputProductResults_url_file_null() {
        ProductRef prod = null;
        File file;
        String fileFormat = "VegFileFormat";

        try {
            prod = _factory.createInputProductRef(_file, fileFormat, null);
            // we must get something in return
            assertNotNull(prod);
            // prod must give the correct url
            file = prod.getFile();
            assertNotNull(file);
            // toString wurde wegen Performancesteigerung bei URL vergleichen verwendet
            assertEquals(_file.toString(), file.toString());
            // fileFormat must be correct
            assertEquals(fileFormat, prod.getFileFormat());
            // typeID must be null
            assertNull(prod.getTypeId());
        } catch (RequestElementFactoryException e) {
            fail("No Exception expected");
        } catch (IllegalArgumentException e) {
            fail("No Exception expected");
        }
    }

    /**
     * Tests that createInputProductRef() returns the correct values when fed with all parameters
     */
    public void testInputProductResults_url_file_type() {
        ProductRef prod = null;
        File file;
        String fileFormat = "VegFileFormat";
        String typeId = "VegTypeID";

        try {
            prod = _factory.createInputProductRef(_file, fileFormat, typeId);
            // we must get something in return
            assertNotNull(prod);
            // prod must give the correct url
            file = prod.getFile();
            assertNotNull(file);
            // toString wurde wegen Performancesteigerung bei URL vergleichen verwendet
            assertEquals(_file.toString(), file.toString());
            // fileFormat must be correct
            assertEquals(fileFormat, prod.getFileFormat());
            // typeID must be correct
            assertEquals(typeId, prod.getTypeId());
        } catch (RequestElementFactoryException e) {
            fail("No Exception expected");
        } catch (IllegalArgumentException e) {
            fail("No Exception expected");
        }
    }

    /**
     * Tests the output product method for expected behaviour
     */
    public void testOutputProductErrors() {
        String fileFormat = "VegFileFormat";
        String typeId = "VegTypeID";

        // must throw exception when no url is set
        try {
            _factory.createOutputProductRef(null, fileFormat, typeId);
            fail("IllegalArgumentException expected");
        } catch (RequestElementFactoryException e) {
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
        }

        // other parameter may be null
        try {
            _factory.createOutputProductRef(_file, null, typeId);
        } catch (RequestElementFactoryException e) {
            fail("No Exception expected");
        } catch (IllegalArgumentException e) {
            fail("No Exception expected expected");
        }

        try {
            _factory.createOutputProductRef(_file, fileFormat, null);
        } catch (RequestElementFactoryException e) {
            fail("No Exception expected");
        } catch (IllegalArgumentException e) {
            fail("No Exception expected expected");
        }
    }

    /**
     * Tests that createOutputProductRef() returns the correct values when fed with an url and two null parameters
     */
    public void testOutputProductResults_url_null_null() {
        ProductRef prod = null;
        File file;

        try {
            prod = _factory.createOutputProductRef(_file, null, null);
            // we must get something in return
            assertNotNull(prod);
            // prod must give the correct url
            file = prod.getFile();
            assertNotNull(file);
            // toString wurde wegen Performancesteigerung bei URL vergleichen verwendet
            assertEquals(_file.toString(), file.toString());
            // fileFormat must be null
            assertNull(prod.getFileFormat());
            // typeID must be null
            assertNull(prod.getTypeId());
        } catch (RequestElementFactoryException e) {
            fail("No Exception expected");
        } catch (IllegalArgumentException e) {
            fail("No Exception expected");
        }
    }

    /**
     * Tests that createOutputProductRef() returns the correct values when fed with an url and a file format
     */
    public void testOutputProductResults_url_file_null() {
        ProductRef prod = null;
        File file;
        String fileFormat = "VegOutFormat";

        try {
            prod = _factory.createOutputProductRef(_file, fileFormat, null);
            // we must get something in return
            assertNotNull(prod);
            // prod must give the correct url
            file = prod.getFile();
            assertNotNull(file);
            // toString wurde wegen Performancesteigerung bei URL vergleichen verwendet
            assertEquals(_file.toString(), file.toString());
            // fileFormat must be correct
            assertEquals(fileFormat, prod.getFileFormat());
            // typeID must be null
            assertNull(prod.getTypeId());
        } catch (RequestElementFactoryException e) {
            fail("No Exception expected");
        } catch (IllegalArgumentException e) {
            fail("No Exception expected");
        }
    }

    /**
     * Tests that createOutputProductRef() returns the correct values when fed with all parameters
     */
    public void testOutputProductResults_url_file_type() {
        ProductRef prod = null;
        File file;
        String fileFormat = "VegFormat";
        String typeId = "VegType";

        try {
            prod = _factory.createOutputProductRef(_file, fileFormat, typeId);
            // we must get something in return
            assertNotNull(prod);
            // prod must give the correct url
            file = prod.getFile();
            assertNotNull(file);
            // toString wurde wegen Performancesteigerung bei URL vergleichen verwendet
            assertEquals(_file.toString(), file.toString());
            // fileFormat must be correct
            assertEquals(fileFormat, prod.getFileFormat());
            // typeID must be correct
            assertEquals(typeId, prod.getTypeId());
        } catch (RequestElementFactoryException e) {
            fail("No Exception expected");
        } catch (IllegalArgumentException e) {
            fail("No Exception expected");
        }
    }

    /**
     * Tests the correct creation of the parameter bitmask
     */
    public void testBitmaskParameter() {
        Parameter param;
        String correctVal = "l1_flags.LAND_OCEAN";

        // must throw exception on null value argument
        try {
            param = _factory.createParameter(null, null);
            fail("IllegalArgumentException expected");
        } catch (RequestElementFactoryException e) {
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
        }

        // now check the parameter returned for correct settings
        try {
            param = _factory.createParameter(ToaVegConstants.BITMASK_PARAM_NAME, correctVal);
            // we must get a parameter
            assertNotNull(param);
            // name must fit
            assertEquals(ToaVegConstants.BITMASK_PARAM_NAME, param.getName());
            // value class must match
            assertEquals(String.class, param.getValueType());
            // editor must be present
            assertNotNull(param.getEditor());
            // param info must be present
            assertNotNull(param.getProperties());
            // label must fit
            assertEquals(ToaVegConstants.BITMASK_PARAM_LABEL,
                         param.getProperties().getLabel());
            // description must fit
            assertEquals(ToaVegConstants.BITMASK_PARAM_DESCRIPTION,
                         param.getProperties().getDescription());
            // default value must fit
            assertEquals(ToaVegConstants.BITMASK_PARAM_DEFAULT,
                         ((String) param.getProperties().getDefaultValue()));
            // and the value should be set.
            assertEquals(correctVal, param.getValueAsText());
        } catch (RequestElementFactoryException e) {
            fail("no RequestElementFactoryException expected");
        }
    }


}
