package xmlProject;

import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;

public class CustomTab extends Tab{

    private Controller mainWindowController;

    // Merken der Stelle, wo mit Rechtsklick das Menü aufgerufen wurde, um dort dann den neuen Node zu platzieren
    private Point2D point2D = new Point2D(0,0);

    private static final int offsetX = 100;
    private static final int offsetY = 60;

    private int height = 1;

    private AnchorPane treeContent;

    private XMLBuilder xmlBuilder;

    // Aus Schema/DTD oder vorhandener xml-Datei definierte Tags speichern
    private ArrayList xmlNodes = new ArrayList<String>();

    public CustomTab(Controller c, String text) {
        super(text);
        mainWindowController = c;

        xmlBuilder = new XMLBuilder();
        System.out.println("new CustomTab");
        TabPane tabPane = new TabPane();
        tabPane.setSide(Side.BOTTOM);
        tabPane.setPadding(new Insets(0,0,0,0));
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab treeTab = new Tab("Tree");

        ScrollPane scrollPane = new ScrollPane();

        treeTab.setContent(scrollPane);

        /*ScrollBar scrollBarV = new ScrollBar();
        scrollBarV.setMin(0);

        scrollBarV.setOrientation(Orientation.VERTICAL);*/

        treeContent = new AnchorPane();

        scrollPane.setContent(treeContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);


        //treeContent.getChildren().add(scrollBarV);

        //kein Effekt?
        /*scrollBarV.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number old_val, Number new_val) {
                treeContent.setLayoutY(-new_val.doubleValue());
            }
        });


        AnchorPane.setTopAnchor(scrollBarV,0.0);
        AnchorPane.setRightAnchor(scrollBarV,0.0);
        AnchorPane.setBottomAnchor(scrollBarV,11.0);
        //treeContent.setLeftAnchor(scrollBarV,0.0);*/

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

                    if (source != null && target != null) {
                        link.bindEnds(source, target);
                        source.getElement().appendChild(target.getElement());
                    }
                }

            }

            event.consume();
        });
        treeTab.setContent(treeContent);
        tabPane.getTabs().addAll(treeTab, new Tab("Code"));

        AnchorPane pane = new AnchorPane();
        pane.getChildren().add(tabPane);
        AnchorPane.setBottomAnchor(tabPane,0.0);
        AnchorPane.setTopAnchor(tabPane,0.0);
        AnchorPane.setRightAnchor(tabPane,0.0);
        AnchorPane.setLeftAnchor(tabPane,0.0);

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

            DraggableNode node = new DraggableNode(mainWindowController, xmlBuilder);
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
            //showXML(root,0);
            showXML(root);
        });

        contextMenu.getItems().add(item1);

        this.setContent(pane);


    }

    public CustomTab(Controller c, String text, File file) {
        this(c, text);
        //xmlBuilder = new XMLBuilder(file);
        xmlBuilder.readFile(file);
    }

    public XMLBuilder getXmlBuilder() {
        return xmlBuilder;
    }

    public void setSchema(File schema) {
        xmlBuilder.setSchema(schema);
    }

    public DraggableNode showXML(Element root, int depth) {
        Node currElement = root;
        DraggableNode rootDraggableNode = null;

        while(currElement!=null) {
            Node children = currElement.getFirstChild();
            NodeList childrenList = currElement.getChildNodes();

            if (currElement instanceof Element){
                String name = ((Element) currElement).getTagName();
                System.out.println("Tagname: " + name);
                DraggableNode dNode = new DraggableNode(mainWindowController, xmlBuilder);
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
                    if(children.getNodeType() != Node.ELEMENT_NODE)

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

    public void showXML(Element root) {
        if(root!=null) {
            point2D.add(20, 20);
            String label = root.getTagName();
            DraggableNode node = new DraggableNode(mainWindowController, xmlBuilder);
            node.tabContent = treeContent;
            node.setLabel(label);

            treeContent.getChildren().add(node);
            node.relocateToPoint(point2D);

            node.setElement(root);
            node.xmlBuilder = xmlBuilder;

            if(root.hasChildNodes()) {
                NodeList children = root.getChildNodes();
                ArrayList<DraggableNode> draggableNodes = showChildNodes(children,1);
                for(int i=0; i<draggableNodes.size(); i++) {
                    NodeLink link = new NodeLink();
                    link.bindEnds(node, draggableNodes.get(i));
                    treeContent.getChildren().add(0,link);
                }
            }
        }
    }

    private ArrayList<DraggableNode> showChildNodes(NodeList list, int depth) {
        ArrayList<DraggableNode> draggableNodes = new ArrayList<>();
        //if(list==null) return;
        DraggableNode dNode = null;
        for(int i = 0; i < list.getLength(); i++) {
            //height++;
            Node currentNode = list.item(i);
            if(currentNode.getNodeType() == Node.ELEMENT_NODE) {
                String label = ((Element) currentNode).getTagName();
                dNode = new DraggableNode(mainWindowController, xmlBuilder);
                dNode.tabContent = treeContent;
                dNode.setLabel(label);

                dNode.xmlBuilder = xmlBuilder;
                dNode.setElement((Element)currentNode);

                treeContent.getChildren().add(dNode);

                //Atttribute in Tabelle anzeigen
                if(currentNode.hasAttributes()) {
                    //Tabelle
                    NamedNodeMap attributeMap = currentNode.getAttributes();
                    for(int j=0; j<attributeMap.getLength(); j++) {
                        Node attribute = attributeMap.item(j);
                        String attrName = attribute.getNodeName();
                        String attrValue = attribute.getNodeValue();
                        //Attribute a = new Attribute();
                        dNode.tableViewContent.addRow(attrName,attrValue);

                    }
                }

                // An Position schieben
                Point2D point = new Point2D(point2D.getX() + depth*offsetX, point2D.getY() + height*offsetY);
                height++;

                dNode.relocateToPoint(point);
                draggableNodes.add(dNode);


                if(currentNode.hasChildNodes()) {
                    ArrayList<DraggableNode> children = showChildNodes(currentNode.getChildNodes(), depth+1);
                    for(int k = 0; k < children.size(); k++) {
                        NodeLink link = new NodeLink();
                        link.bindEnds(dNode,children.get(k));
                        treeContent.getChildren().add(0,link);
                    }
                }


            }
            else if(currentNode.getNodeType() == Node.TEXT_NODE) {
                // neues Text-Knoten-Fenster?
            }

        }
        return draggableNodes;
    }

}
