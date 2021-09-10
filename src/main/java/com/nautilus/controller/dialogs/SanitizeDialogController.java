package com.nautilus.controller.dialogs;

import com.nautilus.config.Config;
import com.nautilus.controller.SanitizeController;
import com.nautilus.domain.Customer;
import com.nautilus.domain.Sanitize;
import com.nautilus.service.CustomerService;
import com.nautilus.service.SanitizeService;
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
public class SanitizeDialogController implements Initializable {

    @Setter
    private SanitizeController sanitizeController;

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
    private Button reset;

    @FXML
    private Button saveSanitize;

    @FXML
    private ListView<Customer> customersList;

    private Sanitize sanitize = null;

    private boolean reloadSanitizeDetailsNeeded = false;

    private Timer timer;

    private final ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
    private final SanitizeService sanitizeService = (SanitizeService) context.getBean("sanitizeServiceImpl");
    private final CustomerService customerService = (CustomerService) context.getBean("customerServiceImpl");


    @FXML
    void cancel(MouseEvent event) {
        ((Node) (event.getSource())).getScene().getWindow().hide();
        if (reloadSanitizeDetailsNeeded)
            sanitizeController.loadSanitizeDetails();
    }

    @FXML
    void reset(ActionEvent event) {
        clearFields();
        title.setText(FxmlView.SANITIZE_DIALOG.getTitle());
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
            this.sanitize.setCustomer(customersList.getSelectionModel().getSelectedItem());
            userSearch.setText("");
            clearUsersList();
            this.name.setText(sanitize.getCustomer().getName());
            this.address.setText(sanitize.getCustomer().getAddress());
            this.city.setText(sanitize.getCustomer().getCity());
            this.phone.setText(sanitize.getCustomer().getPhone());
            if (sanitize.getCustomer().getLegalForm().equals(LEGAL_ENTITY))
                this.legalForm.setText("Pravno lice");
            else this.legalForm.setText("Fizičko lice");
        }
    }

    @FXML
    private void saveSanitize(ActionEvent event) {
        if (validateName(getNameValue()) &&
                emptyValidation(date.getEditor().getText().isEmpty(), "datum")) {

            if (this.sanitize.getId() == null) {
                fillSanitizeFields();
                Sanitize newSanitize = sanitizeService.insert(sanitize);
//                saveAlert("Sanitize for", newSanitize.getCustomer().getName(), newSanitize.getId());
                ((Node) (event.getSource())).getScene().getWindow().hide();
                sanitizeController.loadSanitizeDetails();
            } else {
                fillSanitizeFields();
                Optional<Sanitize> updatedSanitize = sanitizeService.update(sanitize);
                if (updatedSanitize.isPresent()) {
//                    updateAlertSuccess(updatedUser.get());
                    ((Node) (event.getSource())).getScene().getWindow().hide();
                    sanitizeController.loadSanitizeDetails();

                } else {
                    updateAlertFail("Sanitize");
                    reloadSanitizeDetailsNeeded = true;
                    sanitize = sanitizeService.findById(this.sanitize.getId());
                    name.setText(sanitize.getCustomer().getName());
                    address.setText(sanitize.getCustomer().getAddress());
                    city.setText(sanitize.getCustomer().getCity());
                    phone.setText(sanitize.getCustomer().getPhone());
                    legalForm.setText(sanitize.getCustomer().getLegalForm().toString());
                    date.setValue(sanitize.getDate());
                }
            }

        }

    }

    private void fillSanitizeFields() {
        sanitize.setDate(getDateValue());
    }


    public void setSanitize(Sanitize sanitize) {
        this.sanitize = sanitize;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        customersList.setVisible(false);

        Formatter.setDatePickerFormat(date);
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

    private void clearFields() {
        this.sanitize = new Sanitize();
        name.clear();
        address.clear();
        city.clear();
        phone.clear();
        legalForm.clear();
        date.getEditor().clear();
    }

    private void clearUsersList() {
        Platform.runLater(() -> customersList.getItems().clear());
        customersList.setPrefHeight(0);
        customersList.setVisible(false);
    }

}
