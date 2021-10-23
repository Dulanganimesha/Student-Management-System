package controller;

import com.jfoenix.controls.JFXProgressBar;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;

public class SplashScreenFormController {
    public JFXProgressBar pgb;

    public void initialize() {

        pgb.setProgress(0);

        Timeline tl = new Timeline(new KeyFrame(Duration.millis(50), event -> {
            if (pgb.getProgress() <= 1) {
                pgb.setProgress(pgb.getProgress() + 0.01);
            }
        }));

        tl.setRate(3);
        tl.setCycleCount(Animation.INDEFINITE);
        tl.playFromStart();

        pgb.progressProperty().addListener((observable, oldValue, newValue) -> {
            try {

                if (newValue.intValue() == 1) {
                    tl.stop();
                    initializeLogInForm();
                } else if (newValue.doubleValue() >= 0.95) {
                    spinUpRedisServerInstance();
                }

            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Failed to load the app, please contact DEPPO!").showAndWait();
                System.exit(1);
            }
        });
    }

    private void initializeLogInForm() throws IOException {
        Stage primaryStage = (Stage) pgb.getScene().getWindow();
        primaryStage.hide();

        Stage loginStage = new Stage();
        AnchorPane root = FXMLLoader.load(this.getClass().getResource("/view/LoginForm.fxml"));
        loginStage.setUserData(primaryStage);
        Scene loginScene = new Scene(root);
        loginStage.setScene(loginScene);
        loginStage.initStyle(StageStyle.TRANSPARENT);
        loginScene.setFill(Color.TRANSPARENT);
        loginStage.show();
        loginStage.centerOnScreen();
    }

    private void spinUpRedisServerInstance() throws Exception {
        String[] commands = {"redis-server", "redis.conf", "--requirepass", "redis"};

        Process redisServer = Runtime.getRuntime().exec(commands);
        int exitCode = redisServer.waitFor();

        if (exitCode != 0) {
            throw new Exception("Failed to spin up the redis server instance");
        }
    }
}
