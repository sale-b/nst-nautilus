package com.nautilus.controller;

import com.jfoenix.controls.JFXButton;
import com.nautilus.config.Config;
import com.nautilus.controller.dialogs.PackagingDialogController;
import com.nautilus.domain.Customer;
import com.nautilus.domain.Packaging;
import com.nautilus.service.PackagingService;
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

public class PackagingController implements Initializable {

    @FXML
    private TableView<Packaging> packagingTable;

    @FXML
    private MenuItem deletePackaging;

    @FXML
    private TableColumn<Customer, String> colCustomerName;

    @FXML
    private TableColumn<Customer, String> colCustomerAddress;

    @FXML
    private TableColumn<Customer, String> colCustomerPhone;

    @FXML
    private TableColumn<Customer, LocalDate> colDate;

    @FXML
    private TableColumn<Packaging, String> colWaterSmall;

    @FXML
    private TableColumn<Packaging, String> colWaterLarge;

    @FXML
    private TableColumn<Packaging, Boolean> colPackagingEdit;

    @FXML
    private TextField searchBox;

    @FXML
    private JFXButton add;

    @FXML
    private JFXButton delete;

    private final ObservableList<Packaging> packagingList = FXCollections.observableArrayList();

    private PackagingDialogController packagingDialogController;

    ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
    PackagingService packagingService = (PackagingService) context.getBean("packagingServiceImpl");


    @FXML
    void addPackaging(ActionEvent event) {
        showPackagingDialog(FxmlView.PACKAGING_DIALOG.getTitle());
    }

    @FXML
    void deletePackaging(ActionEvent event) {
        ObservableList<Packaging> packaging = packagingTable.getSelectionModel().getSelectedItems();
        Optional<ButtonType> action = deleteAlert();
        if (action.isPresent() && action.get() == ButtonType.OK) packagingService.deleteAll(packaging);

        loadPackagingDetails();
    }

    @FXML
    void search(KeyEvent event) {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        packagingTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        packagingTable.setPlaceholder(new Label("Podaci se učitavaju. Molimo sačekajte..."));
        setColumnProperties();
        loadPackagingDetails();
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
        colWaterSmall.setCellValueFactory(new PropertyValueFactory<>("waterSmallReturned"));
        colWaterLarge.setCellValueFactory(new PropertyValueFactory<>("waterLargeReturned"));
        colPackagingEdit.setCellFactory(editCellFactory);

    }

    Callback<TableColumn<Packaging, Boolean>, TableCell<Packaging, Boolean>> editCellFactory =
            new Callback<TableColumn<Packaging, Boolean>, TableCell<Packaging, Boolean>>() {
                @Override
                public TableCell<Packaging, Boolean> call(final TableColumn<Packaging, Boolean> param) {
                    return new TableCell<Packaging, Boolean>() {
                        final Image imgEdit = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/edit.png")));
                        final Button btnEdit = new Button();

                        @Override
                        public void updateItem(Boolean check, boolean empty) {
                            super.updateItem(check, empty);
                            if (empty) {
                                setGraphic(null);
                            } else {
                                btnEdit.setOnAction(e -> {
                                    Packaging packaging = getTableView().getItems().get(getIndex());
                                    updatePackagingFields(packagingService.findById(packaging.getId()));
                                });
                                styleEditButton(btnEdit, imgEdit);
                                setGraphic(btnEdit);
                                setAlignment(Pos.CENTER);
                            }
                            setText(null);
                        }

                        private void updatePackagingFields(Packaging packaging) {
                            showPackagingDialog("Izmena postojećеg razduživanja ambalaže");
                            packagingDialogController.setPackaging(packaging);
                            packagingDialogController.getName().setText(packaging.getCustomer().getName());
                            packagingDialogController.getAddress().setText(packaging.getCustomer().getAddress());
                            packagingDialogController.getCity().setText(packaging.getCustomer().getCity());
                            packagingDialogController.getPhone().setText(packaging.getCustomer().getPhone());
                            packagingDialogController.getLegalForm().setText(packaging.getCustomer().getLegalForm().toString());
                            packagingDialogController.getDate().setValue(packaging.getDate());
                            packagingDialogController.getPackagingSmallQuantity().getValueFactory().setValue(packaging.getWaterSmallReturned());
                            packagingDialogController.getPackagingLargeQuantity().getValueFactory().setValue(packaging.getWaterLargeReturned());
                        }
                    };
                }
            };

    private void showPackagingDialog(String title) {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        FxmlViewComponent packagingDialogViewComponent = StageManager.getComponentWithController(FxmlView.PACKAGING_DIALOG);
        Pane dialogPane = (Pane) packagingDialogViewComponent.getRoot();
        packagingDialogController = (PackagingDialogController) packagingDialogViewComponent.getController();
        makeDialogDraggable(dialog, dialogPane);
        packagingDialogController.setPackagingController(getController());
        packagingDialogController.getTitle().setText(title);
        packagingDialogController.setPackaging(new Packaging());
    }

    private PackagingController getController() {
        return this;
    }

    public void loadPackagingDetails() {

        Runnable runnable = () -> {
            packagingList.clear();
            packagingTable.setItems(null);
            packagingList.addAll(packagingService.getAll());
            packagingTable.setItems(packagingList);
            Platform.runLater(() -> packagingTable.setPlaceholder(new Label("Nema podataka.")));
        };
        Thread t = new Thread(runnable);
        t.setDaemon(true);
        t.start();
    }
}
