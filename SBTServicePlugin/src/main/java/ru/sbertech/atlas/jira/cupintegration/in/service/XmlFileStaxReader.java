package ru.sbertech.atlas.jira.cupintegration.in.service;

import org.apache.commons.lang.NullArgumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sbertech.atlas.jira.cupintegration.exception.ParseException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.trimToNull;

/**
 * Created by Sedelnikov FM on 28/12/2015.
 *
 * Read xml Cup file. Use like iterator for get Map with parameters of one node in file
 */
public class XmlFileStaxReader {

    private static final Logger log = LoggerFactory.getLogger(XmlFileStaxReader.class);

    private XMLStreamReader xmlr;
    private String currentObjectTagInXml;
    private String[] nodeTags;
    private boolean hasNext = false;

    /**
     * Set up tag (node-tags) inside of which contain data
     *
     * @param nodeTags
     * @throws org.apache.commons.lang.NullArgumentException if inputStream is null
     * @throws ParseException                                if XMLStreamReader have creating/initialization errors
     */
    public XmlFileStaxReader(InputStream inputStream, String... nodeTags) {

        if (inputStream == null) {
            throw new NullArgumentException("inputStream");
        }

        try {
            xmlr = XMLInputFactory.newInstance().createXMLStreamReader(inputStream);
        } catch (XMLStreamException e) {
            log.error("Error while XML initializing", e);
            throw new ParseException("Error while XML initializing", e);
        }

        this.nodeTags = nodeTags;
    }

    /**
     * @return true when you can get the next element
     */
    public boolean hasNext() {
        if (hasNext)
            return true;

        try {
            while (xmlr.hasNext()) {
                xmlr.next();

                if (isStartOfNode()) {
                    hasNext = true;
                    return hasNext;
                }
            }
        } catch (XMLStreamException e) {
            log.error("Error while XML parsing", e);
            throw new ParseException("Error while XML parsing", e);
        }

        return false;
    }

    /**
     * @return Map with parameters of object
     */
    public Map<String, String> next() {
        hasNext = false;
        try {
            return getNodeMap();
        } catch (XMLStreamException e) {
            log.error("Error while XML parsing", e);
            throw new ParseException("Error while XML parsing", e);
        }
    }

    private boolean isEndOfNode() {
        return (xmlr.isEndElement() && xmlr.getLocalName().equalsIgnoreCase(currentObjectTagInXml));
    }

    private boolean isStartOfNode() {

        if (!xmlr.isStartElement()) {
            return false;
        }

        for (String tag : nodeTags) {
            if (xmlr.getLocalName().equalsIgnoreCase(tag)) {
                currentObjectTagInXml = tag;
                return true;
            }
        }

        return false;
    }

    private Map<String, String> getNodeMap() throws XMLStreamException {
        Map<String, String> node = new HashMap<>();
        String tag = null;

        while (xmlr.hasNext()) {

            xmlr.next();
            if (isEndOfNode()) {
                break;
            }

            if (xmlr.isStartElement()) {
                tag = xmlr.getLocalName();
                continue;
            }
            if (xmlr.hasText() && !isEmpty(trimToNull(xmlr.getText()))) {
                node.put(tag, xmlr.getText());
            }
        }
        return node;
    }
}