package com.example.cnn_java.neuralNetworkPackage;

import com.example.cnn_java.dataPackage.InOut;
import com.example.cnn_java.dataPackage.dataProcessor.ClassificationProcessor;
import com.example.cnn_java.neuralNetworkPackage.activationFunctions.Activation;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class NeuralNetwork {
    private final List<NeuralNetworkLayer> layers;
    private final Activation function;

    @Builder
    public NeuralNetwork(@Singular List<Integer> layers, Activation function) {
        this.function = function;
        this.layers = new ArrayList<>();
        for (int i = 0; i < layers.size() - 1; i++) {
            NeuralNetworkLayer layer = new NeuralNetworkLayer(layers.get(i), layers.get(i + 1));
            this.layers.add(layer);

            if (i > 0) {
                layer.setPreviousLayer(this.layers.get(i - 1));
                this.layers.get(i - 1).setNextLayer(layer);
            }

        }
    }

    public float[] predict(float[] input) {
        NeuralNetworkLayer inputLayer = layers.get(0);
        return inputLayer.forward(input, function);
    }

    public float[] train(InOut data, float learningRate, int epochs) {
        float[][] inputs = data.getInputs();
        float[][] targets = data.getOutputs();
        if (inputs.length != targets.length) {
            throw new IllegalArgumentException("Number of input samples must match the number of target samples.");
        }
        float[] epochsGradiantAverageSum = new float[epochs];

        for (int epoch = 0; epoch < epochs; epoch++) {
            epochsGradiantAverageSum[epoch] = 0;
            for (int i = 0; i < inputs.length; i++) {
                float[] input = inputs[i];
                float[] target = targets[i];

                float[] predictedOutput = predict(input);


                epochsGradiantAverageSum[epoch] += backpropagate(target, predictedOutput, learningRate);
            }
            epochsGradiantAverageSum[epoch] /= inputs.length;
        }
        return epochsGradiantAverageSum;
    }

    private float backpropagate(float[] target, float[] predictedOutput, float learningRate) {
        int lastLayerIndex = layers.size() - 1;
        NeuralNetworkLayer outputLayer = layers.get(lastLayerIndex);


        float[] outputGradients = new float[target.length];
        for (int i = 0; i < target.length; i++) {
            outputGradients[i] = target[i] - predictedOutput[i];
        }

        float[] gradiantSum = {0};
        outputLayer.backward(outputGradients, function, learningRate, gradiantSum);
        return gradiantSum[0];
    }


    public float classificationTest(InOut testingData, ClassificationProcessor classificationProcessor) {
        float[][] inputs = testingData.getInputs();
        float[][] outputs = testingData.getOutputs();
        float right = 0;
        float length = inputs.length;
        for (int i = 0; i < length; i++) {
            if (Objects.equals(classificationProcessor.classifyOutInverse(predict(inputs[i])),
                    classificationProcessor.classifyOutInverse(outputs[i])))
                right++;
        }
        return (right / length) * 100f;
    }
}

