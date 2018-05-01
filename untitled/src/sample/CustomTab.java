package sample;

import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;


public class CustomTab extends Tab{


    // Merken der Stelle, wo mit Rechtsklick das Menü aufgerufen wurde, um dort dann den neuen Node zu platzieren
    private Point2D point2D = new Point2D(0,0);

    private static final int offsetX = 100;
    private static final int offsetY = 60;

    private int height = 0;

    private AnchorPane treeContent;

    // Aus Schema/DTD oder vorhandener xml-Datei definierte Tags speichern
    private ArrayList xmlNodes = new ArrayList<String>();

    public CustomTab(String text) {
        super(text);
        System.out.println("new CustomTab");
        TabPane tabPane = new TabPane();
        tabPane.setSide(Side.BOTTOM);
        tabPane.setPadding(new Insets(0,0,0,0));
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab treeTab = new Tab("Baum");
        treeContent = new AnchorPane();
        treeContent.setOnDragDone(event -> {

            //AddLink drag operation
            DragContainer container =
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

                    for (javafx.scene.Node n: treeContent.getChildren()) {

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
            node.tabContent=treeContent;

            //Point point = MouseInfo.getPointerInfo().getLocation();
            treeContent.getChildren().add(node);


            //node.relocateToPoint(new Point2D(point.getX(),point.getY()));
            node.relocateToPoint(point2D);

            //TODO neues XML Element erstellen
        });
        contextMenu.getItems().add(item);

        MenuItem item1 = new MenuItem("Testdatei");
        item1.setOnAction(event -> {
            XMLBuilder builder = new XMLBuilder();
            Element root = builder.createTestXML();
            showXML(root,0);
        });

        contextMenu.getItems().add(item1);

        this.setContent(pane);


    }

    private DraggableNode showXML(Element root, int depth) {
        Node currElement = root;
        DraggableNode rootDraggableNode = null;

        while(currElement!=null) {
            Node children = currElement.getFirstChild();
            NodeList childrenList = currElement.getChildNodes();

            if (currElement instanceof Element){
                String name = ((Element) currElement).getTagName();
                System.out.println("Tagname: " + name);
                DraggableNode dNode = new DraggableNode();
                dNode.tabContent = treeContent;
                if(currElement.hasAttributes()) {
                    // Attribute anfügen als Knoten/in Tabelle?
                }
                dNode.setLabel(name);

                treeContent.getChildren().add(dNode);


                Point2D point = new Point2D(point2D.getX() + depth*offsetX, point2D.getY() + height*offsetY);
                height++;
                dNode.relocateToPoint(point);
                if(children!=null) {

                    //childrenList.getLength();
                    rootDraggableNode = showXML((Element) children, depth+1);
                    NodeLink link = new NodeLink();

                    //link.setStart(new Point2D(dNode.rightDragPane.getLayoutX(),dNode.rightDragPane.getLayoutY()));
                    //link.setEnd(new Point2D(rootDraggableNode.leftDragPane.getLayoutX(), rootDraggableNode.leftDragPane.getLayoutY()));
                    link.bindEnds(dNode,rootDraggableNode);

                    treeContent.getChildren().add(0,link);
                    //link.setVisible(true);

                }
                else {
                    rootDraggableNode = dNode;
                }


            }
            currElement = currElement.getNextSibling(); //nächstes Kind
        }
        return rootDraggableNode;
    }

}
