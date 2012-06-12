package org.esa.beam.processor.toa;

import org.esa.beam.framework.datamodel.RasterDataNode;
import org.esa.beam.framework.gpf.pointop.WritableSample;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests compoutePixel method of LAI operator against values generated with old ToaVeg processor.
 *
 * @author Martin Boettcher
 */
public class ToaVegOpTest {
    @Test
    public void testToaVegOp() throws Exception {
        MyWritableSample[] sourceSamples = new MyWritableSample[21];
        for (int i = 0; i < 21; ++i) {
            sourceSamples[i] = new MyWritableSample();
        }
        sourceSamples[0].set(-36.75359);
        sourceSamples[1].set(139.91522);
        sourceSamples[2].set(37.036976);
        sourceSamples[3].set(76.98884);
        sourceSamples[4].set(4.858894);
        sourceSamples[5].set(104.1936);
        sourceSamples[6].set(1017.6813);
        sourceSamples[7].set(69.1);
        sourceSamples[8].set(63.21793);
        sourceSamples[9].set(52.071564);
        sourceSamples[10].set(48.635902);
        sourceSamples[11].set(43.334785);
        sourceSamples[12].set(36.944813);
        sourceSamples[13].set(34.617016);
        sourceSamples[14].set(33.69079);
        sourceSamples[15].set(43.28586);
        sourceSamples[16].set(60.045914);
        sourceSamples[17].set(18.8284);
        sourceSamples[18].set(58.957314);
        sourceSamples[19].set(54.879032);
        sourceSamples[20].set(54.268143);

        MyWritableSample[] targetSamples = new MyWritableSample[2];
        for (int i = 0; i < 2; ++i) {
            targetSamples[i] = new MyWritableSample();
        }

        ToaVegOp op = new ToaVegOp();
//        op.setBlueSolarFlux(1816.5496f);
//        op.setGreenSolarFlux(1747.469f);
//        op.setRedSolarFlux(1425.911f);
//        op.setNirSolarFlux(928.46783f);
//        op.setGreenBandPresent(true);
        op.computePixel(0, 0, sourceSamples, targetSamples);

        // compare with values generated with old ToaVeg processor
        assertEquals(16, targetSamples[0].getInt());
        assertEquals(1.4706547f, targetSamples[1].getFloat(), 0.0f);
    }

    class MyWritableSample implements WritableSample {
        double value;

        @Override
        public void set(int bitIndex, boolean v) {
        }

        @Override
        public void set(boolean v) {
            value = v ? 1.0 : 0.0;
        }

        @Override
        public void set(int v) {
            value = (double) v;
        }

        @Override
        public void set(float v) {
            value = v;
        }

        @Override
        public void set(double v) {
            value = v;
        }

        @Override
        public RasterDataNode getNode() {
            return null;
        }

        @Override
        public int getIndex() {
            return 0;
        }

        @Override
        public int getDataType() {
            return 0;
        }

        @Override
        public boolean getBit(int bitIndex) {
            return false;
        }

        @Override
        public boolean getBoolean() {
            return false;
        }

        @Override
        public int getInt() {
            return (int) value;
        }

        @Override
        public float getFloat() {
            return (float) value;
        }

        @Override
        public double getDouble() {
            return value;
        }
    }
}

