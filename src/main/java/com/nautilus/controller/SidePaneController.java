package com.nautilus.controller;

import com.nautilus.config.Config;
import com.nautilus.domain.dto.CustomerDto;
import com.nautilus.service.CustomerService;
import com.nautilus.util.Formatter;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import lombok.Setter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SidePaneController implements Initializable {

    ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
    CustomerService customerService = (CustomerService) context.getBean("customerServiceImpl");

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
    private Label labelPackagingDebt;

    @FXML
    private Label labelUnfulfilledObligation;

    @FXML
    private Label labelSanitizeLateTitle;

    @FXML
    private Label labelSanitizeNeededTitle;

    @FXML
    private Label labelPaymentLateTitle;

    @FXML
    private Label labelPackagingDebtTitle;

    @FXML
    private Label labelUnfulfilledObligationTitle;

    @FXML
    void unfulfilledObligation(MouseEvent event) {
        List<CustomerDto> customersDto = customerService.getDtoWithUnfulfilledObligation();
        resetCustomersDtoList(customersDto);
    }

    @FXML
    void packagingDebt(MouseEvent event) {
        List<CustomerDto> customersDto = customerService.getDtoWithPackagingDebt();
        resetCustomersDtoList(customersDto);
    }

    @FXML
    void paymentLate(MouseEvent event) {
        List<CustomerDto> customersDto = customerService.getDtoWithDebt();
        resetCustomersDtoList(customersDto);
    }

    @FXML
    void sanitizeLate(MouseEvent event) {
        List<CustomerDto> customersDto = customerService.getDtoWithSanitizeLate();
        resetCustomersDtoList(customersDto);
    }

    @FXML
    void sanitizeNeeded(MouseEvent event) {
        List<CustomerDto> customersDto = customerService.getDtoWithSanitizeNeeded();
        resetCustomersDtoList(customersDto);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    private void resetCustomersDtoList(List<CustomerDto> customersDto) {
        customerController.getCustomerList().clear();
        customerController.getCustomerList().addAll(customersDto);
        customerController.getCustomerTable().setItems(customerController.getCustomerList());
    }

    void resetCountsForAllFilters() {
        Platform.runLater(() -> {
            Integer count = customerService.countDtoWithUnfulfilledObligation();
            labelUnfulfilledObligation.setText(count.toString());
            Formatter.setObligationLabelStyle(labelUnfulfilledObligationTitle, count);
        });

        Platform.runLater(() -> {
            Integer count = customerService.countDtoWithSanitizeLate();
            labelSanitizeLate.setText(count.toString());
            Formatter.setObligationLabelStyle(labelSanitizeLateTitle, count);
        });

        Platform.runLater(() -> {
            Integer count = customerService.countDtoWithSanitizeNeeded();
            labelSanitizeNeeded.setText(count.toString());
            Formatter.setSanitizeTitleLabelStyle(labelSanitizeNeededTitle, count);
        });

        Platform.runLater(() -> {
            Integer count = customerService.countDtoWithDebt();
            labelPaymentLate.setText(count.toString());
            Formatter.setObligationLabelStyle(labelPaymentLateTitle, count);
        });

        Platform.runLater(() -> {
            Integer count = customerService.countDtoWithPackagingDebt();
            labelPackagingDebt.setText(count.toString());
            Formatter.setObligationLabelStyle(labelPackagingDebtTitle, count);
        });
    }
}
