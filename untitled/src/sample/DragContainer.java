package sample;

import javafx.scene.input.DataFormat;
import javafx.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DragContainer implements Serializable {

    private static final long serialnumber = -1890998765646621338L;

    public static final DataFormat AddNode =
            new DataFormat("application.DragIcon.add");

    public static final DataFormat DragNode =
            new DataFormat("application.DraggableNode.drag");

    public static final DataFormat AddLink =
            new DataFormat("application.NodeLink.add");

    private final List<Pair<String, Object> > mDataPairs = new ArrayList<Pair<String, Object> >();

    public void addData (String key, Object value) {
        mDataPairs.add(new Pair<String, Object>(key, value));
    }

    public <T> T getValue (String key) {

        for (Pair<String, Object> data: mDataPairs) {

            if (data.getKey().equals(key))
                return (T) data.getValue();

        }

        return null;
    }

    public List <Pair<String, Object> > getData () { return mDataPairs; }

}
