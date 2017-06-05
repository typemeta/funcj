package org.funcj.codec.xml;

import org.funcj.control.Exceptions;
import org.w3c.dom.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.Optional;

public class XmlUtils {
    public static String nodeToString(Document doc, boolean pretty) {
        try {
            final Transformer tf = TransformerFactory.newInstance().newTransformer();
            tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            if (pretty) {
                tf.setOutputProperty(OutputKeys.INDENT, "yes");
                tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            }

            final Writer out = new StringWriter();
            tf.transform(new DOMSource(doc), new StreamResult(out));
            return out.toString();
        } catch (TransformerException ex) {
            throw new XmlCodecException(ex);
        }
    }

    private static DocumentBuilder createDocumentBuilder() {
        return Exceptions.wrap(() -> DocumentBuilderFactory.newInstance().newDocumentBuilder());
    }

    public static Element addElement(Document doc, Node parent, String name) {
        final Element elem = doc.createElement(name);
        parent.appendChild(elem);
        return elem;
    }

    public static Element firstChildElement(Node node, String name) {
        for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child.getNodeType() == Node.ELEMENT_NODE && name.equals(child.getNodeName())) {
                return (Element) child;
            }
        }
        throw new XmlCodecException("No ELEMENT_NODE child found for node " + node.getNodeName());
    }

    public static Optional<Text> firstChildTextOpt(Node node) {
        for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child.getNodeType() == Node.TEXT_NODE) {
                return Optional.of((Text) child);
            }
        }
        return Optional.empty();
    }

    public static Text firstChildText(Node node) {
        for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child.getNodeType() == Node.TEXT_NODE) {
                return (Text) child;
            }
        }
        throw new XmlCodecException("No TEXT_NODE child found for node " + node.getNodeName());
    }

    public static String getAttrValue(Element elem, String name) {
        return elem.getAttribute(name);
    }

    public static Element setAttrValue(Element elem, String name, String value) {
        elem.setAttribute(name, value);
        return elem;
    }
}
