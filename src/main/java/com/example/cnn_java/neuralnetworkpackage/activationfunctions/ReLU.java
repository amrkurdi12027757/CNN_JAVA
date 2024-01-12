package com.example.cnn_java.neuralnetworkpackage.activationfunctions;

public class ReLU implements Activation {
  @Override
  public float activationFunction(float in) {
    return in < 0 ? -0.001f : in;
  }

  @Override
  public float activationFunctionDerivative(float in) {
    return in <= 0 ? (float) 0.01 : 1;
  }

  @Override
  public String toCpp() {
    return """
            float activationFunction(float in) {
                    return in < 0 ? -0.001f : in;
                }
                """;
  }

  @Override
  public String toH() {
    return """
            float activationFunction(float);
            """;
  }
}
