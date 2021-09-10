package com.nautilus.controller;

import com.nautilus.config.Config;
import com.nautilus.controller.dialogs.ArticleDialogController;
import com.nautilus.domain.Article;
import com.nautilus.service.ArticleService;
import com.nautilus.util.Validation;
import com.nautilus.view.FxmlView;
import com.nautilus.view.FxmlViewComponent;
import com.nautilus.view.StageManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.net.URL;
import java.util.*;

import static com.nautilus.util.Formatter.*;
import static com.nautilus.util.Validation.deleteAlert;
import static com.nautilus.view.StageManager.makeDialogDraggable;

/**
 * @author Aleksandar.Brankovic
 */

@SuppressWarnings("unused")
@Slf4j
public class ArticleController implements Initializable {

    @FXML
    private TableView<Article> articleTable;

    @FXML
    private MenuItem deleteArticles;

    @FXML
    private Button add;

    @FXML
    private Button deleteSelected;

    @FXML
    private TextField searchBox;

    private Timer timer;

    private ArticleDialogController articleDialogController;

    ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
    ArticleService articleService = (ArticleService) context.getBean("articleServiceImpl");

    private final ObservableList<Article> articleList = FXCollections.observableArrayList();

    @FXML
    void addArticle(ActionEvent event) {
        showArticleDialog("Dodavanje novog artikla");
    }

    private void showArticleDialog(String title) {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        FxmlViewComponent ArticleDialogViewComponent = StageManager.getComponentWithController(FxmlView.ARTICLE_DIALOG);
        Pane dialogPane = (Pane) ArticleDialogViewComponent.getRoot();
        articleDialogController = (ArticleDialogController) ArticleDialogViewComponent.getController();
        makeDialogDraggable(dialog, dialogPane);
        articleDialogController.setArticleController(getController());
        articleDialogController.getTitle().setText(title);
    }

    @FXML
    void deleteSelected(ActionEvent event) {
        delete();
    }


    @FXML
    private void deleteArticles(ActionEvent event) {
        delete();
    }

    @FXML
    private void delete() {
        List<Article> articles = articleTable.getSelectionModel().getSelectedItems();
        Optional<ButtonType> action = deleteAlert();
        articles.forEach(a -> {
            if (a.getMandatory())
                Validation.deleteAlertForbidden(String.format("Artikal %s nije moguće obrisati.", a.getName()));
        });
        if (action.isPresent() && action.get() == ButtonType.OK) articleService.deleteAll(articles);

        loadArticleDetails();
    }

    @FXML
    void search(KeyEvent event) {
        if (timer != null) {
            timer.cancel();
        }
        TimerTask timerTask = new TimerTask() {

            @Override
            public void run() {
                if (searchBox.getText().trim().length() > 2) {
                    loadSearchDetails(searchBox.getText().toLowerCase().trim());
                } else if (searchBox.getText().equals("")) {
                    reloadArticleDetails();
                }
                timer.cancel();
            }
        };
        timer = new Timer();
        timer.schedule(timerTask, 1000);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        articleTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        articleTable.setPlaceholder(new Label("Podaci se učitavaju. Molimo sačekajte..."));
        setColumnProperties();
        loadArticleDetails();
    }


    /*
     *  Set All ArticleTable column properties
     */
    private void setColumnProperties() {
        setTableColumn("Naziv", "name", "leftAlignedTableColumnHeader");
        setTableColumn("Cena", priceCellFactory, "rightAlignedTableColumnHeader");
        setTableColumn("PDV", taxCellFactory, "rightAlignedTableColumnHeader");
        setTableColumn("Cena + PDV", totalPriceCellFactory, "rightAlignedTableColumnHeader");
        setTableColumn("Izmeni", editCellFactory, null);
    }

    private void setTableColumn(String name, String property, String alignment) {
        TableColumn<Article, String> col = new TableColumn<>();
        col.setText(name);
        col.getStyleClass().add(alignment);
        col.setCellValueFactory(new PropertyValueFactory<>(property));
        articleTable.getColumns().add(col);
    }

    private void setTableColumn(String name, Callback<TableColumn<Article, String>, TableCell<Article, String>> cellFactory, String alignment) {
        TableColumn<Article, String> col = new TableColumn<>();
        col.setText(name);
        col.getStyleClass().add(alignment);
        col.setCellFactory(cellFactory);
        articleTable.getColumns().add(col);
    }

    Callback<TableColumn<Article, String>, TableCell<Article, String>> priceCellFactory =
            new Callback<TableColumn<Article, String>, TableCell<Article, String>>() {
                @Override
                public TableCell<Article, String> call(final TableColumn<Article, String> param) {
                    return new TableCell<Article, String>() {
                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setText(null);
                            } else {
                                int currentIndex = indexProperty()
                                        .getValue() < 0 ? 0
                                        : indexProperty().getValue();
                                Double clmValue = param
                                        .getTableView().getItems()
                                        .get(currentIndex).getPrice();
                                setText(formatPrice(clmValue));
                            }
                            setStyle("-fx-alignment: CENTER-RIGHT;");
                        }
                    };
                }
            };

    Callback<TableColumn<Article, String>, TableCell<Article, String>> taxCellFactory =
            new Callback<TableColumn<Article, String>, TableCell<Article, String>>() {
                @Override
                public TableCell<Article, String> call(final TableColumn<Article, String> param) {
                    return new TableCell<Article, String>() {
                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setText(null);
                            } else {
                                int currentIndex = indexProperty()
                                        .getValue() < 0 ? 0
                                        : indexProperty().getValue();
                                Double clmValue = param
                                        .getTableView().getItems()
                                        .get(currentIndex).getTax();
                                setText(formatTax(clmValue));
                            }
                            setStyle("-fx-alignment: CENTER-RIGHT;");
                        }
                    };
                }
            };

    Callback<TableColumn<Article, String>, TableCell<Article, String>> totalPriceCellFactory =
            new Callback<TableColumn<Article, String>, TableCell<Article, String>>() {
                @Override
                public TableCell<Article, String> call(final TableColumn<Article, String> param) {
                    return new TableCell<Article, String>() {
                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setText(null);
                            } else {
                                int currentIndex = indexProperty()
                                        .getValue() < 0 ? 0
                                        : indexProperty().getValue();
                                Double clmValue = (1+param
                                        .getTableView().getItems()
                                        .get(currentIndex).getTax()/100)*param
                                        .getTableView().getItems()
                                        .get(currentIndex).getPrice();
                                setText(formatPrice(clmValue));
                            }
                            setStyle("-fx-alignment: CENTER-RIGHT;");
                        }
                    };
                }
            };

    Callback<TableColumn<Article, String>, TableCell<Article, String>> editCellFactory =
            new Callback<TableColumn<Article, String>, TableCell<Article, String>>() {
                @Override
                public TableCell<Article, String> call(final TableColumn<Article, String> param) {
                    return new TableCell<Article, String>() {
                        final Image imgEdit = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/edit.png")));
                        final Button btnEdit = new Button();

                        @Override
                        public void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setGraphic(null);
                            } else {
                                btnEdit.setOnAction(e -> {
                                    Article article = getTableView().getItems().get(getIndex());
                                    updateArticleFields(article);
                                });

                                styleEditButton(btnEdit, imgEdit);
                                setGraphic(btnEdit);
                            }
                            setText(null);
                            setStyle("-fx-alignment: CENTER;");
                        }

                        private void updateArticleFields(Article article) {
                            showArticleDialog("Izmena postojećeg artikla");
                            articleDialogController.setArticle(article);
                            articleDialogController.getName().setText(article.getName());
                            articleDialogController.getPrice().setText(formatDouble(article.getPrice()));
                            articleDialogController.getTax().setText(formatDouble(article.getTax()));
                            if (article.getMandatory()) {
                                articleDialogController.getName().setDisable(true);
                            }
                        }
                    };
                }
            };

    private ArticleController getController() {
        return this;
    }

    /*
     *  Add All Articles to observable list and update table
     */
    public void loadArticleDetails() {
        Runnable runnable = () -> {
            articleList.clear();
            articleTable.setItems(null);
            articleList.addAll(articleService.getAll());
            articleTable.setItems(articleList);
            Platform.runLater(() -> articleTable.setPlaceholder(new Label("Nema podataka.")));
        };
        Thread t = new Thread(runnable);
        t.setDaemon(true);
        t.start();
    }

    public void loadSearchDetails(String text) {
        List<Article> articles = articleService.findByTextFields(text);
        articleList.clear();
        articleList.addAll(articles);
        articleTable.setItems(articleList);
    }

    public void reloadArticleDetails() {
        List<Article> articles = articleService.getAll();
        articleList.clear();
        articleList.addAll(articles);
        articleTable.setItems(articleList);
        articleTable.refresh();
    }


}