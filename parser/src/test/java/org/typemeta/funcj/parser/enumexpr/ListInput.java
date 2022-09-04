package org.typemeta.funcj.parser.enumexpr;

import org.typemeta.funcj.parser.Input;

import java.util.*;

public class ListInput<T> implements Input<T> {

    private final List<T> data;
    private int position;
    private final ListInput<T> other;

    ListInput(List<T> data) {
        this.data = data;
        this.position = 0;
        this.other = new ListInput<T>(this, data);
    }

    ListInput(ListInput<T> other, List<T> data) {
        this.data = data;
        this.position = 0;
        this.other = other;
    }

    private ListInput<T> setPosition(int position) {
        this.position = position;
        return this;
    }

    @Override
    public String toString() {
        final String dataStr = isEof() ? "EOF" : "...";
        return "ListInput{" + position + ",data=\"" + dataStr + "\"";
    }

    @Override
    public boolean isEof() {
        return position >= data.size();
    }

    @Override
    public T get() {
        return data.get(position);
    }

    @Override
    public Input<T> next() {
        return other.setPosition(position + 1);
    }

    @Override
    public Object position() {
        return position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ListInput<T> that = (ListInput) o;
        return position == that.position &&
                data == that.data;
    }

    @Override
    public int hashCode() {
        return Objects.hash(data, position);
    }
}
