package xmlProject;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;

import javafx.event.EventHandler;
import org.w3c.dom.Element;

import java.io.IOException;
import java.util.*;

public class DraggableNode extends AnchorPane {

    private Controller mainWindowController;

    @FXML private AnchorPane dragableNode;

    // Name des Elements
    @FXML private TextField nodeTextField;

    @FXML private Label moveNodeLabel;
    @FXML private Label deleteNodeLabel;

    @FXML AnchorPane leftDragPane;
    @FXML private AnchorPane contentPane;
    @FXML AnchorPane rightDragPane;

    // Bewegen der Knoten innerhalb des Tabs
    private EventHandler <DragEvent> mContextDragOver;
    private EventHandler <DragEvent> mContextDragDropped;

    // Verbindung ziehen startet -> Unterschied zw. leftDragPane und rightDragPane
    private EventHandler <MouseEvent> mLinkHandleDragDetected;
    // Verbindung losgelassen
    private EventHandler <DragEvent> mLinkHandleDragDropped;

    // Verbindung zu einem Knoten gezogen
    private EventHandler <DragEvent> mContextLinkDragOver;
    private EventHandler <DragEvent> mContextLinkDragDropped;

    private Point2D mDragOffset = new Point2D(0.0,0.0);

    private NodeLink mDragLink = null;

    //Parent = Inhalt des Tabs
    AnchorPane tabContent = null;

    ArrayList<String>linkedChildren = new ArrayList<>();
    ArrayList<String>linkedAttributes = new ArrayList<>(); //nötig?

    // Referenz auf Elternknoten wenn vorhanden
    private DraggableNode parent;

    // Referenz auf das Element in dem DOM zur Manipulation von Attributen und Eltern-/Kindbeziehungen
    private Element element;
    XMLBuilder xmlBuilder;

    TableViewContent tableViewContent = new TableViewContent();

    private final DraggableNode self;

    public DraggableNode(Controller c, XMLBuilder builder) {

        mainWindowController = c;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("DraggableNode.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        self = this;
        xmlBuilder = builder;
        element = xmlBuilder.createElement("Label");
        try {
            fxmlLoader.load();

        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        setId(UUID.randomUUID().toString());

    }

    @FXML public void initialize() {

        dragableNode.setOnMousePressed(event -> {
            // Rechtklick -> neues Fenster?
            if(event.getButton() == MouseButton.SECONDARY) {

            }
            // Linksklick -> Focus für Tabellenanzeige
            else if(event.getButton() == MouseButton.PRIMARY) {
                //mainWindowController.setFocusedNode(self);
                if(mainWindowController.focusedNode != null) mainWindowController.focusedNode.getStyleClass().remove("node-focus");
                mainWindowController.focusedNode = self;
                mainWindowController.focusedNode.getStyleClass().add("node-focus");
                mainWindowController.showTable();
            }
        });

        setNodeDragHandlers();
        setNodeLinkHandlers();

        // Elternelement verknüpfen -> kann nur ein Elternknoten haben
        leftDragPane.setOnDragDetected(mLinkHandleDragDetected);
        //leftDragPane.setOnDragDropped(mLinkHandleDragDropped);
        dragableNode.setOnDragDropped(mLinkHandleDragDropped);

        // Kindknoten verknüpfen -> kann mehrere Kindknoten haben
        rightDragPane.setOnDragDetected(mLinkHandleDragDetected);
        //rightDragPane.setOnDragDropped(mLinkHandleDragDropped);
        dragableNode.setOnDragDropped(mLinkHandleDragDropped);

        mDragLink = new NodeLink();
        mDragLink.setVisible(false);

        //tabContent = (AnchorPane) self.getParent();

    }


    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public void relocateToPoint (Point2D p) {

        //relocates the object to a point that has been converted to
        //scene coordinates
        Point2D localCoords = getParent().sceneToLocal(p);

        relocate (
                (int) (localCoords.getX() - mDragOffset.getX()),
                (int) (localCoords.getY() - mDragOffset.getY())
        );
    }

    public void setNodeDragHandlers() {

        mContextDragOver = event->{

            event.acceptTransferModes(TransferMode.ANY);
            relocateToPoint(new Point2D( event.getSceneX(), event.getSceneY()));

            event.consume();
        };

        //dragdrop for node dragging
        mContextDragDropped = event ->{

            //@Override
            //public void handle(DragEvent event) {

            getParent().setOnDragOver(null);
            getParent().setOnDragDropped(null);

            event.setDropCompleted(true);

            event.consume();
            //}
        };

        // Knoten entfernen beim Drücken auf X /TODO nur beim entfernten Knoten verschwindet die ID aus der Liste
        deleteNodeLabel.setOnMouseClicked(event -> {
            AnchorPane parent = (AnchorPane) self.getParent();
            parent.getChildren().remove(self);

            // Grafische Verbindungen entfernen
            for (ListIterator<String> iterId = linkedChildren.listIterator();
                 iterId.hasNext();) {

                String id = iterId.next();

                for (ListIterator <Node> iterNode = parent.getChildren().listIterator();
                     iterNode.hasNext();) {

                    Node node = iterNode.next();

                    if (node.getId() == null)
                        continue;

                    if (node.getId().equals(id))
                        iterNode.remove();
                }

                iterId.remove();
            }

            // Bei den Kindknoten den Vermerk des Elternknotens entfernen

            // Bei Elternknoten sich selbst entfernen


        });

        nodeTextField.getStyleClass().add("text-field");
        nodeTextField.textProperty().addListener((observable, oldValue, newValue) -> {

            setLabel(newValue);


        });

        moveNodeLabel.setOnDragDetected(event -> {

            getParent().setOnDragOver(null);
            getParent().setOnDragDropped(null);

            getParent().setOnDragOver(mContextDragOver);
            getParent().setOnDragDropped(mContextDragDropped);

            //begin drag ops
            mDragOffset = new Point2D(event.getX(), event.getY());

            relocateToPoint(
                    new Point2D(event.getSceneX(), event.getSceneY())
            );

            ClipboardContent content = new ClipboardContent();
            DragContainer container = new DragContainer();

            //container.addData ("type", mType.toString());
            content.put(DragContainer.AddNode, container);

            startDragAndDrop (TransferMode.ANY).setContent(content);

            event.consume();

        });

    }

    public void setNodeLinkHandlers() {

        // Link wird zum ersten Mal gezogen
        mLinkHandleDragDetected = new EventHandler <MouseEvent> () {

            @Override
            public void handle(MouseEvent event) {
                System.out.println("link handle drag detected");

                getParent().setOnDragOver(null);
                getParent().setOnDragDropped(null);

                getParent().setOnDragOver(mContextLinkDragOver);
                getParent().setOnDragDropped(mContextLinkDragDropped);

                //Set up user-draggable link
                //rightDragPane.getChildren().add(0,mDragLink);
                tabContent.getChildren().add(0,mDragLink);


                mDragLink.setVisible(false);

                Point2D p = new Point2D(
                        getLayoutX() + (getWidth() / 2.0),
                        getLayoutY() + (getHeight() / 2.0)
                );

                mDragLink.setStart(p);

                //Drag content code
                ClipboardContent content = new ClipboardContent();
                DragContainer container = new DragContainer ();

                //pass the UUID of the source node for later lookup
                container.addData("source", getId());

                content.put(DragContainer.AddLink, container);

                startDragAndDrop (TransferMode.ANY).setContent(content);

                event.consume();
            }
        };

        // Link wird über den Knoten gezogen -> Speichern des Links zum späteren Einfügen als ClipboardContent auf dem tabContent
        mLinkHandleDragDropped = new EventHandler <DragEvent> () {

            @Override
            public void handle(DragEvent event) {
                System.out.println("link handle dropped");

                getParent().setOnDragOver(null);
                getParent().setOnDragDropped(null);

                //get the drag data.  If it's null, abort.
                //This isn't the drag event we're looking for.
                DragContainer container =
                        (DragContainer) event.getDragboard().getContent(DragContainer.AddLink);

                if (container == null)
                    return;

                //hide the draggable NodeLink and remove it from the right-hand AnchorPane's children
                mDragLink.setVisible(false);
                //rightDragPane.getChildren().remove(0);
                tabContent.getChildren().remove(0);

                AnchorPane link_handle = (AnchorPane) event.getSource();

                ClipboardContent content = new ClipboardContent();

                //pass the UUID of the target node for later lookup
                container.addData("target", getId());

                content.put(DragContainer.AddLink, container);

                event.getDragboard().setContent(content);
                event.setDropCompleted(true);
                event.consume();
            }
        };

        // Link wird über den Bildschirm gezogen -> aktualisiere den Endpunkt der Linie zur Darstellung
        mContextLinkDragOver = new EventHandler <DragEvent> () {

            @Override
            public void handle(DragEvent event) {
                System.out.println("context link drag over");

                event.acceptTransferModes(TransferMode.ANY);

                //Relocate end of user-draggable link
                if (!mDragLink.isVisible())
                    mDragLink.setVisible(true);

                mDragLink.setEnd(new Point2D(event.getX(), event.getY()));

                event.consume();

            }
        };

        // Link wird losgelassen, entferne die erstellte Linie
        mContextLinkDragDropped = new EventHandler <DragEvent> () {

            @Override
            public void handle(DragEvent event) {
                System.out.println("context link drag dropped");

                getParent().setOnDragOver(null);
                getParent().setOnDragDropped(null);

                //hide the draggable NodeLink and remove it from the right-hand AnchorPane's children
                mDragLink.setVisible(false);

                //rightDragPane.getChildren().remove(0);
                tabContent.getChildren().remove(0);

                event.setDropCompleted(true);
                event.consume();
            }

        };

    }

    public void registerLink(String id) {
        linkedChildren.add(id);
    }

    public void setLabel(String label) {
        nodeTextField.setText(label);
    }
}
