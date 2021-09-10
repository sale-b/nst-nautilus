package com.nautilus.view.borderless;

import com.nautilus.view.FxmlView;
import com.nautilus.view.StageManager;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class TransparentWindow extends StackPane {

    @FXML
    private StackPane stackPane;

    private final Stage window = new Stage();


    public TransparentWindow() {
        StageManager.getComponentWithController(FxmlView.TRANSPARENT_WINDOW, this, this);
        window.setTitle("Transparent Window");
        window.initStyle(StageStyle.TRANSPARENT);
        window.initModality(Modality.NONE);
        window.setScene(new Scene(this, Color.TRANSPARENT));
    }

    public Stage getWindow() {
        return window;
    }

    public void close() {
        window.close();
    }

    public void show() {
        if (!window.isShowing())
            window.show();
        else
            window.requestFocus();
    }
}
