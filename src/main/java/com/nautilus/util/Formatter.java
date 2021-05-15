package com.nautilus.util;

import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Formatter {

    public static final String WATER_SMALL = "Voda 15L";
    public static final String WATER_LARGE = "Voda 19L";

    public static void setDatePickerFormat(DatePicker datePicker) {
        datePicker.setConverter(new StringConverter<LocalDate>() {
            final String pattern = "dd.MM.yyyy";
            final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);

            {
                datePicker.setPromptText(pattern.toLowerCase());
            }

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        });

        Callback<DatePicker, DateCell> dayCellFactory = new Callback<DatePicker, DateCell>() {
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        // Must call super
                        super.updateItem(item, empty);

                        // Show Weekends in blue color
                        DayOfWeek day = DayOfWeek.from(item);
                        if (day == DayOfWeek.SUNDAY) {
                            this.setTextFill(Color.RED);
                        }
                    }
                };
            }
        };

        // Set the day cell factory to the DatePicker
        datePicker.setDayCellFactory(dayCellFactory);

    }

    public static void setLabelStyle(Label label, Integer clmStatus) {
        if (clmStatus > 0) {
            label.setStyle("-fx-background-color: #e36e6e;-fx-label-padding: 1 10 1 10;-fx-background-radius: 5px;-fx-font-family: 'Roboto';-fx-text-fill: #ffffff;");
        } else {
            label.setStyle("-fx-background-color: #76e36e;-fx-label-padding: 1 10 1 10;-fx-background-radius: 5px;-fx-font-family: 'Roboto';-fx-text-fill: #ffffff;");
        }
    }

    public static void styleEditButton(Button btnEdit, Image imgEdit) {
        btnEdit.setStyle("-fx-background-color: transparent;");
        ImageView iv = new ImageView();
        iv.setImage(imgEdit);
        iv.setPreserveRatio(true);
        iv.setSmooth(true);
        iv.setCache(true);
        btnEdit.setGraphic(iv);
    }


}
