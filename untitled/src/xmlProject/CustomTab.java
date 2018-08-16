package xmlProject;

import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.control.MenuItem;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.awt.event.KeyListener;
import java.io.File;
import java.security.Key;
import java.util.ArrayList;

public class CustomTab extends Tab{

    private Controller mainWindowController;

    // Merken der Stelle, wo mit Rechtsklick das Menü aufgerufen wurde, um dort dann den neuen Node zu platzieren
    private Point2D point2D = new Point2D(0,0);

    private static final int offsetX = 100;
    private static final int offsetY = 60;

    private int height = 1;
    private int maxdepth = 1;

    AnchorPane treeContent;

    private XMLBuilder xmlBuilder;

    // Aus Schema/DTD oder vorhandener xml-Datei definierte Tags speichern
    private ArrayList xmlNodes = new ArrayList<String>();

    // Neue leere xml-Datei
    public CustomTab(Controller c, String text) {
        super(text);
        mainWindowController = c;
        setOnClosed(event -> {
            mainWindowController.emptyTable();
        });

        xmlBuilder = new XMLBuilder();
        //xmlBuilder.setXmlFile(new File(text));

        System.out.println("new CustomTab");
        TabPane tabPane = new TabPane();
        tabPane.setSide(Side.BOTTOM);
        tabPane.setPadding(new Insets(0,0,0,0));
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab treeTab = new Tab("Tree");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        treeTab.setContent(scrollPane);

        /*ScrollBar scrollBarV = new ScrollBar();
        scrollBarV.setMin(0);

        scrollBarV.setOrientation(Orientation.VERTICAL);*/

        treeContent = new AnchorPane();


        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        scrollPane.setPannable(true);

        scrollPane.setContent(treeContent);

        //treeContent.getChildren().add(scrollBarV);


        AnchorPane.setTopAnchor(scrollPane,0.0);
        AnchorPane.setRightAnchor(scrollPane,0.0);
        AnchorPane.setBottomAnchor(scrollPane,0.0);
        AnchorPane.setLeftAnchor(scrollPane, 0.0);
        //treeContent.setLeftAnchor(scrollBarV,0.0);

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
                        System.out.println("kindknoten anhängen");
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

            DraggableNode node = new DraggableNode(mainWindowController, xmlBuilder, "Label");
            node.tabContent=treeContent;

            treeContent.getChildren().add(node);
            node.relocateToPoint(point2D);

        });
        MenuItem chapter = new MenuItem("chapter");
        chapter.setOnAction(event1 -> {
            DraggableNode node = new DraggableNode(mainWindowController, xmlBuilder, "chapter");
            node.tableViewContent.addRow("nr","1");
            node.linkedAttributes.add(new Attribute("nr", "1"));
            node.getElement().setAttribute("nr", "1");
            node.tabContent=treeContent;
            treeContent.getChildren().add(node);
            node.relocateToPoint(point2D);
        });
        MenuItem page = new MenuItem("page");
        page.setOnAction(event1 -> {
            DraggableNode node = new DraggableNode(mainWindowController, xmlBuilder, "page");
            node.tableViewContent.addRow("title","");
            node.linkedAttributes.add(new Attribute("title", ""));
            node.getElement().setAttribute("title", "");
            node.tabContent=treeContent;
            treeContent.getChildren().add(node);
            node.relocateToPoint(point2D);
        });
        MenuItem content = new MenuItem("content");
        content.setOnAction(event1 -> {
            DraggableNode node = new DraggableNode(mainWindowController, xmlBuilder, "content");
            node.tableViewContent.addRow("type","0");
            node.linkedAttributes.add(new Attribute("type", "0"));
            node.getElement().setAttribute("type", "0");
            node.tabContent=treeContent;
            treeContent.getChildren().add(node);
            node.relocateToPoint(point2D);
        });
        /*if(mainWindowController.getCopyElement() != null) {
            MenuItem copy = new MenuItem("Einfügen");
            copy.setOnAction(event -> {
                DraggableNode n = mainWindowController.getCopyElement();
                treeContent.getChildren().add(n);
                n.relocateToPoint(point2D);
            });
            contextMenu.getItems().add(copy);
        }*/

        contextMenu.getItems().addAll(item, chapter, page, content);

        /*MenuItem item1 = new MenuItem("Testdatei");
        item1.setOnAction(event -> {
            XMLBuilder builder = new XMLBuilder();
            Element root = builder.createTestXML();
            //showXML(root,0);
            showXML(root);
        });

        contextMenu.getItems().add(item1);*/

        this.setContent(pane);

        /*
        KeyCombination copyKeys = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN);

        treeContent.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if(copyKeys.match(event)) {
                MenuItem copy = new MenuItem("Einfügen");
                copy.setOnAction(event1 -> {
                    DraggableNode n = mainWindowController.getCopyElement();
                    treeContent.getChildren().add(n);
                    n.relocateToPoint(point2D);
                });
                contextMenu.getItems().add(copy);

                System.out.println("Kopieren");
            }

        });*/

    }

    // Öffnen einer xml-Datei oder neue leere Datei mit Schema/DTD
    public CustomTab(Controller c, String text, File file) {
        this(c, text);
        if(file.getName().endsWith(".xml")) {
            xmlBuilder = new XMLBuilder(file);
            xmlBuilder.readFile(file);
        }
        else {
            //xmlBuilder = new XMLBuilder(); // Bereits im oberen Konstruktor
            //xmlBuilder = new XMLBuilder(null, file);
            xmlBuilder.setSchema(file);
            xmlBuilder.validateSchema(file);

        }
    }
    // Öffnen einer Datei mit Schema/DTD
    public CustomTab(Controller c, String text, File file, File schema) {
        this(c, text);
        //xmlBuilder = new XMLBuilder(file, schema);
        xmlBuilder.setSchema(schema);
        //xmlBuilder.validateSchema(schema);
        xmlBuilder.readFile(file);
    }

    public XMLBuilder getXmlBuilder() {
        return xmlBuilder;
    }

    public void setSchema(File schema) {
        xmlBuilder.setSchema(schema);

    }

    public void showXML(Element root) {
        if(root!=null) {
            point2D.add(20, 20);
            String label = root.getTagName();
            DraggableNode node = new DraggableNode(mainWindowController, xmlBuilder, label);
            node.tabContent = treeContent;
            node.setLabel(label);

            treeContent.getChildren().add(node);
            node.relocateToPoint(point2D);

            node.setElement(root);
            node.isRoot = true;
            node.setRoot();
            node.xmlBuilder = xmlBuilder;

            if(root.hasAttributes()) {
                NamedNodeMap attributeMap = root.getAttributes();
                for(int j=0; j<attributeMap.getLength(); j++) {
                    Node attribute = attributeMap.item(j);
                    String attrName = attribute.getNodeName();
                    String attrValue = attribute.getNodeValue();
                    //Attribute a = new Attribute();
                    node.tableViewContent.addRow(attrName,attrValue);

                }
            }


            if(root.hasChildNodes()) {
                NodeList children = root.getChildNodes();
                ArrayList<DraggableNode> draggableNodes = showChildNodes(children,1);
                for(int i=0; i<draggableNodes.size(); i++) {
                    NodeLink link = new NodeLink();
                    link.bindEnds(node, draggableNodes.get(i));
                    treeContent.getChildren().add(0,link);
                }
            }
            //treeContent.resize(height*offsetX, maxdepth*offsetY);

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
                dNode = new DraggableNode(mainWindowController, xmlBuilder, label);
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
                    if(depth+1 > maxdepth) maxdepth++;
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
