package org.tizzer.smmgr.system.resolver;

import org.tizzer.smmgr.system.model.Preference;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author tizzer
 * @version 1.0
 */
public class XMLHandler extends DefaultHandler {

    private Preference preference;
    private String tagName;

    public XMLHandler(Preference preference) {
        this.preference = preference;
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        tagName = localName;
        if (tagName.equals("entry")) {
            preference = new Preference();
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        String tagValue = new String(ch, start, length);
        switch (tagName) {
            case "autoLogin":
                preference.setAutoLogin(Boolean.parseBoolean(tagValue));
            case "pendingAlarm":
                preference.setPendingAlarm(Boolean.parseBoolean(tagValue));
            case "birthAlarm":
                preference.setBirthAlarm(Boolean.parseBoolean(tagValue));
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
    }

    @Override
    public void endDocument() throws SAXException {
        tagName = "";
    }

}
