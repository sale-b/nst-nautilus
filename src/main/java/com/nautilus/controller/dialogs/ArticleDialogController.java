package com.nautilus.controller.dialogs;

import com.nautilus.config.Config;
import com.nautilus.controller.ArticleController;
import com.nautilus.domain.Article;
import com.nautilus.service.ArticleService;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.util.StringUtils;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.nautilus.util.Formatter.*;
import static com.nautilus.util.Validation.*;


@SuppressWarnings("unused")
@Slf4j
public class ArticleDialogController implements Initializable {

    @Setter
    private ArticleController articleController;

    @FXML
    private FontAwesomeIconView cancel;

    @FXML
    @Getter
    private Label title;

    @FXML
    @Getter
    private TextField name;

    @FXML
    @Getter
    private TextField price;

    @FXML
    @Getter
    private TextField tax;

    @FXML
    private Button reset;

    @FXML
    private Button saveArticle;

    @Setter
    private Article article = null;

    private boolean reloadArticleDetailsNeeded = false;

    private final ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
    private final ArticleService articleService = (ArticleService) context.getBean("articleServiceImpl");

    @FXML
    void cancel(MouseEvent event) {
        ((Node) (event.getSource())).getScene().getWindow().hide();
        if (reloadArticleDetailsNeeded)
            articleController.loadArticleDetails();
    }

    @FXML
    void reset(ActionEvent event) {
        clearFields();
        title.setText("Dodavanje novog artikla");
    }

    @FXML
    private void saveArticle(ActionEvent event) {
        if (emptyValidation(getNameValue().isEmpty(), "naziv artikla")
                && validateDouble(getPriceValue(), "cena")
                && validateDouble(getTaxValue(), "PDV")) {
            if (this.article == null) {
                article = new Article();
                fillArticleFields(article);
                Article newArticle = articleService.insert(article);
//                saveAlert("Article", newArticle.getName(), newArticle.getId());
                ((Node) (event.getSource())).getScene().getWindow().hide();
                articleController.loadArticleDetails();
            } else {
                fillArticleFields(article);
                Optional<Article> updatedArticle = articleService.update(article);
                if (updatedArticle.isPresent()) {
//                    updateAlertSuccess(updatedArticle.get());
                    ((Node) (event.getSource())).getScene().getWindow().hide();
                    articleController.loadArticleDetails();
                } else {
                    updateAlertFail("Artikal");
                    reloadArticleDetailsNeeded = true;
                    article = articleService.findById(this.article.getId());
                    name.setText(article.getName());
                    price.setText(formatDouble(article.getPrice()));
                    tax.setText(formatDouble(article.getTax()));
                }
            }

        }

    }

    private void fillArticleFields(Article article) {
        article.setName(StringUtils.capitalize(getNameValue()));
        article.setPrice(Double.parseDouble(getPriceValue()));
        article.setTax(Double.parseDouble(getTaxValue()));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public String getNameValue() {
        return name.getText();
    }

    public String getPriceValue() {
        return price.getText();
    }

    public String getTaxValue() {
        return tax.getText();
    }


    private void clearFields() {
        this.article = null;
        name.clear();
        price.clear();
        name.setDisable(false);
    }

}
