/*
 * Copyright (C) 2017 North Carolina A&T State University
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package printerqueueviewcontroller;

import java.io.File;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javax.xml.transform.Result;
import printerqueue.PrintJob;
import printerqueue.PrintStatus;
import printerqueue.PrintType;
import printerqueue.Student;
import printerqueue.StudentDirectory;

/**
 *
 * @author CCannon
 */
public class AddPrintJobTextInputDialog extends Dialog<PrintJob> {

    private ButtonType add = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
    private ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

    private Date dueDate;

    //Add constructor
    //See https://stackoverflow.com/questions/31230228/get-multiple-results-from-custom-dialog-javafx
    public AddPrintJobTextInputDialog() {
        StudentDirectory directory = new StudentDirectory();
        directory.loadStudentDirectory();
        this.dueDate = new Date();

        setTitle("Add Print Job");
        setHeaderText("Complete the following form to add your print job to the queue");

        GridPane printInfoPane = new GridPane();
        Label stlPathLabel = new Label("Path to .stl file");
        Label printTypeLabel = new Label("What is this print for?");
        Label dueDateLabel = new Label("When is this print needed by?");
        Label studentLabel = new Label("Please select your name");
        Label commentsLabel = new Label("Enter any additional instructions for printing here");

        TextField stlPathTextField = new TextField();
        ComboBox printTypeComboBox = new ComboBox();
        printTypeComboBox.getItems().addAll(
                "Assigned Class Project",
                "Project for Student Group",
                "Personal Project/For Funsies"
        );
        DatePicker dueDatePicker = new DatePicker();
        dueDatePicker.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                setDueDate(dueDatePicker.getValue());
            }
        });
        ComboBox studentComboBox = new ComboBox();
        studentComboBox.setItems(FXCollections.observableList(new ArrayList<Student>(directory.getStudents())));
        TextArea commentsTextArea = new TextArea();

        Button stlPathBrowseButton = new Button("Browse");
        stlPathBrowseButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                FileChooser chooser = new FileChooser();
                File file = chooser.showOpenDialog(null);
                if (file != null) {
                    stlPathTextField.setText(file.getAbsolutePath());
                }
            }
        });
        Button newStudentButton = new Button("Register new student");

        newStudentButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                AddStudentTextInputDialog addStudentDialog = new AddStudentTextInputDialog();
                Optional<Student> result = addStudentDialog.showAndWait();

                if(result.isPresent()) {
                    studentComboBox.getItems().add(result.get());
                }
            }
        });
        
        printInfoPane.add(stlPathLabel, 0, 0);
        printInfoPane.add(printTypeLabel, 0, 1);
        printInfoPane.add(dueDateLabel, 0, 2);
        printInfoPane.add(studentLabel, 0, 3);
        printInfoPane.add(commentsLabel, 0, 4);
        printInfoPane.add(stlPathTextField, 1, 0);
        printInfoPane.add(printTypeComboBox, 1, 1);
        printInfoPane.add(dueDatePicker, 1, 2);
        printInfoPane.add(studentComboBox, 1, 3);
        printInfoPane.add(commentsTextArea, 1, 4);
        printInfoPane.add(stlPathBrowseButton, 2, 0);
        printInfoPane.add(newStudentButton, 2, 3);

        getDialogPane().getButtonTypes().addAll(add, cancel);
        getDialogPane().setContent(printInfoPane);

        setResultConverter((ButtonType button) -> {
            if (button.equals(add)) {
                PrintType type = PrintType.ASSIGNMENT;
                switch (printTypeComboBox.getSelectionModel().getSelectedIndex()) {
                    case 0:
                        type = PrintType.ASSIGNMENT;
                        break;
                    case 1:
                        type = PrintType.TEAM_PROJECT;
                        break;
                    case 2:
                        type = PrintType.PERSONAL;
                        break;
                }

                Instant instant = Instant.from(dueDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()));
                Date date = Date.from(instant);
                
                
                PrintJob newJob = new PrintJob(stlPathTextField.getText(), type, date, PrintStatus.READY_TO_PRINT, commentsTextArea.getText(), (Student) studentComboBox.getSelectionModel().getSelectedItem());
                return newJob;
            }
            return null;
        });
    }

    private void setDueDate(LocalDate selectedDate) {
        dueDate = Date.from(selectedDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }
   
}
