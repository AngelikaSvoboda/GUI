package sample;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.*;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.IOException;

public class Controller{

    @FXML private TreeView<String> projectTreeView;
    @FXML private MenuItem openProjectMenu;
    @FXML private MenuItem openFileMenu;

    @FXML private TabPane tabPane;
    @FXML private AnchorPane rightSidePanel;

    @FXML private TableView nodeContentTableView;

    public DraggableNode focusedNode;

    public Controller(){

    }

    @FXML public void initialize() {

    }

    //Ordnersymbol der Projekts
    private final Node rootProjectImg = new ImageView(
            new Image(getClass().getResourceAsStream(""))
    );


    /**
     * Nach dem Auswählen des Menüpunkts Datei->Neu->XML wird ein neues Fenster zur Bearbeitung
     * der XML geöffnet, sowie das Projektverzeichnis auf der linken Seite aktualisiert
     */
    public void handleNewProject() {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("newFileDialog.fxml"));
            //fxmlLoader.setLocation();

            Scene scene = new Scene(fxmlLoader.load(), 320, 110);
            Stage stage = new Stage();
            stage.setTitle("New XML File");
            stage.initStyle(StageStyle.UTILITY);
            stage.setScene(scene);
            stage.show();

            NewFileDialog dialogController = fxmlLoader.getController();

            stage.setOnHidden(event -> {
                String fileText = dialogController.getFileText();
                System.out.println(dialogController.isWindowCancelled());

                if(!dialogController.isWindowCancelled()) {

                    System.out.println("Neue Datei");
                    CustomTab tab = new CustomTab(fileText);
                    tab.setClosable(true);
                    tabPane.getTabs().add(tab);

                    // Einbinden einer Schema aktiviert und Datei ausgewählt -> Schema validieren
                    if(dialogController.isCheckBoxEnabled() &&
                            !dialogController.getFileText().isEmpty()) {
                        System.out.println("Neue Datei mit Schema");
                        File schema = new File(dialogController.getSchemaFilePath());


                    }
                    // Ohne Schema
                    else {

                    }

                }
                if(event.getSource() instanceof Button) {
                    Button button = (Button) event.getSource();
                    if(button.isCancelButton()) {
                        System.out.println("Abbrechen");
                    }
                }

            });
        }
        catch (IOException e) {
            e.printStackTrace();

        }
    }

    /**
     * Nach dem Auswählen des Menpunkts Datei->Öffnen wird ein Dialog zum wählen eines
     * Verzeichnisses geöffnet, worauf dann beim Wählen eines Projektordners das Projektverzeichnis
     * auf der linken Seite aktualisiert wird.
     */
    public void handleOpenProject() {
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
            //CustomTab tab = new CustomTab();

        }

    }

    public void handleOpenFile() {
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File(System.getProperty("user.home")));
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML-File .xml" , "*.xml"));
        File choice = fc.showOpenDialog(openFileMenu.getParentPopup().getScene().getWindow());

        if(choice != null) {
            CustomTab tab = new CustomTab(choice.getName());
            XMLBuilder builder = new XMLBuilder();
            Element root = builder.readFile(choice);

            tabPane.getTabs().add(tab);
            tab.showXML(root);
        }

    }

    public void handleMouseClicked() {
        rightSidePanel.setOnMouseClicked((event) -> {
                if(event.getButton() == MouseButton.SECONDARY) {
                    //Rectangle rect = new Rectangle(50,30);
                    //rightSidePanel.getChildren().add(rect);
                    ContextMenu menu = new ContextMenu();
                    MenuItem item = new MenuItem("Neuer Knoten");
                    item.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            DraggableNode node = new DraggableNode();
                            rightSidePanel.getChildren().add(node);
                        }
                    });
                    menu.getItems().addAll();
                    menu.show(rightSidePanel, event.getScreenX(), event.getSceneY());

                }});

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
