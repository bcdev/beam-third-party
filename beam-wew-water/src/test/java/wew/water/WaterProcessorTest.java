package wew.water;

import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductData;
import org.esa.beam.framework.datamodel.TiePointGrid;
import org.esa.beam.framework.processor.ProcessorException;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Marco Peters
 * @version $Revision: $ $Date: $
 * @since FUB-WeW 1.3
 */
public class WaterProcessorTest {

    @Test
    public void testValidationWithTiePointGrids() {
        Product testProduct = new Product("test", "type", 10, 10);
        addTiePointGrids(testProduct, WaterProcessor.REQUIRED_TIE_POINT_GRID_NAMES);
        addBands(testProduct, WaterProcessor.L1FLAGS_INPUT_BAND_NAME);
        try {
            WaterProcessor.validateInputProduct(testProduct);
        } catch (ProcessorException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testValidationWithBands() {
        Product testProduct = new Product("test", "type", 10, 10);
        addBands(testProduct, WaterProcessor.REQUIRED_TIE_POINT_GRID_NAMES);
        addBands(testProduct, WaterProcessor.L1FLAGS_INPUT_BAND_NAME);
        try {
            WaterProcessor.validateInputProduct(testProduct);
        } catch (ProcessorException e) {
            fail(e.getMessage());
        }
    }

    @Test(expected = ProcessorException.class)
    public void testValidationWithoutFlagsBand() throws ProcessorException {
        Product testProduct = new Product("test", "type", 10, 10);
        addTiePointGrids(testProduct, WaterProcessor.REQUIRED_TIE_POINT_GRID_NAMES);
        WaterProcessor.validateInputProduct(testProduct);
    }

    @Test(expected = ProcessorException.class)
    public void testValidationWithoutTiePointGrid() throws ProcessorException {
        Product testProduct = new Product("test", "type", 10, 10);
        addBands(testProduct, WaterProcessor.L1FLAGS_INPUT_BAND_NAME);
        WaterProcessor.validateInputProduct(testProduct);
    }

    private void addBands(Product testProduct, String... names) {
        for (String name : names) {
            testProduct.addBand(name, ProductData.TYPE_INT8);
        }
    }

    private void addTiePointGrids(Product testProduct, String... names) {
        for (String name : names) {
            int sceneRasterWidth = testProduct.getSceneRasterWidth();
            int sceneRasterHeight = testProduct.getSceneRasterHeight();
            testProduct.addTiePointGrid(new TiePointGrid(name, sceneRasterWidth, sceneRasterHeight,
                                                         0, 0, 1, 1, new float[sceneRasterWidth * sceneRasterHeight]));
        }
    }

}
