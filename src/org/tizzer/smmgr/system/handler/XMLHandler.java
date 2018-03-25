package org.tizzer.smmgr.system.handler;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author tizzer
 * @version 1.0
 */
public class XMLHandler {

    private File file;
    private Document doc;
    private Element root;

    public XMLHandler(String xmlName) throws DocumentException {
        file = new File("" + xmlName);//TODO
        doc = new SAXReader().read(file);
        root = doc.getRootElement();
    }

    public Object getObject(String tagName) {
        return root.element(tagName).getData();
    }

    public String getString(String tagName) {
        return String.valueOf(getObject(tagName));
    }

    public boolean getBoolean(String tagName) {
        return Boolean.parseBoolean(getString(tagName));
    }

    public Map<String, Object> getAttributes(String tagName) {
        Map<String, Object> map = new HashMap<>();
        List<Attribute> attributes = root.element(tagName).attributes();
        for (Attribute attribute : attributes) {
            map.put(attribute.getName(), attribute.getValue());
        }
        return map;
    }

    public XMLHandler putElementValue(String tagName, Object value) {
        Element element = root.element(tagName);
        element.setText(String.valueOf(value));
        return this;
    }

    public XMLHandler putElementValue(Map<String, Object> valMap) {
        Set<Map.Entry<String, Object>> entry = valMap.entrySet();
        for (Map.Entry<String, Object> map : entry) {
            Element element = root.element(map.getKey());
            element.setText(String.valueOf(map.getValue()));
        }
        return this;
    }

    public XMLHandler putAttributeValue(String tagName, Map<String, Object> valMap) {
        Element element = root.element(tagName);
        Set<Map.Entry<String, Object>> entry = valMap.entrySet();
        for (Map.Entry<String, Object> map : entry) {
            Attribute attribute = element.attribute(map.getKey());
            attribute.setValue(String.valueOf(map.getValue()));
        }
        return this;
    }

    public void apply() {
        FileOutputStream fos = null;
        XMLWriter writer = null;
        try {
            fos = new FileOutputStream(file.getCanonicalPath());
            OutputFormat of = OutputFormat.createPrettyPrint();
            of.setEncoding("utf-8");
            writer = new XMLWriter(fos, of);
            writer.write(doc);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
