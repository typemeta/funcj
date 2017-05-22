package org.funcj.document;

import org.funcj.data.IList;
import org.funcj.util.Folds;

import java.util.List;

public abstract class API {
    private static final int INDENT = 4;

    /**
     * An empty document.
     */
    public static final Document empty = Document.Nil.INSTANCE;

    /**
     * A line break.
     */
    public static final Document lbreak = Document.Break.INSTANCE;

    /**
     * A section of text which does not contain newlines, and should not get broken up.
     */
    public static Document text(String s) {
        if (s.equals(System.lineSeparator())) {
            return lbreak;
        } else {
            return new Document.Text(s);
        }
    }

    /**
     * A section of text which does not contain newlines, and should not get broken up.
     */
    public static Document text(char c) {
        return new Document.Text(new String(new char[]{c}));
    }

    /**
     * Defines document elements that can be grouped onto a single line
     * (if it fits within the current width) by removing line breaks.
     */
    public static Document group(Document doc) {
        return new Document.Group(doc);
    }

    /**
     * Indicate that the specified doc should formatted with nesting (i.e. indentation)
     */
    public static Document nest(int indent, Document doc) {
        return new Document.Nest(indent, doc);
    }

    /**
     * Concatenate multiple document elements.
     */
    public static Document concat(IList<Document> docs) {
        return new Document.Concat(docs);
    }

    /**
     * Concatenate multiple document elements.
     */
    public static Document concat(Document... docs) {
        return new Document.Concat(IList.ofArray(docs));
    }

    /**
     * Concatenate multiple document elements.
     */
    public static Document concat(Iterable<Document> docs) {
        return new Document.Concat(IList.ofIterable(docs));
    }

    /**
     * Concatenate multiple document elements, that can be split as separate lines if needed.
     */
    public static Document lines(IList<Document> docs) {
        if (docs.isEmpty()) {
            return empty;
        } else {
            return concat(docs.foldRight((d, acc) -> acc.add(lbreak).add(d), IList.nil()));
        }
    }

    /**
     * Concatenate multiple document elements, that can be split as separate lines if needed.
     */
    public static Document lines(Document... docs) {
        return lines(IList.ofArray(docs));
    }

    /**
     * Concatenate multiple document elements, that can be split as separate lines if needed.
     */
    public static Document lines(Iterable<Document> docs) {
        return lines(IList.ofIterable(docs));
    }

    /**
     * Create a document from elements by separating them with <code>sep</code>,
     * and enclosing them in <code>open</code> and <code>close</code>.
     */
    public static Document enclose(Document open, Document sep, Document close, IList<Document> docs) {
        final Document elems = docs.isEmpty() ?
                empty :
                docs.foldRight1((d, acc) -> concat(d, sep, lbreak, acc));
        return enclose(open, elems, close);
    }

    /**
     * Create a document from elements by separating them with <code>sep</code>,
     * and enclosing them in <code>open</code> and <code>close</code>.
     */
    public static Document enclose(Document open, Document sep, Document close, List<Document> docs) {
        final Document elems = docs.isEmpty() ?
                empty :
                Folds.foldRight1((d, acc) -> concat(d, sep, lbreak, acc), docs);
        return enclose(open, elems, close);
    }

    /**
     * Enclose a single document element.
     */
    public static Document enclose(Document open, Document doc, Document close) {
        return group(
                concat(
                        open,
                        nest(INDENT, concat(lbreak, doc)),
                        lbreak,
                        close
                )
        );
    }
}
