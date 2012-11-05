package wew.water.gpf.ui;

import com.bc.ceres.binding.ValidationException;
import com.bc.ceres.swing.binding.BindingContext;
import org.esa.beam.framework.datamodel.Product;

import java.beans.PropertyChangeListener;

/**
 * @author Tonio Fincke
 */
interface WaterFormConstants {

    static String PROPERTY_KEY_SOURCE_PRODUCT = "sourceProduct";
    static String PROPERTY_KEY_COMPUTE_CHL = "computeCHL";
    static String PROPERTY_KEY_COMPUTE_YS = "computeYS";
    static String PROPERTY_KEY_COMPUTE_TSM = "computeTSM";
    static String PROPERTY_KEY_COMPUTE_ATMO = "computeAtmCorr";
    static String PROPERTY_KEY_CHECK_SUSPECT = "checkWhetherSuspectIsValid";
    static String PROPERTY_KEY_EXPRESSION = "expression";

}
