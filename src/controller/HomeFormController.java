package controller;

import com.jfoenix.controls.JFXRippler;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import util.AppBarIcon;

import java.io.IOException;

public class HomeFormController {

    public JFXRippler rprAddNewStudent;
    public AnchorPane pneAddNewStudent;
    public JFXRippler rprSearchStudents;
    public AnchorPane pneSearchStudents;
    public AnchorPane root;

    private double x;
    private double y;
    private boolean reverse;

    public void initialize() {
        rprAddNewStudent.setControl(pneAddNewStudent);
        rprSearchStudents.setControl(pneSearchStudents);
        pneAddNewStudent.setFocusTraversable(true);
        pneSearchStudents.setFocusTraversable(true);
        animateWave();
    }

    private void animateWave() {
        root.layoutBoundsProperty().addListener(new ChangeListener<Bounds>() {
            @Override
            public void changed(ObservableValue<? extends Bounds> observable, Bounds oldValue, Bounds newValue) {
                Canvas canvas = new Canvas(root.getWidth(), root.getHeight());
                GraphicsContext ctx = canvas.getGraphicsContext2D();
                x = canvas.getWidth() / 2;

                root.getChildren().add(canvas);
                canvas.toBack();

                Timeline timeline = new Timeline(new KeyFrame(Duration.millis(50), event -> {

                    if (x >= canvas.getWidth() / 4 * 4.25) {
                        reverse = true;
                    } else if (x <= (-canvas.getWidth() / 4)) {
                        reverse = false;
                    }

                    if (reverse) {
                        x = x - 2;
                    } else {
                        x = x + 2;
                    }

                    // PI/180 = 1deg
                    double yy = canvas.getHeight() - 100 + (Math.sin(y * Math.PI / 180) * 20);
                    double xc2 = x - 200;
                    double yc2 = yy + 100;
                    double xc3 = Math.abs(x - xc2);
                    double yc3 = Math.abs(yc2 - (yy));
                    double angle = yc3 / xc3;
                    xc3 = x + 200;
                    yc3 = yc2 - (xc3 - xc2) * angle;
                    y++;

                    ctx.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                    ctx.beginPath();
                    ctx.moveTo(-canvas.getWidth() / 4, canvas.getHeight() - 100);
                    ctx.bezierCurveTo(0, canvas.getHeight() - 200, xc2, yc2, x, yy);
                    ctx.bezierCurveTo(xc3, yc3, xc3, yc2, canvas.getWidth() / 4 * 5, canvas.getHeight() - 100);
                    ctx.lineTo(canvas.getWidth(), canvas.getHeight());
                    ctx.lineTo(0, canvas.getHeight());
                    ctx.lineTo(0, canvas.getHeight() - 50);
                    ctx.setFill(Color.LIGHTBLUE);
                    ctx.fill();
                }));
                timeline.setCycleCount(Animation.INDEFINITE);
                timeline.setRate(3);
                timeline.playFromStart();
            }
        });

    }

    public void pneAddNewStudent_OnKeyPress(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER || keyEvent.getCode() == KeyCode.SPACE) {
            rprAddNewStudent.createManualRipple().run();
        }
    }

    public void pneSearchStudents_OnKeyPress(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER || keyEvent.getCode() == KeyCode.SPACE) {
            rprSearchStudents.createManualRipple().run();
        }
    }

    public void pneSearchStudents_OnMouseClicked(MouseEvent mouseEvent) {
        navigate("Search Students", "/view/SearchStudentsForm.fxml");
    }

    public void pneAddNewStudent_OnMouseClicked(MouseEvent mouseEvent) throws IOException {
        navigate("Add New Student", "/view/StudentForm.fxml");
    }

    public void pneAddNewStudent_OnKeyReleased(KeyEvent keyEvent) throws IOException {
        if (keyEvent.getCode() == KeyCode.ENTER || keyEvent.getCode() == KeyCode.SPACE) {
            navigate("Add New Student", "/view/StudentForm.fxml");
        }
    }

    public void pneSearchStudents_OnKeyReleased(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER || keyEvent.getCode() == KeyCode.SPACE) {
            navigate("Search Students", "/view/SearchStudentsForm.fxml");
        }
    }

    private void navigate(String title, String url) {
        MainFormController ctrl = (MainFormController) pneSearchStudents.getScene().getUserData();
        ctrl.navigate(title, url, AppBarIcon.NAV_ICON_BACK, () ->
                ctrl.navigate("Student Management System", "/view/HomeForm.fxml", AppBarIcon.NAV_ICON_HOME));
    }
}
