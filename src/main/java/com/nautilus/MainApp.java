package com.nautilus;

import com.nautilus.config.Config;
import com.nautilus.config.LiqubaseHelpper;
import com.nautilus.config.PropertiesCache;
import com.nautilus.view.StageManager;
import com.nautilus.view.borderless.CustomStage;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.sql.DataSource;
import java.util.Locale;

@Slf4j
public class MainApp extends Application {

    ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
    DataSource dataSource = (DataSource) context.getBean("dataSource");

    @Override
    public void start(Stage primaryStage) {
        if (Boolean.parseBoolean(PropertiesCache.getInstance().getProperty("spring.liquibase.enabled"))) {
            LiqubaseHelpper liquibase = new LiqubaseHelpper(dataSource);
            liquibase.update();
        }

        //Create custom stage
        CustomStage stage = new CustomStage(StageStyle.UNDECORATED);
        StageManager.displayInitialScene(stage);
//        StageManager.borderlessScene.maximizeStage();
        Locale.setDefault(new Locale.Builder().setLanguage("sr").setRegion("RS").setScript("Latn").build());


    }

    public static void main(String[] args) {
        launch(args);
    }


}
