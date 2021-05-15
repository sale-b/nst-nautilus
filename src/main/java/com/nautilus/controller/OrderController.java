package com.nautilus.controller;

import com.jfoenix.controls.JFXButton;
import com.nautilus.config.Config;
import com.nautilus.controller.dialogs.OrderDialogController;
import com.nautilus.domain.Order;
import com.nautilus.domain.dto.OrderDto;
import com.nautilus.service.OrderService;
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
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.nautilus.util.Formatter.styleEditButton;
import static com.nautilus.util.Validation.deleteAlert;
import static com.nautilus.view.StageManager.makeDialogDraggable;

public class OrderController implements Initializable {

    @FXML
    private TableView<OrderDto> orderTable;

    @FXML
    private MenuItem deleteOrders;

    @FXML
    private TableColumn<OrderDto, String> colCustomerName;

    @FXML
    private TableColumn<OrderDto, String> colCustomerAddress;


    @FXML
    private TableColumn<OrderDto, String> colCustomerPhone;

    @FXML
    private TableColumn<OrderDto, String> colWaterSmall;

    @FXML
    private TableColumn<OrderDto, String> colWaterLarge;

    @FXML
    private TableColumn<OrderDto, String> colGlasses;

    @FXML
    private TableColumn<OrderDto, String> colOrderTotalPrice;

    @FXML
    private TableColumn<OrderDto, String> colOrderDeliveredBy;

    @FXML
    private TableColumn<OrderDto, String> colOrderPaid;

    @FXML
    private TableColumn<OrderDto, String> colOrderNote;

    @FXML
    private TableColumn<OrderDto, Boolean> colOrderEdit;

    @FXML
    private TextField searchBox;

    @FXML
    private JFXButton add;

    @FXML
    private JFXButton delete;

    private final ObservableList<OrderDto> orderList = FXCollections.observableArrayList();

    private OrderDialogController orderDialogController;

    ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
    OrderService orderService = (OrderService) context.getBean("orderServiceImpl");


    @FXML
    void addOrder(ActionEvent event) {
        showOrderDialog("Dodavanje nove porudžbine");
    }

    @FXML
    void deleteOrders(ActionEvent event) {
        ObservableList<OrderDto> orders = orderTable.getSelectionModel().getSelectedItems();
        Optional<ButtonType> action = deleteAlert();
        if (action.isPresent() && action.get() == ButtonType.OK) orderService.deleteAllDto(orders);

        loadOrderDetails();
    }

    @FXML
    void search(KeyEvent event) {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        orderTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        orderTable.setPlaceholder(new Label("Podaci se učitavaju. Molimo sačekajte..."));
        setColumnProperties();
        loadOrderDetails();
    }

    private void setColumnProperties() {

        colCustomerName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCustomerAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colCustomerPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colWaterSmall.setCellValueFactory(new PropertyValueFactory<>("waterSmall"));
        colWaterLarge.setCellValueFactory(new PropertyValueFactory<>("waterLarge"));
        colGlasses.setCellValueFactory(new PropertyValueFactory<>("glasses"));
        colOrderTotalPrice.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        colOrderDeliveredBy.setCellFactory(deliveredByFactory);
        colOrderPaid.setCellFactory(payedCellFactory);
        colOrderNote.setCellValueFactory(new PropertyValueFactory<>("note"));
        colOrderEdit.setCellFactory(editCellFactory);

    }

    Callback<TableColumn<OrderDto, String>, TableCell<OrderDto, String>> deliveredByFactory =
            new Callback<TableColumn<OrderDto, String>, TableCell<OrderDto, String>>() {
                @Override
                public TableCell<OrderDto, String> call(final TableColumn<OrderDto, String> param) {
                    return new TableCell<OrderDto, String>() {
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
                                String clmStatus = param
                                        .getTableView().getItems()
                                        .get(currentIndex).getDeliveredBy();
                                Formatter.setLabelStyle(label, clmStatus.equals("Nije isporučeno") ? 1 : 0);
                                label.setText(clmStatus);
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

    Callback<TableColumn<OrderDto, String>, TableCell<OrderDto, String>> payedCellFactory =
            new Callback<TableColumn<OrderDto, String>, TableCell<OrderDto, String>>() {
                @Override
                public TableCell<OrderDto, String> call(final TableColumn<OrderDto, String> param) {
                    return new TableCell<OrderDto, String>() {
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
                                String clmStatus = param
                                        .getTableView().getItems()
                                        .get(currentIndex).getPayed();
                                Formatter.setLabelStyle(label, clmStatus.equals("NE") ? 1 : 0);
                                label.setText(clmStatus);
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

    Callback<TableColumn<OrderDto, Boolean>, TableCell<OrderDto, Boolean>> editCellFactory =
            new Callback<TableColumn<OrderDto, Boolean>, TableCell<OrderDto, Boolean>>() {
                @Override
                public TableCell<OrderDto, Boolean> call(final TableColumn<OrderDto, Boolean> param) {
                    return new TableCell<OrderDto, Boolean>() {
                        final Image imgEdit = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/edit.png")));
                        final Button btnEdit = new Button();

                        @Override
                        public void updateItem(Boolean check, boolean empty) {
                            super.updateItem(check, empty);
                            if (empty) {
                                setGraphic(null);
                            } else {
                                btnEdit.setOnAction(e -> {
                                    OrderDto orderDto = getTableView().getItems().get(getIndex());
                                    updateOrderFields(orderService.findById(orderDto.getId()));
                                });
                                styleEditButton(btnEdit, imgEdit);
                                setGraphic(btnEdit);
                                setAlignment(Pos.CENTER);
                            }
                            setText(null);
                        }

                        private void updateOrderFields(Order order) {
                            showOrderDialog("Izmena postojećеg korisnika");
                            orderDialogController.setOrder(order);
                            orderDialogController.getName().setText(order.getCustomer().getName());
                            orderDialogController.getAddress().setText(order.getCustomer().getAddress());
                            orderDialogController.getCity().setText(order.getCustomer().getCity());
                            orderDialogController.getPhone().setText(order.getCustomer().getPhone());
                            orderDialogController.getType().setText(order.getCustomer().getType().toString());
                            orderDialogController.getDate().setValue(order.getDate());
                            orderDialogController.getDeliveredBy().getSelectionModel().select(order.getDeliveredBy());
                            orderDialogController.getPayed().setSelected(order.getPayed());
                            orderDialogController.getNote().setText(order.getNote());
                        }
                    };
                }
            };

    private void showOrderDialog(String title) {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        FxmlViewComponent orderDialogViewComponent = StageManager.getComponentWithController(FxmlView.ORDER_DIALOG);
        Pane dialogPane = (Pane) orderDialogViewComponent.getRoot();
        orderDialogController = (OrderDialogController) orderDialogViewComponent.getController();
        makeDialogDraggable(dialog, dialogPane);
        orderDialogController.setOrderController(getController());
        orderDialogController.getTitle().setText(title);
        orderDialogController.setOrder(new Order());
    }

    private OrderController getController() {
        return this;
    }

    public void loadOrderDetails() {

        Runnable runnable = () -> {
            orderList.clear();
            orderTable.setItems(null);
            orderList.addAll(orderService.getAllDto());
            orderTable.setItems(orderList);
            Platform.runLater(() -> orderTable.setPlaceholder(new Label("Nema podataka.")));
        };
        Thread t = new Thread(runnable);
        t.setDaemon(true);
        t.start();
    }
}
