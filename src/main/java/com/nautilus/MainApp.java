package com.nautilus;

import com.nautilus.config.Config;
import com.nautilus.config.StageManager;
import com.nautilus.util.LiqubaseHelpper;
import com.nautilus.view.FxmlView;
import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.sql.DataSource;


public class MainApp extends Application {

    protected StageManager stageManager;
    ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
    DataSource dataSource = (DataSource) context.getBean("dataSource");

    @Override
    public void start(Stage stage) throws Exception {

        new Thread(() ->
        {
            LiqubaseHelpper liquibase = new LiqubaseHelpper(dataSource);
            liquibase.update();
        }).start();

        stageManager = new StageManager(stage);
        displayInitialScene();
    }

    public static void main(String[] args) {
        launch(args);
    }

    protected void displayInitialScene() {
        stageManager.switchScene(FxmlView.USER);
    }

}
