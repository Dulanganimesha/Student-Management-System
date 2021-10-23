package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import service.UserServiceRedisImpl;
import util.AppBarIcon;

import java.io.IOException;

public class LoginFormController {

    public TextField txtUserName;
    public PasswordField txtPassword;

    private final UserServiceRedisImpl userService = new UserServiceRedisImpl();

    public void btnSignIn_OnAction(ActionEvent actionEvent) throws IOException {

        // We need to do the validation
//        if (txtUserName.getText().isEmpty())

        if (userService.authenticate(txtUserName.getText(), txtPassword.getText())){
            initializeUI();
        }else{
            new Alert(Alert.AlertType.ERROR, "Invalid login credentials").showAndWait();
            txtUserName.clear();
            txtPassword.clear();
            txtUserName.requestFocus();
        }

    }

    public void imgExit_OnMouseClicked(MouseEvent mouseEvent) {
        System.exit(0);
    }

    private void initializeUI() throws IOException {
        Stage primaryStage = ((Stage) txtUserName.getScene().getWindow().getUserData());
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/view/MainForm.fxml"));
        Parent root = fxmlLoader.load();
        Scene mainScene = new Scene(root);
        primaryStage.setScene(mainScene);
        MainFormController ctrl = fxmlLoader.getController();
        ctrl.navigate("Student Management System", "/view/HomeForm.fxml", AppBarIcon.NAV_ICON_NONE);
        mainScene.setUserData(ctrl);
        mainScene.setFill(Color.TRANSPARENT);
//        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setTitle("Student Management System");
        txtUserName.getScene().getWindow().hide();
        primaryStage.show();
        primaryStage.centerOnScreen();
    }
}
