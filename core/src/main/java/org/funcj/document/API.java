package org.funcj.document;

import org.funcj.data.IList;
import org.funcj.util.Folds;

import java.util.List;

public abstract class API {
    private static final int INDENT = 4;

    /**
     * An empty document.
     */
    public static final Document.Nil empty = Document.Nil.INSTANCE;

    /**
     * A line break.
     */
    public static final Document.Break lbreak = Document.Break.INSTANCE;

    /**
     * Construct a {@link Document} from a {@link String}.
     * @param s         the text contents
     * @return          if the {@code s} argument is a line break then a {@link Document.Break},
     *                  otherwise a {@link Document.Text}
     */
    public static Document text(String s) {
        if (s.equals(System.lineSeparator())) {
            return lbreak;
        } else {
            return new Document.Text(s);
        }
    }

    /**
     * Construct a {@link Document.Text} from a {@code char}.
     * @param c         the text contents
     * @return          the {@code Document.Text} object
     */
    public static Document text(char c) {
        return new Document.Text(new String(new char[]{c}));
    }

    /**
     * Construct a {@link Document.Group} from a {@code Document}.
     * @param doc       the document to group
     * @return          the {@code Document.Group} object
     */
    public static Document.Group group(Document doc) {
        return new Document.Group(doc);
    }

    /**
     * Construct a {@link Document.Nest} from a {@code Document}.
     * @param indent    the level of indentation
     * @param doc       the document to nest
     * @return          the {@code Document.Nest} object
     */
    public static Document.Nest nest(int indent, Document doc) {
        return new Document.Nest(indent, doc);
    }

    /**
     * Construct a {@link Document.Concat} from an {@link IList} of {@code Document}s.
     * @param docs      the collection of {@code Document}s
     * @return          the {@code Document.Concat} object
     */
    public static Document.Concat concat(IList<Document> docs) {
        return new Document.Concat(docs);
    }

    /**
     * Construct a {@link Document.Concat} from a varargs array of {@code Document}s.
     * @param docs      the varargs array of {@code Document}s
     * @return          the {@code Document.Concat} object
     */
    public static Document.Concat concat(Document... docs) {
        return new Document.Concat(IList.ofArray(docs));
    }

    /**
     * Construct a {@link Document.Concat} from an {@link Iterable} of {@code Document}s.
     * @param docs      the iterable collection of {@code Document}s
     * @return          the {@code Document.Concat} object
     */
    public static Document.Concat concat(Iterable<Document> docs) {
        return new Document.Concat(IList.ofIterable(docs));
    }

    /**
     * Construct a {@link Document} from an {@link IList} of {@code Document}s,
     * by interspersing them with line breaks.
     * @param docs      the collection of {@code Document}s
     * @return          the {@code Document} object
     */
    public static Document lines(IList<Document> docs) {
        return docs.nonEmptyOpt()
                .map(l -> (Document)lines(l))
                .orElse(empty);
    }

    /**
     * Construct a {@link Document} from an {@link IList.NonEmpty} of {@code Document}s,
     * by interspersing them with line breaks.
     * @param docs      the non-empty list of {@code Document}s
     * @return          the {@code Document.Concat} object
     */
    public static Document.Concat lines(IList.NonEmpty<Document> docs) {
        final IList<Document> docs2 =
                docs.tail()
                        .foldRight(
                                (d, acc) -> acc.add(lbreak).add(d),
                                IList.of(docs.head()));
        return concat(docs2);
    }

    /**
     * Construct a {@link Document} from a list of {@code Document}s,
     * by interspersing them with line breaks.
     * @param docs      the varargs array of {@code Document}s
     * @return          the {@code Document} object
     */
    public static Document lines(Document... docs) {
        return lines(IList.ofArray(docs));
    }

    /**
     * Construct a {@link Document} from a non-empty list of {@code Document}s,
     * by interspersing them with line breaks.
     * @param doc       the first {@code Document}
     * @param docs      the varargs array of {@code Document}s
     * @return          the {@code Document.Concat} object
     */
    public static Document.Concat lines(Document doc, Document... docs) {
        return lines(IList.of(doc, docs));
    }

    /**
     * Construct a {@link Document} from an {@link Iterable} of {@code Document}s,
     * by interspersing them with line breaks.
     * @param docs      the iterable collection of {@code Document}s
     * @return          the {@code Document} object
     */
    public static Document lines(Iterable<Document> docs) {
        return lines(IList.ofIterable(docs));
    }

    /**
     * Create a {@code Document} from elements by separating them with {@code sep},
     * and enclosing them in {@code open} and {@code close}.
     * @param open      the opening {@code Document}
     * @param sep       the separator {@code Document}
     * @param close     the closing {@code Document}
     * @param docs      the {@code Document}s to be enclosed
     * @return          the enclosed {@code Document} value
     */
    public static Document enclose(
            Document open,
            Document sep,
            Document close,
            IList<Document> docs) {
        final Document elems = docs.isEmpty() ?
                empty :
                docs.foldRight1((d, acc) -> concat(d, sep, lbreak, acc));
        return enclose(open, elems, close);
    }

    /**
     * Create a {@code Document} from {@code Document} elements
     * by separating them with {@code sep},
     * and enclosing them in {@code open} and {@code close}.
     * @param open      the opening {@code Document}
     * @param sep       the separator {@code Document}
     * @param close     the closing {@code Document}
     * @param docs      the {@code Document}s to be enclosed
     * @return          the enclosed {@code Document} value
     */
    public static Document enclose(
            Document open,
            Document sep,
            Document close,
            List<Document> docs) {
        final Document elems = docs.isEmpty() ?
                empty :
                Folds.foldRight1((d, acc) -> concat(d, sep, lbreak, acc), docs);
        return enclose(open, close, elems);
    }

    /**
     * Create a {@code Document} by enclosing a {@code Document} in {@code open} and {@code close}.
     * @param open      the opening {@code Document}
     * @param close     the closing {@code Document}
     * @param doc       the {@code Document} to be enclosed
     * @return          the enclosed {@code Document} value
     */
    public static Document enclose(Document open, Document close, Document doc) {
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
