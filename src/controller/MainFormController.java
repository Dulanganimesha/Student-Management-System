package controller;

import com.jfoenix.controls.JFXRippler;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import util.AppBarIcon;
import util.NavActionListener;

import java.io.IOException;

public class MainFormController {
    public ImageView imgNav;
    public ImageView imgMinimize;
    public ImageView imgClose;
    public AnchorPane pneAppBar;
    public Label lblTitle;
    public AnchorPane pneStage;
    private double xMousePos;
    private double yMousePos;
    private AppBarIcon icon =  AppBarIcon.NAV_ICON_NONE;
    private NavActionListener navActionListener = null;

    public void initialize() {
        initWindow();
    }

    public void navigate(String title, String url, AppBarIcon icon){
        navigate(title, url, icon, null);
    }

    public void navigate(String title, String url, AppBarIcon icon, NavActionListener navActionListener) {
        navigate(title, url, icon, navActionListener, null);
    }

    public void navigate(String title, String url, AppBarIcon icon, NavActionListener navActionListener, Object data) {
        try {
            this.icon = icon;
            this.navActionListener = navActionListener;
            imgNav.setVisible(true);

            if (this.navActionListener == null){
                imgNav.setCursor(Cursor.DEFAULT);
            }else{
                imgNav.setCursor(Cursor.HAND);
            }

            /* Let's set the icon */
            switch (icon) {
                case NAV_ICON_NONE:
                    imgNav.setVisible(false);
                    imgNav.setUserData(null);
                    break;
                case NAV_ICON_HOME:
                    imgNav.setImage(new Image("/view/assets/icons/home.png"));
                    imgNav.setUserData(new Image("/view/assets/icons/home-hover.png"));
                    break;
                case NAV_ICON_BACK:
                    imgNav.setImage(new Image("/view/assets/icons/back.png"));
                    imgNav.setUserData(new Image("/view/assets/icons/back-hover.png"));
                    break;
            }
            Stage primaryStage = (Stage) (pneStage.getScene().getWindow());
            AnchorPane root = FXMLLoader.load(this.getClass().getResource(url));
            root.setUserData(data);
            FadeTransition ft = new FadeTransition(Duration.millis(750), root);

            lblTitle.setText(title);
            pneStage.getChildren().clear();
            pneStage.getChildren().add(root);
            ft.setFromValue(0.5);
            ft.setToValue(1);
            ft.play();

            Platform.runLater(() -> {
                primaryStage.sizeToScene();
                primaryStage.centerOnScreen();
//                    Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
//                    primaryStage.setX((visualBounds.getWidth() - primaryStage.getWidth())/2);
//                    primaryStage.setY((visualBounds.getHeight() - primaryStage.getHeight())/2);
            });
        } catch (IOException e) {
//            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void initWindow() {
        lblTitle.setMouseTransparent(true);

        Platform.runLater(() -> {
            lblTitle.setText(((Stage) (imgClose.getScene().getWindow())).getTitle());
        });

        pneAppBar.setOnMousePressed(event -> {
            xMousePos = event.getX();
            yMousePos = event.getY();
        });

        pneAppBar.setOnMouseDragged(event -> {
            if (event.isPrimaryButtonDown()) {
                Window mainWindow = imgMinimize.getScene().getWindow();
                mainWindow.setX(event.getScreenX() - xMousePos);
                mainWindow.setY(event.getScreenY() - yMousePos);
            }
        });

        imgNav.setOnMouseEntered(event -> swapNavIcon());
        imgNav.setOnMouseExited(event -> swapNavIcon());

        imgNav.setOnMouseClicked(event -> {
            if (navActionListener != null) {
                navActionListener.handle();
            }
        });

        imgClose.setOnMouseEntered(event -> imgClose.setImage(new Image("/view/assets/icons/close-hover.png")));
        imgClose.setOnMouseExited(event -> imgClose.setImage(new Image("/view/assets/icons/close.png")));
        imgClose.setOnMouseClicked(event -> ((Stage) (imgClose.getScene().getWindow())).close());

        imgMinimize.setOnMouseEntered(event -> imgMinimize.setImage(new Image("/view/assets/icons/minimize-hover.png")));
        imgMinimize.setOnMouseExited(event -> imgMinimize.setImage(new Image("/view/assets/icons/minimize.png")));
        imgMinimize.setOnMouseClicked(event -> ((Stage) (imgClose.getScene().getWindow())).setIconified(true));
    }

    private void swapNavIcon(){
        if (icon != AppBarIcon.NAV_ICON_NONE && navActionListener != null){
            Image temp = imgNav.getImage();

            imgNav.setImage((Image) imgNav.getUserData());
            imgNav.setUserData(temp);
        }
    }
}
