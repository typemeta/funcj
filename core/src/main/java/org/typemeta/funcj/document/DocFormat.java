package org.typemeta.funcj.document;

import org.typemeta.funcj.data.IList;
import org.typemeta.funcj.util.Exceptions;

import java.io.*;

/**
 * Format a {@code Document} into a {@code String}.
 */
public class DocFormat {
    private static final int DEFAULT_INDENT_SIZE = 4;

    private static final String indentStr = "                                                                ";

    private static final int indentStrLen = indentStr.length();

    public static String format(int width, Document doc) {
        return format(width, DEFAULT_INDENT_SIZE, doc);
    }

    public static void format(Writer wtr, int width, Document doc) {
        format(wtr, DEFAULT_INDENT_SIZE, width, doc);
    }

    public static String format(int width, int indent, Document doc) {
        final StringWriter wtr = new StringWriter();
        format(wtr, indent, width, doc);
        return wtr.toString();
    }

    public static void format(Writer wtr, int indent, int width, Document doc) {
        new DocFormat(wtr, indent, width).format(doc);
        Exceptions.wrap(wtr::flush);
    }

    private static String indentStr(int depth) {
        if (depth < indentStrLen) {
            return indentStr.substring(0, depth);
        } else {
            final StringBuilder sb = new StringBuilder();
            while(depth > indentStrLen) {
                sb.append(indentStr);
                depth -= indentStrLen;
            }
            return sb.append(indentStr, 0, depth).toString();
        }
    }

    private static class FmtState {
        static FmtState of(int rw, boolean flag, Document doc) {
            return new FmtState(rw, flag, doc);
        }

        final int rw;
        final boolean flag;
        final Document doc;

        private FmtState(int rw, boolean flag, Document doc) {
            this.rw = rw;
            this.flag = flag;
            this.doc = doc;
        }

        @Override
        public String toString() {
            return "FmtState{" +
                    "rw=" + rw +
                    ", flag=" + flag +
                    ", doc=" + doc +
                    '}';
        }
    }

    private final StringBuilder line = new StringBuilder();
    private final Writer wtr;
    private final int width;
    private final int indSize;

    public DocFormat(Writer wtr, int indSize, int width) {
        this.wtr = wtr;
        this.width = width;
        this.indSize = indSize;
    }

    private void format(Document doc) {
        format(0, IList.of(FmtState.of(0, false, doc)));
        flush();
    }

    private DocFormat newLine() {
        Exceptions.wrap(() -> {
            final String line2 = Utils.trimTrailing(line.toString());
            wtr.append(line2).append("\n");
            line.setLength(0);
        });
        return this;
    }

    private DocFormat flush() {
        Exceptions.wrap(() -> {
            final String line2 = Utils.trimTrailing(line.toString());
            wtr.append(line2);
            line.setLength(0);
        });
        return this;
    }

    private DocFormat indent(int depth) {
        Exceptions.wrap(() -> line.append(indentStr(depth)));
        return this;
    }

    private DocFormat write(String s) {
        Exceptions.wrap(() -> line.append(s));
        return this;
    }

    private boolean fits(int w, IList<FmtState> states) {
        while(!states.isEmpty()) {
            if (w < 0) {
                return false;
            } else {
                final FmtState hd = states.head();
                states = states.tail();

                if (hd.doc instanceof Document.Nil) {
                } else if (hd.doc instanceof Document.Break) {
                    if (hd.flag) {
                        return true;
                    }
                } else if (hd.doc instanceof Document.Text) {
                    final Document.Text text = (Document.Text) hd.doc;
                    w -= text.text.length();
                } else if (hd.doc instanceof Document.Group) {
                    final Document.Group group = (Document.Group) hd.doc;
                    states = states.add(FmtState.of(hd.rw, false, group.doc));
                } else if (hd.doc instanceof Document.Nest) {
                    final Document.Nest nest = (Document.Nest) hd.doc;
                    states = states.add(FmtState.of(hd.rw + nest.indent * indSize, hd.flag, nest.doc));
                } else if (hd.doc instanceof Document.Concat) {
                    final Document.Concat concat = (Document.Concat) hd.doc;
                    final IList<FmtState> states2 = concat.children.map(c -> FmtState.of(hd.rw, hd.flag, c));
                    states = states.addAll(states2);
                } else {
                    throw new IllegalStateException("");
                }
            }
        }

        return true;
    }

    private void format(int k, IList<FmtState> states) {
        while(!states.isEmpty()) {
            final FmtState hd = states.head();
            states = states.tail();

            if (hd.doc instanceof Document.Nil) {
            } else if (hd.doc instanceof Document.Break) {
                if (hd.flag) {
                    newLine().indent(hd.rw);
                    k = hd.rw;
                }
            } else if (hd.doc instanceof Document.Text) {
                final Document.Text text = (Document.Text) hd.doc;
                write(text.text);
                k += text.text.length();
            } else if (hd.doc instanceof Document.Group) {
                final Document.Group group = (Document.Group) hd.doc;
                final boolean fitsFlat = fits(width - k, states.add(FmtState.of(hd.rw, false, group.doc)));
                states = states.add(FmtState.of(hd.rw, !fitsFlat, group.doc));
            } else if (hd.doc instanceof Document.Nest) {
                final Document.Nest nest = (Document.Nest) hd.doc;
                states = states.add(FmtState.of(hd.rw + nest.indent * indSize, hd.flag, nest.doc));
            } else if (hd.doc instanceof Document.Concat) {
                final Document.Concat concat = (Document.Concat) hd.doc;
                final IList<FmtState> states2 = concat.children.map(c -> FmtState.of(hd.rw, hd.flag, c));
                states = states.addAll(states2);
            } else {
                throw new IllegalStateException("");
            }
        }
    }
}
