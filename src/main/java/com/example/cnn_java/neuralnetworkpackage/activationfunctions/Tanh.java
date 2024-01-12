package com.example.cnn_java.neuralnetworkpackage.activationfunctions;

public class Tanh implements Activation {
  @Override
  public float activationFunction(float in) {
    return (float) Math.tanh(in);
  }

  @Override
  public float activationFunctionDerivative(float in) {
    return (float) (1 - (Math.tanh(in) * Math.tanh(in)));
  }

  @Override
  public String toCpp() {
    return """
            float activationFunction(float in) {
              return (float) tanh(in);
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
