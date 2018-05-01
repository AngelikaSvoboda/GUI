package sample;


import com.sun.org.apache.xerces.internal.impl.xs.XSImplementationImpl;
import com.sun.org.apache.xerces.internal.xs.XSLoader;
import com.sun.org.apache.xerces.internal.xs.XSModel;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;

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

    public boolean validateSchema(File inputSchema) {
        // TODO herausfinden, wie man die verfügbaren Elemente und Attribute aus einer Schema entnimmt
        System.setProperty(DOMImplementationRegistry.PROPERTY,
                "com.sun.org.apache.xerces.internal.dom.DOMXSImplementationSourceImpl");
        try {
            DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
            com.sun.org.apache.xerces.internal.impl.xs.XSImplementationImpl impl = (XSImplementationImpl) registry.getDOMImplementation("XS-Loader");
            XSLoader schemaLoader = impl.createXSLoader(null);
            XSModel model = schemaLoader.loadURI(inputSchema.getAbsolutePath());

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
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
        Element root = document.createElement("course");

        Element chapter = document.createElement("chapter");
        chapter.setAttribute("nr", "Chapter 1");

        Element chapter2 = document.createElement("chapter");
        chapter.setAttribute("nr", "Chapter 2");

        Element page = document.createElement("page");
        page.setAttribute("title", "How to set up a Server Part 1");

        Element page2 = document.createElement("page");
        page.setAttribute("title", "How to set up a Server Part 2");

        Element page3 = document.createElement("page");
        page.setAttribute("title", "How to set up a Server Part 3");

        Element content = document.createElement("content");
        //content.setAttribute("id", "1");

        content.setAttribute("type", "textfile");
        content.setAttribute("file", "/location/to/file.txt");

        page.appendChild(content);

        chapter.appendChild(page);
        chapter.appendChild(page2);
        chapter.appendChild(page3);

        root.appendChild(chapter);

        root.appendChild(chapter2);

        return root;
    }


}