package com.nautilus.controller;

import com.jfoenix.controls.JFXButton;
import com.nautilus.config.Config;
import com.nautilus.controller.dialogs.SanitizeDialogController;
import com.nautilus.domain.Customer;
import com.nautilus.domain.Sanitize;
import com.nautilus.service.SanitizeService;
import com.nautilus.view.FxmlView;
import com.nautilus.view.FxmlViewComponent;
import com.nautilus.view.StageManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.nautilus.util.Formatter.styleEditButton;
import static com.nautilus.util.Validation.deleteAlert;
import static com.nautilus.view.StageManager.makeDialogDraggable;

public class SanitizeController implements Initializable {

    @FXML
    private TableView<Sanitize> sanitizeTable;

    @FXML
    private MenuItem deleteSanitize;

    @FXML
    private TableColumn<Customer, String> colCustomerName;

    @FXML
    private TableColumn<Customer, String> colCustomerAddress;

    @FXML
    private TableColumn<Customer, String> colCustomerPhone;

    @FXML
    private TableColumn<Customer, LocalDate> colDate;

    @FXML
    private TableColumn<Sanitize, Boolean> colSanitizeEdit;

    @FXML
    private TextField searchBox;

    @FXML
    private JFXButton add;

    @FXML
    private JFXButton delete;

    private final ObservableList<Sanitize> sanitizeList = FXCollections.observableArrayList();

    private SanitizeDialogController sanitizeDialogController;

    ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
    SanitizeService sanitizeService = (SanitizeService) context.getBean("sanitizeServiceImpl");


    @FXML
    void addSanitize(ActionEvent event) {
        showSanitizeDialog(FxmlView.SANITIZE_DIALOG.getTitle());
    }

    @FXML
    void deleteSanitize(ActionEvent event) {
        ObservableList<Sanitize> sanitize = sanitizeTable.getSelectionModel().getSelectedItems();
        Optional<ButtonType> action = deleteAlert();
        if (action.isPresent() && action.get() == ButtonType.OK) sanitizeService.deleteAll(sanitize);

        loadSanitizeDetails();
    }

    @FXML
    void search(KeyEvent event) {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sanitizeTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        sanitizeTable.setPlaceholder(new Label("Podaci se učitavaju. Molimo sačekajte..."));
        setColumnProperties();
        loadSanitizeDetails();
    }

    private void setColumnProperties() {
        colCustomerName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colCustomerAddress.setCellValueFactory(new PropertyValueFactory<>("customerAddress"));
        colCustomerPhone.setCellValueFactory(new PropertyValueFactory<>("customerPhone"));
        colDate.setCellFactory(column -> new TableCell<Customer, LocalDate>() {

            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    this.setText(item.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));

                }
            }
        });
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colSanitizeEdit.setCellFactory(editCellFactory);

    }

    Callback<TableColumn<Sanitize, Boolean>, TableCell<Sanitize, Boolean>> editCellFactory =
            new Callback<TableColumn<Sanitize, Boolean>, TableCell<Sanitize, Boolean>>() {
                @Override
                public TableCell<Sanitize, Boolean> call(final TableColumn<Sanitize, Boolean> param) {
                    return new TableCell<Sanitize, Boolean>() {
                        final Image imgEdit = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/edit.png")));
                        final Button btnEdit = new Button();

                        @Override
                        public void updateItem(Boolean check, boolean empty) {
                            super.updateItem(check, empty);
                            if (empty) {
                                setGraphic(null);
                            } else {
                                btnEdit.setOnAction(e -> {
                                    Sanitize sanitize = getTableView().getItems().get(getIndex());
                                    updateSanitizeFields(sanitizeService.findById(sanitize.getId()));
                                });
                                styleEditButton(btnEdit, imgEdit);
                                setGraphic(btnEdit);
                                setAlignment(Pos.CENTER);
                            }
                            setText(null);
                        }

                        private void updateSanitizeFields(Sanitize sanitize) {
                            showSanitizeDialog("Izmena postojećеg razduživanja ambalaže");
                            sanitizeDialogController.setSanitize(sanitize);
                            sanitizeDialogController.getName().setText(sanitize.getCustomer().getName());
                            sanitizeDialogController.getAddress().setText(sanitize.getCustomer().getAddress());
                            sanitizeDialogController.getCity().setText(sanitize.getCustomer().getCity());
                            sanitizeDialogController.getPhone().setText(sanitize.getCustomer().getPhone());
                            sanitizeDialogController.getLegalForm().setText(sanitize.getCustomer().getLegalForm().toString());
                            sanitizeDialogController.getDate().setValue(sanitize.getDate());
                        }
                    };
                }
            };

    private void showSanitizeDialog(String title) {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        FxmlViewComponent sanitizeDialogViewComponent = StageManager.getComponentWithController(FxmlView.SANITIZE_DIALOG);
        Pane dialogPane = (Pane) sanitizeDialogViewComponent.getRoot();
        sanitizeDialogController = (SanitizeDialogController) sanitizeDialogViewComponent.getController();
        makeDialogDraggable(dialog, dialogPane);
        sanitizeDialogController.setSanitizeController(getController());
        sanitizeDialogController.getTitle().setText(title);
        sanitizeDialogController.setSanitize(new Sanitize());
    }

    private SanitizeController getController() {
        return this;
    }

    public void loadSanitizeDetails() {

        Runnable runnable = () -> {
            sanitizeList.clear();
            sanitizeTable.setItems(null);
            sanitizeList.addAll(sanitizeService.getAll());
            sanitizeTable.setItems(sanitizeList);
            Platform.runLater(() -> sanitizeTable.setPlaceholder(new Label("Nema podataka.")));
        };
        Thread t = new Thread(runnable);
        t.setDaemon(true);
        t.start();
    }
}
