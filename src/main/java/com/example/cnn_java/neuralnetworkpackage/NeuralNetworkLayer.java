package com.example.cnn_java.neuralnetworkpackage;

import com.example.cnn_java.arduino.Ciable;
import com.example.cnn_java.neuralnetworkpackage.activationfunctions.Activation;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Random;

@ToString
public class NeuralNetworkLayer implements Ciable {
  private static int layersCount = 0;
  @Getter
  private final int layerNumber;
  private final int inputSize;
  private final int outputSize;
  private float[][] weights;
  private float[] biases;
  private float[] inputs;
  @Getter
  private float[] outputs;
  private float[] gradients;
  @Setter
  private NeuralNetworkLayer nextLayer;
  @Setter
  @ToString.Exclude
  private NeuralNetworkLayer previousLayer;

  @Builder
  public NeuralNetworkLayer(int inputSize, int outputSize) {
    this.inputSize = inputSize;
    this.outputSize = outputSize;
    layerNumber = layersCount;
    layersCount++;


    initializeWeightsAndBiases();
  }

  private void initializeWeightsAndBiases() {
    Random random = new Random();


    weights = new float[outputSize][inputSize];
    for (int i = 0; i < outputSize; i++) {
      for (int j = 0; j < inputSize; j++) {
        weights[i][j] = (float) random.nextGaussian();
      }
    }


    biases = new float[outputSize];
    for (int i = 0; i < outputSize; i++) {
      biases[i] = (float) random.nextGaussian();
    }
  }

  public float[] forward(float[] input, Activation activation) {
    if (input.length != inputSize) {
      throw new IllegalArgumentException("Input size does not match the expected size.");
    }

    this.inputs = input;


    outputs = new float[outputSize];
    for (int i = 0; i < outputSize; i++) {
      float sum = 0;
      for (int j = 0; j < inputSize; j++) {
        sum += weights[i][j] * input[j];
      }
      outputs[i] = activation.activationFunction(sum + biases[i]);
    }


    if (nextLayer != null) {
      return nextLayer.forward(outputs, activation);
    }
    return outputs;
  }

  public void backward(float[] outputGradients, Activation activation, float learningRate, float[] gradientSum) {
    if (outputGradients.length != outputSize) {
      throw new IllegalArgumentException("Output gradients size does not match the expected size.");
    }


    gradients = new float[inputSize];
    float gradient = 0;
    for (int i = 0; i < outputSize; i++) {
      gradient = outputGradients[i] * activation.activationFunctionDerivative(outputs[i]);
      for (int j = 0; j < inputSize; j++) {
        gradients[j] += gradient * weights[i][j];
        weights[i][j] += learningRate * gradient * inputs[j];
      }
      biases[i] += learningRate * gradient;
    }
    gradientSum[0] += Math.abs(gradient);


    if (previousLayer != null) {
      previousLayer.backward(gradients, activation, learningRate, gradientSum);
    }
  }

  @Override
  public String toCpp() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("float* ").append("l").append(layerNumber).append("forward(float* in){\n")
            .append("   float* outputs = new float[").append(outputSize).append("];\n");
    for (int i = 0; i < outputSize; i++) {
      stringBuilder.append((i == 0) ? "   float sum = 0;\n" : "   sum = 0;\n");
      for (int j = 0; j < inputSize; j++) {
        stringBuilder.append("    sum += ").append("l").append(layerNumber).append("w").append(i).append(j).append("* in[").append(j).append("];\n");
      }
      stringBuilder.append("    outputs[").append(i).append("] = ").append("activationFunction(sum + l").append(layerNumber).append("b").append(i).append(");\n");
    }
    stringBuilder.append("    delete[] in;\n");
    if (nextLayer != null) {
      stringBuilder.append("    return l").append(layerNumber + 1).append("forward(outputs);\n");
    } else stringBuilder.append("   return outputs;\n");
    stringBuilder.append("}\n");
    return stringBuilder.toString();
  }

  @Override
  public String toH() {
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < weights.length; i++) {
      float[] weight = weights[i];
      for (int j = 0; j < weight.length; j++) {
        float w = weight[j];
        stringBuilder.append("#define l").append(layerNumber).append("w").append(i).append(j).append(" ").append(w).append("f\n");
      }
    }
    for (int i = 0; i < biases.length; i++) {
      float b = biases[i];
      stringBuilder.append("#define l").append(layerNumber).append("b").append(i).append(" ").append(b).append("f\n");
    }
    stringBuilder.append("float* ").append("l").append(layerNumber).append("forward(float*);\n");
    return stringBuilder.toString();
  }
}
