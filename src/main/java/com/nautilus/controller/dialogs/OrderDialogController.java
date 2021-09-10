package com.nautilus.controller.dialogs;

import com.nautilus.config.Config;
import com.nautilus.controller.OrderController;
import com.nautilus.domain.Article;
import com.nautilus.domain.Customer;
import com.nautilus.domain.Order;
import com.nautilus.domain.OrderItem;
import com.nautilus.service.ArticleService;
import com.nautilus.service.CustomerService;
import com.nautilus.service.OrderService;
import com.nautilus.view.FxmlView;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.nautilus.domain.Customer.LegalForm.LEGAL_ENTITY;
import static com.nautilus.util.Formatter.formatPrice;
import static com.nautilus.util.Formatter.formatTax;
import static com.nautilus.util.Validation.*;

@SuppressWarnings("unused")
@Slf4j
public class OrderDialogController implements Initializable {

    @Setter
    private OrderController orderController;

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
    @Getter
    private ComboBox<Order.DeliveredBy> deliveredBy;

    @FXML
    @Getter
    private CheckBox payed;

    @FXML
    @Getter
    private TextArea note;

    @FXML
    private FontAwesomeIconView cancel;

    @FXML
    private ComboBox<Article> comBoxArticles;

    @FXML
    private Spinner<Integer> articleQuantity;

    @FXML
    private Button btnAdd;

    @FXML
    private TableView<OrderItem> orderItemsTable;

    @FXML
    private TableColumn<OrderItem, String> colName;

    @FXML
    private TableColumn<OrderItem, String> colQuantity;

    @FXML
    private TableColumn<OrderItem, String> colUnitPrice;

    @FXML
    private TableColumn<OrderItem, String> colUnitTax;

    @FXML
    private TableColumn<OrderItem, String> colItemTotalPrice;

    @FXML
    private TableColumn<OrderItem, String> colRemove;


    @FXML
    private Label totalPrice;

    @FXML
    private Label totalTax;


    @FXML
    private Button reset;

    @FXML
    private Button saveOrder;

    @FXML
    private ListView<Customer> customersList;

    private Order order = null;
    private Article article = null;

    private boolean reloadOrderDetailsNeeded = false;

    private Timer timer;

    private final ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
    private final OrderService orderService = (OrderService) context.getBean("orderServiceImpl");
    private final ArticleService articleService = (ArticleService) context.getBean("articleServiceImpl");
    private final CustomerService customerService = (CustomerService) context.getBean("customerServiceImpl");
    private final ObservableList<OrderItem> orderItemsList = FXCollections.observableArrayList();
    private final String ORDER_ITEMS_CELL_STYLE = "-fx-background-color: #ffffff;-fx-label-padding: 0;-fx-pref-width:5000;-fx-pref-height:35;-fx-font-family: 'Roboto';-fx-text-fill: #000000;";

    @FXML
    void cancel(MouseEvent event) {
        ((Node) (event.getSource())).getScene().getWindow().hide();
        if (reloadOrderDetailsNeeded)
            orderController.loadOrderDetails();
    }

    @FXML
    void comboAction(ActionEvent event) {
        if (deliveredBy.getSelectionModel().isSelected(0)) {
            payed.setDisable(true);
            payed.setSelected(false);
        } else {
            payed.setDisable(false);
        }
    }

    @FXML
    void addOrderItem(ActionEvent event) {
        if (this.article != null) {
            OrderItem orderItem = new OrderItem();
            orderItem.setArticleName(this.article.getName());
            orderItem.setArticlePrice(this.article.getPrice());
            orderItem.setArticleTax(this.article.getTax());
            orderItem.setQuantity(this.articleQuantity.getValue());
            this.order.addItem(orderItem);
            refreshOrderItems();
        } else {
            emptyValidation(true, "artikal");
        }
    }

    private void refreshOrderItems() {
        loadItemsTable();
        this.articleQuantity.getValueFactory().setValue(1);
        this.comBoxArticles.getSelectionModel().clearSelection();
        loadComboBoxArticles();
        recalculateTotalPrice();
        recalculateTotalTax();
    }

    @FXML
    void reset(ActionEvent event) {
        clearFields();
        title.setText(FxmlView.ORDER_DIALOG.getTitle());
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
        this.order.setCustomer(customersList.getSelectionModel().getSelectedItem());
        userSearch.setText("");
        clearUsersList();
        this.name.setText(order.getCustomer().getName());
        this.address.setText(order.getCustomer().getAddress());
        this.city.setText(order.getCustomer().getCity());
        this.phone.setText(order.getCustomer().getPhone());
        if (order.getCustomer().getLegalForm().equals(LEGAL_ENTITY))
            this.legalForm.setText("Pravno lice");
        else this.legalForm.setText("Fizičko lice");
    }

    @FXML
    private void saveOrder(ActionEvent event) {

        if (validateName(getNameValue()) &&
                emptyValidation(date.getEditor().getText().isEmpty(), "datum") &&
                emptyValidation(orderItemsTable.getItems().isEmpty(), "stavka porudžbine")) {

            if (this.order.getId() == null) {
                fillOrderFields();
                Order newOrder = orderService.insert(order);
//                saveAlert("Order for", newOrder.getCustomer().getName(), newOrder.getId());
                ((Node) (event.getSource())).getScene().getWindow().hide();
                orderController.loadOrderDetails();
            } else {
                fillOrderFields();
                Optional<Order> updatedOrder = orderService.update(order);
                if (updatedOrder.isPresent()) {
//                    updateAlertSuccess(updatedUser.get());
                    ((Node) (event.getSource())).getScene().getWindow().hide();
                    orderController.loadOrderDetails();

                } else {
                    updateAlertFail("Order");
                    reloadOrderDetailsNeeded = true;
                    order = orderService.findById(this.order.getId());
                    name.setText(order.getCustomer().getName());
                    address.setText(order.getCustomer().getAddress());
                    city.setText(order.getCustomer().getCity());
                    phone.setText(order.getCustomer().getPhone());
                    legalForm.setText(order.getCustomer().getLegalForm().toString());
                    date.setValue(order.getDate());
                    deliveredBy.getSelectionModel().select(order.getDeliveredBy());
                    payed.setSelected(order.getPayed());
                    note.setText(order.getNote());
                    refreshOrderItems();
                }
            }

        }

    }

    private void fillOrderFields() {
        order.setDate(getDateValue());
        order.setPayed(getPayedValue());
        order.setDeliveredBy(getDeliveredByValue());
        order.setNote(getNoteValue());
    }


    public void setOrder(Order order) {
        this.order = order;
        loadItemsTable();
        refreshOrderItems();
    }

    private void loadItemsTable() {
        orderItemsTable.getItems().clear();
        orderItemsList.clear();
        orderItemsList.addAll(order.getItems());
        orderItemsTable.setItems(orderItemsList);
    }

    private void loadComboBoxArticles() {
        ArrayList<String> usedArticles = new ArrayList<>();
        this.order.getItems().forEach(orderItem -> usedArticles.add(orderItem.getArticleName()));
        comBoxArticles.getItems().clear();
        comBoxArticles.getItems().addAll(
                articleService.getAll().stream()
                        .filter(article -> !usedArticles.contains(article.getName()))
                        .collect(Collectors.toList()));

        comBoxArticles.valueProperty().addListener((obs, oldVal, newVal) -> this.article = newVal);
    }

    private void recalculateTotalPrice() {
        this.totalPrice.setText(
                String.format("Ukupno: %s", formatPrice(order.getItems().stream().map(orderItem -> orderItem.getArticlePrice() * (1 + orderItem.getArticleTax() / 100) * orderItem.getQuantity()).mapToDouble(Double::doubleValue).sum())));
    }

    private void recalculateTotalTax() {
        this.totalTax.setText(
                String.format("PDV: %s", formatPrice(order.getItems().stream().map(orderItem -> orderItem.getArticlePrice() * (orderItem.getArticleTax() / 100) * orderItem.getQuantity()).mapToDouble(Double::doubleValue).sum())));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.articleQuantity.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 2147483640, 1));
        customersList.setVisible(false);

        orderItemsTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        orderItemsTable.setPlaceholder(new Label("Dodajte stavke porudžbine."));
        orderItemsTable.getPlaceholder().setStyle("-fx-text-fill: white");
        setColumnProperties();

        this.deliveredBy.getItems().add(Order.DeliveredBy.NONE);
        this.deliveredBy.getItems().add(Order.DeliveredBy.FIRST);
        this.deliveredBy.getItems().add(Order.DeliveredBy.SECOND);
        this.deliveredBy.getSelectionModel().select(Order.DeliveredBy.NONE);
        payed.setDisable(true);

        articleQuantity.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                articleQuantity.getEditor().setText(newValue.replaceAll("[^\\d]", ""));
            }
            if (!articleQuantity.getEditor().getText().equals("")) {
                articleQuantity.increment(0); // won't change value, but will commit editor
            }
        });
    }


    private void setColumnProperties() {
        setTableColumn(colName, nameCellFactory);
        setTableColumn(colQuantity, quantityCellFactory);
        setTableColumn(colUnitPrice, unitPriceCellFactory);
        setTableColumn(colUnitTax, unitTaxCellFactory);
        setTableColumn(colItemTotalPrice, itemPriceCellFactory);
        setTableColumn(colRemove, removeCellFactory);
    }

    private void setTableColumn(TableColumn<OrderItem, String> col, Callback<TableColumn<OrderItem, String>, TableCell<OrderItem, String>> cellFactory) {
        col.setCellFactory(cellFactory);
    }

    Callback<TableColumn<OrderItem, String>, TableCell<OrderItem, String>> nameCellFactory =
            new Callback<TableColumn<OrderItem, String>, TableCell<OrderItem, String>>() {
                @Override
                public TableCell<OrderItem, String> call(final TableColumn<OrderItem, String> param) {
                    return new TableCell<OrderItem, String>() {
                        final Label label = new Label();
                        final String CELL_NAME = "name";

                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            updateGenericItem(item, empty, this, param, label, CELL_NAME);
                        }
                    };
                }
            };

    Callback<TableColumn<OrderItem, String>, TableCell<OrderItem, String>> quantityCellFactory =
            new Callback<TableColumn<OrderItem, String>, TableCell<OrderItem, String>>() {
                @Override
                public TableCell<OrderItem, String> call(final TableColumn<OrderItem, String> param) {
                    return new TableCell<OrderItem, String>() {
                        final Label label = new Label();
                        final String CELL_NAME = "quantity";

                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            updateGenericItem(item, empty, this, param, label, CELL_NAME);
                        }
                    };
                }
            };

    Callback<TableColumn<OrderItem, String>, TableCell<OrderItem, String>> unitPriceCellFactory =
            new Callback<TableColumn<OrderItem, String>, TableCell<OrderItem, String>>() {
                @Override
                public TableCell<OrderItem, String> call(final TableColumn<OrderItem, String> param) {
                    return new TableCell<OrderItem, String>() {
                        final Label label = new Label();
                        final String CELL_NAME = "price";

                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            updateGenericItem(item, empty, this, param, label, CELL_NAME);
                        }
                    };
                }
            };

    Callback<TableColumn<OrderItem, String>, TableCell<OrderItem, String>> unitTaxCellFactory =
            new Callback<TableColumn<OrderItem, String>, TableCell<OrderItem, String>>() {
                @Override
                public TableCell<OrderItem, String> call(final TableColumn<OrderItem, String> param) {
                    return new TableCell<OrderItem, String>() {
                        final Label label = new Label();
                        final String CELL_NAME = "tax";

                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            updateGenericItem(item, empty, this, param, label, CELL_NAME);
                        }
                    };
                }
            };

    Callback<TableColumn<OrderItem, String>, TableCell<OrderItem, String>> itemPriceCellFactory =
            new Callback<TableColumn<OrderItem, String>, TableCell<OrderItem, String>>() {
                @Override
                public TableCell<OrderItem, String> call(final TableColumn<OrderItem, String> param) {
                    return new TableCell<OrderItem, String>() {
                        final Label label = new Label();
                        final String CELL_NAME = "itemPrice";

                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            updateGenericItem(item, empty, this, param, label, CELL_NAME);
                        }
                    };
                }
            };

    private void updateGenericItem(String item, boolean empty, TableCell<OrderItem, String> tableCell, TableColumn<OrderItem, String> param, Label label, String CELL_NAME) {
        if (empty) {
            tableCell.setGraphic(null);
        } else {
            int currentIndex = tableCell.indexProperty()
                    .getValue() < 0 ? 0
                    : tableCell.indexProperty().getValue();
            String cellValue = null;
            label.setStyle(ORDER_ITEMS_CELL_STYLE);
            switch (CELL_NAME) {
                case "name":
                    cellValue = param
                            .getTableView().getItems()
                            .get(currentIndex).getArticleName();
                    label.setStyle(label.getStyle() + "-fx-background-radius: 15px 0 0 15px;");
                    break;
                case "quantity":
                    cellValue = param
                            .getTableView().getItems()
                            .get(currentIndex).getQuantity().toString();
                    break;
                case "price":
                    cellValue = formatPrice(param
                            .getTableView().getItems()
                            .get(currentIndex).getArticlePrice());
                    break;
                case "tax":
                    cellValue = formatTax(param
                            .getTableView().getItems()
                            .get(currentIndex).getArticleTax());
                    break;
                case "itemPrice":
                    cellValue = formatPrice(param
                            .getTableView().getItems()
                            .get(currentIndex).getArticlePrice() * (1 + param
                            .getTableView().getItems()
                            .get(currentIndex).getArticleTax() / 100) * param
                            .getTableView().getItems()
                            .get(currentIndex).getQuantity());
                    break;
            }
            label.setAlignment(Pos.CENTER);
            label.setText(cellValue);
            label.setTooltip(new Tooltip(cellValue));
            tableCell.setGraphic(label);
        }

        tableCell.setText(null);
        tableCell.setAlignment(Pos.CENTER);
    }

    Callback<TableColumn<OrderItem, String>, TableCell<OrderItem, String>> removeCellFactory =
            new Callback<TableColumn<OrderItem, String>, TableCell<OrderItem, String>>() {
                @Override
                public TableCell<OrderItem, String> call(final TableColumn<OrderItem, String> param) {
                    return new TableCell<OrderItem, String>() {
                        final Image imgEdit = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/remove.png")));
                        final Button btnEdit = new Button();

                        @Override
                        public void updateItem(String check, boolean empty) {
                            super.updateItem(check, empty);
                            if (empty) {
                                setGraphic(null);
                            } else {
                                btnEdit.setOnAction(e -> {
                                    OrderItem orderItem = getTableView().getItems().get(getIndex());
                                    removeOrderItem(orderItem);
                                    loadComboBoxArticles();
                                    recalculateTotalPrice();
                                    recalculateTotalTax();
                                });

                                btnEdit.setStyle("-fx-background-color: transparent;");
                                ImageView iv = new ImageView();
                                iv.setImage(imgEdit);
                                iv.setPreserveRatio(true);
                                iv.setSmooth(true);
                                iv.setCache(true);
                                btnEdit.setGraphic(iv);
                                btnEdit.setStyle(ORDER_ITEMS_CELL_STYLE + "-fx-background-radius: 0 15px 15px 0;");
                                btnEdit.setAlignment(Pos.CENTER);
                                setGraphic(btnEdit);
                                setAlignment(Pos.CENTER);
                            }
                            setText(null);
                        }

                        private void removeOrderItem(OrderItem orderItem) {
                            orderItemsTable.getItems().remove(orderItem);
                            order.removeItem(orderItem);
                        }
                    };
                }
            };

    public String getNameValue() {
        return getString(name);
    }

    static String getString(TextField name) {
        StringBuilder sb = new StringBuilder();
        List<String> words = Arrays.asList(name.getText().trim().replaceAll(" +", " ").split(" "));
        words.forEach(w -> sb.append(w.trim()).append(" "));
        return sb.toString().trim();
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

    public LocalDate getDateValue() {
        return date.getValue();
    }

    public Order.DeliveredBy getDeliveredByValue() {
        return deliveredBy.getValue();
    }

    public Boolean getPayedValue() {
        return payed.isSelected();
    }

    public String getNoteValue() {
        return note.getText();
    }

    private void clearFields() {
        this.order = new Order();
        name.clear();
        address.clear();
        city.clear();
        phone.clear();
        legalForm.clear();
        date.getEditor().clear();
        deliveredBy.getSelectionModel().select(Order.DeliveredBy.NONE);
        payed.setSelected(false);
        payed.setDisable(true);
        note.clear();
        refreshOrderItems();
    }

    private void clearUsersList() {
        Platform.runLater(() -> customersList.getItems().clear());
        customersList.setPrefHeight(0);
        customersList.setVisible(false);
    }

}
