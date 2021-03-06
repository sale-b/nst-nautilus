package com.nautilus.controller;

import com.jfoenix.controls.JFXButton;
import com.nautilus.view.FxmlView;
import com.nautilus.view.FxmlViewComponent;
import com.nautilus.view.StageManager;
import com.nautilus.view.borderless.BorderlessScene;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainWindowController extends StackPane {

    @FXML
    private BorderPane mainPane;

    @FXML
    private Label topLabel;

    @FXML
    private JFXButton minimize;

    @FXML
    @Getter
    private JFXButton maximizeNormalize;

    @FXML
    private JFXButton exit;

    private BorderlessScene borderlessScene;

    @Getter
    private CustomerController customerController;

    @FXML
    private Button bt1;

    @FXML
    private Button bt2;

    @FXML
    private Button bt3;

    @FXML
    private Button bt4;

    @FXML
    private Button bt5;

    @FXML
    void customersPage(ActionEvent event) {
        FxmlViewComponent userViewComponent = StageManager.getComponentWithController(FxmlView.CUSTOMER);
        mainPane.setCenter(userViewComponent.getRoot());
        restoreIsButtonPressed();
        bt1.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
    }

    @FXML
    void ordersPage(ActionEvent event) {
        FxmlViewComponent userViewComponent = StageManager.getComponentWithController(FxmlView.ORDER);
        mainPane.setCenter(userViewComponent.getRoot());
        restoreIsButtonPressed();
        bt2.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
    }

    @FXML
    void articlesPage(ActionEvent event) {
        FxmlViewComponent userViewComponent = StageManager.getComponentWithController(FxmlView.ARTICLE);
        mainPane.setCenter(userViewComponent.getRoot());
        restoreIsButtonPressed();
        bt3.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
    }

    @FXML
    void packagingPage(ActionEvent event) {
        FxmlViewComponent userViewComponent = StageManager.getComponentWithController(FxmlView.PACKAGING);
        mainPane.setCenter(userViewComponent.getRoot());
        restoreIsButtonPressed();
        bt4.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
    }

    @FXML
    void sanitizePage(ActionEvent event) {
        FxmlViewComponent userViewComponent = StageManager.getComponentWithController(FxmlView.SANITIZE);
        mainPane.setCenter(userViewComponent.getRoot());
        restoreIsButtonPressed();
        bt5.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
    }

    public MainWindowController() {
        StageManager.getComponentWithController(FxmlView.MAIN_WINDOW, this, this);
    }

    private void restoreIsButtonPressed() {
        bt1.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        bt2.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        bt3.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        bt4.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
        bt5.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
    }

    public void initActions() {

        // To move the window around by pressing a node:
        borderlessScene.setMoveControl(topLabel);

        /* Action Buttons */
        exit.setOnAction(a -> StageManager.primaryStage.close());

        minimize.setOnAction(a -> {
            mainPane.requestFocus();
            StageManager.primaryStage.setIconified(true);
        });

        maximizeNormalize.setOnAction(a -> StageManager.borderlessScene.maximizeStage());

        FxmlViewComponent userViewComponent = StageManager.getComponentWithController(FxmlView.CUSTOMER);
        mainPane.setCenter(userViewComponent.getRoot());
        customerController = (CustomerController) userViewComponent.getController();
        mainPane.requestFocus();
        bt1.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        topLabel.setText(FxmlView.MAIN_WINDOW.getTitle());

    }

    public void setBorderlessScene(BorderlessScene borderlessScene) {
        this.borderlessScene = borderlessScene;
    }
}
