package wew.water.gpf;

public interface NeuralNetworkOperation {

    void compute(float[] in, float[] out, int mask, int errMask, float a);

    int getNumberOfInputNodes();

    int getNumberOfOutputNodes();
}
