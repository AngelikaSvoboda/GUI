package xmlProject;

import javafx.beans.property.StringProperty;
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

    public void setRow(int row, String attr, String value){
        setAttribute(row, attr);
        setValue(row, value);
    }

    public void setAttribute(int row, String attr) {
        rows.get(row).setAttribute(attr);
        Attribute a = rows.get(row);
        a.setAttribute(attr);
        rows.set(row, a);
    }

    public void setValue(int row, String value) {
        rows.get(row).setValue(value);
        Attribute a = rows.get(row);
        a.setValue(value);
        rows.set(row, a);

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
