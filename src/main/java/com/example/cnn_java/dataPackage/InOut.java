package com.example.cnn_java.dataPackage;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InOut {
    private float[][] inputs;
    private float[][] outputs;
}
