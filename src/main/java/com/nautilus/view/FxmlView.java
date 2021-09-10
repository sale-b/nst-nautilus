package com.nautilus.view;

import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

@SuppressWarnings("unused")
@Slf4j
public enum FxmlView {

    BORDERLESS {
        @Override
        public String getTitle() {
            return getStringFromResourceBundle("");
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/Borderless.fxml";
        }
    },

    MAIN_WINDOW {
        @Override
        public String getTitle() {
            return getStringFromResourceBundle("app.title");
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/MainWindow.fxml";
        }

    },

    TRANSPARENT_WINDOW {
        @Override
        public String getTitle() {
            return getStringFromResourceBundle("");
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/TransparentWindow.fxml";
        }
    },

    SIDE_PANE {
        @Override
        public String getTitle() {
            return getStringFromResourceBundle("");
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/sidepanel.fxml";
        }
    },

    CUSTOMER {
        @Override
        public String getTitle() {
            return getStringFromResourceBundle("");
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/Customers.fxml";
        }
    },

    ORDER {
        @Override
        public String getTitle() {
            return getStringFromResourceBundle("");
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/Orders.fxml";
        }
    },

    ARTICLE {
        @Override
        public String getTitle() {
            return getStringFromResourceBundle("");
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/Articles.fxml";
        }
    },

    PACKAGING {
        @Override
        public String getTitle() {
            return getStringFromResourceBundle("");
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/Packaging.fxml";
        }
    },

    SANITIZE {
        @Override
        public String getTitle() {
            return getStringFromResourceBundle("");
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/Sanitize.fxml";
        }
    },

    CUSTOMER_DIALOG {
        @Override
        public String getTitle() {
            return getStringFromResourceBundle("customer-dialog.title");
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/CustomerDialog.fxml";
        }
    },

    ORDER_DIALOG {
        @Override
        public String getTitle() {
            return getStringFromResourceBundle("order-dialog.title");
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/OrderDialog.fxml";
        }
    },

    ARTICLE_DIALOG {
        @Override
        public String getTitle() {
            return getStringFromResourceBundle("article-dialog.title");
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/ArticleDialog.fxml";
        }
    },
    PACKAGING_DIALOG {
        @Override
        public String getTitle() {
            return getStringFromResourceBundle("packaging-dialog.title");
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/PackagingDialog.fxml";
        }
    },
    SANITIZE_DIALOG {
        @Override
        public String getTitle() {
            return getStringFromResourceBundle("sanitize-dialog.title");
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/SanitizeDialog.fxml";
        }
    };


    public abstract String getTitle();

    public abstract String getFxmlFile();

    String getStringFromResourceBundle(String key) {
        return new String((ResourceBundle.getBundle("bundle").getString(key))
                .getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
    }

}
