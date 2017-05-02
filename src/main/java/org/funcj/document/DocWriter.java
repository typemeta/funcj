package org.funcj.document;

import org.funcj.control.Exceptions;
import org.funcj.data.IList;

import java.io.*;

public class DocWriter {
    public static String format(int width, Document doc) {
        final StringWriter wtr = new StringWriter();
        format(wtr, width, doc);
        return wtr.toString();
    }

    public static void format(Writer wtr, int width, Document doc) {
        final BufferedWriter bw = new BufferedWriter(wtr) {
            @Override
            public void newLine() throws IOException {
                write('\n');
            }
        };
        format(bw, width, doc);
    }

    public static void format(BufferedWriter wtr, int width, Document doc) {
        new DocWriter(wtr, width).format(doc);
        Exceptions.wrap(wtr::flush);
    }

    private static final String indent = "                                ";
    private static final int indentLen = indent.length();

    private static String indentStr(int depth) {
        if (depth < indentLen) {
            return indent.substring(0, depth);
        } else {
            final StringBuilder sb = new StringBuilder();
            while(depth > indentLen) {
                sb.append(indent);
                depth -= indentLen;
            }
            return sb.append(indent.substring(0, depth)).toString();
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

    private final BufferedWriter wtr;
    private final int width;

    public DocWriter(BufferedWriter wtr, int width) {
        this.wtr = wtr;
        this.width = width;
    }

    private void format(Document doc) {
        format(0, IList.of(FmtState.of(0, false, API.group(doc))));
    }

    private DocWriter newLine() {
        Exceptions.wrap(wtr::newLine);
        return this;
    }

    private DocWriter indent(int depth) {
        Exceptions.wrap(() -> wtr.write(indentStr(depth)));
        return this;
    }

    private DocWriter write(String s) {
        Exceptions.wrap(() -> wtr.write(s));
        return this;
    }

    private static boolean fits(int w, IList<FmtState> states) {
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
                    states = states.add(FmtState.of(hd.rw + nest.indent, hd.flag, nest.doc));
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
                states = states.add(FmtState.of(hd.rw + nest.indent, hd.flag, nest.doc));
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
