package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class DraggableNode extends AnchorPane {

    @FXML private AnchorPane dragableNode;
    @FXML private Label nodeLabel;
    @FXML private Label deleteNodeLabel;

    private final DraggableNode self;

    public DraggableNode() {
        self = this;
    }

    /**
     * Schließen des Knotens bei Druck auf X-Label
     */
    @FXML public void handleNodeClosed(){
        AnchorPane parent = (AnchorPane) self.getParent();
        parent.getChildren().remove(self);
    }


    public void handleDragDetected(MouseEvent mouseEvent) {

    }
}
