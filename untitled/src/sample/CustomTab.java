package sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;


public class CustomTab extends Tab{

    public CustomTab(String text) {
        super(text);
        System.out.println("new CustomTab");
        TabPane tabPane = new TabPane();
        tabPane.setSide(Side.BOTTOM);
        tabPane.setPadding(new Insets(0,0,0,0));
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        Tab treeTab =new Tab("Baum");
        tabPane.getTabs().addAll(treeTab, new Tab("Code"));
        AnchorPane pane = new AnchorPane();
        pane.getChildren().add(tabPane);
        pane.setBottomAnchor(tabPane,0.0);
        pane.setTopAnchor(tabPane,0.0);
        pane.setRightAnchor(tabPane,0.0);
        pane.setLeftAnchor(tabPane,0.0);

        tabPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getButton() == MouseButton.SECONDARY) {
                    ContextMenu menu = new ContextMenu();
                    MenuItem item = new MenuItem("Neuer Knoten");
                    item.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            DraggableNode node = new DraggableNode();
                            treeTab.setContent(node);
                            treeTab.setContent(new Rectangle(20,10));

                            System.out.println("new Node");
                        }
                    });
                    menu.getItems().add(item);

                    menu.show(pane, event.getScreenX(), event.getScreenY());

                }
            }
        });
        this.setContent(pane);


    }
    private void handleRightClick() {

    }
}
