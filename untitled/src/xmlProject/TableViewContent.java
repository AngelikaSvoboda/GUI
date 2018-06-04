package xmlProject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public class TableViewContent {

    private ObservableList<Attribute> rows;

    public TableViewContent() {
        rows = FXCollections.observableArrayList();
    }

    public void addRow(String attr, String value) {
        Attribute a = new Attribute(attr,value);
        rows.add(a);
    }

    public void deleteRow(int index) {
        rows.remove(index);
    }

    public void deleteAll(){
        rows.remove(0, rows.size()-1);
    }

    public ObservableList<Attribute> getRows() {
        return rows;
    }

}
