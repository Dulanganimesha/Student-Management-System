package controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Student;
import model.StudentTM;
import util.AppBarIcon;
import util.MaterialUI;

import java.io.IOException;
import java.util.Optional;

public class SearchStudentsFormController {
    public TextField txtQuery;
    public TableView<StudentTM> tblResults;

    private StudentServiceRedisImpl studentService = new StudentServiceRedisImpl();

    public void initialize(){
        MaterialUI.paintTextFields(txtQuery);

        tblResults.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("nic"));
        tblResults.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("fullName"));
        tblResults.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("address"));
        TableColumn<StudentTM, HBox> lastCol = (TableColumn<StudentTM, HBox>) tblResults.getColumns().get(3);

        lastCol.setCellValueFactory(param -> {
            ImageView imgEdit = new ImageView("/view/assets/Edit.png");
            ImageView imgTrash = new ImageView("/view/assets/Trash.png");

            imgEdit.getStyleClass().add("action-icons");
            imgTrash.getStyleClass().add("action-icons");

            imgEdit.setOnMouseClicked(event -> updateStudent(param.getValue()));
            imgTrash.setOnMouseClicked(event -> deleteStudent(param.getValue()));

            return new ReadOnlyObjectWrapper<>(new HBox(10, imgEdit, imgTrash));
        });

        txtQuery.textProperty().addListener((observable, oldValue, newValue) -> loadAllStudents(newValue));

        loadAllStudents("");
    }

    private void deleteStudent(StudentTM tm){
        try {
            Optional<ButtonType> buttonType = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure to delete this student?", ButtonType.YES, ButtonType.NO).showAndWait();
            if (buttonType.get() == ButtonType.YES) {
                studentService.deleteStudent(tm.getNic());
                tblResults.getItems().remove(tm);
            }
        }catch (NotFoundException e){
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something terribly went wrong, please contact DEPPO!", ButtonType.OK).show();
        }
    }

    private void updateStudent(StudentTM tm){
        try {
            Stage secondaryStage = new Stage();
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/view/MainForm.fxml"));
            Scene secondaryScene = new Scene(loader.load());
            MainFormController ctrl = loader.getController();

            secondaryStage.setScene(secondaryScene);
            secondaryScene.setFill(Color.TRANSPARENT);
            secondaryStage.initStyle(StageStyle.TRANSPARENT);
            secondaryStage.initModality(Modality.WINDOW_MODAL);
            secondaryStage.initOwner(txtQuery.getScene().getWindow());
            secondaryStage.setTitle("Update Student");
            ctrl.navigate("Update Student","/view/StudentForm.fxml", AppBarIcon.NAV_ICON_NONE, null, tm);

            secondaryStage.showAndWait();
            tblResults.refresh();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadAllStudents(String query){
        tblResults.getItems().clear();

        for (Student student : studentService.findStudents(query)) {
            tblResults.getItems().add(new StudentTM(student.getNic(), student.getFullName(), student.getAddress()));
        }
    }

    public void tblResults_OnKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.DELETE){
            deleteStudent(tblResults.getSelectionModel().getSelectedItem());
        }else if (keyEvent.getCode() == KeyCode.ENTER){
            updateStudent(tblResults.getSelectionModel().getSelectedItem());
        }
    }
}
