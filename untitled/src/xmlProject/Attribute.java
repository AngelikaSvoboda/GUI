package xmlProject;

import javafx.beans.property.SimpleStringProperty;

public class Attribute {

    private String attribute;
    private String value;
    //private SimpleStringProperty attributeProperty;
    //private SimpleStringProperty valueProperty;

    public Attribute(){
        //attribute = "";
        //value = "";
    }

    public Attribute(String attr, String val){
        attribute = attr;
        value = val;
        //setAttributeProperty(attr);
        //setValueProperty(val);
    }

    /*public String getAttributeProperty() {
        return attributeProperty.get();
    }

    public SimpleStringProperty attributePropertyProperty() {
        return attributeProperty;
    }

    public void setAttributeProperty(String attributeProperty) {
        this.attributeProperty.set(attributeProperty);
    }

    public String getValueProperty() {
        return valueProperty.get();
    }

    public SimpleStringProperty valuePropertyProperty() {
        return valueProperty;
    }

    public void setValueProperty(String valueProperty) {
        this.valueProperty.set(valueProperty);
    }
    */

    public String getAttribute() {
        //return attributeProperty.get();
        return  attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
