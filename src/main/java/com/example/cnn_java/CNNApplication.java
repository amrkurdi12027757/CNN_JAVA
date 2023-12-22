package com.example.cnn_java;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;


public class CNNApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {

        Parent load = FXMLLoader.load(Objects.requireNonNull(CNNApplication.class.getResource("view.fxml")));

        Scene scene = new Scene(load,1100,700);
        primaryStage.setScene(scene);
        primaryStage.setTitle("App");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();

    }


}