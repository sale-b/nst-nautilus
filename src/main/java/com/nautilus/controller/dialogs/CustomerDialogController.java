package com.nautilus.controller.dialogs;

import com.nautilus.config.Config;
import com.nautilus.controller.CustomerController;
import com.nautilus.domain.Customer;
import com.nautilus.service.CustomerService;
import com.nautilus.util.Formatter;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import static com.nautilus.controller.dialogs.OrderDialogController.getString;
import static com.nautilus.domain.Customer.CustomerType.INDIVIDUAL;
import static com.nautilus.domain.Customer.CustomerType.LEGAL_ENTITY;
import static com.nautilus.util.Validation.*;

@SuppressWarnings("unused")
@Slf4j
public class CustomerDialogController implements Initializable {

    @Setter
    private CustomerController customerController;

    @FXML
    private Button cancel;

    @FXML
    @Getter
    private Label title;

    @FXML
    @Getter
    private TextField name;

    @FXML
    @Getter
    private TextField city;

    @FXML
    private ListView<String> cityList;

    @FXML
    @Getter
    private TextField address;

    @FXML
    @Getter
    private TextField phone;

    @FXML
    @Getter
    private DatePicker date;

    @FXML
    @Getter
    private Spinner<Integer> sanitisePeriod;

    @FXML
    @Getter
    private RadioButton rbIndividual;

    @FXML
    private ToggleGroup type;

    @FXML
    @Getter
    private RadioButton rbLegalEntity;

    @FXML
    private Button reset;

    @FXML
    private Button saveCustomer;

    @Setter
    private Customer customer = null;

    private boolean reloadUserDetailsNeeded = false;

    private Timer timer;

    private final ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
    private final CustomerService customerService = (CustomerService) context.getBean("customerServiceImpl");

    //todo
    @FXML
    void cancel(ActionEvent event) {
        ((Node) (event.getSource())).getScene().getWindow().hide();
        if (reloadUserDetailsNeeded)
            customerController.loadCustomerDetails();
    }

    @FXML
    void reset(ActionEvent event) {
        clearFields();
        title.setText("Dodavanje novog klojenta");
    }

    @FXML
    void search(KeyEvent event) {
        if (timer != null) {
            timer.cancel();
        }
        TimerTask timerTask = new TimerTask() {

            @Override
            public void run() {
                if (!city.getText().equals("")) {
                    Platform.runLater(() -> {
                        cityList.getItems().clear();
                        cityList.getItems().addAll(customerService.findDistinctCities(city.getText().toLowerCase().trim()));
                        //ROW HEIGHT = 23
                        cityList.setVisible(true);
                        cityList.setPrefHeight(Math.min(cityList.getItems().size(), 10) * 23 + 2);
                    });
                } else {
                    clearCityList();
                }
                timer.cancel();
            }
        };
        timer = new Timer();
        timer.schedule(timerTask, 1000);
    }

    @FXML
    void citySelect(MouseEvent event) {
        city.setText(cityList.getSelectionModel().getSelectedItem());
        clearCityList();
    }

    @FXML
    private void saveCustomer(ActionEvent event) {
        if (validateName(getNameValue()) &&
                emptyValidation(date.getEditor().getText().isEmpty())) {
            if (this.customer == null) {
                customer = new Customer();
                fillCustomerFields(customer);
                Customer newCustomer = customerService.insert(customer);
//                saveAlert("Customer", newCustomer.getName(), newCustomer.getId());
                ((Node) (event.getSource())).getScene().getWindow().hide();
                customerController.loadCustomerDetails();
            } else {
                fillCustomerFields(customer);
                Optional<Customer> updatedUser = customerService.update(customer);
                if (updatedUser.isPresent()) {
//                    updateAlertSuccess(updatedUser.get());
                    ((Node) (event.getSource())).getScene().getWindow().hide();
                    customerController.loadCustomerDetails();
                } else {
                    updateAlertFail("Customer");
                    reloadUserDetailsNeeded = true;
                    customer = customerService.findById(this.customer.getId());
                    name.setText(customer.getName());
                    address.setText(customer.getAddress());
                    city.setText(customer.getCity());
                    date.setValue(customer.getDate());
                    if (this.customer.getType().equals(INDIVIDUAL)) rbIndividual.setSelected(true);
                    else getRbLegalEntity().setSelected(true);
                }
            }

        }

    }

    private void fillCustomerFields(Customer customer) {
        customer.setName(getNameValue());
        customer.setAddress(getAddressValue());
        customer.setCity(getCityValue());
        customer.setPhone(getPhoneValue());
        customer.setSanitisePeriodInMonths(getSanitizePeriodValue());
        customer.setDate(getDateValue());
        customer.setType(getTypeValue());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.sanitisePeriod.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 12, 1));
        cityList.setVisible(false);
        Formatter.setDatePickerFormat(date);
    }


    public String getNameValue() {
        return getString(name);
    }

    public String getAddressValue() {
        return address.getText().trim().replaceAll(" +", " ");
    }

    public String getCityValue() {
        return city.getText().trim().replaceAll(" +", " ");
    }

    public String getPhoneValue() {
        return phone.getText().replaceAll(" ", "");
    }

    public Integer getSanitizePeriodValue() {
        return sanitisePeriod.getValue();
    }

    public LocalDate getDateValue() {
        return date.getValue();
    }

    public Customer.CustomerType getTypeValue() {
        return rbIndividual.isSelected() ? INDIVIDUAL : LEGAL_ENTITY;
    }


    private void clearFields() {
        this.customer = null;
        name.clear();
        address.clear();
        city.clear();
        date.getEditor().clear();
        phone.clear();
        sanitisePeriod.getEditor().setText("1");
        rbIndividual.setSelected(true);
        rbLegalEntity.setSelected(false);
    }

    private void clearCityList() {
        Platform.runLater(() -> cityList.getItems().clear());
        cityList.setPrefHeight(0);
        cityList.setVisible(false);
    }

    private boolean emptyValidation(boolean empty) {
        if (!empty) {
            return true;
        } else {
            validationAlert(true, "date");
            return false;
        }
    }

}
