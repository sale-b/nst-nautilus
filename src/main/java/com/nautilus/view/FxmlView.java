package com.nautilus.view;

import java.util.ResourceBundle;

@SuppressWarnings("unused")
public enum FxmlView {

    BORDERLESS {
        @Override
        public String getTitle() {
            return getStringFromResourceBundle("user.title");
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/Borderless.fxml";
        }
    },

    MAIN_WINDOW {
        @Override
        public String getTitle() {
            return getStringFromResourceBundle("user.title");
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/MainWindow.fxml";
        }

    },

    TRANSPARENT_WINDOW {
        @Override
        public String getTitle() {
            return getStringFromResourceBundle("user.title");
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/TransparentWindow.fxml";
        }
    },

    USER_DIALOG {
        @Override
        public String getTitle() {
            return getStringFromResourceBundle("window.title");
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/CustomerDialog.fxml";
        }
    },

    CUSTOMER {
        @Override
        public String getTitle() {
            return getStringFromResourceBundle("user.title");
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/Customers.fxml";
        }
    },

    SIDE_PANE {
        @Override
        public String getTitle() {
            return getStringFromResourceBundle("user.title");
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/sidepanel.fxml";
        }
    },

    ORDER {
        @Override
        public String getTitle() {
            return getStringFromResourceBundle("user.title");
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/Orders.fxml";
        }
    },

    ARTICLE {
        @Override
        public String getTitle() {
            return getStringFromResourceBundle("user.title");
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/Articles.fxml";
        }
    },

    ARTICLE_DIALOG {
        @Override
        public String getTitle() {
            return getStringFromResourceBundle("window.title");
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/ArticleDialog.fxml";
        }
    },

    ORDER_DIALOG {
        @Override
        public String getTitle() {
            return getStringFromResourceBundle("window.title");
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/OrderDialog.fxml";
        }
    },


    PACKAGING {
        @Override
        public String getTitle() {
            return getStringFromResourceBundle("user.title");
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/Page3.fxml";
        }
    },

    SANITIZE {
        @Override
        public String getTitle() {
            return getStringFromResourceBundle("user.title");
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/Page4.fxml";
        }
    };


    public abstract String getTitle();

    public abstract String getFxmlFile();

    String getStringFromResourceBundle(String key) {
        return ResourceBundle.getBundle("Bundle").getString(key);
    }

}
