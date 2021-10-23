package controller;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import model.Student;
import model.StudentTM;
import service.StudentServiceRedisImpl;
import service.exception.DuplicateEntryException;
import service.exception.NotFoundException;
import util.MaterialUI;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;

import static util.ValidationUtil.*;

public class StudentFormController {

    private final StudentServiceRedisImpl studentService = new StudentServiceRedisImpl();
    public TextField txtNIC;
    public TextField txtFullName;
    public TextField txtAddress;
    public TextField txtContactNumber;
    public TextField txtEmail;
    public TextField txtDOB;
    public JFXButton btnSave;
    public Label lblTitle;
    public Label lblAge;
    public AnchorPane root;
    public ImageView imgLogo;

    public void initialize() {

        MaterialUI.paintTextFields(txtNIC, txtFullName, txtAddress, txtDOB, txtContactNumber, txtEmail);

        Platform.runLater(() -> {

            if (root.getUserData() != null) {
                StudentTM tm = (StudentTM) root.getUserData();
                Student student = null;
                try {
                    student = studentService.findStudent(tm.getNic());
                } catch (NotFoundException e) {
                    e.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "Something terribly went wrong, please contact DEPPO!", ButtonType.OK).show();
                }

                txtNIC.setEditable(false);
                txtNIC.setText(student.getNic());
                txtFullName.setText(student.getFullName());
                txtAddress.setText(student.getAddress());
                txtContactNumber.setText(student.getContact());
                txtEmail.setText(student.getEmail());
                txtDOB.setText(student.getDateOfBirth().toString());

                btnSave.setText("UPDATE STUDENT");
                lblTitle.setText("Update Student");
                imgLogo.setImage(new Image("/view/assets/Update Student.png"));
            }
        });

        setMaxLength(txtDOB, 10);
        setMaxLength(txtContactNumber, 11);

        txtDOB.textProperty().addListener((observable, oldValue, newValue) -> {

            if (txtDOB.getLength() == 10) {
                try {
//                    Date dob = parseDate(txtDOB.getText());
//                    Date today = new Date();
//
//                    long diff = today.getTime() - dob.getTime();
//                    double year = diff / (1000 * 60 * 60 * 24 * 365.0) ;
//                    System.out.println("Year: " + year);

                    LocalDate dob2 = LocalDate.parse(txtDOB.getText());
                    Period between = Period.between(dob2, LocalDate.now());

                    lblAge.setText(between.getYears() + " Years and " + between.getMonths() + " Months " + between.getDays() + " Days old");

                } catch (DateTimeParseException e) {
                    lblAge.setText("Invalid date of birth");
                }
            }

        });
    }

//    private Date parseDate(String input) throws ParseException {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        return sdf.parse(input);
//    }

    private void setMaxLength(TextField txt, int maxLength) {
        txt.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > maxLength) {
                txt.setText(txt.getText(0, maxLength));
            }
        });
    }

    public void txtDOB_OnKeyTyped(KeyEvent keyEvent) {

        if (keyEvent.getCharacter().equals("-") && (txtDOB.getText().length() == 4 || txtDOB.getText().length() == 7)) {
            return;
        }

        if (!Character.isDigit(keyEvent.getCharacter().charAt(0))) {
            keyEvent.consume();     // This is not going to forward to the Java FX Runtime Env.
            return;
        }

        if ((txtDOB.getText().length() == 4 || txtDOB.getText().length() == 7) && (txtDOB.getCaretPosition() == txtDOB.getLength())) {
            txtDOB.appendText("-");
            txtDOB.positionCaret(txtDOB.getText().length() + 1);
        }
    }

    public void txtContactNumber_OnKeyTyped(KeyEvent keyEvent) {
        if (keyEvent.getCharacter().equals("-") && (txtContactNumber.getText().length() == 3)) {
            return;
        }

        if (!Character.isDigit(keyEvent.getCharacter().charAt(0))) {
            keyEvent.consume();     // This is not going to forward to the Java FX Runtime Env.
            return;
        }

        if ((txtContactNumber.getText().length() == 3) && (txtContactNumber.getCaretPosition() == txtContactNumber.getLength())) {
            txtContactNumber.appendText("-");
            txtContactNumber.positionCaret(txtContactNumber.getText().length() + 1);
        }
    }

    public void btnSave_OnAction(ActionEvent actionEvent) {
        try {

            if (!isValidated()) {
                return;
            }

            Student student = new Student(txtNIC.getText(),
                    txtFullName.getText(),
                    txtAddress.getText(),
                    LocalDate.parse(txtDOB.getText()),
                    txtContactNumber.getText(),
                    txtEmail.getText());

            if (btnSave.getText().equals("ADD NEW STUDENT")) {
                studentService.saveStudent(student);

                txtNIC.clear();
                txtFullName.clear();
                txtAddress.clear();
                txtDOB.clear();
                lblAge.setText("Enter DOB to display the age");
                txtContactNumber.clear();
                txtEmail.clear();
                txtNIC.requestFocus();

            } else {
                StudentTM tm = (StudentTM) root.getUserData();
                tm.setFullName(txtFullName.getText());
                tm.setAddress(txtAddress.getText());
                studentService.updateStudent(student);
            }
            new Alert(Alert.AlertType.NONE, "Student has been saved successfully", ButtonType.OK).show();
        } catch (DuplicateEntryException e) {
            new Alert(Alert.AlertType.ERROR, "A student already exits with the same NIC", ButtonType.OK).show();
            txtNIC.requestFocus();
        } catch (NotFoundException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something terribly went wrong, please contact DEPPO!", ButtonType.OK).show();
        }
    }

    private boolean isValidated() {
        String nic = txtNIC.getText();
        String fullName = txtFullName.getText();
        String address = txtAddress.getText();
        String dob = txtDOB.getText();
        String contact = txtContactNumber.getText();
        String email = txtEmail.getText();

        if (!((nic.length() == 10 && (nic.endsWith("V") || nic.endsWith("v")) && isInteger(nic.substring(0, 9)))
                || (nic.length() == 12 && isInteger(nic)))) {
            new Alert(Alert.AlertType.ERROR, "Invalid NIC").show();
            txtNIC.requestFocus();
            return false;
        } else if (!(isValid(fullName, true, false) && fullName.trim().length() >= 3)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Name. Name should contain at least 3 letters and can contain only alphabetic letters and spaces").show();
            txtFullName.requestFocus();
            return false;
        } else if (!(address.trim().length() >= 4 && isValid(address, true, true, ':', '.', ',', '-', '/', '\\'))) {
            new Alert(Alert.AlertType.ERROR, "Invalid Address. Address should be at least 4 digits and can contain only alphabetic letters, spaces and - , . / \\").show();
            txtAddress.requestFocus();
            return false;
        } else if (!isValidDate(dob)) {
            new Alert(Alert.AlertType.ERROR, "Invalid DOB").show();
            txtDOB.requestFocus();
            return false;
        } else if (!(contact.length() == 11 && isInteger(contact.substring(0, 3)) && isInteger(contact.substring(4, 11)))) {
            new Alert(Alert.AlertType.ERROR, "Invalid Contact Number").show();
            txtContactNumber.requestFocus();
            return false;
        } else if (!isValidEmail(email)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Email. Email can contain only letters, digits, periods and underscore.").show();
            txtEmail.requestFocus();
            return false;
        } else {
            return true;
        }
    }
}
