package it.jrc.beam.fapar;

import org.esa.beam.framework.datamodel.RasterDataNode;
import org.esa.beam.framework.gpf.pointop.WritableSample;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests compoutePixel method of FAPAR operator against values generated with old FAPAR processor.
 *
 * @author Martin Boettcher
 */
public class FaparOpTest {
    @Test
    public void testFaparOp() throws Exception {
        MyWritableSample[] sourceSamples = new MyWritableSample[9];
        for (int i = 0; i < 9; ++i) {
            sourceSamples[i] = new MyWritableSample();
        }
        sourceSamples[0].set(59.570065);
        sourceSamples[1].set(43.02177);
        sourceSamples[2].set(34.32196);
        sourceSamples[3].set(56.512672);
        sourceSamples[4].set(16);
        sourceSamples[5].set(28.111);
        sourceSamples[6].set(121.93718);
        sourceSamples[7].set(2.1207128);
        sourceSamples[8].set(103.739586);

        MyWritableSample[] targetSamples = new MyWritableSample[8];
        for (int i = 0; i < 8; ++i) {
            targetSamples[i] = new MyWritableSample();
        }

        FaparOp op = new FaparOp();
        op.setBlueSolarFlux(1816.5496f);
        op.setGreenSolarFlux(1747.469f);
        op.setRedSolarFlux(1425.911f);
        op.setNirSolarFlux(928.46783f);
        op.setGreenBandPresent(true);
        op.computePixel(0, 0, sourceSamples, targetSamples);

        // compare with values generated with old fapar processor
        assertEquals(0.21259843f, (((int) (targetSamples[0].getFloat() * 254.0f + 1.0f)) - 1) / 254.0f, 0.0f);
        assertEquals(0.11680036f, targetSamples[1].getFloat(), 0.0f);
        assertEquals(0.0876884f, targetSamples[2].getFloat(), 0.0f);
        assertEquals(0.08573201f, targetSamples[3].getFloat(), 0.0f);
        assertEquals(0.21679154f, targetSamples[4].getFloat(), 0.0f);
        assertEquals(0.1814117f, targetSamples[5].getFloat(), 0.0f);
        assertEquals(0.057058826f, targetSamples[6].getFloat(), 0.0f);
        assertEquals(16, targetSamples[7].getInt());
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

