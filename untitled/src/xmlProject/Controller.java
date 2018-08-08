package xmlProject;

import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.*;
import javafx.util.Callback;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Controller{

    @FXML private TreeView<String> projectTreeView;
    @FXML private MenuItem openProjectMenu;
    @FXML private MenuItem openFileMenu;

    @FXML private TabPane tabPane;
    @FXML private VBox rightSidePanel;

    @FXML TableView<Attribute> nodeContentTableView;
    @FXML private TableColumn tableColumnAttribute;
    @FXML private TableColumn tableColumnValue;

    @FXML private Button addAttributeButton;
    @FXML private Button editAttributeButton;
    @FXML private Button deleteAttributeButton;

    private EventHandler<TableColumn.CellEditEvent<Attribute, String>> handleEditCell;

    public DraggableNode focusedNode;

    public Controller(){

    }

    @FXML public void initialize() {

        tableColumnAttribute.setPrefWidth(100);

        tableColumnValue.prefWidthProperty().bind(
                nodeContentTableView.widthProperty().subtract(
                        tableColumnAttribute.widthProperty().subtract(2)
                        )
                );

        handleEditCell = new EventHandler<TableColumn.CellEditEvent<Attribute, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Attribute, String> t) {
                ((Attribute) t.getTableView().getItems().get(
                        t.getTablePosition().getRow())
                ).setAttribute(t.getNewValue());
            }
        };

        tableColumnAttribute.setCellValueFactory(new PropertyValueFactory<Attribute,String>("Attribute"));
        tableColumnValue.setCellValueFactory(new PropertyValueFactory<Attribute,String>("Value"));

        Callback<TableColumn, TableCell> cellFactory =
                new Callback<TableColumn, TableCell>() {
                    public TableCell call(TableColumn p) {
                        return new EditCell();
                    }
                };



        tableColumnAttribute.setCellFactory(TextFieldTableCell.forTableColumn());
        //tableColumnAttribute.setCellFactory(new TextFieldCellFactory());
        tableColumnAttribute.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Attribute, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Attribute, String> t) {
                        Attribute a = t.getTableView().getItems().get(
                                t.getTablePosition().getRow());
                        a.setAttribute(t.getNewValue());
                        System.out.println("" + t.getNewValue());
                        focusedNode.tableViewContent.setAttribute(t.getTablePosition().getRow(), t.getNewValue());
                    }
                }
        );


        //tableColumnValue.setCellFactory(new TextFieldCellFactory());
        tableColumnValue.setCellFactory(TextFieldTableCell.forTableColumn());
        tableColumnValue.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Attribute, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Attribute, String> t) {
                        Attribute a = t.getTableView().getItems().get(
                                t.getTablePosition().getRow());
                        ((Attribute) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setValue(t.getNewValue());

                        focusedNode.tableViewContent.setValue(t.getTablePosition().getRow(), t.getNewValue());
                    }
                }
        );

                Image image = new Image(getClass().getResourceAsStream("/Images/addButton1.png"));
        addAttributeButton.setGraphic(new ImageView(image));
        Image image1 = new Image(getClass().getResourceAsStream("/Images/editButton1.png"));
        editAttributeButton.setGraphic(new ImageView(image1));
        Image image2 = new Image(getClass().getResourceAsStream("/Images/deleteButton1.png"));
        deleteAttributeButton.setGraphic(new ImageView(image2));

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

            Scene scene = new Scene(fxmlLoader.load(), 405, 148);
            scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
            Stage stage = new Stage();
            stage.setTitle("New XML File");
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();

            NewFileDialog dialogController = fxmlLoader.getController();

            stage.setOnHidden(event -> {
                String fileText = dialogController.getFileText();
                System.out.println(dialogController.isWindowCancelled());

                if(!dialogController.isWindowCancelled()) {

                    if(!fileText.endsWith(".xml")) fileText +=".xml";
                    CustomTab tab;
                    DraggableNode root;


                    // Einbinden einer Schema aktiviert und Datei ausgewählt -> Schema validieren
                    if(dialogController.isCheckBoxEnabled() &&
                            !dialogController.getFileText().isEmpty()) {
                        System.out.println("Neue Datei mit Schema");
                        File schema = new File(dialogController.getSchemaFilePath());
                        tab = new CustomTab(Controller.this, fileText, schema);
                        root = new DraggableNode(this, tab.getXmlBuilder(), dialogController.getRootTextField());
                        //File schema = new File(dialogController.getSchemaFilePath());
                        //tab = new CustomTab(Controller.this, fileText);
                        tab.setSchema(schema);

                    }
                    // Ohne Schema
                    else {
                        System.out.println("Neue Datei");
                        tab = new CustomTab(Controller.this,fileText);
                        root = new DraggableNode(this, tab.getXmlBuilder(), dialogController.getRootTextField());
                    }

                    root.setLabel(dialogController.getRootTextField());
                    root.tabContent = tab.treeContent;
                    root.setRoot();
                    tab.getXmlBuilder().setRoot(root.getElement());

                    tab.treeContent.getChildren().add(root);

                    tab.setClosable(true);
                    tabPane.getTabs().add(tab);


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
            alert.setHeaderText("Verzeichnis konnte nicht geöffnet werden.");
            alert.setContentText("Die Verzeichnis ist ungültig.");

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
            CustomTab tab = new CustomTab(Controller.this, choice.getName(), choice);
            XMLBuilder builder =tab.getXmlBuilder();
            Element root = builder.readFile(choice);

            tabPane.getTabs().add(tab);
            tab.showXML(root);
        }

    }

    private CustomTab getOpenedTab() {
        SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
        CustomTab tab = null;
        Tab openendTab = selectionModel.getSelectedItem();
        if(openendTab instanceof CustomTab) {
            tab = (CustomTab) openendTab;
        }
        return tab;
    }

    public void handleSave() {
        CustomTab tab = getOpenedTab();
        if(tab !=null) {
            XMLBuilder b = tab.getXmlBuilder();
            String s =b.saveFile(getOpenedTab().getText(), openFileMenu.getParentPopup().getScene().getWindow());
            if(!s.equals(tab.getText()))
                tab.setText(s);
        }


    }
    public void handleSaveAs(){
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Speichern unter");
        CustomTab tab = getOpenedTab();
        if(tab!=null) {
            File originalFile = getOpenedTab().getXmlBuilder().getXmlFile();
            chooser.setInitialDirectory(originalFile);
            chooser.setInitialFileName(tab.getText());
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML-File .xml" , "*.xml"));
            File choice = chooser.showSaveDialog(openProjectMenu.getParentPopup().getScene().getWindow());
            if (choice != null) {
                tab.getXmlBuilder().saveFileAs(choice);
            }
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Speichern fehlgeschlagen");
            alert.setContentText("Es ist keine Datei zum Speichern geöffnet.");

            alert.showAndWait();
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
                            DraggableNode node = new DraggableNode(Controller.this, getOpenedTab().getXmlBuilder(), "Label");

                            rightSidePanel.getChildren().add(node);
                        }
                    });
                    menu.getItems().addAll();
                    menu.show(rightSidePanel, event.getScreenX(), event.getSceneY());

                }});


        }

    public void showTable() {
        ObservableList<Attribute> list =  focusedNode.tableViewContent.getRows();
        ObservableList items = nodeContentTableView.getItems();
        if(list.size()==0) {
            nodeContentTableView.setPlaceholder(new Label("Keine Attribute"));
        }
        nodeContentTableView.setItems(list);

    }
    public void emptyTable() {
        nodeContentTableView.getItems().removeAll(nodeContentTableView.getItems());
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

    public void handleAddAttribute(ActionEvent actionEvent) {


        focusedNode.tableViewContent.addRow("attr", "val");
        focusedNode.getElement().setAttribute("attr", "val");
        nodeContentTableView.setItems(focusedNode.tableViewContent.getRows());
    }

    public void handleEditAttribute(ActionEvent actionEvent) {
    }

    public void handleDeleteAttribute(ActionEvent actionEvent) {
        Attribute a = nodeContentTableView.getSelectionModel().getSelectedItem();
        focusedNode.tableViewContent.deleteRow(nodeContentTableView.getSelectionModel().getFocusedIndex());
        nodeContentTableView.getItems().remove(a);
    }

    public void handleStartAdler() {
        /*ProcessBuilder pb = new ProcessBuilder("java", "-jar", "adler/adler_v1.jar");
        File file = new File("C:\\Program Files (x86)\\Java\\jre1.8.0_171\\bin");
        //pb.directory(new File("adler"));
        pb.directory(file);
        try {
            Process p = pb.start();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        System.out.println("Adler starten");
        File file = new File("C:\\Users\\Angi\\IdeaProjects\\untitled\\src\\adler\\adler_v1.jar");
        try {
            Desktop.getDesktop().open(file);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(file.getAbsolutePath());

        /*
        try {
            Runtime.getRuntime().exec("java -jar " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        /*try {
            File file = new File("adler/adler_v1.jar");
            Runtime.getRuntime().exec("java", new String[] {"-jar"}, file);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
}
