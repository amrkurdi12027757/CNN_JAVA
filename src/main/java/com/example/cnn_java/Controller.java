package com.example.cnn_java;

import com.example.cnn_java.datapackage.CSVData;
import com.example.cnn_java.datapackage.InOut;
import com.example.cnn_java.datapackage.dataprocessor.ClassificationProcessor;
import com.example.cnn_java.neuralnetworkpackage.NeuralNetwork;
import com.example.cnn_java.neuralnetworkpackage.activationfunctions.Activation;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static com.example.cnn_java.InterfaceImplementingClassesFinder.findImplementingClasses;


public class Controller {
  @FXML
  public ScatterChart testChart;
  @FXML
  public ScatterChart predictedChart;
  private static ClassificationProcessor classificationProcessor;
  private static LinkedHashMap<String, XYChart.Series> seriesList;

  private static InOut trainingData;

  private static InOut testingData;
  private static NeuralNetwork neuralNetwork;
  private static float[][] inputs;
  private static float[][] outputs;
  private static CSVData csvData;
  private static int runNumber;
  @FXML
  public ChoiceBox functionSelection;
  @FXML
  public Spinner epochsSpinner;
  @FXML
  public Spinner learningRateSpinner;
  @FXML
  public Spinner trainingDataSpinner;
  @FXML
  public AreaChart gradientGraph;
  @FXML
  public Label accuracy;
  @FXML
  public CheckBox goalCheckBox;
  @FXML
  public HBox parent;

  @FXML
  public Label loadedFileName;
  @FXML
  public Button resetButton;
  @FXML
  public Button startButton;
  @FXML
  public HBox testDataContainer;
  private boolean init = true;

  @FXML
  public Spinner hiddenNeuronsSpinner;
  private FileChooser fileChooser;


  @FXML
  public void initialize() throws IOException, ClassNotFoundException {
    startButton.setDisable(true);
    resetButton.setDisable(true);
    fileChooser = new FileChooser();
    List<Class<?>> implementedActivationFunctions = findImplementingClasses(Activation.class);
    ObservableList<Object> items = FXCollections.observableArrayList();
    assert implementedActivationFunctions != null;
    items.addAll(implementedActivationFunctions);
    functionSelection.setItems(items);
    functionSelection.setValue(items.get(0));
    functionSelection.setConverter(new StringConverter() {
      @Override
      public String toString(Object o) {
        return ((Class<?>) o).getSimpleName();
      }

      @Override
      public Object fromString(String s) {
        return null;
      }
    });

  }

  @FXML
  public void onStartButtonClick(ActionEvent actionEvent) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
    Platform.runLater(() -> {
              if (init) {
                classificationProcessor = new ClassificationProcessor(csvData, ((Double) trainingDataSpinner.getValue()).floatValue());
                trainingData = csvData.getTrainingData(classificationProcessor);
                testingData = csvData.getTestingData(classificationProcessor);
                try {
                  neuralNetwork = NeuralNetwork.builder()
                          .layer(classificationProcessor.classes.size() - 1)
                          .layer((int) hiddenNeuronsSpinner.getValue())
                          .layer(classificationProcessor.classes.get("class").size())
                          .function((Activation) ((Class<?>) functionSelection.getValue()).getDeclaredConstructor().newInstance())
                          .build();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                         NoSuchMethodException e) {
                  throw new RuntimeException(e);
                }
                inputs = testingData.getInputs();
                outputs = testingData.getOutputs();
                seriesList = new LinkedHashMap<>();
                runNumber = 0;
                fillGraph(testChart, inputs, outputs, "", false);
                gradientGraph.getData().add(new XYChart.Series<>());
                disableInputs();
                init = false;
              }
              Integer epochsSpinnerValue = (Integer) epochsSpinner.getValue();
              Platform.runLater(() -> {
                do {
                  float[] gradiantAverageSum = neuralNetwork.train(trainingData, ((Double) learningRateSpinner.getValue()).floatValue(), goalCheckBox.isSelected() ? 1 : epochsSpinnerValue);
                  List<float[]> inputs = new ArrayList<>();
                  List<float[]> outputs = new ArrayList<>();
                  for (int i = 0; i < Controller.inputs.length; i++) {
                    float[] input = Controller.inputs[i];
                    float[] predicted = neuralNetwork.predict(input);
                    if (Objects.equals(classificationProcessor.classifyOutInverse(predicted), classificationProcessor.classifyOutInverse(Controller.outputs[i]))) {
                      inputs.add(input);
                      outputs.add(predicted);
                    }
                  }
                  if (outputs.size() > 0) {
                    fillGraph(predictedChart, inputs.toArray(float[][]::new), outputs.toArray(float[][]::new), "predicted", true);
                  }
                  float classificationTest = neuralNetwork.classificationTest(testingData, classificationProcessor);
                  accuracy.setText(String.format("%.2f %%", classificationTest));
                  XYChart.Series series = (XYChart.Series) gradientGraph.getData().get(0);

                  for (int i = 0; i < gradiantAverageSum.length; i++) {
                    series.getData().add(new XYChart.Data<>((runNumber + i), gradiantAverageSum[i]));
                  }
                  runNumber += epochsSpinnerValue;
                  if (goalCheckBox.isSelected() && classificationTest >= epochsSpinnerValue) break;
                } while (goalCheckBox.isSelected());
              });
            }
    );
  }

  private void fillGraph(ScatterChart scatterChart, float[][] inputs, float[][] outputs, String name, boolean rebuild) {
    if (rebuild) {

      Set<Map.Entry<String, XYChart.Series>> entries = seriesList.entrySet();
      for (Map.Entry<String, XYChart.Series> next : entries) {
        if (next.getKey().contains(name))
          next.getValue().getData().clear();
      }
    }
    for (int i = 0; i < inputs.length; i++) {
      float color = inputs[i][1];
      StringBuilder combination = new StringBuilder(classificationProcessor.classifyOutInverse(outputs[i])).append(color);
      XYChart.Series series = seriesList.get(combination + name);
      if (series == null) {
        XYChart.Series<Object, Object> serieS = new XYChart.Series<>();
        serieS.getData().add(new XYChart.Data<>(inputs[i][1], inputs[i][0]));
        seriesList.put(combination + name, serieS);
        scatterChart.getData().add(serieS);
      } else
        series.getData().add(new XYChart.Data<>(inputs[i][1], inputs[i][0]));
    }

    int size = 8 - scatterChart.getData().size();
    for (int i = 0; i < size; i++) {
      scatterChart.getData().add(new XYChart.Series<>());
    }
  }

  @FXML
  public void onResetButtonClick(ActionEvent actionEvent) {
    enableInputs();
    clearGraphs();
    accuracy.setText(String.format("%.2f %%", 0f));
    init = true;
  }

  private void disableInputs() {
    if (goalCheckBox.isSelected())
      epochsSpinner.setDisable(true);
    learningRateSpinner.setDisable(true);
    hiddenNeuronsSpinner.setDisable(true);
    trainingDataSpinner.setDisable(true);
  }

  private void enableInputs() {
    goalCheckBox.setSelected(false);
    epochsSpinner.setDisable(false);
    learningRateSpinner.setDisable(false);
    hiddenNeuronsSpinner.setDisable(false);
    trainingDataSpinner.setDisable(false);
  }

  private void clearGraphs() {
    if (!init) {
      for (XYChart.Series series : seriesList.values()) {
        series.getData().clear();
      }
      gradientGraph.getData().clear();
    }
  }

  public void onLoadFileClick(ActionEvent actionEvent) {
    File file = fileChooser.showOpenDialog(this.parent.getScene().getWindow());
    if (file != null) {
      loadedFileName.setText(file.getName());
      csvData = new CSVData(file);
      startButton.setDisable(false);
      resetButton.setDisable(false);
      testDataContainer.getChildren().clear();
      for (int i = 0; i < csvData.getNumCols(); i++) {
        Label label = new Label();
        label.setText(csvData.getRows().get(0)[i]);
        VBox vBox = new VBox();
        vBox.getChildren()
                .addAll(label, new TextField());
        testDataContainer.getChildren().add(vBox);
      }
      Button button = new Button();
      button.setText("Predict");
      testDataContainer.getChildren().add(button);
      Label label = new Label();
      AtomicReference<TextField> textField = new AtomicReference<>();
      testDataContainer.getChildren().add(label);
      button.setOnMouseClicked(mouseEvent -> {
        label.setText("");
        List<String> inputs = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        ObservableList<Node> children = testDataContainer.getChildren();
        int lastBox = children.size() - 3;
        for (int i = 0; i < children.size() - 2; i++) {
          Node outerNode = children.get(i);
          ObservableList<Node> nodes = ((VBox) outerNode).getChildren();
          for (Node node : nodes) {
            if (node instanceof TextField) {
              String text = ((TextField) node).getText();
              if (text == null || text.isBlank() && outerNode != children.get(lastBox))
                label.setText("Wierd Input");
              if (outerNode == children.get(lastBox)) {
                textField.set((TextField) node);
                node.setDisable(true);
              } else
                inputs.add(text);
            }
            if (node instanceof Label) {
              String text = ((Label) node).getText();
              if (text == null || text.isBlank())
                label.setText("Wierd Input");
              else
                labels.add(text);
            }
          }
        }
        float[] inputsFloat = new float[inputs.size()];
        for (int i = 0; i < inputsFloat.length; i++) {
          inputsFloat[i] = classificationProcessor.classifyIn(inputs.get(i), labels.get(i));
        }
        textField.get().setText(classificationProcessor.classifyOutInverse(neuralNetwork.predict(inputsFloat)));
      });
    }

  }

  public void onExportCButtonClick(ActionEvent actionEvent) throws IOException {
    // Creating nuralNetwork.h
    String headerFileName = "nuralNetwork.h";
    FileWriter writerH = new FileWriter(headerFileName);
    writerH.write("// Header file for nuralNetwork\n#include \"Arduino.h\"\n");
    writerH.write(classificationProcessor.toH());
    writerH.write(neuralNetwork.toH());
    writerH.close();

    // Creating nuralNetwork.cpp
    FileWriter writerCpp = new FileWriter("nuralNetwork.cpp");
    writerCpp.write("// Implementation file for nuralNetwork\n#include \"" + headerFileName + "\"\n");
    writerCpp.write(classificationProcessor.toCpp());
    writerCpp.write(neuralNetwork.toCpp());
    writerCpp.close();
  }
}