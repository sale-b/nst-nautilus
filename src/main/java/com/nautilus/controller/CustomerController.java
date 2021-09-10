package com.nautilus.controller;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import com.nautilus.config.Config;
import com.nautilus.controller.dialogs.CustomerDialogController;
import com.nautilus.domain.Customer;
import com.nautilus.domain.dto.CustomerDto;
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
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.nautilus.domain.Customer.LegalForm.LEGAL_ENTITY;
import static com.nautilus.util.Formatter.*;
import static com.nautilus.util.Validation.deleteAlert;
import static com.nautilus.view.StageManager.makeDialogDraggable;
import static java.time.temporal.ChronoUnit.DAYS;

/**
 * @author Aleksandar.Brankovic
 */

@Slf4j
public class CustomerController implements Initializable {

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox rightVbox;

    @FXML
    private JFXDrawer drawer;

    @FXML
    @Getter
    private TableView<CustomerDto> customerTable;

    @FXML
    private TableColumn<CustomerDto, String> colCustomerName;

    @FXML
    private TableColumn<CustomerDto, String> colCustomerCity;

    @FXML
    private TableColumn<CustomerDto, String> colCustomerAddress;

    @FXML
    private TableColumn<CustomerDto, String> colCustomerPhone;

    @FXML
    private TableColumn<CustomerDto, String> colCustomerLegalForm;

    @FXML
    private TableColumn<CustomerDto, LocalDate> colCustomerDate;

    @FXML
    private TableColumn<CustomerDto, Boolean> colCustomerEdit;

    @FXML
    private TableColumn<CustomerDto, Integer> colSanitize;

    @FXML
    private TableColumn<CustomerDto, Integer> colObligation;

    @FXML
    private TableColumn<CustomerDto, Double> colDebt;

    @FXML
    private TableColumn<CustomerDto, Integer> colPackagingSmall;

    @FXML
    private TableColumn<CustomerDto, Integer> colPackagingLarge;

    @FXML
    private Button add;

    @FXML
    private Button deleteSelected;

    @FXML
    private TextField searchBox;


    @FXML
    private JFXHamburger hamburger;

    private Timer searchTimer;
    private Timer tooltipTimer;

    private CustomerDialogController customerDialogController;
    private SidePaneController sidePaneController;

    ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
    CustomerService customerService = (CustomerService) context.getBean("customerServiceImpl");

    @Getter
    private final ObservableList<CustomerDto> customerList = FXCollections.observableArrayList();

    @FXML
    void addCustomer(ActionEvent event) {
        showCustomerDialog(FxmlView.CUSTOMER_DIALOG.getTitle());
    }

    private void showCustomerDialog(String title) {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        FxmlViewComponent userDialogViewComponent = StageManager.getComponentWithController(FxmlView.CUSTOMER_DIALOG);
        Pane dialogPane = (Pane) userDialogViewComponent.getRoot();
        customerDialogController = (CustomerDialogController) userDialogViewComponent.getController();
        makeDialogDraggable(dialog, dialogPane);
        customerDialogController.setCustomerController(getController());
        customerDialogController.getTitle().setText(title);
    }

    @FXML
    void deleteSelected(ActionEvent event) {
        List<CustomerDto> customers = customerTable.getSelectionModel().getSelectedItems();
        Optional<ButtonType> action = deleteAlert();
        if (action.isPresent() && action.get() == ButtonType.OK) customerService.deleteAllDto(customers);

        loadCustomerDetails();
    }


    @FXML
    private void deleteCustomers(ActionEvent event) {
        List<CustomerDto> customers = customerTable.getSelectionModel().getSelectedItems();
        Optional<ButtonType> action = deleteAlert();
        if (action.isPresent() && action.get() == ButtonType.OK) customerService.deleteAllDto(customers);

        loadCustomerDetails();
    }

    @FXML
    void search(KeyEvent event) {
        if (searchTimer != null) {
            searchTimer.cancel();
        }
        TimerTask timerTask = new TimerTask() {

            @Override
            public void run() {
                if (searchBox.getText().trim().length() > 2) {
                    loadSearchDetails(searchBox.getText().toLowerCase().trim());
                } else if (searchBox.getText().equals("")) {
                    reloadCustomerDetails();
                }
                searchTimer.cancel();
            }
        };
        searchTimer = new Timer();
        searchTimer.schedule(timerTask, 1000);
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
        sidePaneController = ((SidePaneController) sidePaneViewComponent.getController());
        sidePaneController.setCustomerController(getController());
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

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
        colCustomerDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colCustomerLegalForm.setCellValueFactory(new PropertyValueFactory<>("legalForm"));
        colDebt.setCellValueFactory(new PropertyValueFactory<>("debt"));
        colPackagingSmall.setCellValueFactory(new PropertyValueFactory<>("packagingSmall"));
        colPackagingLarge.setCellValueFactory(new PropertyValueFactory<>("packagingLarge"));
        colObligation.setCellValueFactory(new PropertyValueFactory<>("monthsWithoutFulfilledMonthlyObligation"));
        colCustomerDate.setCellFactory(dateCellFactory);
        colSanitize.setCellValueFactory(new PropertyValueFactory<>("monthsUntilSanitize"));
        colDebt.setCellFactory(debtCellFactory);
        colPackagingSmall.setCellFactory(packagingSmallCellFactory);
        colPackagingLarge.setCellFactory(packagingLargeCellFactory);
        colSanitize.setCellFactory(sanitizeCellFactory);
        colObligation.setCellFactory(obligationCellFactory);
        colCustomerEdit.setCellFactory(editCellFactory);
    }

    Callback<TableColumn<CustomerDto, LocalDate>, TableCell<CustomerDto, LocalDate>> dateCellFactory = column -> new TableCell<CustomerDto, LocalDate>() {
        @Override
        protected void updateItem(LocalDate item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText(null);
            } else if (item == null) {
                setText("Bez ugovora");
            } else {
                this.setText(item.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            }
        }
    };

    Callback<TableColumn<CustomerDto, Integer>, TableCell<CustomerDto, Integer>> packagingSmallCellFactory =
            new Callback<TableColumn<CustomerDto, Integer>, TableCell<CustomerDto, Integer>>() {
                @Override
                public TableCell<CustomerDto, Integer> call(final TableColumn<CustomerDto, Integer> param) {
                    return new TableCell<CustomerDto, Integer>() {
                        final Label label = new Label();

                        @Override
                        protected void updateItem(Integer item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setGraphic(null);
                            } else {
                                Formatter.setPackagingLabelStyle(label, item);
                                label.setText(item.toString());
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

    Callback<TableColumn<CustomerDto, Integer>, TableCell<CustomerDto, Integer>> packagingLargeCellFactory =
            new Callback<TableColumn<CustomerDto, Integer>, TableCell<CustomerDto, Integer>>() {
                @Override
                public TableCell<CustomerDto, Integer> call(final TableColumn<CustomerDto, Integer> param) {
                    return new TableCell<CustomerDto, Integer>() {
                        final Label label = new Label();

                        @Override
                        protected void updateItem(Integer item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setGraphic(null);
                            } else {
                                setPackagingLabelStyle(label, item);
                                label.setText(item.toString());
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

    Callback<TableColumn<CustomerDto, Integer>, TableCell<CustomerDto, Integer>> sanitizeCellFactory =
            new Callback<TableColumn<CustomerDto, Integer>, TableCell<CustomerDto, Integer>>() {
                @Override
                public TableCell<CustomerDto, Integer> call(final TableColumn<CustomerDto, Integer> param) {
                    return new TableCell<CustomerDto, Integer>() {
                        final Label label = new Label();
                        CustomerDto customerDto = null;

                        @Override
                        protected void updateItem(Integer item, boolean empty) {
                            customerDto = (CustomerDto) this.getTableRow().getItem();
                            super.updateItem(item, empty);
                            if (empty) {
                                setGraphic(null);
                            } else {
                                if (item == null) {
                                    setNotApplicableLabelStyle(label);
                                } else {
                                    label.setText(item.toString());
                                    setSanitizeLabelStyle(label, item);
                                    if (customerDto != null) {
                                        int requiredNumberOfDays = Math.toIntExact(DAYS.between(customerDto.getLastSanitiseDate().plusDays(1),
                                                customerDto.getLastSanitiseDate().plusMonths(customerDto.getRequiredSanitisePeriodInMonths())));
                                        CustomerController.setTooltip(label,
                                                "Preostalo dana: " + Math.toIntExact(requiredNumberOfDays - DAYS.between(customerDto.getLastSanitiseDate().plusDays(1), LocalDate.now())));
                                    }
                                }
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

    Callback<TableColumn<CustomerDto, Integer>, TableCell<CustomerDto, Integer>> obligationCellFactory =
            new Callback<TableColumn<CustomerDto, Integer>, TableCell<CustomerDto, Integer>>() {
                @Override
                public TableCell<CustomerDto, Integer> call(final TableColumn<CustomerDto, Integer> param) {
                    return new TableCell<CustomerDto, Integer>() {
                        final Label label = new Label();
                        CustomerDto customerDto = null;

                        @Override
                        protected void updateItem(Integer item, boolean empty) {
                            super.updateItem(item, empty);

                            if (empty) {
                                setGraphic(null);
                            } else {
                                if (item == null) {
                                    setNotApplicableLabelStyle(label);
                                } else {
                                    label.setText(item.toString());
                                    setObligationLabelStyle(label, item);
                                    customerDto = (CustomerDto) this.getTableRow().getItem();
                                }
                                label.setPrefWidth(4000.0);
                                label.setAlignment(Pos.CENTER);
                                setGraphic(label);
                                this.hoverProperty().addListener((observable, oldValue, newValue) -> {
                                    if (newValue) {
                                        if (tooltipTimer != null) {
                                            tooltipTimer.cancel();
                                        }
                                        TimerTask timerTask = new TimerTask() {
                                            @Override
                                            public void run() {
                                                if (customerDto.getDate() != null && customerDto.getDate() != null) {
                                                    if (customerDto.getMonthsWithoutFulfilledMonthlyObligation() < 1) {
                                                        Platform.runLater(() -> label.setTooltip(null));
                                                    } else {
                                                        Platform.runLater(() -> CustomerController.setTooltip(label, "Učitavanje..."));
                                                        Runnable runnable = () -> {
                                                            StringBuilder sb = new StringBuilder();
                                                            customerService.selectDatesWithUnfulfilledObligationForCustomer(customerDto).forEach(
                                                                    date -> sb.append(date.format(DateTimeFormatter.ofPattern("MM.yyyy"))).append("\n")
                                                            );
                                                            Platform.runLater(() -> label.getTooltip().setText(sb.toString()));
                                                        };
                                                        Thread t = new Thread(runnable);
                                                        t.setDaemon(true);
                                                        t.start();
                                                    }
                                                } else {
                                                    label.setTooltip(null);
                                                }
                                            }
                                        };
                                        tooltipTimer = new Timer();
                                        tooltipTimer.schedule(timerTask, 800);
                                    } else if (tooltipTimer != null) {
                                        tooltipTimer.cancel();
                                    }
                                });
                            }
                            setText(null);
                            setAlignment(Pos.CENTER);
                        }
                    };
                }
            };

    Callback<TableColumn<CustomerDto, Double>, TableCell<CustomerDto, Double>> debtCellFactory =
            new Callback<TableColumn<CustomerDto, Double>, TableCell<CustomerDto, Double>>() {
                @Override
                public TableCell<CustomerDto, Double> call(final TableColumn<CustomerDto, Double> param) {
                    return new TableCell<CustomerDto, Double>() {
                        final Label label = new Label();

                        @Override
                        protected void updateItem(Double item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setGraphic(null);
                            } else {
                                setPackagingLabelStyle(label, (int) Math.round(item));
                                label.setText(formatPrice(item));
                                CustomerController.setTooltip(label, formatPrice(item));
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

    Callback<TableColumn<CustomerDto, Boolean>, TableCell<CustomerDto, Boolean>> editCellFactory =
            new Callback<TableColumn<CustomerDto, Boolean>, TableCell<CustomerDto, Boolean>>() {
                @Override
                public TableCell<CustomerDto, Boolean> call(final TableColumn<CustomerDto, Boolean> param) {
                    return new TableCell<CustomerDto, Boolean>() {
                        final Image imgEdit = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/edit.png")));
                        final Button btnEdit = new Button();

                        @Override
                        public void updateItem(Boolean check, boolean empty) {
                            super.updateItem(check, empty);
                            if (empty) {
                                setGraphic(null);
                            } else {
                                btnEdit.setOnAction(e -> {
                                    CustomerDto customerDto = (CustomerDto) this.getTableRow().getItem();
                                    updateCustomerFields(customerService.findById(customerDto.getId()));
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
                            customerDialogController.getSanitisePeriod().getValueFactory().setValue(customer.getRequiredSanitisePeriodInMonths());
                            customerDialogController.getDate().setValue(customer.getDate());
                            if (customer.getLegalForm().equals(LEGAL_ENTITY))
                                customerDialogController.getRbLegalEntity().setSelected(true);
                            else customerDialogController.getRbIndividual().setSelected(true);
                            if (customer.getDate() != null)
                                customerDialogController.getTbWithContract().setSelected(true);
                            else {
                                customerDialogController.getTbWithContract().setSelected(false);
                                customerDialogController.getDate().setDisable(true);
                                customerDialogController.getDate().setValue(null);
                                customerDialogController.getSanitizeLabel().setDisable(true);
                                customerDialogController.getSanitisePeriod().setDisable(true);
                            }
                        }
                    };
                }
            };

    private static void setTooltip(Label label, String tooltip) {
        Tooltip tt = new Tooltip(tooltip);
        tt.setStyle("-fx-text-fill: white;");
        label.setTooltip(tt);
    }

    private CustomerController getController() {
        return this;
    }

    /*
     *  Add All users to observable list and update table
     */
    public void loadCustomerDetails() {
        Runnable runnable = () -> Platform.runLater(() -> {
            customerList.clear();
            customerTable.getSortOrder().clear();
            customerTable.setItems(null);
            customerList.addAll(customerService.getAllDto());
            customerTable.setItems(customerList);
            customerTable.setPlaceholder(new Label("Nema podataka."));
        });
        Thread t = new Thread(runnable);
        t.setDaemon(true);
        t.start();
        sidePaneController.resetCountsForAllFilters();
    }

    public void loadSearchDetails(String text) {
        List<CustomerDto> customersDto = customerService.findDtoByTextFields(text);
        customerList.clear();
        Platform.runLater(() -> customerTable.getSortOrder().clear());
        customerList.addAll(customersDto);
        customerTable.setItems(customerList);
    }

    public void reloadCustomerDetails() {
        List<CustomerDto> customersDto = customerService.getAllDto();
        customerList.clear();
        Platform.runLater(() -> customerTable.getSortOrder().clear());
        customerList.addAll(customersDto);
        customerTable.setItems(customerList);
        customerTable.refresh();
    }

}