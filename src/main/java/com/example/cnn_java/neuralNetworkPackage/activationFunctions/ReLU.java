package com.example.cnn_java.neuralNetworkPackage.activationFunctions;

public class ReLU implements Activation {
    @Override
    public float activationFunction(float in) {
        return in < 0 ? -0.001f : in;
    }

    @Override
    public float activationFunctionDerivative(float in) {
        return in <= 0 ? (float) 0.01 : 1;
    }
}
