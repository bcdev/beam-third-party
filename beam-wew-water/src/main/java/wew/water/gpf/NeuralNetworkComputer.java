package wew.water.gpf;

public class NeuralNetworkComputer {

    public static void compute(float[] in, float[] out, int mask, int errMask, float a,
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
        int i;
        int j;
        int nodes_input = 18;
        int nodes_input_bias = 1;
        int nodes_input_pca = 1;
        int nodes_hidden = input_hidden_weights[0].length;
        int nodes_hidden_bias = 1;
        double nodes_hidden_temperature = 1.000000;
        int nodes_output = out.length;
        double[] vt;
        double[] vt1;
        double t;
        //double limexp = -Math.log(Float.MIN_VALUE), dh, dhr, dhe, dhmax=Float.MAX_VALUE, expo;

        vt = new double[nodes_input + nodes_input_bias];
        vt1 = new double[nodes_hidden + nodes_hidden_bias];

        // Range check ??
        int rcheck = 0;
        if (a < 0.0f) {
            rcheck = 1;
        }
        a = 1f;

        if (mask == 0) {
            // Check input range
            if (rcheck != 0) {
                for (i = 0; i < nodes_input && a > 0f; i++) {
                    if ((in[i] < (float) input_scale_limits[i][0]) || (in[i] > (float) input_scale_limits[i][1])) {
                        a -= 3f;
                    }
                }
                if (a < 0f) {
                    mask |= errMask;
                }
            }

            //if(mask[k] == 0) {
            // Apply input transformation
            for (i = 0; i < nodes_input; i++) {
                if (input_scale_flag[i] == -1) {
                    in[i] = (float) Math.log((double) in[i]);
                }
                if (input_scale_flag[i] == -2) {
                    in[i] = (float) Math.exp((double) in[i]);
                }
            }

            // Apply input scale layer parameters
            for (i = 0; i < nodes_input; i++) {
                in[i] = (float) input_scale_offset_factors[i] + (in[i] - (float) input_intercept_and_slope[i][0]) / (float) input_intercept_and_slope[i][1];
            }

            // Apply input PCA layer parameters
            if (nodes_input_pca != 0) {
                for (i = 0; i < nodes_input; i++) {
                    vt[i] = (double) in[i];
                    if (input_scale_flag[i] == 1) {
                        vt[i] = 0.0;
                        for (j = 0; j < nodes_input; j++) {
                            if (input_scale_flag[j] == 1) {
                                vt[i] += (double) in[j] * input_pca_eigenvectors[j][i];
                            }
                        }
                    }
                }
                for (i = 0; i < nodes_input; i++) {
                    in[i] = (float) vt[i];
                }
            }

            // Pump through the first layer
            for (i = 0; i < nodes_input; i++) {
                vt[i] = (double) in[i];
            }
            for (i = nodes_input; i < nodes_input + nodes_input_bias; i++) {
                vt[i] = 1.0;
            }

            for (i = 0; i < nodes_hidden; i++) {
                vt1[i] = 0.0;
                for (j = 0; j < nodes_input + nodes_input_bias; j++) {
                    vt1[i] += vt[j] * input_hidden_weights[j][i];
                }
            }
            // Pump through sigmoid
            t = nodes_hidden_temperature / (double) (nodes_input);
            for (i = 0; i < nodes_hidden; i++) {
                vt1[i] = 1.0 / (1.0 + Math.exp(-t * vt1[i]));
            }

            // Pump through the second layer
            for (i = nodes_hidden; i < nodes_hidden + nodes_hidden_bias; i++) {
                vt1[i] = 1f;
            }
            for (i = 0; i < nodes_output; i++) {
                out[i] = 0f;
                for (j = 0; j < nodes_hidden + nodes_hidden_bias; j++) {
                    out[i] += (float) (vt1[j] * output_weights[j][i]);
                }
            }
            // Pump through sigmoid
            t = nodes_hidden_temperature / (double) (nodes_hidden);
            for (i = 0; i < nodes_output; i++) {
                out[i] = (float) (1.0 / (1.0 + Math.exp(-t * out[i])));
            }

            // Apply output scale layer parameters
            for (i = 0; i < nodes_output; i++) {
                out[i] = (float) output_intercept_and_slope[i][0] + (out[i] - (float) output_scale_offset_factors[i]) * (float) output_intercept_and_slope[i][1];
            }

            // Apply output transformation
            for (i = 0; i < nodes_output; i++) {
                if (output_scale_flags[i] == -1) {
                    out[i] = (float) Math.log((double) out[i]);
                }
                if (output_scale_flags[i] == -2) {
                    out[i] = (float) Math.exp((double) out[i]);
                }
            }

            // Check output range
            if (rcheck != 0) {
                for (i = 0; i < nodes_output && a >= -2f; i++) {
                    if ((out[i] < (float) output_scale_limits[i][0]) || (out[i] > (float) output_scale_limits[i][1])) {
                        a -= 20f;
                    }
                }
                if (a < 0f) {
                    mask |= errMask;
                }
            }
        }
    }
}
