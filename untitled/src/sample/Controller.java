package sample;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

public class Controller{

    @FXML private TreeView<String> projectTreeView;
    @FXML private MenuItem openProjectMenu;

    @FXML private TabPane tabPane;

    //Ordnersymbol der Projekts
    private final Node rootProjectImg = new ImageView(
            new Image(getClass().getResourceAsStream(""))
    );


    /**
     * Nach dem Auswählen des Menüpunkts Datei->Neu->XML wird ein neues Fenster zur Bearbeitung
     * der XML geöffnet, sowie das Projektverzeichnis auf der linken Seite aktualisiert
     */
    public void handleNewProject() {
        Tab tab = new CustomTab("new.xml");
        tab.setClosable(true);

        tabPane.getTabs().add(tab);
    }

    /**
     * Nach dem Auswählen des Menpunkts Datei->Öffnen wird ein Dialog zum wählen eines
     * Verzeichnisses geöffnet, worauf dann beim Wählen eines Projektordners das Projektverzeichnis
     * auf der linken Seite aktualisiert wird.
     */
    public void handleOpenProject() {
        projectTreeView = new TreeView<String>();
        DirectoryChooser dc = new DirectoryChooser();
        dc.setInitialDirectory(new File(System.getProperty("user.home")));
        File choice = dc.showDialog(openProjectMenu.getParentPopup().getScene().getWindow());
        if(choice == null || ! choice.isDirectory()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Could not open directory");
            alert.setContentText("The file is invalid.");

            alert.showAndWait();
        } else {
            projectTreeView.setRoot(getNodesForDirectory(choice));

        }

    }

    public TreeItem<String> getNodesForDirectory(File directory) { //Returns a TreeItem representation of the specified directory
        TreeItem<String> root = new TreeItem<String>(directory.getName());
        for(File f : directory.listFiles()) {
            System.out.println("Loading " + f.getName());
            if(f.isDirectory()) { //Then we call the function recursively
                root.getChildren().add(getNodesForDirectory(f));
            } else {
                root.getChildren().add(new TreeItem<String>(f.getName()));
            }
        }
        return root;
    }

}
