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
        sourceSamples[0].set(1);
        sourceSamples[1].set(-36.75359f);
        sourceSamples[2].set(139.91522f);
        sourceSamples[3].set(37.036976f);
        sourceSamples[4].set(76.98884f);
        sourceSamples[5].set(4.858894f);
        sourceSamples[6].set(104.1936f);
        sourceSamples[7].set(1017.6813f);

        sourceSamples[8].set(69.1f);
        sourceSamples[9].set(63.21793f);
        sourceSamples[10].set(52.071564f);
        sourceSamples[11].set(48.635902f);
        sourceSamples[12].set(43.334785f);
        sourceSamples[13].set(36.944813f);
        sourceSamples[14].set(34.617016f);
        sourceSamples[15].set(33.69079f);
        sourceSamples[16].set(43.28586f);
        sourceSamples[17].set(60.045914f);
        sourceSamples[18].set(58.957314f);
        sourceSamples[19].set(54.879032f);
        sourceSamples[20].set(54.268143f);

        MyWritableSample[] targetSamples = new MyWritableSample[9];
        for (int i = 0; i < 9; ++i) {
            targetSamples[i] = new MyWritableSample();
        }

        ToaVegOp op = new ToaVegOp();
        op.loadAuxiliaryData();
        op.setSolarSpecFlux(1775.2848f, 0);
        op.setSolarSpecFlux(1945.2028f, 1);
        op.setSolarSpecFlux(1996.392f, 2);
        op.setSolarSpecFlux(1997.0122f, 3);
        op.setSolarSpecFlux(1866.7103f, 4);
        op.setSolarSpecFlux(1709.0328f, 5);
        op.setSolarSpecFlux(1585.6805f, 6);
        op.setSolarSpecFlux(1524.1238f, 7);
        op.setSolarSpecFlux(1457.6317f, 8);
        op.setSolarSpecFlux(1310.724f, 9);
        op.setSolarSpecFlux(1218.8073f, 10);
        op.setSolarSpecFlux(992.2085f, 11);
        op.setSolarSpecFlux(962.6538f, 12);
        op.computePixel(0, 0, sourceSamples, targetSamples);

        // compare with values generated with old ToaVeg processor  1.4706547	0.22243302	64.181	0.46277326	0.72667503	0.06573455	0.077173844	29.809309
        assertEquals(1.4706547f, targetSamples[0].getFloat(), 0.0f);
        assertEquals(0.22243302f, targetSamples[1].getFloat(), 0.0f);
        assertEquals(64.181f, targetSamples[2].getFloat(), 0.0f);
        assertEquals(0.46277326f, targetSamples[3].getFloat(), 0.0f);
        assertEquals(0.72667503f, targetSamples[4].getFloat(), 0.0f);
        assertEquals(0.06573455f, targetSamples[5].getFloat(), 0.0f);
        assertEquals(0.077173844f, targetSamples[6].getFloat(), 0.0f);
        assertEquals(29.809309f, targetSamples[7].getFloat(), 0.0f);
        assertEquals(0, targetSamples[8].getInt());
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
            return value != 0.0;
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

