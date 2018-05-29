package org.tizzer.smmgr.system.handler;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author tizzer
 * @version 1.0
 */
public class XMLHandler {

    private File file;
    private Document doc;
    private Element root;

    public XMLHandler(String xmlName) {
        file = new File("config/" + xmlName);
        try {
            doc = new SAXReader().read(file);
            root = doc.getRootElement();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取Object类型的结点值
     *
     * @param tagName
     * @return
     */
    public Object getObject(String tagName) {
        return root.element(tagName).getData();
    }

    /**
     * 获取String类型的结点值
     *
     * @param tagName
     * @return
     */
    public String getString(String tagName) {
        return String.valueOf(getObject(tagName));
    }

    /**
     * 获取boolean类型的结点值
     *
     * @param tagName
     * @return
     */
    public boolean getBoolean(String tagName) {
        return Boolean.parseBoolean(getString(tagName));
    }

    public XMLHandler putElementValue(String tagName, Object value) {
        Element element = root.element(tagName);
        element.setText(String.valueOf(value));
        return this;
    }

    /**
     * 提交变化
     */
    public void commit() {
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
