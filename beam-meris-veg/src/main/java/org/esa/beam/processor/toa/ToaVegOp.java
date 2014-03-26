/*
 * Copyright (C) 2012 Brockmann Consult GmbH (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 */

package org.esa.beam.processor.toa;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.jnn.JnnException;
import org.esa.beam.dataio.envisat.EnvisatConstants;
import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.FlagCoding;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductData;
import org.esa.beam.framework.datamodel.ProductNodeFilter;
import org.esa.beam.framework.gpf.OperatorException;
import org.esa.beam.framework.gpf.OperatorSpi;
import org.esa.beam.framework.gpf.annotations.OperatorMetadata;
import org.esa.beam.framework.gpf.annotations.Parameter;
import org.esa.beam.framework.gpf.annotations.SourceProduct;
import org.esa.beam.framework.gpf.pointop.PixelOperator;
import org.esa.beam.framework.gpf.pointop.ProductConfigurer;
import org.esa.beam.framework.gpf.pointop.Sample;
import org.esa.beam.framework.gpf.pointop.SampleConfigurer;
import org.esa.beam.framework.gpf.pointop.WritableSample;
import org.esa.beam.framework.processor.ProcessorException;
import org.esa.beam.processor.common.utils.VegFlagsManager;
import org.esa.beam.processor.common.utils.VegGenericPixel;
import org.esa.beam.processor.common.utils.VegProcessorConfiguration;
import org.esa.beam.processor.toa.algorithm.ToaVegAlgorithm;
import org.esa.beam.processor.toa.auxdata.ToaVegInputStatisticsLoader;
import org.esa.beam.processor.toa.auxdata.ToaVegOutputStatisticsLoader;
import org.esa.beam.processor.toa.auxdata.ToaVegUncertaintyModelLoader;
import org.esa.beam.processor.toa.utils.ToaVegMerisPixel;
import org.esa.beam.processor.toa.utils.ToaVegProcessorConfigurationParser;
import org.esa.beam.util.ResourceInstaller;
import org.esa.beam.util.StringUtils;
import org.esa.beam.util.SystemUtils;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

@OperatorMetadata(alias = "ToaVeg", authors = "Martin Boettcher, Ralf Quast", copyright = "Brockmann Consult GmbH",
                  version = "1.1.2",
                  description = "Computes LAI from MERIS products.")
public class ToaVegOp extends PixelOperator {

    @Parameter(defaultValue = "false", label = "If set to true, Lai will be multiplied by 10000 and written as int")
    private boolean outputLaiAsInt = false;

    @SourceProduct(alias = "source",
                   description = "The path of the MERIS source product",
                   label = "MERIS source product",
                   bands = {
                           "radiance_1",
                           "radiance_2",
                           "radiance_3",
                           "radiance_4",
                           "radiance_5",
                           "radiance_6",
                           "radiance_7",
                           "radiance_8",
                           "radiance_9",
                           "radiance_10",
                           "radiance_12",
                           "radiance_13",
                           "radiance_14"
                           // TODO - can tie-point grids be added here?
                   })
    private Product sourceProduct;

    private final transient ToaVegAlgorithm algorithm = new ToaVegAlgorithm();
    private final float[] solarSpecFlux = new float[ToaVegConstants.NUM_BANDS];

    @Override
    protected void computePixel(int x, int y, Sample[] sourceSamples, WritableSample[] targetSamples) {
        ToaVegMerisPixel inPixel = new ToaVegMerisPixel();
        VegGenericPixel outPixel = new VegGenericPixel();
        outPixel.reset();

        if (!sourceSamples[0].getBoolean()) {
            outPixel.setInvalidInputFlag();
        } else {
            inPixel.setBand_Lat(sourceSamples[1].getFloat());
            inPixel.setBand_Lon(sourceSamples[2].getFloat());
            inPixel.setBand_Sza(sourceSamples[3].getFloat());
            inPixel.setBand_Saa(sourceSamples[4].getFloat());
            inPixel.setBand_Vza(sourceSamples[5].getFloat());
            inPixel.setBand_Vaa(sourceSamples[6].getFloat());
            inPixel.setBand_Pressure(sourceSamples[7].getFloat());
            for (int n = 0; n < ToaVegConstants.NUM_BANDS; n++) {
                inPixel.setBand(sourceSamples[8 + n].getFloat(), n);
                inPixel.setBand_SolarSpecFlux(solarSpecFlux[n],n);
            }
            synchronized (algorithm){
                algorithm.processPixel(inPixel, outPixel);
            }
        }

        if (outputLaiAsInt) {
            targetSamples[0].set((int) (outPixel.getBand_LAI() * 10000.0f));
        } else {
            targetSamples[0].set(outPixel.getBand_LAI());
        }
        targetSamples[1].set(outPixel.getBand_fCover());
        targetSamples[2].set(outPixel.getBand_CabxLAI());
        targetSamples[3].set(outPixel.getBand_fAPAR());
        targetSamples[4].set(outPixel.getBand_sigma_LAI());
        targetSamples[5].set(outPixel.getBand_sigma_fCover());
        targetSamples[6].set(outPixel.getBand_sigma_fApar());
        targetSamples[7].set(outPixel.getBand_sigma_LAIxCab());
        targetSamples[8].set(outPixel.getFlagMask());
    }

    @Override
    protected void configureSourceSamples(SampleConfigurer sampleConfigurer) throws OperatorException {

        sampleConfigurer.defineSample(0, "_mask_");
        sampleConfigurer.defineSample(1, ToaVegConstants.LAT_TIEPOINT_NAME);
        sampleConfigurer.defineSample(2, ToaVegConstants.LON_TIEPOINT_NAME);
        sampleConfigurer.defineSample(3, ToaVegConstants.SZA_TIEPOINT_NAME);
        sampleConfigurer.defineSample(4, ToaVegConstants.SAA_TIEPOINT_NAME);
        sampleConfigurer.defineSample(5, ToaVegConstants.VZA_TIEPOINT_NAME);
        sampleConfigurer.defineSample(6, ToaVegConstants.VAA_TIEPOINT_NAME);
        sampleConfigurer.defineSample(7, ToaVegConstants.PRESS_TIEPOINT_NAME);
        for (int i = 0; i < ToaVegConstants.REFLEC_BAND_NAMES.length; i++) {
            sampleConfigurer.defineSample(8 + i, ToaVegConstants.REFLEC_BAND_NAMES[i]);
        }
    }

    @Override
    protected void configureTargetSamples(SampleConfigurer sampleConfigurer) throws OperatorException {
        sampleConfigurer.defineSample(0, ToaVegConstants.LAI_BAND_NAME);
        sampleConfigurer.defineSample(1, ToaVegConstants.FCOVER_BAND_NAME);
        sampleConfigurer.defineSample(2, ToaVegConstants.LAIXCAB_BAND_NAME);
        sampleConfigurer.defineSample(3, ToaVegConstants.FAPAR_BAND_NAME);
        sampleConfigurer.defineSample(4, ToaVegConstants.SIGMA_LAI_BAND_NAME);
        sampleConfigurer.defineSample(5, ToaVegConstants.SIGMA_FCOVER_BAND_NAME);
        sampleConfigurer.defineSample(6, ToaVegConstants.SIGMA_FAPAR_BAND_NAME);
        sampleConfigurer.defineSample(7, ToaVegConstants.SIGMA_LAIXCAB_BAND_NAME);
        sampleConfigurer.defineSample(8, ToaVegConstants.VEG_FLAGS_BAND_NAME);
    }

    @Override
    protected void configureTargetProduct(ProductConfigurer productConfigurer) {

        //final Product sourceProduct = productConfigurer.getSourceProduct();
        for (int n=0; n < ToaVegConstants.NUM_BANDS; n++) {
            solarSpecFlux[n] = sourceProduct.getBand(ToaVegConstants.REFLEC_BAND_NAMES[n]).getSolarFlux();
        }

        productConfigurer.copyTiePointGrids();
        productConfigurer.copyBands(new ProductNodeFilter<Band>() {
            @Override
            public boolean accept(Band band) {
                return band.getFlagCoding() != null;
            }
        });

        productConfigurer.copyBands(EnvisatConstants.MERIS_AMORGOS_L1B_CORR_LATITUDE_BAND_NAME,
                                    EnvisatConstants.MERIS_AMORGOS_L1B_CORR_LONGITUDE_BAND_NAME,
                                    EnvisatConstants.MERIS_AMORGOS_L1B_ALTIUDE_BAND_NAME);
        productConfigurer.copyGeoCoding();

        int laiProductType = ProductData.TYPE_FLOAT32;
        if (outputLaiAsInt) {
            laiProductType = ProductData.TYPE_INT16;
        }
        final Band laiBand = productConfigurer.addBand(ToaVegConstants.LAI_BAND_NAME, laiProductType);
        laiBand.setDescription(ToaVegConstants.LAI_BAND_DESCRIPTION);
        laiBand.setUnit(ToaVegConstants.LAI_BAND_UNIT);
        laiBand.setValidPixelExpression(
                "!" + ToaVegConstants.VEG_FLAGS_BAND_NAME + "." + VegFlagsManager.INVALID_FLAG_NAME + " && !" + ToaVegConstants.VEG_FLAGS_BAND_NAME + "." + VegFlagsManager.LAI_OUT_OF_RANGE_FLAG_NAME);

        final Band _fCover_band = productConfigurer.addBand(ToaVegConstants.FCOVER_BAND_NAME, ProductData.TYPE_FLOAT32);
        _fCover_band.setDescription(ToaVegConstants.FCOVER_BAND_DESCRIPTION);
        _fCover_band.setValidPixelExpression(
                "!" + ToaVegConstants.VEG_FLAGS_BAND_NAME + "." + VegFlagsManager.INVALID_FLAG_NAME + " && !" + ToaVegConstants.VEG_FLAGS_BAND_NAME + "." + VegFlagsManager.FCOVER_OUT_OF_RANGE_FLAG_NAME);

        final Band _cabxLai_band = productConfigurer.addBand(ToaVegConstants.LAIXCAB_BAND_NAME, ProductData.TYPE_FLOAT32);
        _cabxLai_band.setDescription(ToaVegConstants.LAIXCAB_BAND_DESCRIPTION);
        _cabxLai_band.setUnit(ToaVegConstants.LAIXCAB_BAND_UNIT);
        _cabxLai_band.setValidPixelExpression(
                "!" + ToaVegConstants.VEG_FLAGS_BAND_NAME + "." + VegFlagsManager.INVALID_FLAG_NAME + " && !" + ToaVegConstants.VEG_FLAGS_BAND_NAME + "." + VegFlagsManager.LAIXCAB_OUT_OF_RANGE_FLAG_NAME);

        final Band _fapar_band = productConfigurer.addBand(ToaVegConstants.FAPAR_BAND_NAME, ProductData.TYPE_FLOAT32);
        _fapar_band.setDescription(ToaVegConstants.FAPAR_BAND_DESCRIPTION);
        _fapar_band.setValidPixelExpression(
                "!" + ToaVegConstants.VEG_FLAGS_BAND_NAME + "." + VegFlagsManager.INVALID_FLAG_NAME + " && !" + ToaVegConstants.VEG_FLAGS_BAND_NAME + "." + VegFlagsManager.FAPAR_OUT_OF_RANGE_FLAG_NAME);

        final Band _sigma_lai_band = productConfigurer.addBand(ToaVegConstants.SIGMA_LAI_BAND_NAME, ProductData.TYPE_FLOAT32);
        _sigma_lai_band.setDescription(ToaVegConstants.SIGMA_LAI_BAND_DESCRIPTION);

        final Band _sigma_fcover_band = productConfigurer.addBand(ToaVegConstants.SIGMA_FCOVER_BAND_NAME, ProductData.TYPE_FLOAT32);
        _sigma_fcover_band.setDescription(ToaVegConstants.SIGMA_FCOVER_BAND_DESCRIPTION);

        final Band _sigma_fapar_band = productConfigurer.addBand(ToaVegConstants.SIGMA_FAPAR_BAND_NAME, ProductData.TYPE_FLOAT32);
        _sigma_fapar_band.setDescription(ToaVegConstants.SIGMA_FAPAR_BAND_DESCRIPTION);

        final Band _sigma_laixcab_band = productConfigurer.addBand(ToaVegConstants.SIGMA_LAIXCAB_BAND_NAME, ProductData.TYPE_FLOAT32);
        _sigma_laixcab_band.setDescription(ToaVegConstants.SIGMA_LAIXCAB_BAND_DESCRIPTION);

        final Band vegFlagsBand = productConfigurer.addBand(ToaVegConstants.VEG_FLAGS_BAND_NAME, ProductData.TYPE_UINT16);
        vegFlagsBand.setDescription(ToaVegConstants.VEG_FLAGS_BAND_DESCRIPTION);
        final FlagCoding flagCoding = VegFlagsManager.getCoding(ToaVegConstants.VEG_FLAGS_BAND_NAME);
        vegFlagsBand.setSampleCoding(flagCoding);

        final Product targetProduct = productConfigurer.getTargetProduct();
        targetProduct.getFlagCodingGroup().add(flagCoding);
        VegFlagsManager.addBitmaskDefsToProduct(targetProduct, ToaVegConstants.VEG_FLAGS_BAND_NAME);

        productConfigurer.copyMetadata();
    }


    @Override
    protected void prepareInputs() throws OperatorException {
        super.prepareInputs();
        sourceProduct.addMask("_mask_", "l1_flags.LAND_OCEAN", "", Color.RED, 1f);
        loadAuxiliaryData();
    }

    public void setSolarSpecFlux(float value, int n) {
         solarSpecFlux[n] = value;
    }

    protected void loadAuxiliaryData() {

        // TODO - allow for parameter for aux data location
        File auxdataPath = new File(SystemUtils.getApplicationDataDir(), getSymbolicName() + "/auxdata");
        File configFile = new File(auxdataPath, ToaVegConstants.CONFIG_FILE);
        try {
            final ResourceInstaller resourceInstaller = new ResourceInstaller(ResourceInstaller.getSourceUrl(getClass()),
                                                                              "auxdata/"+ ToaVegConstants.AUXDATA_DIR,
                                                                              auxdataPath);
            resourceInstaller.install(".*", ProgressMonitor.NULL);

            URL configPath = null;
            try {
                configPath = configFile.toURI().toURL();
            } catch (MalformedURLException e) {
                throw new ProcessorException("Failed to create configuration URL for " + configFile.getPath(), e);
            }

            ToaVegProcessorConfigurationParser parser = new ToaVegProcessorConfigurationParser();
            parser.parseConfigurationFile(configPath, auxdataPath);
            VegProcessorConfiguration _config = parser.getConfiguration();


            ToaVegInputStatisticsLoader _inStatAux = new ToaVegInputStatisticsLoader();
            _inStatAux.load(_config.getInputStatisticsAuxFile());
            algorithm.setInputStatisticsAccess(_inStatAux);

            ToaVegOutputStatisticsLoader _outStatAux = new ToaVegOutputStatisticsLoader();
            _outStatAux.load(_config.getOutputStatisticsAuxFile());
            algorithm.setOutputStatisticsAccess(_outStatAux);

            ToaVegUncertaintyModelLoader _uncertaintyAux = new ToaVegUncertaintyModelLoader();
            _uncertaintyAux.load(_config.getUncertaintyAuxFile());
            algorithm.setUncertaintyModelAccess(_uncertaintyAux);
            algorithm.InitAlgo();
            algorithm.setNn_LaiAuxPath(_config.getNN_LaiAuxFile());
            algorithm.setNn_fCoverAuxPath(_config.getNN_fCoverAuxFile());
            algorithm.setNn_fAPARAuxPath(_config.getNN_fAPARAuxFile());
            algorithm.setNn_LAIxCabAuxPath(_config.getNN_LAIxCabAuxFile());

        } catch (MalformedURLException e) {
            throw new OperatorException("Failed to create configuration URL for " + configFile.getPath(), e);
        } catch (ProcessorException e) {
            throw new OperatorException("Failed to parse configuration " + configFile.getPath() + " with aux data " + auxdataPath.getPath(), e);
        } catch (IOException e) {
            throw new OperatorException("Failed to load auxiliary from " + auxdataPath.getPath(), e);
        } catch (JnnException e) {
            throw new OperatorException("Failed to load auxiliary from " + auxdataPath.getPath(), e);
        }
    }

    public String getSymbolicName() {
        return StringUtils.createValidName(OperatorSpi.getOperatorAlias(getClass()).toLowerCase(), new char[]{'-', '.'}, '-');
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(ToaVegOp.class);
        }
    }
}
