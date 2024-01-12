package com.example.cnn_java.neuralnetworkpackage.activationfunctions;

import com.example.cnn_java.arduino.Ciable;

public interface Activation extends Ciable {
    float  activationFunction( float in);
    float activationFunctionDerivative( float in);
}
