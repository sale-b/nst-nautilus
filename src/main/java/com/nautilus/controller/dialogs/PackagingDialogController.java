package com.nautilus.controller.dialogs;

import com.nautilus.config.Config;
import com.nautilus.controller.PackagingController;
import com.nautilus.domain.Customer;
import com.nautilus.domain.Packaging;
import com.nautilus.service.CustomerService;
import com.nautilus.service.PackagingService;
import com.nautilus.util.Formatter;
import com.nautilus.view.FxmlView;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
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
import java.util.*;

import static com.nautilus.domain.Customer.LegalForm.LEGAL_ENTITY;
import static com.nautilus.util.Validation.*;

@SuppressWarnings("unused")
@Slf4j
public class PackagingDialogController implements Initializable {

    @Setter
    private PackagingController packagingController;

    @FXML
    @Getter
    private Label title;

    @FXML
    private TextField userSearch;

    @FXML
    @Getter
    private TextField name;

    @FXML
    @Getter
    private TextField city;

    @FXML
    @Getter
    private TextField address;

    @FXML
    @Getter
    private TextField phone;

    @FXML
    @Getter
    private TextField legalForm;

    @FXML
    @Getter
    private DatePicker date;

    @FXML
    private FontAwesomeIconView cancel;

    @FXML
    @Getter
    private Spinner<Integer> packagingSmallQuantity;

    @FXML
    @Getter
    private Spinner<Integer> packagingLargeQuantity;

    @FXML
    private Button reset;

    @FXML
    private Button savePackaging;

    @FXML
    private ListView<Customer> customersList;

    private Packaging packaging = null;

    private boolean reloadPackagingDetailsNeeded = false;

    private Timer timer;

    private final ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
    private final PackagingService packagingService = (PackagingService) context.getBean("packagingServiceImpl");
    private final CustomerService customerService = (CustomerService) context.getBean("customerServiceImpl");


    @FXML
    void cancel(MouseEvent event) {
        ((Node) (event.getSource())).getScene().getWindow().hide();
        if (reloadPackagingDetailsNeeded)
            packagingController.loadPackagingDetails();
    }

    @FXML
    void reset(ActionEvent event) {
        clearFields();
        title.setText(FxmlView.PACKAGING_DIALOG.getTitle());
    }

    @FXML
    void search(KeyEvent event) {
        if (timer != null) {
            timer.cancel();
        }
        TimerTask timerTask = new TimerTask() {

            @Override
            public void run() {
                if (userSearch.getText().trim().length() > 2) {
                    Platform.runLater(() -> {
                        customersList.getItems().clear();
                        customersList.getItems().addAll(customerService.findByTextFields(
                                userSearch.getText().toLowerCase().trim()));
                        //ROW HEIGHT = 23
                        customersList.setVisible(true);
                        customersList.setPrefHeight(Math.min(customersList.getItems().size(), 10) * 23 + 16);
                    });
                } else if (userSearch.getText().equals("")) {
                    clearUsersList();
                }
                timer.cancel();
            }
        };
        timer = new Timer();
        timer.schedule(timerTask, 500);
    }

    @FXML
    void customerSelect(MouseEvent event) {
        if (customersList.getSelectionModel().getSelectedItem() != null) {
            this.packaging.setCustomer(customersList.getSelectionModel().getSelectedItem());
            userSearch.setText("");
            clearUsersList();
            this.name.setText(packaging.getCustomer().getName());
            this.address.setText(packaging.getCustomer().getAddress());
            this.city.setText(packaging.getCustomer().getCity());
            this.phone.setText(packaging.getCustomer().getPhone());
            if (packaging.getCustomer().getLegalForm().equals(LEGAL_ENTITY))
                this.legalForm.setText("Pravno lice");
            else this.legalForm.setText("Fizičko lice");
            this.packagingSmallQuantity.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, this.packaging.getCustomer().getPackagingSmall() > 0 ? this.packaging.getCustomer().getPackagingSmall() : 0, 0));
            this.packagingLargeQuantity.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, this.packaging.getCustomer().getPackagingLarge() > 0 ? this.packaging.getCustomer().getPackagingLarge() : 0, 0));
        }
    }

    @FXML
    private void savePackaging(ActionEvent event) {
        if (validateName(getNameValue()) &&
                emptyValidation(date.getEditor().getText().isEmpty(), "datum") &&
                emptyValidation((getPackagingLargeQuantityValue() == 0 && getPackagingSmallQuantityValue() == 0), "razduziavanje balona 15L/19L")) {

            if (this.packaging.getId() == null) {
                fillPackagingFields();
                Packaging newPackaging = packagingService.insert(packaging);
//                saveAlert("Packaging for", newPackaging.getCustomer().getName(), newPackaging.getId());
                ((Node) (event.getSource())).getScene().getWindow().hide();
                packagingController.loadPackagingDetails();
            } else {
                fillPackagingFields();
                Optional<Packaging> updatedPackaging = packagingService.update(packaging);
                if (updatedPackaging.isPresent()) {
//                    updateAlertSuccess(updatedUser.get());
                    ((Node) (event.getSource())).getScene().getWindow().hide();
                    packagingController.loadPackagingDetails();

                } else {
                    updateAlertFail("Packaging");
                    reloadPackagingDetailsNeeded = true;
                    packaging = packagingService.findById(this.packaging.getId());
                    name.setText(packaging.getCustomer().getName());
                    address.setText(packaging.getCustomer().getAddress());
                    city.setText(packaging.getCustomer().getCity());
                    phone.setText(packaging.getCustomer().getPhone());
                    legalForm.setText(packaging.getCustomer().getLegalForm().toString());
                    date.setValue(packaging.getDate());
                    packagingSmallQuantity.getValueFactory().setValue(packaging.getWaterSmallReturned());
                    packagingLargeQuantity.getValueFactory().setValue(packaging.getWaterLargeReturned());
                }
            }

        }

    }

    private void fillPackagingFields() {
        packaging.setDate(getDateValue());
        packaging.setWaterSmallReturned(getPackagingSmallQuantityValue());
        packaging.setWaterLargeReturned(getPackagingLargeQuantityValue());
    }


    public void setPackaging(Packaging packaging) {
        this.packaging = packaging;
        if (this.packaging.getId() != null) {
            this.packagingSmallQuantity.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, this.packaging.getCustomer().getPackagingSmall() + this.packaging.getWaterSmallReturned(), 0));
            this.packagingLargeQuantity.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, this.packaging.getCustomer().getPackagingLarge() + this.packaging.getWaterLargeReturned(), 0));
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.packagingSmallQuantity.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 0, 0));
        this.packagingLargeQuantity.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 0, 0));

        customersList.setVisible(false);

        Formatter.setDatePickerFormat(date);

        packagingSmallQuantity.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                packagingSmallQuantity.getEditor().setText(newValue.replaceAll("[^\\d]", ""));
            }
            if (!packagingSmallQuantity.getEditor().getText().equals("")) {
                packagingSmallQuantity.increment(0); // won't change value, but will commit editor
            }
        });

        packagingLargeQuantity.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                packagingLargeQuantity.getEditor().setText(newValue.replaceAll("[^\\d]", ""));
            }
            if (!packagingLargeQuantity.getEditor().getText().equals("")) {
                packagingLargeQuantity.increment(0); // won't change value, but will commit editor
            }
        });
    }

    public String getNameValue() {
        return getTrimmedString(name);
    }

    static String getTrimmedString(TextField name) {
        StringBuilder sb = new StringBuilder();
        List<String> words = Arrays.asList(name.getText().trim().replaceAll(" +", " ").split(" "));
        words.forEach(w -> sb.append(w.trim()).append(" "));
        return sb.toString().trim();
    }

    public LocalDate getDateValue() {
        return date.getValue();
    }

    public Integer getPackagingSmallQuantityValue() {
        return packagingSmallQuantity.getValue();
    }

    public Integer getPackagingLargeQuantityValue() {
        return packagingLargeQuantity.getValue();
    }

    private void clearFields() {
        this.packaging = new Packaging();
        name.clear();
        address.clear();
        city.clear();
        phone.clear();
        legalForm.clear();
        date.getEditor().clear();
        packagingSmallQuantity.getValueFactory().setValue(0);
        packagingLargeQuantity.getValueFactory().setValue(0);
    }

    private void clearUsersList() {
        Platform.runLater(() -> customersList.getItems().clear());
        customersList.setPrefHeight(0);
        customersList.setVisible(false);
    }

}
