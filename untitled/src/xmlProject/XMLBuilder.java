package xmlProject;


import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.impl.xs.XSImplementationImpl;
import com.sun.org.apache.xerces.internal.xs.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;
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

    Document document;
    private Element root;

    private File xmlFile;
    private File schema;

    private String filePath;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public File getXmlFile() {
        return xmlFile;
    }

    public void setXmlFile(File xmlFile) {
        this.xmlFile = xmlFile;
    }



    public File getSchema() {
        return schema;
    }

    public void setSchema(File schema) {
        this.schema = schema;

    }



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

            System.setProperty(DOMImplementationRegistry.PROPERTY, "com.sun.org.apache.xerces.internal.dom.DOMXSImplementationSourceImpl");
            DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
            com.sun.org.apache.xerces.internal.impl.xs.XSImplementationImpl impl = (XSImplementationImpl) registry.getDOMImplementation("XS-Loader");
            XSLoader schemaLoader = impl.createXSLoader(null);
            XSModel model = schemaLoader.loadURI(schema.getAbsolutePath());
            System.out.println("Schemamodel geladen");

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Element getRoot() {
        return root;
    }

    public void setRoot(Element element) {
        root = element;
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

    public String saveFile(String filename, Window w){
        String returnName=filename;
        System.out.println("Speichern");
        Transformer transformer = null;

        // Neue Datei: Speicherort wählen und neues File erstellen
        if(xmlFile == null) {
            System.out.println("Speicherort wählen");
            FileChooser chooser = new FileChooser();
            chooser.setInitialDirectory(new File(System.getProperty("user.home")));
            chooser.setTitle("Speichern");
            chooser.setInitialFileName(filename);
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML-File .xml" , "*.xml"));

            File choice = chooser.showSaveDialog(w);
            if(choice != null) {
                xmlFile = new File(choice.getAbsolutePath());
                filePath = xmlFile.getAbsolutePath();
                if(choice.getName() != filename) {
                    returnName = choice.getName();
                }
            }
            else {
                return filename;
            }
        }

        try {
            transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            //Result output = new StreamResult(new File("output.xml"));
            Result output = new StreamResult(xmlFile);
            Source input = new DOMSource(root);

            transformer.transform(input, output);
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return returnName;

    }

    public void saveFileAs(File newFile) {
        Transformer transformer = null;
        try {
            transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            xmlFile = newFile;
            filePath = newFile.getAbsolutePath();
            Result output = new StreamResult(newFile);
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
            XSNamedMap map =  model.getComponents(XSConstants.ELEMENT_DECLARATION); // gibt nur das oberste Element?
            for (int i=0; i< map.size(); i++){
                XSObject o = map.item(i);
                if(o!=null) {
                    System.out.println(o.getName());
                }
            }
            System.out.println("");

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Element createElement(String name){
        return document.createElement(name);
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
    public Element removeElement(Element element) {
        // TODO DOM durchlaufen?
        Element parent = (Element) element.getParentNode();
        try {
            if(parent != null)
                return (Element) parent.removeChild(element);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
