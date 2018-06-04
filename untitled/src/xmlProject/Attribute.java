package xmlProject;

public class Attribute {

    private String attribute;
    private String value;

    public Attribute(){
        attribute = "";
        value = "";
    }

    public Attribute(String attr, String val){
        attribute = attr;
        value = val;
    }

    public String getAttribute() {
        return attribute;
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
