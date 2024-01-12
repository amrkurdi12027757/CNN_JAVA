package com.example.cnn_java.neuralnetworkpackage.activationfunctions;

public class Sigmoid implements Activation {
    @Override
    public float activationFunction(float in) {
        return (float) (1 / (1 + Math.exp(-in)));
    }

    @Override
    public float activationFunctionDerivative(float in) {
        float sigmoid = activationFunction(in);
        return sigmoid * (1 - sigmoid);
    }

    @Override
    public String toCpp() {
        return """
                float activationFunction(float in) {
                  return (float) (1 / (1 + exp(-in)));
                }
                """;
    }

    @Override
    public String toH() {
        return """
                #include<math.h>
                float activationFunction(float);
                """;
    }
}
