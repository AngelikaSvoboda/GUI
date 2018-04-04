package sample;

import javafx.geometry.Side;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;


public class CustomTab extends Tab{
    public CustomTab(String text) {
        super(text);
        System.out.println("new CustomTab");
        TabPane tabPane = new TabPane();
        tabPane.setSide(Side.BOTTOM);
        tabPane.getTabs().addAll(new Tab("Baum"), new Tab("Code"));
    }
}
