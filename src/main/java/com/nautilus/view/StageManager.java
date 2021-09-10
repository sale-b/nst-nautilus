package com.nautilus.view;

import com.nautilus.controller.MainWindowController;
import com.nautilus.view.borderless.BorderlessScene;
import com.nautilus.view.borderless.CustomStage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class StageManager {

    public static final MainWindowController mainWindowController = new MainWindowController();
    public static BorderlessScene borderlessScene;
    public static CustomStage primaryStage;

    public static FxmlViewComponent getComponentWithController(FxmlView viewFx) {

        FXMLLoader loader = new FXMLLoader(StageManager.class.getResource(viewFx.getFxmlFile()));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new FxmlViewComponent(root, loader.getController());
    }

    public static void getComponentWithController(FxmlView viewFx, Object root, Object controller) {

        FXMLLoader loader = new FXMLLoader(StageManager.class.getResource(viewFx.getFxmlFile()));
        loader.setController(controller);
        loader.setRoot(root);
        try {
            loader.load();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void displayInitialScene(CustomStage stage) {
        stage.setMinWidth(250);
        stage.setMinHeight(250);

        //Primary Stage. Notice that we don't care about the one got as a parameter. Instead we use our custom one.
        primaryStage = stage;

        // Set the scene to your stage and you're done!
        borderlessScene = stage.craftBorderlessScene(mainWindowController);

        //mainWindowController
        mainWindowController.setBorderlessScene(borderlessScene);
        mainWindowController.initActions();


        //Show
        stage.setScene(borderlessScene);
        stage.setWidth(1200);
        stage.setHeight(700);

        //IMPORTANT: Notice that I used showAndAdjust() rather than show().
        //It is custom method, that, besides just showing the stage, implements additional features.
        //I couldn't just override show() method, because it is final method.
        stage.showAndAdjust();

    }

    public static void makeDialogDraggable(Stage dialog, Pane dialogPane) {
        dialogPane.setStyle("-fx-background-color: transparent;");
        Scene dialogScene = new Scene(dialogPane);
        dialogScene.setFill(Color.TRANSPARENT);
        dialog.setScene(dialogScene);
        dialog.initStyle(StageStyle.TRANSPARENT);
        dialogPane.setOnMousePressed(pressEvent -> dialogPane.setOnMouseDragged(dragEvent -> {
            dialog.setX(dragEvent.getScreenX() - pressEvent.getSceneX());
            dialog.setY(dragEvent.getScreenY() - pressEvent.getSceneY());
        }));
        dialog.show();
    }
}
