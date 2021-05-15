package com.nautilus.controller;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import com.nautilus.config.Config;
import com.nautilus.controller.dialogs.CustomerDialogController;
import com.nautilus.domain.Customer;
import com.nautilus.service.CustomerService;
import com.nautilus.util.Formatter;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.nautilus.domain.Customer.CustomerType.LEGAL_ENTITY;
import static com.nautilus.util.Formatter.styleEditButton;
import static com.nautilus.util.Validation.deleteAlert;
import static com.nautilus.view.StageManager.makeDialogDraggable;

/**
 * @author Aleksandar.Brankovic
 * @since 05-04-2017
 */

@Slf4j
public class CustomerController implements Initializable {


    @FXML
    private VBox rightVbox;

    @FXML
    private JFXDrawer drawer;

    @FXML
    private TableView<Customer> customerTable;

    @FXML
    private TableColumn<Customer, String> colCustomerName;

    @FXML
    private TableColumn<Customer, String> colCustomerCity;

    @FXML
    private TableColumn<Customer, String> colCustomerAddress;

    @FXML
    private TableColumn<Customer, String> colCustomerPhone;

    @FXML
    private TableColumn<Customer, String> colCustomerType;

    @FXML
    private TableColumn<Customer, LocalDate> colCustomerDate;

    @FXML
    private TableColumn<Customer, Boolean> colCustomerEdit;


    @FXML
    private TableColumn<Customer, String> colDebt;

    @FXML
    private TableColumn<Customer, String> colPackagingSmall;

    @FXML
    private TableColumn<Customer, String> colPackagingLarge;

    @FXML
    private Button add;

    @FXML
    private Button deleteSelected;

    @FXML
    private TextField searchBox;


    @FXML
    private JFXHamburger hamburger;

    private Timer timer;

    private CustomerDialogController customerDialogController;

    ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
    CustomerService customerService = (CustomerService) context.getBean("customerServiceImpl");

    private final ObservableList<Customer> customerList = FXCollections.observableArrayList();

    @FXML
    void addCustomer(ActionEvent event) {
        showCustomerDialog("Dodavanje novog korisnika");
    }

    private void showCustomerDialog(String title) {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        FxmlViewComponent userDialogViewComponent = StageManager.getComponentWithController(FxmlView.USER_DIALOG);
        Pane dialogPane = (Pane) userDialogViewComponent.getRoot();
        customerDialogController = (CustomerDialogController) userDialogViewComponent.getController();
        makeDialogDraggable(dialog, dialogPane);
        customerDialogController.setCustomerController(getController());
        customerDialogController.getTitle().setText(title);
    }

    @FXML
    void deleteSelected(ActionEvent event) {
        List<Customer> customers = customerTable.getSelectionModel().getSelectedItems();
        Optional<ButtonType> action = deleteAlert();
        if (action.isPresent() && action.get() == ButtonType.OK) customerService.deleteAll(customers);

        loadCustomerDetails();
    }


    @FXML
    private void deleteCustomers(ActionEvent event) {
        List<Customer> customers = customerTable.getSelectionModel().getSelectedItems();
        Optional<ButtonType> action = deleteAlert();
        if (action.isPresent() && action.get() == ButtonType.OK) customerService.deleteAll(customers);

        loadCustomerDetails();
    }

    @FXML
    void search(KeyEvent event) {
        if (timer != null) {
            timer.cancel();
        }
        TimerTask timerTask = new TimerTask() {

            @Override
            public void run() {
                if (!searchBox.getText().equals("")) {
                    loadSearchDetails(searchBox.getText().toLowerCase().trim());
                } else {
                    reloadCustomerDetails();
                }
                timer.cancel();
            }
        };
        timer = new Timer();
        timer.schedule(timerTask, 1000);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        HamburgerBackArrowBasicTransition transition = new HamburgerBackArrowBasicTransition(hamburger);
        transition.setRate(-1);
        hamburger.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
            transition.setRate(transition.getRate() * -1);
            transition.play();

            if (drawer.isOpened()) {
                drawer.close();
                rightVbox.toBack();
            } else {
                drawer.open();
                rightVbox.toFront();
            }
        });


        FxmlViewComponent sidePaneViewComponent = StageManager.getComponentWithController(FxmlView.SIDE_PANE);
        drawer.setSidePane(sidePaneViewComponent.getRoot());
        ((SidePaneController) sidePaneViewComponent.getController()).setCustomerController(getController());

        customerTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        setColumnProperties();
        customerTable.setPlaceholder(new Label("Podaci se učitavaju. Molimo sačekajte..."));
        // Add all users into table
        loadCustomerDetails();
    }


    /*
     *  Set All customerTable column properties
     */
    private void setColumnProperties() {

        colCustomerName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCustomerAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colCustomerCity.setCellValueFactory(new PropertyValueFactory<>("city"));
        colCustomerPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colCustomerDate.setCellFactory(column -> new TableCell<Customer, LocalDate>() {

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
        colCustomerDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colCustomerType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colCustomerEdit.setCellFactory(editCellFactory);
        colDebt.setCellFactory(debtCellFactory);
        colPackagingSmall.setCellFactory(packagingSmallCellFactory);
        colPackagingLarge.setCellFactory(packagingLargeCellFactory);
    }

    Callback<TableColumn<Customer, String>, TableCell<Customer, String>> packagingSmallCellFactory =
            new Callback<TableColumn<Customer, String>, TableCell<Customer, String>>() {
                @Override
                public TableCell<Customer, String> call(final TableColumn<Customer, String> param) {
                    return new TableCell<Customer, String>() {
                        final Label label = new Label();

                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setGraphic(null);
                            } else {
                                int currentIndex = indexProperty()
                                        .getValue() < 0 ? 0
                                        : indexProperty().getValue();
                                Integer clmStatus = param
                                        .getTableView().getItems()
                                        .get(currentIndex).getPackagingSmall();
                                Formatter.setLabelStyle(label, param.getTableView().getItems().get(currentIndex)
                                        .getBacklogPackagingSmall() ? 1 : 0);
                                label.setText(clmStatus.toString());
                                label.setPrefWidth(4000.0);
                                label.setAlignment(Pos.CENTER);
                                setGraphic(label);
                            }

                            setText(null);
                            setAlignment(Pos.CENTER);
                        }


                    };
                }
            };

    Callback<TableColumn<Customer, String>, TableCell<Customer, String>> packagingLargeCellFactory =
            new Callback<TableColumn<Customer, String>, TableCell<Customer, String>>() {
                @Override
                public TableCell<Customer, String> call(final TableColumn<Customer, String> param) {
                    return new TableCell<Customer, String>() {
                        final Label label = new Label();

                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setGraphic(null);
                            } else {
                                int currentIndex = indexProperty()
                                        .getValue() < 0 ? 0
                                        : indexProperty().getValue();
                                Integer clmStatus = param
                                        .getTableView().getItems()
                                        .get(currentIndex).getPackagingLarge();
                                Formatter.setLabelStyle(label, param.getTableView().getItems().get(currentIndex)
                                        .getBacklogPackagingLarge() ? 1 : 0);
                                label.setText(clmStatus.toString());
                                label.setPrefWidth(4000.0);
                                label.setAlignment(Pos.CENTER);
                                setGraphic(label);
                            }

                            setText(null);
                            setAlignment(Pos.CENTER);
                        }


                    };
                }
            };

    Callback<TableColumn<Customer, String>, TableCell<Customer, String>> debtCellFactory =
            new Callback<TableColumn<Customer, String>, TableCell<Customer, String>>() {
                @Override
                public TableCell<Customer, String> call(final TableColumn<Customer, String> param) {
                    return new TableCell<Customer, String>() {
                        final Label label = new Label();

                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setGraphic(null);
                            } else {
                                int currentIndex = indexProperty()
                                        .getValue() < 0 ? 0
                                        : indexProperty().getValue();
                                Double clmStatus = param
                                        .getTableView().getItems()
                                        .get(currentIndex).getDebt();
                                Formatter.setLabelStyle(label, (int) Math.round(clmStatus));
                                label.setText(String.format("%.02f", clmStatus));
                                label.setPrefWidth(4000.0);
                                label.setAlignment(Pos.CENTER);
                                setGraphic(label);
                            }

                            setText(null);
                            setAlignment(Pos.CENTER);
                        }


                    };
                }
            };

    Callback<TableColumn<Customer, Boolean>, TableCell<Customer, Boolean>> editCellFactory =
            new Callback<TableColumn<Customer, Boolean>, TableCell<Customer, Boolean>>() {
                @Override
                public TableCell<Customer, Boolean> call(final TableColumn<Customer, Boolean> param) {
                    return new TableCell<Customer, Boolean>() {
                        final Image imgEdit = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/edit.png")));
                        final Button btnEdit = new Button();

                        @Override
                        public void updateItem(Boolean check, boolean empty) {
                            super.updateItem(check, empty);
                            if (empty) {
                                setGraphic(null);
                            } else {
                                btnEdit.setOnAction(e -> {
                                    Customer customer = getTableView().getItems().get(getIndex());
                                    updateCustomerFields(customer);
                                });

                                styleEditButton(btnEdit, imgEdit);

                                setGraphic(btnEdit);
                                setAlignment(Pos.CENTER);
                            }
                            setText(null);
                        }

                        private void updateCustomerFields(Customer customer) {
                            showCustomerDialog("Izmena postojećеg korisnika");
                            customerDialogController.setCustomer(customer);
                            customerDialogController.getName().setText(customer.getName());
                            customerDialogController.getAddress().setText(customer.getAddress());
                            customerDialogController.getCity().setText(customer.getCity());
                            customerDialogController.getPhone().setText(customer.getPhone());
                            customerDialogController.getSanitisePeriod().getValueFactory().setValue(customer.getSanitisePeriodInMonths());
                            customerDialogController.getDate().setValue(customer.getDate());
                            if (customer.getType().equals(LEGAL_ENTITY))
                                customerDialogController.getRbLegalEntity().setSelected(true);
                            else customerDialogController.getRbIndividual().setSelected(true);
                        }
                    };
                }
            };


    private CustomerController getController() {
        return this;
    }

    /*
     *  Add All users to observable list and update table
     */
    public void loadCustomerDetails() {

        Runnable runnable = () -> {
            customerList.clear();
            customerTable.setItems(null);
            customerList.addAll(customerService.getAll());
            customerTable.setItems(customerList);
            Platform.runLater(() -> customerTable.setPlaceholder(new Label("Nema podataka.")));
        };
        Thread t = new Thread(runnable);
        t.setDaemon(true);
        t.start();
    }

    public void loadSearchDetails(String text) {
        List<Customer> customers = customerService.findByTextFields(text);
        customerList.clear();
        customerList.addAll(customers);
        customerTable.setItems(customerList);
    }

    public void reloadCustomerDetails() {
        List<Customer> customers = customerService.getAll();
        customerList.clear();
        customerList.addAll(customers);
        customerTable.setItems(customerList);
        customerTable.refresh();
    }

}