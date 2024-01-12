package com.example.cnn_java.datapackage;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InOut {
    private float[][] inputs;
    private float[][] outputs;
}
