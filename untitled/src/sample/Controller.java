package sample;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Controller extends Application{

    @FXML
    private TreeView<String> projectTreeView;

    //Ordnersymbol der Projekts
    private final Node rootProjectImg = new ImageView(
            new Image(getClass().getResourceAsStream(""))
    );


    @Override
    public void start(Stage primaryStage) throws Exception {

        //Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

        TreeItem<String> rootProject = new TreeItem<String>();


    }
}
