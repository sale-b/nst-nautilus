package com.nautilus.util;

import javafx.scene.control.ButtonType;

import java.util.Optional;

@SuppressWarnings("unused")
public class Validation {

    public static boolean validateDouble(String value, String fieldName) {
        if (!value.isEmpty()) {
            try {
                Double.parseDouble(value);
            } catch (Exception e) {
                validationAlert(false, fieldName);
                return false;
            }
            return true;

        }
        validationAlert(false, fieldName);
        return false;
    }

    public static boolean validateName(String value) {
        if (!value.isEmpty()) {
            for (String word : value.split(" ")) {
                if (!Character.isUpperCase(word.codePointAt(0))) {
                    validationAlert(false, "name");
                    return false;
                }
            }
            return true;
        } else {
            validationAlert(false, "name");
            return false;
        }
    }

    public static boolean emptyValidation(boolean empty, String fieldName) {
        if (!empty) {
            return true;
        } else {
            validationAlert(true, fieldName);
            return false;
        }
    }

    public static void validationAlert(boolean empty, String fieldName) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
        alert.setTitle("Validation Error");
        alert.setHeaderText(null);
        if (fieldName.equals("Role")) alert.setContentText("Please Select " + fieldName);
        else {
            if (empty) alert.setContentText("Please Enter " + fieldName);
            else alert.setContentText("Please Enter Valid " + fieldName);
        }
        alert.showAndWait();
    }

    public static void saveAlert(String objectType, String objectField, Object objectId) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("User saved successfully.");
        alert.setHeaderText(null);
        alert.setContentText(String.format("%s  %s has been created and saved id is %s", objectType, objectField, objectId.toString()));
        alert.showAndWait();
    }

    public static Optional<ButtonType> deleteAlert(){
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete selected?");
        return alert.showAndWait();
    }

    public static void updateAlertFail(String objectType) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
        alert.setTitle("Promena neuspešna.");
        alert.setHeaderText(null);
        alert.setContentText(objectType + " je promenjen eksterno. Biće vam prikazani osveženi podaci i moći ćete da ih promenite ukoliko je potrebno.");
        alert.showAndWait();
    }


    private void updateAlertSuccess(String objectType, String objectField) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("User updated successfully.");
        alert.setHeaderText(null);
        alert.setContentText(String.format("%s %s has been updated.", objectType, objectField));
        alert.showAndWait();
    }
}
