package com.nautilus.view;

import javafx.scene.Parent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FxmlViewComponent {

    private Parent root;
    private Object controller;

}
