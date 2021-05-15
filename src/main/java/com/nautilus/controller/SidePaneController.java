package com.nautilus.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import lombok.Setter;

import java.net.URL;
import java.util.ResourceBundle;

public class SidePaneController implements Initializable {

    @Setter
    private CustomerController customerController;

    @FXML
    private VBox rightVbox;

    @FXML
    private Label labelSanitizeLate;

    @FXML
    private Label labelSanitizeNeeded;

    @FXML
    private Label labelPaymentLate;

    @FXML
    private Label labelPackagingDept;

    @FXML
    void packagingDept(MouseEvent event) {

    }

    @FXML
    void paymentLate(MouseEvent event) {

    }

    @FXML
    void sanitizeLate(MouseEvent event) {

    }

    @FXML
    void sanitizeNeeded(MouseEvent event) {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
