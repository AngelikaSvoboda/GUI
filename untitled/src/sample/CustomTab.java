package sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;


public class CustomTab extends Tab{

    // Merken der Stelle, wo mit Rechtsklick das Menü aufgerufen wurde, um dort dann den neuen Node zu platzieren
    Point2D point2D = new Point2D(0,0);

    public CustomTab(String text) {
        super(text);
        System.out.println("new CustomTab");
        TabPane tabPane = new TabPane();
        tabPane.setSide(Side.BOTTOM);
        tabPane.setPadding(new Insets(0,0,0,0));
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab treeTab = new Tab("Baum");
        AnchorPane treeContent = new AnchorPane();
        treeContent.setOnDragDone(event -> {

            DragContainer container =
                    (DragContainer) event.getDragboard().getContent(DragContainer.AddNode);

            /*if (container != null) {
                if (container.getValue("scene_coords") != null) {

                        DraggableNode node = new DraggableNode();

                        //node.setType(DragIconType.valueOf(container.getValue("type")));
                        treeContent.getChildren().add(node);

                        Point2D cursorPoint = container.getValue("scene_coords");

                        node.relocateToPoint(
                                new Point2D(cursorPoint.getX() - 32, cursorPoint.getY() - 32)
                        );

                }
            }*/

            //AddLink drag operation
            container =
                    (DragContainer) event.getDragboard().getContent(DragContainer.AddLink);

            if (container != null) {

                //bind the ends of our link to the nodes whose id's are stored in the drag container
                String sourceId = container.getValue("source");
                String targetId = container.getValue("target");

                if (sourceId != null && targetId != null) {

                    //	System.out.println(container.getData());
                    NodeLink link = new NodeLink();

                    //add our link at the top of the rendering order so it's rendered first
                    treeContent.getChildren().add(0,link);

                    DraggableNode source = null;
                    DraggableNode target = null;

                    for (Node n: treeContent.getChildren()) {

                        if (n.getId() == null)
                            continue;

                        if (n.getId().equals(sourceId))
                            source = (DraggableNode) n;

                        if (n.getId().equals(targetId))
                            target = (DraggableNode) n;

                    }

                    if (source != null && target != null)
                        link.bindEnds(source, target);
                }

            }

            event.consume();
        });
        treeTab.setContent(treeContent);
        tabPane.getTabs().addAll(treeTab, new Tab("Code"));
        AnchorPane pane = new AnchorPane();
        pane.getChildren().add(tabPane);

        pane.setBottomAnchor(tabPane,0.0);
        pane.setTopAnchor(tabPane,0.0);
        pane.setRightAnchor(tabPane,0.0);
        pane.setLeftAnchor(tabPane,0.0);


        /*treeContent.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getButton() == MouseButton.SECONDARY) {
                    ContextMenu menu = new ContextMenu();
                    MenuItem item = new MenuItem("Neuer Knoten");
                    item.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event1) {
                            DraggableNode node = new DraggableNode();
                            Point2D point = new Point2D(event.getSceneX(), event.getSceneY());
                            treeContent.getChildren().add(node);
                            node.relocateToPoint(point);
                            //TODO neues XML Element erstellen
                        }
                    });
                    menu.getItems().add(item);

                    menu.show(pane, event.getScreenX(), event.getScreenY());

                }
            }
        });*/

        ContextMenu contextMenu = new ContextMenu();

        treeContent.addEventHandler(ContextMenuEvent.CONTEXT_MENU_REQUESTED, event -> {
            point2D = new Point2D(event.getSceneX(), event.getSceneY());
            contextMenu.show(treeContent, event.getScreenX(), event.getScreenY());
            event.consume();
        });

        treeContent.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            contextMenu.hide();
        });

        MenuItem item = new MenuItem("Neuer Knoten");
        item.setOnAction(event -> {
                DraggableNode node = new DraggableNode();
                //Point point = MouseInfo.getPointerInfo().getLocation();
                treeContent.getChildren().add(node);

                //node.relocateToPoint(new Point2D(point.getX(),point.getY()));
                node.relocateToPoint(point2D);

                //TODO neues XML Element erstellen
        });
        contextMenu.getItems().add(item);

        this.setContent(pane);


    }

}
