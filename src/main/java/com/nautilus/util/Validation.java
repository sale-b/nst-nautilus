package com.nautilus.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

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
        final String FIELD_NAME = "ime/naziv";
        if (!value.isEmpty()) {
            for (String word : value.split(" ")) {
                if (!Character.isUpperCase(word.codePointAt(0))) {
                    validationAlert(false, FIELD_NAME);
                    return false;
                }
            }
            return true;
        } else {
            validationAlert(false, FIELD_NAME);
            return false;
        }
    }

    public static boolean validateCity(String value) {
        final String FIELD_NAME = "mesto";
        if (!value.isEmpty()) {
                if (!Character.isUpperCase(value.split(" ")[0].codePointAt(0))) {
                    validationAlert(false, FIELD_NAME);
                    return false;
                }

            return true;
        } else {
            validationAlert(false, FIELD_NAME);
            return false;
        }
    }

    public static boolean validateAddress(String value) {
        final String FIELD_NAME = "adresa";
        if (!value.isEmpty()) {
            if (!Character.isUpperCase(value.split(" ")[0].codePointAt(0))) {
                validationAlert(false, FIELD_NAME);
                return false;
            }

            return true;
        } else {
            validationAlert(false, FIELD_NAME);
            return false;
        }
    }

    public static boolean validatePhone(String value) {
        final String FIELD_NAME = "telefon";
        if (!value.isEmpty()) {
            if (!value.matches("^[0-9]*$")) {
                validationAlert(false, FIELD_NAME);
                return false;
            }

            return true;
        } else {
            validationAlert(false, FIELD_NAME);
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
        alert.setTitle("Neuspesna validacija podataka");
        alert.setHeaderText(null);
        if (fieldName.equals("Role")) alert.setContentText("Izaberite " + fieldName);
        else {
            if (empty) alert.setContentText("Polje " + fieldName + " ne sme biti prazno.");
            else alert.setContentText("Polje " + fieldName + " nije validno popunjeno.");
        }
        alert.showAndWait();
    }

    public static Optional<ButtonType> deleteAlert() {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        alert.setTitle("Potvrdite brisanje");
        alert.setHeaderText(null);
        alert.setContentText("Da li ste sigurni da želite da obrišete selektovane redove?");
        return alert.showAndWait();
    }

    public static void deleteAlertForbidden(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Brisanje onemogućeno");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void updateAlertFail(String objectType) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
        alert.setTitle("Promena neuspešna.");
        alert.setHeaderText(null);
        alert.setContentText(objectType + " je promenjen eksterno. Biće vam prikazani osveženi podaci i moći ćete da ih promenite ukoliko je potrebno.");
        alert.showAndWait();
    }

    public static void noDbAlert() {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(Alert.AlertType.ERROR);
        alert.setTitle("Greška");
        alert.setHeaderText(null);
        alert.setContentText("Neuspešna konekcija na bazu podataka!");
        alert.showAndWait();
    }

}
