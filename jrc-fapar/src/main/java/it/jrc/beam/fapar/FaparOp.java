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

package it.jrc.beam.fapar;

import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.FlagCoding;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductData;
import org.esa.beam.framework.datamodel.ProductNodeFilter;
import org.esa.beam.framework.gpf.OperatorException;
import org.esa.beam.framework.gpf.OperatorSpi;
import org.esa.beam.framework.gpf.annotations.OperatorMetadata;
import org.esa.beam.framework.gpf.annotations.SourceProduct;
import org.esa.beam.framework.gpf.pointop.PixelOperator;
import org.esa.beam.framework.gpf.pointop.ProductConfigurer;
import org.esa.beam.framework.gpf.pointop.Sample;
import org.esa.beam.framework.gpf.pointop.SampleConfigurer;
import org.esa.beam.framework.gpf.pointop.WritableSample;
import org.esa.beam.util.ProductUtils;

import java.awt.Color;

@OperatorMetadata(alias = "Fapar", authors = "Martin Boettcher, Ralf Quast", copyright = "Brockmann Consult GmbH",
                  version = "2.3",
                  description = "Computes FAPAR from MERIS products.")
public class FaparOp extends PixelOperator {

    private static final String SOURCE_BAND_NAME_BLUE = "radiance_2";
    private static final String SOURCE_BAND_NAME_GREEN = "radiance_5";
    private static final String SOURCE_BAND_NAME_RED = "radiance_8";
    private static final String SOURCE_BAND_NAME_NIR = "radiance_13";
    private static final String SOURCE_BAND_NAME_L1_FLAGS = "l1_flags";

    private static final String TARGET_BAND_NAME_FAPAR = "FAPAR";
    private static final String TARGET_BAND_NAME_BLUE = "reflectance_TOA_2";
    private static final String TARGET_BAND_NAME_GREEN = "reflectance_TOA_5";
    private static final String TARGET_BAND_NAME_RED = "reflectance_TOA_8";
    private static final String TARGET_BAND_NAME_NIR = "reflectance_TOA_13";
    private static final String TARGET_BAND_NAME_RECTIFIED_NIR = "rectified_reflectance_13";
    private static final String TARGET_BAND_NAME_RECTIFIED_RED = "rectified_reflectance_8";
    private static final String TARGET_BAND_NAME_L2_FLAGS = "l2_flags";

    private static final String FAPAR_VALID_EXPRESSION = "l2_flags.LAND_OCEAN && !(l2_flags.BRIGHT)";

    @SourceProduct(alias = "source",
                   description = "The path of the MERIS source product",
                   label = "MERIS source product",
                   bands = {
                           SOURCE_BAND_NAME_BLUE,
                           SOURCE_BAND_NAME_RED,
                           SOURCE_BAND_NAME_NIR
                           // TODO - can tie-point grids be added here?
                   })
    private Product sourceProduct;

    private transient boolean greenBandPresent;

    @Override
    protected void computePixel(int x, int y, Sample[] sourceSamples, WritableSample[] targetSamples) {
        /*
           // first of all - allocate memory for a single scan line
           // -----------------------------------------------------
           int width = _inputProduct.getSceneRasterWidth();
           int height = _inputProduct.getSceneRasterHeight();

           // Reflectances
           float[] blue_reflectance = new float[width];
           float[] red_reflectance = new float[width];
           float[] nir_reflectance = new float[width];
           float[] green_reflectance = new float[width * height];

           // MC ++
           float[] corrlatitude = new float[width];
           float[] corrlongitude = new float[width];
           // MC --

           //float green_sun_flux;

           // Flags
           int[] process = new int[width];
           //int[][] flg=new int[height][width];	// flg is initialize only once (not line by line)
           int[] flg = new int[height * width];
           if (_inFlag != null)            // only if there was flags in the input product
           //for (int h=0;h<height;h++)
           //    _inFlag.readPixels(0,h,width, 1, flg[h]);
           {
               _inFlag.readPixels(0, 0, width, height, flg, ProgressMonitor.NULL);
           }

   // ANDREA: flags to be processed.

           int[] flgPerLine = new int[width];


           // variable used to read the radiance
           float[] radiance = new float[width];
           float[] green_radiance = new float[width * height];
           float[] green_sza = new float[width * height];

           // Angles
           float[] sza = new float[width];
           float[] saa = new float[width];
           float[] vza = new float[width];
           float[] vaa = new float[width];

           // progress bar init
           // -----------------
   //	    fireProcessStarted("Processing FAPAR.", 0, height);

           // Begin the computation
           // ---------------------

           // Get the solar fux for each Band
           _sun_spec[_blue] = _blueInputBand.getSolarFlux();
           _sun_spec[_red] = _redInputBand.getSolarFlux();
           _sun_spec[_nir] = _nirInputBand.getSolarFlux();

           // Get the green radiance
           if (_greenInputBand != null) {
               //green_sun_flux = _greenInputBand.getSolarFlux();
               _szaBand.readPixels(0, 0, width, height, green_sza, ProgressMonitor.NULL);
               green_radiance = _greenInputBand.readPixels(0, 0, width, height, green_radiance, ProgressMonitor.NULL);
               green_reflectance = RsMathUtils.radianceToReflectance(green_radiance, green_sza,
                                                                     _greenInputBand.getSolarFlux(), null);
               // MC 22.11.07 - debug - System.out.println (width);
               // MC 22.11.07 - debug - System.out.println (height);
               _reflectanceGreenBand.writePixels(0, 0, width, height, green_reflectance, ProgressMonitor.NULL);
               // MC 22.11.07 - debug - _logger.info("coucou 2");
               green_radiance = null;
               green_reflectance = null;

           }

   // ANDREA: added two variables, storing the coding of flags LAND_OCEAN and BRIGHT. In case one or both of such flags are missing, the value of the variables is set to 0.
   // These two variables are used later, in order to detect cloud pixel over land which have not been detected by MGVI processing.

           int Land_Ocean_flagMask = 0;
           int Bright_flagMask = 0;
           FlagCoding inputFlags = _inputProduct.getFlagCodingGroup().get(_flagName);
           if (inputFlags != null) {
               Land_Ocean_flagMask = inputFlags.getFlagMask("LAND_OCEAN");
               Bright_flagMask = inputFlags.getFlagMask("BRIGHT");
           }

   // ANDREA: END


           // Loop over every line
           // --------------------
           pm.beginTask("Processing FAPAR...", height - 1);
           try {
               for (int y = 0; y < height; y++) {

   // ANDREA: Read flags of the current line.

                   _inFlag.readPixels(0, y, width, 1, flgPerLine, ProgressMonitor.NULL);

   // ANDREA: END


                   // Read the angles values for the line
                   _szaBand.readPixels(0, y, width, 1, sza, ProgressMonitor.NULL);
                   _saaBand.readPixels(0, y, width, 1, saa, ProgressMonitor.NULL);
                   _vzaBand.readPixels(0, y, width, 1, vza, ProgressMonitor.NULL);
                   _vaaBand.readPixels(0, y, width, 1, vaa, ProgressMonitor.NULL);

                   // Get the blue radiance
                   // if exception is thrown, it is transfered
                   // all reflectances may contain valules <0 or >1
                   radiance = _blueInputBand.readPixels(0, y, width, 1, radiance, ProgressMonitor.NULL);
                   blue_reflectance = RsMathUtils.radianceToReflectance(radiance, sza, _blueInputBand.getSolarFlux(),
                                                                        null);

                   // Get the red radiance
                   radiance = _redInputBand.readPixels(0, y, width, 1, radiance, ProgressMonitor.NULL);
                   red_reflectance = RsMathUtils.radianceToReflectance(radiance, sza, _redInputBand.getSolarFlux(), null);

                   // Get the nir radiance
                   radiance = _nirInputBand.readPixels(0, y, width, 1, radiance, ProgressMonitor.NULL);
                   nir_reflectance = RsMathUtils.radianceToReflectance(radiance, sza, _nirInputBand.getSolarFlux(), null);

                   // Initialize the flag values for each pixel of the line
                   // 1=Bad, 2=Cloud,snow,ice, 3=water, deep shadow, 4=bright surface
                   for (int i = 0; i < width; i++) {
                       if (blue_reflectance[i] <= 0 || red_reflectance[i] <= 0 || nir_reflectance[i] <= 0) {
                           process[i] = 1;
                       } else if (blue_reflectance[i] >= 0.3 || red_reflectance[i] >= 0.5 || nir_reflectance[i] >= 0.7) {
                           process[i] = 2;
                       } else if (blue_reflectance[i] > nir_reflectance[i]) {
                           process[i] = 3;
                       }
   // ANDREA: replaced 1.25 with 1.3
   //			    else if (nir_reflectance[i]<=1.25*red_reflectance[i])
                       else if (nir_reflectance[i] <= 1.3 * red_reflectance[i])
   // ANDREA: END
                       {
                           process[i] = 4;
                       } else {
                           process[i] = 0;
                       }

   // ANDREA: check cloud pixel over land which have not been detected by MGVI processing.

                       if (Bright_flagMask != 0 && Land_Ocean_flagMask != 0) {
                           int isBright = flgPerLine[i] & Bright_flagMask;
                           int isLand_Ocean = flgPerLine[i] & Land_Ocean_flagMask;
                           if (isLand_Ocean == 0 || isBright != 0) {
                               process[i] = 5;
                           }
                       }

   // ANDREA: END
                   }

                   for (int i = 0; i < width; i++) {
                       if (!(process[i] == 0 || process[i] == 4)) {
                           int isLand_Ocean = flgPerLine[i] & Land_Ocean_flagMask;
                           if (isLand_Ocean != 0) {
                               int newFlagMask = flgPerLine[i] ^ Land_Ocean_flagMask;
                               flgPerLine[i] = newFlagMask;
                           }
                       }
                   }

   //		    _inFlag.setPixels(0, y, width, 1, flgPerLine);

                   // Compute the fapar calling the algorithm
                   // ---------------------------------------
                   float[] fapar = _algorithm.run(sza, saa, vza, vaa, blue_reflectance, red_reflectance, nir_reflectance,
                                                  process);

                   // Variable used to write the data in type INT
                   int[] fapar_i = new int[width];


                   // Check the flags and modify the wrong values of Fapar
                   // Mapp the values between 0 and 250 + 251-255 for the flags
                   // ---------------------------------------------------------
                   for (int i = 0; i < width; i++) {
   // ANDREA: replaced "if ... else ..." with "switch", and updated FAPAR values.
                       switch (process[i]) {
                           case 0:
                               fapar_i[i] = Math.round(fapar[i] * 254 + 1);
                               break;
                           case 4:
                               fapar_i[i] = 1;
                               break;
                           default:
                               fapar_i[i] = 0;
                       }

                       if (process[i] != 0) {
                           flg[i + y * width] += Math.pow(2, process[i] - 1) * 256;
                       }
                   }
        */
    }

    @Override
    protected void configureSourceSamples(SampleConfigurer sampleConfigurer) throws OperatorException {
        sampleConfigurer.defineSample(0, SOURCE_BAND_NAME_BLUE);
        if (greenBandPresent) {
            sampleConfigurer.defineSample(1, SOURCE_BAND_NAME_GREEN);
        }
        sampleConfigurer.defineSample(2, SOURCE_BAND_NAME_RED);
        sampleConfigurer.defineSample(3, SOURCE_BAND_NAME_NIR);
        sampleConfigurer.defineSample(4, SOURCE_BAND_NAME_L1_FLAGS);
        sampleConfigurer.defineSample(5, "sun_zenith");
        sampleConfigurer.defineSample(6, "sun_azimuth");
        sampleConfigurer.defineSample(7, "view_zenith");
        sampleConfigurer.defineSample(8, "view_azimuth");

    }

    @Override
    protected void configureTargetSamples(SampleConfigurer sampleConfigurer) throws OperatorException {
        sampleConfigurer.defineSample(0, TARGET_BAND_NAME_FAPAR);
        sampleConfigurer.defineSample(1, TARGET_BAND_NAME_BLUE);
        sampleConfigurer.defineSample(2, TARGET_BAND_NAME_GREEN);
        sampleConfigurer.defineSample(3, TARGET_BAND_NAME_RED);
        sampleConfigurer.defineSample(4, TARGET_BAND_NAME_NIR);
        sampleConfigurer.defineSample(5, TARGET_BAND_NAME_RECTIFIED_NIR);
        sampleConfigurer.defineSample(6, TARGET_BAND_NAME_RECTIFIED_RED);
        sampleConfigurer.defineSample(7, TARGET_BAND_NAME_L2_FLAGS);
    }

    @Override
    protected void configureTargetProduct(ProductConfigurer productConfigurer) {
        final Product sourceProduct = productConfigurer.getSourceProduct();
        final Product targetProduct = productConfigurer.getTargetProduct();
        targetProduct.setDescription("Fraction of Photosyntheticaly Absorbed radiation computed by the MGVI algorithm");

        productConfigurer.copyBands(new ProductNodeFilter<Band>() {
            @Override
            public boolean accept(Band band) {
                return band.getFlagCoding() != null;
            }
        });

        FlagCoding targetFlagCoding = targetProduct.getFlagCodingGroup().get(SOURCE_BAND_NAME_L1_FLAGS);
        if (targetFlagCoding == null) {
            targetFlagCoding = new FlagCoding(TARGET_BAND_NAME_L2_FLAGS);
            targetProduct.getFlagCodingGroup().add(targetFlagCoding);
        } else {
            targetFlagCoding.setName(TARGET_BAND_NAME_L2_FLAGS);
        }
        targetFlagCoding.addFlag("MGVI_BAD_DATA", 0x100, "Bad pixel flagged by MGVI processing");
        targetFlagCoding.addFlag("MGVI_CSI", 0x200, "Cloud, snow or ice pixel flagged by MGVI processing");
        targetFlagCoding.addFlag("MGVI_WS", 0x400, "Water or deep shadow pixel flagged by MGVI processing");
        targetFlagCoding.addFlag("MGVI_BRIGHT", 0x800, "Bright pixel flagged by MGVI processing");
        targetFlagCoding.addFlag("MGVI_INVAL_FAPAR", 0x1000, "Invalid rectification flagged by MGVI processing");

        productConfigurer.copyMasks();

        targetProduct.addMask("mgvi_bad", "l2_flags.MGVI_BAD_DATA", "Bad pixel flagged by MGVI processing",
                              new Color(51, 255, 204), 0.5);
        targetProduct.addMask("mgvi_csi", "l2_flags.MGVI_CSI", "Cloud, snow or ice pixel flagged by MGVI processing",
                              new Color(51, 153, 255), 0.5);
        targetProduct.addMask("mgvi_ws", "l2_flags.MGVI_WS",
                              "Water or deep shadow pixel flagged by MGVI processing",
                              new Color(51, 204, 255), 0.5);
        targetProduct.addMask("mgvi_bright", "l2_flags.MGVI_BRIGHT", "Bright pixel flagged by MGVI processing",
                              new Color(51, 217, 217), 0.5);
        targetProduct.addMask("mgvi_inval_rec", "l2_flags.MGVI_INVAL_FAPAR",
                              "Invalid rectification flagged by MGVI processing",
                              new Color(255, 102, 255), 0.5);

        final Band faparBand = targetProduct.addBand(TARGET_BAND_NAME_FAPAR, ProductData.TYPE_UINT8);
        faparBand.setNoDataValue(0.0);
        faparBand.setValidPixelExpression(FAPAR_VALID_EXPRESSION);
        faparBand.setScalingFactor(1.0 / 254.0);
        faparBand.setScalingOffset(-1.0 / 254.0);
        faparBand.setDescription("Fraction of photosynthetically absorbed radiation computed by the MGVI algorithm");

        addReflectanceTargetBand(sourceProduct, targetProduct, SOURCE_BAND_NAME_BLUE, TARGET_BAND_NAME_BLUE,
                                 "Top of atmosphere blue reflectance used in the MGVI algorithm");
        greenBandPresent = addReflectanceTargetBand(sourceProduct, targetProduct, SOURCE_BAND_NAME_GREEN,
                                                    TARGET_BAND_NAME_GREEN,
                                                    "Top of atmosphere green reflectance used in the MGVI algorithm");
        addReflectanceTargetBand(sourceProduct, targetProduct, SOURCE_BAND_NAME_RED, TARGET_BAND_NAME_RED,
                                 "Top of atmosphere red reflectance used in the MGVI algorithm");
        addReflectanceTargetBand(sourceProduct, targetProduct, SOURCE_BAND_NAME_NIR, TARGET_BAND_NAME_NIR,
                                 "Top of atmosphere NIR reflectance used in the MGVI algorithm");
        addReflectanceTargetBand(sourceProduct, targetProduct, SOURCE_BAND_NAME_NIR, TARGET_BAND_NAME_RECTIFIED_NIR,
                                 "Angular and atmospheric corrected NIR reflectance");
        addReflectanceTargetBand(sourceProduct, targetProduct, SOURCE_BAND_NAME_RED, TARGET_BAND_NAME_RECTIFIED_RED,
                                 "Angular and atmospheric corrected red reflectance");

        final Band flagBand = productConfigurer.addBand(TARGET_BAND_NAME_L2_FLAGS, ProductData.TYPE_UINT32);
        flagBand.setSampleCoding(targetFlagCoding);
        flagBand.setDescription("Classification and quality flags");

        if (sourceProduct.containsBand("corr_latitude") && sourceProduct.containsBand("corr_longitude")) {
            productConfigurer.copyBands("corr_latitude", "corr_longitude");
        }
        productConfigurer.copyTiePointGrids();
        productConfigurer.copyGeoCoding();
        productConfigurer.copyMetadata();
    }

    private boolean addReflectanceTargetBand(Product sourceProduct,
                                             Product targetProduct,
                                             String sourceBandName,
                                             String targetBandName, String targetBandDescription) {
        final Band sourceBand = sourceProduct.getBand(sourceBandName);
        if (sourceBand != null) {
            final Band targetBand = targetProduct.addBand(targetBandName, ProductData.TYPE_FLOAT32);
            targetBand.setScalingFactor(1.0);
            targetBand.setDescription(targetBandDescription);
            targetBand.setSolarFlux(sourceBand.getSolarFlux());
            targetBand.setSpectralBandwidth(sourceBand.getSpectralBandwidth());
            targetBand.setSpectralWavelength(sourceBand.getSpectralWavelength());
            targetBand.setSpectralBandIndex(sourceBand.getSpectralBandIndex());
            return true;
        }
        return false;
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(FaparOp.class);
        }
    }
}
