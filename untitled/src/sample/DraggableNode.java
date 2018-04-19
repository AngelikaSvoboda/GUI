package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;

import javafx.event.EventHandler;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class DraggableNode extends AnchorPane {

    @FXML private AnchorPane dragableNode;

    @FXML private TextField nodeLabel;
    @FXML private Label moveNodeLabel;
    @FXML private Label deleteNodeLabel;

    @FXML private AnchorPane leftDragPane;
    @FXML private AnchorPane contentPane;
    @FXML private AnchorPane rightDragPane;

    private EventHandler <DragEvent> mContextDragOver;
    private EventHandler <DragEvent> mContextDragDropped;

    private EventHandler <MouseEvent> mLinkHandleDragDetected;
    private EventHandler <DragEvent> mLinkHandleDragDropped;
    private EventHandler <DragEvent> mContextLinkDragOver;
    private EventHandler <DragEvent> mContextLinkDragDropped;

    private Point2D mDragOffset = new Point2D(0.0,0.0);

    private NodeLink mDragLink = null;
    private AnchorPane tabContent = null;

    private ArrayList<String> linkedChildren;
    private ArrayList<String> linkedAttributes; //nötig?

    private DraggableNode parent;

    private final DraggableNode self;

    public DraggableNode() {


        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("DraggableNode.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        self = this;
        try {
            fxmlLoader.load();

        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        setId(UUID.randomUUID().toString());

    }

    @FXML public void initialize() {

        //tabContent = (AnchorPane) getParent();
        parentProperty().addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue observable,
                                Object oldValue, Object newValue) {
                tabContent = (AnchorPane) getParent();

            }

        });

        setNodeDragHandlers();
        setNodeLinkHandlers();

        leftDragPane.setOnDragDetected(mLinkHandleDragDetected);
        rightDragPane.setOnDragDetected(mLinkHandleDragDetected);

        leftDragPane.setOnDragDropped(mLinkHandleDragDropped);
        rightDragPane.setOnDragDropped(mLinkHandleDragDropped);

        dragableNode.setOnMouseClicked(event -> {
            setFocused(true);
            System.out.println("new Focus");

        });

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

        // Knoten entfernen beim Drücken auf X
        deleteNodeLabel.setOnMouseClicked(event -> {
            AnchorPane parent = (AnchorPane) self.getParent();
            parent.getChildren().remove(self);

            // Verbindungen entfernen


        });

        nodeLabel.getStyleClass().add("text-field");

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

        mLinkHandleDragDetected = new EventHandler <MouseEvent> () {

            @Override
            public void handle(MouseEvent event) {

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

        mLinkHandleDragDropped = new EventHandler <DragEvent> () {

            @Override
            public void handle(DragEvent event) {

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

        mContextLinkDragOver = new EventHandler <DragEvent> () {

            @Override
            public void handle(DragEvent event) {
                event.acceptTransferModes(TransferMode.ANY);

                //Relocate end of user-draggable link
                if (!mDragLink.isVisible())
                    mDragLink.setVisible(true);

                mDragLink.setEnd(new Point2D(event.getX(), event.getY()));

                event.consume();

            }
        };

        //drop event for link creation
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
}
