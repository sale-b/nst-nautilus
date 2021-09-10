/*
 *
 */
package com.nautilus.view.borderless;

import com.nautilus.view.FxmlView;
import com.nautilus.view.StageManager;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class BorderlessPane extends AnchorPane {
    public BorderlessPane(BorderlessController controller) throws IOException {
        StageManager.getComponentWithController(FxmlView.BORDERLESS, this, controller);
    }
}
