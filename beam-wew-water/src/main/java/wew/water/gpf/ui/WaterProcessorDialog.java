package wew.water.gpf.ui;

import com.bc.ceres.binding.Property;
import com.bc.ceres.binding.PropertyDescriptor;
import com.bc.ceres.binding.PropertySet;
import com.bc.ceres.binding.ValidationException;
import com.bc.ceres.binding.ValueSet;
import com.bc.ceres.binding.accessors.DefaultPropertyAccessor;
import com.bc.ceres.swing.binding.BindingContext;
import com.bc.ceres.swing.selection.AbstractSelectionChangeListener;
import com.bc.ceres.swing.selection.Selection;
import com.bc.ceres.swing.selection.SelectionChangeEvent;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductNodeEvent;
import org.esa.beam.framework.datamodel.ProductNodeListener;
import org.esa.beam.framework.datamodel.RasterDataNode;
import org.esa.beam.framework.gpf.GPF;
import org.esa.beam.framework.gpf.OperatorSpi;
import org.esa.beam.framework.gpf.internal.RasterDataNodeValues;
import org.esa.beam.framework.gpf.ui.DefaultIOParametersPanel;
import org.esa.beam.framework.gpf.ui.OperatorMenu;
import org.esa.beam.framework.gpf.ui.OperatorParameterSupport;
import org.esa.beam.framework.gpf.ui.SingleTargetProductDialog;
import org.esa.beam.framework.gpf.ui.SourceProductSelector;
import org.esa.beam.framework.gpf.ui.TargetProductSelectorModel;
import org.esa.beam.framework.ui.AppContext;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Tonio Fincke
 */
public class WaterProcessorDialog extends SingleTargetProductDialog {

    private final String operatorName;
    private final OperatorSpi operatorSpi;
    private DefaultIOParametersPanel ioParametersPanel;
    private final OperatorParameterSupport parameterSupport;
    private final BindingContext bindingContext;

    private JTabbedPane form;
    private PropertyDescriptor[] rasterDataNodeTypeProperties;
    private String targetProductNameSuffix;
    private ProductChangedHandler productChangedHandler;

    public WaterProcessorDialog(String operatorName, AppContext appContext, String title, String helpID) {
        super(appContext, title, ID_APPLY_CLOSE, helpID);
        this.operatorName = operatorName;
        targetProductNameSuffix = "";

        operatorSpi = GPF.getDefaultInstance().getOperatorSpiRegistry().getOperatorSpi(operatorName);
        if (operatorSpi == null) {
            throw new IllegalArgumentException("operatorName");
        }

        ioParametersPanel = new DefaultIOParametersPanel(getAppContext(), operatorSpi, getTargetProductSelector());

        parameterSupport = new OperatorParameterSupport(operatorSpi.getOperatorClass());
        final ArrayList<SourceProductSelector> sourceProductSelectorList = ioParametersPanel.getSourceProductSelectorList();
        final PropertySet propertyContainer = parameterSupport.getPopertySet();
        bindingContext = new BindingContext(propertyContainer);

        if (propertyContainer.getProperties().length > 0) {
            if (!sourceProductSelectorList.isEmpty()) {
                Property[] properties = propertyContainer.getProperties();
                List<PropertyDescriptor> rdnTypeProperties = new ArrayList<PropertyDescriptor>(properties.length);
                for (Property property : properties) {
                    PropertyDescriptor parameterDescriptor = property.getDescriptor();
                    if (parameterDescriptor.getAttribute(RasterDataNodeValues.ATTRIBUTE_NAME) != null) {
                        rdnTypeProperties.add(parameterDescriptor);
                    }
                }
                rasterDataNodeTypeProperties = rdnTypeProperties.toArray(
                        new PropertyDescriptor[rdnTypeProperties.size()]);
            }
        }
        productChangedHandler = new ProductChangedHandler();
        if (!sourceProductSelectorList.isEmpty()) {
            sourceProductSelectorList.get(0).addSelectionChangeListener(productChangedHandler);
        }
    }

    @Override
    public int show() {
        ioParametersPanel.initSourceProductSelectors();
        if (form == null) {
            initForm();
            if (getJDialog().getJMenuBar() == null) {
                final OperatorMenu operatorMenu = createDefaultMenuBar();
                getJDialog().setJMenuBar(operatorMenu.createDefaultMenu());
            }
        }
        setContent(form);
        return super.show();
    }

    @Override
    public void hide() {
        productChangedHandler.releaseProduct();
        ioParametersPanel.releaseSourceProductSelectors();
        super.hide();
    }

    @Override
    protected Product createTargetProduct() throws Exception {
        final HashMap<String, Product> sourceProducts = ioParametersPanel.createSourceProductsMap();
        return GPF.createProduct(operatorName, parameterSupport.getParameterMap(), sourceProducts);
    }

    public String getTargetProductNameSuffix() {
        return targetProductNameSuffix;
    }

    private void initForm() {
        form = new JTabbedPane();
        form.add("I/O Parameters", ioParametersPanel);
        if (bindingContext.getPropertySet().getProperties().length > 0) {
            final JPanel waterVariablesPanel = new WaterVariablesPanel(getAppContext(), bindingContext);
            form.add("Processing Parameters", new JScrollPane(waterVariablesPanel));
        }
    }

    private OperatorMenu createDefaultMenuBar() {
        return new OperatorMenu(getJDialog(),
                                operatorSpi.getOperatorClass(),
                                parameterSupport,
                                getHelpID());
    }

    private class ProductChangedHandler extends AbstractSelectionChangeListener implements ProductNodeListener {

        private Product currentProduct;

        public void releaseProduct() {
            if (currentProduct != null) {
                currentProduct.removeProductNodeListener(this);
                currentProduct = null;
            }
        }

        @Override
        public void selectionChanged(SelectionChangeEvent event) {
            Selection selection = event.getSelection();
            if (selection != null) {
                final Product selectedProduct = (Product) selection.getSelectedValue();
                if (selectedProduct != currentProduct) {
                    if (currentProduct != null) {
                        currentProduct.removeProductNodeListener(this);
                    }
                    currentProduct = selectedProduct;
                    if (currentProduct != null) {
                        currentProduct.addProductNodeListener(this);
                    }
                    updateTargetProductname();
                    updateValueSets(currentProduct);
                    try {
                        if(!bindingContext.getPropertySet().isPropertyDefined(WaterFormConstants.PROPERTY_KEY_SOURCE_PRODUCT)) {
                            final PropertyDescriptor descriptor = new PropertyDescriptor(WaterFormConstants.PROPERTY_KEY_SOURCE_PRODUCT, Product[].class);
                            final Property property = new Property(descriptor, new DefaultPropertyAccessor());
                            bindingContext.getPropertySet().addProperty(property);
                        }
                        bindingContext.getPropertySet().getProperty(WaterFormConstants.PROPERTY_KEY_SOURCE_PRODUCT).setValue(new Product[]{selectedProduct});
                    } catch (ValidationException e) {
                        final String msg = String.format("Cannot display source product.\n%s", e.getMessage());
                        getAppContext().handleError(msg, e);
                    }
                }
            }
        }

        @Override
        public void nodeAdded(ProductNodeEvent event) {
            handleProductNodeEvent(event);
        }

        @Override
        public void nodeChanged(ProductNodeEvent event) {
            handleProductNodeEvent(event);
        }

        @Override
        public void nodeDataChanged(ProductNodeEvent event) {
            handleProductNodeEvent(event);
        }

        @Override
        public void nodeRemoved(ProductNodeEvent event) {
            handleProductNodeEvent(event);
        }

        private void updateTargetProductname() {
            String productName = "";
            if (currentProduct != null) {
                productName = currentProduct.getName();
            }
            final TargetProductSelectorModel targetProductSelectorModel = getTargetProductSelector().getModel();
            targetProductSelectorModel.setProductName(productName + getTargetProductNameSuffix());
        }

        private void handleProductNodeEvent(ProductNodeEvent event) {
            updateValueSets(currentProduct);
        }

        private void updateValueSets(Product product) {
            if (rasterDataNodeTypeProperties != null) {
                for (PropertyDescriptor propertyDescriptor : rasterDataNodeTypeProperties) {
                    updateValueSet(propertyDescriptor, product);
                }
            }
        }
    }

    private static void updateValueSet(PropertyDescriptor propertyDescriptor, Product product) {
        String[] values = new String[0];
        if (product != null) {
            Object object = propertyDescriptor.getAttribute(RasterDataNodeValues.ATTRIBUTE_NAME);
            if (object != null) {
                Class<? extends RasterDataNode> rasterDataNodeType = (Class<? extends RasterDataNode>) object;
                boolean includeEmptyValue = !propertyDescriptor.isNotNull() && !propertyDescriptor.isNotEmpty() && !propertyDescriptor.getType().isArray();
                values = RasterDataNodeValues.getNames(product, rasterDataNodeType, includeEmptyValue);
            }
        }
        propertyDescriptor.setValueSet(new ValueSet(values));
    }

    public void setTargetProductNameSuffix(String suffix) {
        targetProductNameSuffix = suffix;
    }

}
