package org.funcj.document;

import org.funcj.data.IList;
import org.funcj.util.Functions.F;

import java.io.Writer;

/**
 * A model structure for formattable documents.
 */
public interface Document {
    enum Nil implements Document {
        INSTANCE {
            @Override
            public <T> T match(
                    F<Nil, T> nil,
                    F<Break, T> brek,
                    F<Text, T> text,
                    F<Group, T> group,
                    F<Nest, T> nest,
                    F<Concat, T> concat) {
                return nil.apply(this);
            }
        }
    }

    enum Break implements Document {
        INSTANCE {
            @Override
            public <T> T match(
                    F<Nil, T> nil,
                    F<Break, T> brek,
                    F<Text, T> text,
                    F<Group, T> group,
                    F<Nest, T> nest,
                    F<Concat, T> concat) {
                return brek.apply(this);
            }
        }
    }

    final class Text implements Document {

        public final String text;

        public Text(String text) {
            this.text = text;
        }

        @Override
        public <T> T match(
                F<Nil, T> nil,
                F<Break, T> brek,
                F<Text, T> text,
                F<Group, T> group,
                F<Nest, T> nest,
                F<Concat, T> concat) {
            return text.apply(this);
        }
    }

    final class Group implements Document {

        public final Document doc;

        public Group(Document doc) {
            this.doc = doc;
        }

        @Override
        public <T> T match(
                F<Nil, T> nil,
                F<Break, T> brek,
                F<Text, T> text,
                F<Group, T> group,
                F<Nest, T> nest,
                F<Concat, T> concat) {
            return group.apply(this);
        }
    }

    final class Nest implements Document {

        public final int indent;
        public final Document doc;

        public Nest(int indent, Document doc) {
            this.indent = indent;
            this.doc = doc;
        }

        @Override
        public <T> T match(
                F<Nil, T> nil,
                F<Break, T> brek,
                F<Text, T> text,
                F<Group, T> group,
                F<Nest, T> nest,
                F<Concat, T> concat) {
            return nest.apply(this);
        }
    }

    final class Concat implements Document {

        public final IList<Document> children;

        public Concat(IList<Document> children) {
            this.children = children;
        }

        @Override
        public <T> T match(
                F<Nil, T> nil,
                F<Break, T> brek,
                F<Text, T> text,
                F<Group, T> group,
                F<Nest, T> nest,
                F<Concat, T> concat) {
            return concat.apply(this);
        }
    }

    <T> T match(
        F<Nil, T> nil,
        F<Break, T> brek,
        F<Text, T> text,
        F<Group, T> group,
        F<Nest, T> nest,
        F<Concat, T> concat);

    default void format(Writer wtr, int width) {
        DocFormat.format(wtr, width, this);
    }
}
