package com.example.cnn_java.neuralNetworkPackage.activationFunctions;

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
}
