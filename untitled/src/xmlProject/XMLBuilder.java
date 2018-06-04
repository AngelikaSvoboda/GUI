package xmlProject;


import com.sun.org.apache.xerces.internal.impl.xs.XSImplementationImpl;
import com.sun.org.apache.xerces.internal.xs.XSLoader;
import com.sun.org.apache.xerces.internal.xs.XSModel;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.IOException;

public class XMLBuilder {

    private DocumentBuilderFactory dbFactory;
    private DocumentBuilder dBuilder;

    SchemaFactory schemaFactory;

    private Document document;
    private Element root;

    public File getXmlFile() {
        return xmlFile;
    }

    public void setXmlFile(File xmlFile) {
        this.xmlFile = xmlFile;
    }

    private File xmlFile;

    public File getSchema() {
        return schema;
    }

    public void setSchema(File schema) {
        this.schema = schema;
    }

    private File schema;

    /**
     * Konstruktor ohne File
     */
    public XMLBuilder() {
        dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setIgnoringComments(true);
        dbFactory.setIgnoringElementContentWhitespace(true);

        try {
            dBuilder = dbFactory.newDocumentBuilder();
            document = dBuilder.newDocument();


        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public XMLBuilder(File file){
        this();
        xmlFile = file;
        try {
            document = dBuilder.parse(file);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public XMLBuilder(File file, File schema){
        this(file);
        this.schema = schema;
        dbFactory.setValidating(true);
        dbFactory.setAttribute("http://java.sun.com/xml/jaxp/ properties/schemaLanguage","http://www.w3.org/2001/XMLSchema");

        dbFactory.setAttribute("http://java.sun.com/xml/jaxp/ properties/schemaSource", schema.getAbsolutePath());

        try {
            dBuilder = dbFactory.newDocumentBuilder();
            dBuilder.parse(file);

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Element getRoot() {
        return root;
    }

    public Element readFile(File file) {
        xmlFile = file;
        try {
            Document document = dBuilder.parse(file);
            document.getDocumentElement().normalize();
            root = (Element) document.getFirstChild();
            return root;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void saveFile(){
        Transformer transformer = null;
        try {
            transformer = TransformerFactory.newInstance().newTransformer();
            Result output = new StreamResult(new File("output.xml"));
            Source input = new DOMSource(root);

            transformer.transform(input, output);
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
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

    public Element removeElement(String elementName, int number){
        NodeList targets = document.getElementsByTagName(elementName);
        return (Element) root.removeChild(targets.item(number));
    }

    public Attr removeAttribute(Element element, String attrType){
        if(element.hasAttribute(attrType)) {
            Attr attr = element.getAttributeNode(attrType);
            element.removeAttribute(attrType);
            return attr;
        }
        else
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
