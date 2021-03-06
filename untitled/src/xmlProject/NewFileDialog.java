package xmlProject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.*;

import java.io.File;


public class NewFileDialog{

    private boolean checkBoxValue = false;

    private boolean windowCancelled = true;

    //@FXML AnchorPane rootNewFileDialog;
    @FXML private TextField fileTextField;
    @FXML private TextField rootTextField;

    private String schemaFilePath;

    @FXML private CheckBox schemaCheckBox;

    @FXML private Button selectSchemaButton;
    @FXML private Button cancelButton;
    @FXML private Button createFileButton;

    private File choice;

    public NewFileDialog() {

    }

    public void initialize() {
        checkBoxValue = schemaCheckBox.isSelected();
        selectSchemaButton.setDisable(true);

        fileTextField.textProperty().addListener(observable -> {
            fileTextField.getStyleClass().remove("error");
        });
        rootTextField.textProperty().addListener(observable -> {
            rootTextField.getStyleClass().remove("error");
        });

    }
    public boolean isWindowCancelled() {
        return windowCancelled;
    }

    public String getFileText() {
        return fileTextField.getText();
    }

    public String getSchemaFilePath() {
        return schemaFilePath;
    }

    public boolean isCheckBoxEnabled(){
        return schemaCheckBox.isSelected();
    }

    public void setFileTextField(String text) {
        fileTextField.setText(text);
    }

    public String getRootTextField() { return rootTextField.getText(); }

    public void handleCheckBox(ActionEvent actionEvent) {
        schemaCheckBox.setSelected(!checkBoxValue);


        selectSchemaButton.setDisable(checkBoxValue);
        checkBoxValue = !checkBoxValue;
    }

    public void handleSelectSchema(ActionEvent actionEvent) {
        FileChooser chooser = new FileChooser();

        chooser.setInitialDirectory(new File(System.getProperty("user.home")));
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Schema Files", "*.xsd"),
                new FileChooser.ExtensionFilter("DTD Files", "*.dtd"));

        choice = chooser.showOpenDialog(selectSchemaButton.getParent().getScene().getWindow());

        if(choice == null) {

        } else {
            schemaFilePath = choice.getAbsolutePath();

            selectSchemaButton.setText(choice.getName());

            System.out.println("öffne Datei "+ choice.getAbsolutePath());

        }
    }


    public void handleCancel(ActionEvent actionEvent) {
        windowCancelled=true;
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    public void handleCreateFile(ActionEvent actionEvent) {
        windowCancelled = false;

        if(getFileText().isEmpty() || getRootTextField().isEmpty()) {
            if(getFileText().isEmpty()) fileTextField.getStyleClass().add("error");
            if(getRootTextField().isEmpty()) rootTextField.getStyleClass().add("error");
        }

        // Schema einlesen und Elemente in das Contextmenu aufnehmen
        if(checkBoxValue) {

        }
        // ohne Schema: leeres XML erzeugen, Schema anlegen(?)
        else if(!getFileText().isEmpty() && !getRootTextField().isEmpty()){
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            stage.close();

        }

    }
}
