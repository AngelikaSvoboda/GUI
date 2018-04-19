package sample;


import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class XMLBuilder {

    private Document document;
    private Element root;


    public XMLBuilder(){
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            document = dBuilder.newDocument();


        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void addElement(Element parent, String eName){

        Element element = document.createElement(eName);
        if(parent == null) {
            document.getFirstChild().appendChild(element);
        }
        else {
            parent.appendChild(element);
        }

    }

    public void addAttribute(Element element, String attrType, String attrValue){
        Attr nAttr = document.createAttribute(attrType);
        nAttr.setValue(attrValue);
        element.setAttributeNode(nAttr);

    }

    public Element removeElement(String elementName){
        return null;
    }

    public Attr removeAttribute(Element element, String attrType){
        return null;
    }

    public Element createTestXML(){
        root = document.createElement("course");

        Element chapter = document.createElement("chapter");
        chapter.setAttribute("id", "Chapter 1");

        Element page = document.createElement("page");
        page.setAttribute("title", "How to set up a Server Part 1");

        Element content = document.createElement("content");
        content.setAttribute("id", "1");

        content.setAttribute("type", "textfile");
        content.setAttribute("file", "/location/to/file.txt");

        page.appendChild(content);

        chapter.appendChild(page);

        root.appendChild(chapter);

        return root;
    }


}
