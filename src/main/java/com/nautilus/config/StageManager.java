package com.nautilus.config;

import com.nautilus.view.FxmlView;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Objects;


/**
 * Manages switching Scenes on the Primary Stage
 */
public class StageManager {

    private final Stage primaryStage;


    public StageManager(Stage stage) {
        this.primaryStage = stage;
    }

    public void switchScene(final FxmlView view) {
        Parent viewRootNodeHierarchy = loadViewNodeHierarchy(view.getFxmlFile());
        show(viewRootNodeHierarchy, view.getTitle());
    }
    
    private void show(final Parent rootnode, String title) {
        Scene scene = prepareScene(rootnode);
        //scene.getStylesheets().add("/styles/Styles.css");
        //primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.centerOnScreen();
        primaryStage.initStyle(StageStyle.UTILITY);
        scene.setFill(Color.TRANSPARENT);
//        WindowStyle.allowDrag(rootnode, primaryStage);
//        ResizeHelper.addResizeListener(primaryStage);
        
        try {
            primaryStage.show();
        } catch (Exception exception) {
//            logAndExit ("Unable to show scene for title" + title,  exception);
            exception.printStackTrace();
        }
    }
    
    private Scene prepareScene(Parent rootnode){
        Scene scene = primaryStage.getScene();

        if (scene == null) {
            scene = new Scene(rootnode);
        }
        scene.setRoot(rootnode);
        return scene;
    }

    /**
     * Loads the object hierarchy from a FXML document and returns to root node
     * of that hierarchy.
     *
     * @return Parent root node of the FXML document hierarchy
     */
    private Parent loadViewNodeHierarchy(String fxmlFilePath) {
        Parent rootNode = null;
        try {
            rootNode = FXMLLoader.load(getClass().getResource(fxmlFilePath));
            Objects.requireNonNull(rootNode, "A Root FXML node must not be null");
        } catch (Exception exception) {
            exception.printStackTrace();
//            logAndExit("Unable to load FXML view " + fxmlFilePath, exception);
        }
        return rootNode;
    }
    
    
    private void logAndExit(String errorMsg, Exception exception) {
//        log.error(errorMsg, exception, exception.getCause());
        System.out.println(errorMsg);
        System.out.println(exception);
        Platform.exit();
    }

}
