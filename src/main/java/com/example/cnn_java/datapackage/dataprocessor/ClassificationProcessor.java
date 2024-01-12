package com.example.cnn_java.datapackage.dataprocessor;

import com.example.cnn_java.arduino.Ciable;
import com.example.cnn_java.datapackage.CSVData;

import java.util.*;

public class ClassificationProcessor implements Processor, Ciable {
  private LinkedHashMap<String, Float> maxValues;
  private LinkedHashMap<String, Float> minValues;
  private LinkedHashMap<String, Float> means;
  private LinkedHashMap<String, Float> standardDeviations;
  public LinkedHashMap<String, LinkedHashSet<String>> classes;


  private final CSVData csv;
  private final float trainToTestRatio;
  private final float[][] processedInputs;
  private final float[][] processedOutputs;
  private String[] lastClasses;

  public ClassificationProcessor(CSVData csv, float trainToTestRatio) {
    this.csv = csv;
    List<String[]> rows = csv.getRows();
    List<String[]> shuffledRows = new ArrayList<>(rows.subList(1, csv.getNumRows()));
    Collections.shuffle(shuffledRows);
    shuffledRows.add(0, rows.get(0));
    csv.setRows(shuffledRows);
    this.trainToTestRatio = trainToTestRatio;
    for (int line = 0; line < csv.getNumRows(); line++) {
      if (line != 0) {
        String[] cells = shuffledRows.get(line);
        int cellIndex = 0;
        for (String label : classes.keySet()) {
          String cellValue = cells[cellIndex];
          try {
            float number = Float.parseFloat(cellValue);
            if (number > maxValues.get(label))
              maxValues.replace(label, number);
            if (number < minValues.get(label))
              minValues.replace(label, number);
            means.replace(label, means.get(label) + number);

            if (line == csv.getNumRows() - 1) {
              means.replace(label, means.get(label) / csv.getNumRows());
              standardDeviationLoop:
              for (int line1 = 1; line1 < csv.getNumRows(); line1++) {
                String[] cells1 = shuffledRows.get(line1);
                int cellIndex1 = 0;
                for (String label1 : classes.keySet()) {
                  String cellValue1 = cells1[cellIndex1];
                  float number1 = Float.parseFloat(cellValue1);
                  standardDeviations.replace(label1, (float) (standardDeviations.get(label1) + Math.pow(number1 - means.get(label1), 2)));
                  if (line1 == csv.getNumRows() - 1)
                    standardDeviations.replace(label1, (float) Math.sqrt(standardDeviations.get(label1) / csv.getNumRows()));
                  cellIndex1++;
                  if (cellIndex1 == csv.getNumCols() - 1)
                    break;
                }
              }
            }

          } catch (NumberFormatException ex) {
            HashSet<String> existingClasses = classes.get(label);
            existingClasses.add(cellValue);
          }
          cellIndex++;
        }
      } else {
        maxValues = new LinkedHashMap<>();
        minValues = new LinkedHashMap<>();
        means = new LinkedHashMap<>();
        standardDeviations = new LinkedHashMap<>();
        classes = new LinkedHashMap<>();
        for (String label :
                shuffledRows.get(0)) {
          String lowerCase = label.toLowerCase();
          maxValues.put(lowerCase, Float.MIN_VALUE);
          minValues.put(lowerCase, Float.MAX_VALUE);
          means.put(lowerCase, 0f);
          standardDeviations.put(lowerCase, 0f);
          classes.put(lowerCase, new LinkedHashSet<>());
        }
      }
    }
    processedInputs = processInput();
    processedOutputs = processOutput();
  }

  private float[][] processInput() {
    float[][] inputs = new float[csv.getNumRows() - 1][csv.getNumCols() - 1];
    List<String[]> csvRows = csv.getRows();
    Set<String> labels = classes.keySet();
    String[] labelsArray = labels.toArray(String[]::new);
    for (int rowIndex = 1; rowIndex < csv.getNumRows(); rowIndex++) {
      String[] row = csvRows.get(rowIndex);
      for (int cellIndex = 0; cellIndex < row.length - 1; cellIndex++) {
        String value = row[cellIndex];
        String label = labelsArray[cellIndex];
        try {
          inputs[rowIndex - 1][cellIndex] = zScoreNormalization(Float.parseFloat(value), label);
        } catch (NumberFormatException ex) {
          inputs[rowIndex - 1][cellIndex] = classifyIn(value, label);
        }
      }
    }
    return inputs;
  }

  private float[][] processOutput() {
    float[][] outputs = new float[csv.getNumRows() - 1][csv.getNumCols() - 1];
    List<String[]> csvRows = csv.getRows();
    Set<String> labels = classes.keySet();
    String[] labelsArray = labels.toArray(String[]::new);
    int lastIndex = classes.size() - 1;
    for (int rowIndex = 1; rowIndex < csv.getNumRows(); rowIndex++) {
      String[] row = csvRows.get(rowIndex);

      String value = row[lastIndex];
      String label = labelsArray[lastIndex];

      outputs[rowIndex - 1] = classifyOut(value, label);

    }
    if (lastClasses == null) {
      LinkedHashSet[] listOfClasses = classes.values().toArray(LinkedHashSet[]::new);
      for (int i = 0; i < listOfClasses.length; i++) {
        if (i == (listOfClasses.length - 1))
          lastClasses = (String[]) listOfClasses[i].toArray(String[]::new);
      }
    }
    return outputs;
  }


  private float minMaxNormalize(float value, String label) {
    return normalize(value, maxValues.get(label), minValues.get(label));
  }

  private float normalize(float value, float maxValue, float minValue) {
    return (value - minValue) / (maxValue - minValue);
  }

  private float zScoreNormalization(float value, String label) {
    return (value - means.get(label)) / standardDeviations.get(label);
  }

  private float[] classifyOut(String value, String label) {
    LinkedHashSet<String> existingClasses = classes.get(label);
    float[] classification = new float[existingClasses.size()];
    int i = 0;
    for (String existingClass
            : existingClasses) {
      classification[i] = value.equalsIgnoreCase(existingClass) ? 1 : 0;
      i++;
    }
    return classification;
  }

  public String classifyOutInverse(float[] out) {
    float max = -Float.MAX_VALUE;
    int index = -1;
    for (int i = 0; i < out.length; i++) {
      if (out[i] > max) {
        max = out[i];
        index = i;
      }
    }
    return lastClasses[index];
  }

  public float classifyIn(String value, String label) {
    String lowerCase = label.toLowerCase();
    LinkedHashSet<String> existingClasses = classes.get(lowerCase);
    if (existingClasses.isEmpty()) {
      try {
        return zScoreNormalization(Float.parseFloat(value), lowerCase);
      } catch (NumberFormatException exception) {
        return 0.0f;
      }
    }
    int i = 0;
    for (String existingClass
            : existingClasses) {
      if (value.equalsIgnoreCase(existingClass)) {
        return normalize(i, existingClasses.size(), 0);
      }
      i++;
    }
    return 0.0f;
  }

  @Override
  public float[][] getTrainInputs() {
    return splitData(processedInputs, true);
  }

  @Override
  public float[][] getTrainOutputs() {
    return splitData(processedOutputs, true);
  }

  @Override
  public float[][] getTestInputs() {
    return splitData(processedInputs, false);
  }

  @Override
  public float[][] getTestOutputs() {
    return splitData(processedOutputs, false);
  }

  private float[][] splitData(float[][] data, boolean upper) {
    float fullLength = data.length;
    int length = (int) (upper ? Math.ceil(fullLength * trainToTestRatio) : Math.floor(fullLength * (1 - trainToTestRatio)));
    float[][] splittedData = new float[length][data[0].length];
    int lowerStartingBound = (int) (fullLength - length);
    for (int i = lowerStartingBound; i < length + lowerStartingBound; i++) {
      System.arraycopy(data[i], 0, splittedData[i - lowerStartingBound], 0, data[0].length);
    }
    return splittedData;
  }

  @Override
  public String toCpp() {
    StringBuilder stringBuilder = new StringBuilder("String classifyOutInverse(float* out) {\n")
            .append("   float max = -9999999;\n   int index = -1;\n");
    for (int i = 0; i < lastClasses.length; i++) {
      stringBuilder.append("    if (out[").append(i).append("] > max) {\n       max = out[").append(i).append("];\n       index = ").append(i).append(";\n    }\n");
    }
    stringBuilder.append("    delete[] out;\n   return clazz[index];\n}\n");
    stringBuilder.append("float* classifyIn(float* in){\n").append("  float* classed = new float[").append(csv.getNumCols() - 1).append("];\n");
    String[] labels = csv.getRows().get(0);
    for (int i = 0; i < labels.length - 1; i++) {
      stringBuilder.append("  classed[").append(i).append("] = ( in[").append(i).append("] - ").append(means.get(labels[i].toLowerCase())).append(" ) / ").append(standardDeviations.get(labels[i].toLowerCase())).append(";\n");
    }
    stringBuilder.append("  return classed;\n}\n");
    return stringBuilder.toString();
  }

  @Override
  public String toH() {
    StringBuilder stringBuilder = new StringBuilder("const String clazz[] = {");
    for (int i = 0; i < lastClasses.length; i++) {
      stringBuilder.append("\"").append(lastClasses[i]).append((i != lastClasses.length - 1) ? "\"," : "\"};\n");
    }
    stringBuilder.append("String classifyOutInverse(float*);\nfloat* classifyIn(float*);\n");
    return stringBuilder.toString();
  }
}
