package wew.water;

import org.esa.beam.dataio.envisat.EnvisatConstants;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * @author Marco Peters
 * @version $Revision: $ $Date: $
 * @since FUB-WeW 1.3
 */
public class WaterProcessorTest {

    @Test
    public void testProductTypeAccaptanceWithValids() {
        String[] validTypes = {
                EnvisatConstants.MERIS_RR_L1B_PRODUCT_TYPE_NAME,
                EnvisatConstants.MERIS_FR_L1B_PRODUCT_TYPE_NAME,
                EnvisatConstants.MERIS_FSG_L1B_PRODUCT_TYPE_NAME,
                EnvisatConstants.MERIS_FRG_L1B_PRODUCT_TYPE_NAME,
                EnvisatConstants.MERIS_FRS_L1B_PRODUCT_TYPE_NAME,
                "MER_RR__1N",   // ICOL
        };
        for (String validType : validTypes) {
            assertTrue(String.format("Failed for valid type [%s]", validType),
                       WaterProcessor.isAcceptedInputType(validType));
        }
    }

    @Test
    public void testProductTypeAccaptanceWithInValids() {
        String[] validTypes = {
                EnvisatConstants.MERIS_RR_L2_PRODUCT_TYPE_NAME,
                EnvisatConstants.MERIS_FR_L2_PRODUCT_TYPE_NAME,
                "MER_RR__1X",
                "Some_MER_TYPE_1P",
                "ONLY_MER_RR__1P_VALID_IN_MIDDLE"
        };
        for (String validType : validTypes) {
            assertFalse(WaterProcessor.isAcceptedInputType(validType));
        }
    }
}
