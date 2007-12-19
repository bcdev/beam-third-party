package org.esa.beam.processor.baer;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.esa.beam.framework.param.Parameter;
import org.esa.beam.framework.processor.ProductRef;
import org.esa.beam.framework.processor.RequestElementFactoryException;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: tom
 * Date: Jul 23, 2003
 * Time: 3:23:50 PM
 * To change this template use Options | File Templates.
 */
public class BaerRequestElementFactoryTest extends TestCase {

    private BaerRequestElementFactory _factory;
    private File _file;

    public BaerRequestElementFactoryTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(BaerRequestElementFactoryTest.class);
    }

    /**
     * Sets up the test environment.
     */
    protected void setUp() {
        _factory = BaerRequestElementFactory.getInstance();
        assertNotNull(_factory);

        _file = new File("z:/not_existing_dir/invalid_file.inv");
    }

    /**
     * Tests the correct functionality of the singleton interface.
     */
    public void testSingletonInterface() {
        BaerRequestElementFactory factory_1;
        BaerRequestElementFactory factory_2;

        // first call MUST return some reference
        factory_1 = BaerRequestElementFactory.getInstance();
        assertNotNull(factory_1);

        // second cvall MUST return some reference
        factory_2 = BaerRequestElementFactory.getInstance();
        assertNotNull(factory_2);

        // both references must point to the same object
        assertSame(factory_1, factory_2);
    }

    /**
     * Tests the createInputProductRef() method for expected behaviour
     */
    public void testInputProductErrors() {
        String fileFormat = "BaerFileFormat";
        String typeId = "BaerTypeID";

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
     * Tests that createInputProductRef() returns the correct values
     * when fed with an url and two null parameters
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
     * Tests that createInputProductRef() returns the correct values
     * when fed with an url and a file format
     */
    public void testInputProductResults_url_file_null() {
        ProductRef prod = null;
        File file;
        String fileFormat = "BaerFileFormat";

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
     * Tests that createInputProductRef() returns the correct values
     * when fed with all parameters
     */
    public void testInputProductResults_url_file_type() {
        ProductRef prod = null;
        File file;
        String fileFormat = "BaerFileFormat";
        String typeId = "BaerTypeID";

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
        String fileFormat = "BaerFileFormat";
        String typeId = "BaerTypeID";

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
     * Tests that createOutputProductRef() returns the correct values
     * when fed with an url and two null parameters
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
     * Tests that createOutputProductRef() returns the correct values
     * when fed with an url and a file format
     */
    public void testOutputProductResults_url_file_null() {
        ProductRef prod = null;
        File file;
        String fileFormat = "BaerOutFormat";

        try {
            prod = _factory.createOutputProductRef(_file, fileFormat, null);
            // we must get something in return
            assertNotNull(prod);
            // prod must give the correct url
            file = prod.getFile();
            assertNotNull(file);
            // toString wurde wegen Performancesteigerung bei URL vergleichen verwendet
            assertEquals(_file, file);
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
     * Tests that createOutputProductRef() returns the correct values
     * when fed with all parameters
     */
    public void testOutputProductResults_url_file_type() {
        ProductRef prod = null;
        File file;
        String fileFormat = "BaerFormat";
        String typeId = "BaerType";

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
     * Tests the correct creation of the parameter BuchholzRayleigh
     */
    public void testRayleighFormulaParameter() {
        //  String correctVal = "FROEH";

        // must throw exception on null value argument
        try {
            _factory.createParameter(null, null);
            fail("IllegalArgumentException expected");
        } catch (RequestElementFactoryException e) {
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
        }


    }

    /**
     * Tests the correct creation of the parameter bitmask
     */
    public void testBitmaskParameter() {
        String correctVal = "l2_flags.LAND";

        // must throw exception on null value argument
        try {
            _factory.createParameter(null, null);
            fail("IllegalArgumentException expected");
        } catch (RequestElementFactoryException e) {
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
        }

        // now check the parameter returned for correct settings
        try {
            Parameter param = _factory.createParameter(BaerConstants.BITMASK_PARAM_NAME, correctVal);
            // we must get a parameter
            assertNotNull(param);
            // name must fit
            assertEquals(BaerConstants.BITMASK_PARAM_NAME, param.getName());
            // value class must match
            assertEquals(String.class, param.getValueType());
            // editor must be present
            assertNotNull(param.getEditor());
            // param info must be present
            assertNotNull(param.getProperties());
            // label must fit
            assertEquals(BaerConstants.BITMASK_PARAM_LABEL,
                         param.getProperties().getLabel());
            // description must fit
            assertEquals(BaerConstants.BITMASK_PARAM_DESCRIPTION,
                         param.getProperties().getDescription());
            // default value must fit
            assertEquals(BaerConstants.BITMASK_PARAM_DEFAULT,
                         ((String) param.getProperties().getDefaultValue()));
            // and the value should be set.
            assertEquals(correctVal, param.getValueAsText());
        } catch (RequestElementFactoryException e) {
            fail("no RequestElementFactoryException expected");
        }
    }

    public void testAerosolPhaseParameter() {
        Parameter param;
        String correctVal = "DESERT";

        // now check the parameter returned for correct settings
        try {
            param = _factory.createParameter(BaerConstants.AER_PHASE_PARAM_NAME, correctVal);
            // we must get a parameter
            assertNotNull(param);
            // name must fit
            assertEquals(BaerConstants.AER_PHASE_PARAM_NAME, param.getName());
            // value class must match
            assertEquals(String[].class, param.getValueType());
            // editor must be present
            assertNotNull(param.getEditor());
            // param info must be present
            assertNotNull(param.getProperties());
            // label must fit
            assertEquals(BaerConstants.AER_PHASE_PARAM_LABEL,
                         param.getProperties().getLabel());
            // description must fit
            assertEquals(BaerConstants.AER_PHASE_PARAM_DESCRIPTION,
                         param.getProperties().getDescription());
            // and the value should be set.
            assertEquals(correctVal, param.getValueAsText());
        } catch (RequestElementFactoryException e) {
            fail("no RequestElementFactoryException expected");
        }
    }
}
