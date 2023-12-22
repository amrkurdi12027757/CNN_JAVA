module com.example.cnn_java {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires lombok;
    requires com.opencsv;

    opens com.example.cnn_java to javafx.fxml;
    exports com.example.cnn_java;
}