package wew.water.gpf;

public class NeuralNetworkComputer {

    public static int compute(float[] in, float[] out, int[] rangeCheckErrorMasks,
                              double[][] input_scale_limits,
                              double[] input_scale_offset_factors,
                              int[] input_scale_flag,
                              double[][] input_pca_eigenvectors,
                              double[][] input_hidden_weights,
                              double[][] input_intercept_and_slope,
                              double[][] output_weights,
                              double[][] output_scale_limits,
                              double[][] output_intercept_and_slope,
                              double[] output_scale_offset_factors,
                              int[] output_scale_flags) {

        // (c) M. Schaale, WeW, 2002-2006
        int rangeCheckError = 0;
        int nodes_input_bias = 1;
        int nodes_input_pca = 1;

        int nodes_input = in.length;

        int nodes_hidden = input_hidden_weights[0].length;
        int nodes_hidden_bias = 1;
        double nodes_hidden_temperature = 1.0;
        final double t = nodes_hidden_temperature / (double) (nodes_input);
        int nodes_output = out.length;
        double[] vt;
        double[] vt1;
        //double limexp = -Math.log(Float.MIN_VALUE), dh, dhr, dhe, dhmax=Float.MAX_VALUE, expo;

        vt = new double[nodes_input + nodes_input_bias];
        vt1 = new double[nodes_hidden + nodes_hidden_bias];

        // Check input range
        for (int i = 0; i < nodes_input; i++) {
            if ((in[i] < (float) input_scale_limits[i][0]) || (in[i] > (float) input_scale_limits[i][1])) {
                rangeCheckError |= rangeCheckErrorMasks[0];
                break;
            }
        }

        for (int i = 0; i < nodes_input; i++) {
            // Apply input transformation
            if (input_scale_flag[i] == -1) {
                in[i] = (float) Math.log(in[i]);
            }
            if (input_scale_flag[i] == -2) {
                in[i] = (float) Math.exp(in[i]);
            }
            // Apply input scale layer parameters
            in[i] = (float) input_scale_offset_factors[i] + (in[i] - (float) input_intercept_and_slope[i][0]) / (float) input_intercept_and_slope[i][1];
        }

        // Apply input PCA layer parameters
        if (nodes_input_pca != 0) {
            for (int i = 0; i < nodes_input; i++) {
                vt[i] = in[i];
                if (input_scale_flag[i] == 1) {
                    vt[i] = 0.0;
                    for (int j = 0; j < nodes_input; j++) {
                        if (input_scale_flag[j] == 1) {
                            vt[i] += in[j] * input_pca_eigenvectors[j][i];
                        }
                    }
                }
            }
            for (int i = 0; i < nodes_input; i++) {
                in[i] = (float) vt[i];
            }
        }

        // Pump through the first layer
        for (int i = 0; i < nodes_input; i++) {
            vt[i] = in[i];
        }
        vt[vt.length - 1] = 1.0;

        for (int i = 0; i < nodes_hidden; i++) {
            vt1[i] = 0.0;
            for (int j = 0; j < nodes_input + nodes_input_bias; j++) {
                vt1[i] += vt[j] * input_hidden_weights[j][i];
            }
            // Pump through sigmoid
            vt1[i] = 1.0 / (1.0 + Math.exp(-t * vt1[i]));
        }

        // Pump through the second layer
        vt1[nodes_hidden] = 1f;
        for (int i = 0; i < nodes_output; i++) {
            out[i] = 0f;
            for (int j = 0; j < nodes_hidden + nodes_hidden_bias; j++) {
                out[i] += (float) (vt1[j] * output_weights[j][i]);
            }
            // Pump through sigmoid
            out[i] = (float) (1.0 / (1.0 + Math.exp(-t * out[i])));

            // Apply output scale layer parameters
            out[i] = (float) output_intercept_and_slope[i][0] + (out[i] - (float) output_scale_offset_factors[i]) * (float) output_intercept_and_slope[i][1];

            // Apply output transformation
            if (output_scale_flags[i] == -1) {
                out[i] = (float) Math.log((double) out[i]);
            }
            if (output_scale_flags[i] == -2) {
                out[i] = (float) Math.exp((double) out[i]);
            }
        }

        // Check output range
        for (int i = 0; i < nodes_output; i++) {
            if ((out[i] < (float) output_scale_limits[i][0]) || (out[i] > (float) output_scale_limits[i][1])) {
                rangeCheckError |= rangeCheckErrorMasks[1];
                break;
            }
        }
        return rangeCheckError;
    }
}
