package com.nautilus.util;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Formatter {

    public static final String WATER_SMALL = "Voda 15L";
    public static final String WATER_LARGE = "Voda 19L";

    public static final String COLOR_ERROR = "#eb707a";
    public static final String COLOR_WARNING = "#ebb870";
    public static final String COLOR_SUCCESS = "#70eb81";

    private static final DecimalFormat formatter = new DecimalFormat("#,##0.00");

    public static void setDatePickerFormat(DatePicker datePicker) {
        datePicker.setConverter(new StringConverter<LocalDate>() {
            final String pattern = "dd.MM.yyyy";
            final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);

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

    public static void setObligationLabelStyle(Label label, Integer clmStatus) {
        if (clmStatus > 0) {
            label.setStyle("-fx-background-color: " + COLOR_ERROR + ";-fx-label-padding: 1 10 1 10;-fx-background-radius: 5px;-fx-font-family: 'Roboto';-fx-text-fill: #ffffff;");
        } else {
            label.setStyle("-fx-background-color: " + COLOR_SUCCESS + ";-fx-label-padding: 1 10 1 10;-fx-background-radius: 5px;-fx-font-family: 'Roboto';-fx-text-fill: #ffffff;");
        }
    }

    public static void setSanitizeTitleLabelStyle(Label label, Integer clmStatus) {
        if (clmStatus > 0) {
            label.setStyle("-fx-background-color: " + COLOR_WARNING + ";-fx-label-padding: 1 10 1 10;-fx-background-radius: 5px;-fx-font-family: 'Roboto';-fx-text-fill: #ffffff;");
        } else {
            label.setStyle("-fx-background-color: " + COLOR_SUCCESS + ";-fx-label-padding: 1 10 1 10;-fx-background-radius: 5px;-fx-font-family: 'Roboto';-fx-text-fill: #ffffff;");
        }
    }

    public static void setSanitizeLabelStyle(Label label, Integer clmStatus) {
        if (clmStatus < 0) {
            label.setStyle("-fx-background-color: " + COLOR_ERROR + ";-fx-label-padding: 1 10 1 10;-fx-background-radius: 5px;-fx-font-family: 'Roboto';-fx-text-fill: #ffffff;");
        } else if (clmStatus > 0) {
            label.setStyle("-fx-background-color: " + COLOR_SUCCESS + ";-fx-label-padding: 1 10 1 10;-fx-background-radius: 5px;-fx-font-family: 'Roboto';-fx-text-fill: #ffffff;");
        } else {
            label.setStyle("-fx-background-color: " + COLOR_WARNING + ";-fx-label-padding: 1 10 1 10;-fx-background-radius: 5px;-fx-font-family: 'Roboto';-fx-text-fill: #ffffff;");
        }
    }

    public static void setNotApplicableLabelStyle(Label label) {
        label.setStyle("-fx-background-color: #bdedba;-fx-label-padding: 1 10 1 10;-fx-background-radius: 5px;-fx-font-family: 'Roboto';-fx-text-fill: #ffffff;");
        label.setText("-");
    }

    public static void setPackagingLabelStyle(Label label, Integer clmStatus) {
        if (clmStatus > 0) {
            label.setStyle("-fx-background-color: " + COLOR_ERROR + ";-fx-label-padding: 1 10 1 10;-fx-background-radius: 5px;-fx-font-family: 'Roboto';-fx-text-fill: #ffffff;");
            label.setGraphic(null);
        } else if (clmStatus == 0) {
            label.setStyle("-fx-background-color: " + COLOR_SUCCESS + ";-fx-label-padding: 1 10 1 10;-fx-background-radius: 5px;-fx-font-family: 'Roboto';-fx-text-fill: #ffffff;");
            label.setGraphic(null);
        } else {
            MaterialDesignIconView icon = new MaterialDesignIconView(MaterialDesignIcon.CLOSE_CIRCLE);
            icon.setGlyphSize(14);
            icon.setStyle("-fx-fill: #ff0000;");
            label.setStyle("-fx-background-color: #ffc6c6;-fx-label-padding: 1 10 1 0;-fx-background-radius: 8px;-fx-font-family: 'Roboto';-fx-text-fill: #ff0000;-fx-border-color: #ff0000; -fx-border-width: 5px; -fx-border-radius: 5px");
            label.setGraphic(icon);
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

    public static String formatPrice(Double price) {
        return String.format("%s din.", formatter.format(price));
    }

    public static String formatTax(Double tax) {
        return String.format("%s %%", formatter.format(tax));
    }

    public static String formatDouble(Double dbl) {
        return formatter.format(dbl);
    }

}
