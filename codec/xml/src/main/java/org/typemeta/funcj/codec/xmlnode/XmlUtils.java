package org.typemeta.funcj.codec.xmlnode;

import org.typemeta.funcj.codec.utils.CodecException;
import org.w3c.dom.*;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Optional;

public abstract class XmlUtils {
    public static Writer write(Node node, Writer writer, boolean pretty) throws CodecException {
        try {
            final Transformer tf = TransformerFactory.newInstance().newTransformer();
            tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            if (pretty) {
                tf.setOutputProperty(OutputKeys.INDENT, "yes");
                tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            }

            tf.transform(new DOMSource(node), new StreamResult(writer));
            return writer;
        } catch (TransformerException ex) {
            throw new CodecException(ex);
        }
    }

    public static OutputStream write(Node node, OutputStream os, boolean pretty) throws CodecException {
        try {
            final Transformer tf = TransformerFactory.newInstance().newTransformer();
            tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            if (pretty) {
                tf.setOutputProperty(OutputKeys.INDENT, "yes");
                tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            }

            tf.transform(new DOMSource(node), new StreamResult(os));
            return os;
        } catch (TransformerException ex) {
            throw new CodecException(ex);
        }
    }

    public static Element addElement(Element parent, String name) {
        final Document doc = parent.getOwnerDocument();
        final Element elem = doc.createElement(name);
        parent.appendChild(elem);
        return elem;
    }

    public static Element addTextElement(Element parent, String value) {
        final Document doc = parent.getOwnerDocument();
        final Text elem = doc.createTextNode(value);
        parent.appendChild(elem);
        return parent;
    }

    public static Element firstChildElement(Node node, String name) throws CodecException {
        for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child.getNodeType() == Node.ELEMENT_NODE && name.equals(child.getNodeName())) {
                return (Element) child;
            }
        }
        throw new CodecException("No ELEMENT_NODE child found for node " + node.getNodeName());
    }

    public static Optional<Text> firstChildTextOpt(Node node) {
        for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child.getNodeType() == Node.TEXT_NODE) {
                return Optional.of((Text) child);
            }
        }
        return Optional.empty();
    }

    public static Text firstChildText(Node node) throws CodecException {
        for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child.getNodeType() == Node.TEXT_NODE) {
                return (Text) child;
            }
        }
        throw new CodecException("No TEXT_NODE child found for node " + node.getNodeName());
    }

    public static String getAttrValue(Element elem, String name) {
        return elem.getAttribute(name);
    }

    public static Element setAttrValue(Element elem, String name, String value) {
        elem.setAttribute(name, value);
        return elem;
    }
}
