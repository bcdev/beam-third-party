package org.esa.beam.processor;/*
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

import org.esa.beam.dataio.envisat.EnvisatConstants;
import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductData;
import org.esa.beam.framework.datamodel.ProductNodeFilter;
import org.esa.beam.framework.gpf.OperatorException;
import org.esa.beam.framework.gpf.annotations.SourceProduct;
import org.esa.beam.framework.gpf.pointop.PixelOperator;
import org.esa.beam.framework.gpf.pointop.ProductConfigurer;
import org.esa.beam.framework.gpf.pointop.Sample;
import org.esa.beam.framework.gpf.pointop.SampleConfigurer;
import org.esa.beam.framework.gpf.pointop.WritableSample;
import org.esa.beam.processor.common.utils.VegFlagsManager;
import org.esa.beam.processor.toa.ToaVegConstants;

public class ToaVegOp extends PixelOperator {

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

    @Override
    protected void computePixel(int x, int y, Sample[] sourceSamples, WritableSample[] targetSamples) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void configureSourceSamples(SampleConfigurer sampleConfigurer) throws OperatorException {
        sampleConfigurer.defineSample(0, ToaVegConstants.LAT_TIEPOINT_NAME);
        sampleConfigurer.defineSample(1, ToaVegConstants.LON_TIEPOINT_NAME);
        sampleConfigurer.defineSample(2, ToaVegConstants.SZA_TIEPOINT_NAME);
        sampleConfigurer.defineSample(3, ToaVegConstants.SAA_TIEPOINT_NAME);
        sampleConfigurer.defineSample(4, ToaVegConstants.VZA_TIEPOINT_NAME);
        sampleConfigurer.defineSample(5, ToaVegConstants.VAA_TIEPOINT_NAME);
        sampleConfigurer.defineSample(6, ToaVegConstants.PRESS_TIEPOINT_NAME);
        for (int i = 0; i < ToaVegConstants.REFLEC_BAND_NAMES.length; i++) {
            sampleConfigurer.defineSample(7 + i, ToaVegConstants.REFLEC_BAND_NAMES[i]);
        }
    }

    @Override
    protected void configureTargetSamples(SampleConfigurer sampleConfigurer) throws OperatorException {
        sampleConfigurer.defineSample(0, ToaVegConstants.LAI_BAND_NAME);

        // TODO - other samples
    }

    @Override
    protected void configureTargetProduct(ProductConfigurer productConfigurer) {
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

        final Band laiBand = productConfigurer.addBand(ToaVegConstants.LAI_BAND_NAME, ProductData.TYPE_FLOAT32);
        laiBand.setDescription(ToaVegConstants.LAI_BAND_DESCRIPTION);
        laiBand.setUnit(ToaVegConstants.LAI_BAND_UNIT);
        laiBand.setValidPixelExpression(
                "!" + ToaVegConstants.VEG_FLAGS_BAND_NAME + "." + VegFlagsManager.INVALID_FLAG_NAME + " && !" + ToaVegConstants.VEG_FLAGS_BAND_NAME + "." + VegFlagsManager.LAI_OUT_OF_RANGE_FLAG_NAME);

        // TODO - other bands

        final Product targetProduct = productConfigurer.getTargetProduct();
        targetProduct.getFlagCodingGroup().add(VegFlagsManager.getCoding(ToaVegConstants.VEG_FLAGS_BAND_NAME));
        VegFlagsManager.addBitmaskDefsToProduct(targetProduct, ToaVegConstants.VEG_FLAGS_BAND_NAME);

        productConfigurer.copyMetadata();
    }

    @Override
    protected void prepareInputs() throws OperatorException {
        super.prepareInputs();

        // TODO - prepare auxiliary data
    }
}
