package org.javafp.data;

import org.javafp.util.Functions;
import org.javafp.util.Functions.*;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.*;

/**
 * Simple recursive, immutable linked list.
 * It allows tails to be shared between lists.
 * Null elements are not allowed.
 * @param <T> element type
 */
public abstract class IList<T> implements Iterable<T> {

    /**
     * Construct an empty list.
     * @param <T> element type
     */
    public static <T> IList<T> nil() {
        return Empty.EMPTY;
    }

    /**
     * Construct an empty list.
     * @param <T> element type
     */
    public static <T> IList<T> of() {
        return nil();
    }

    /**
     * Construct a list with on element.
     * @param <T> element type
     */
    public static <T> NonEmpty<T> of(T elem) {
        return IList.<T>nil().add(Objects.requireNonNull(elem));
    }

    /**
     * Construct a list with multiple elements.
     * @param <T> element type
     */
    public static <T> NonEmpty<T> of(T... elems) {
        IList<T> r = nil();
        for (int i = elems.length - 1; i >= 0; --i) {
            r = r.add(Objects.requireNonNull(elems[i]));
        }
        return (NonEmpty<T>)r;
    }

    /**
     * Construct a list from an Iterable.
     * @param <T> element type
     */
    public static <T> IList<T> of(Iterable<T> elems) {
        IList<T> r = nil();
        for (T elem : elems) {
            r = r.add(Objects.requireNonNull(elem));
        }
        return r.reverse();
    }

    /**
     * Concatenate two lists.
     * @param <T> element type
     */
    public static <T> IList<T> concat(IList<? extends T> l1, IList<? extends T>  l2) {
        IList<T> r = (IList<T>)l2;
        for (T elem : l1.reverse()) {
            r = r.add(elem);
        }
        return r;
    }

    /**
     * Convert a list of Characters into a String.
     */
    public static String listToString(IList<Character> l) {
        final StringBuilder sb = new StringBuilder();
        for (; !l.isEmpty(); l = l.tail()) {
            sb.append(l.head());
        }
        return sb.toString();
    }

    /**
     * Convert a String into a list of Characters.
     */
    public static IList<Character> listToString(String s) {
        IList<Character> r = nil();
        for (int i = s.length() - 1; i >= 0; --i) {
            r = r.add(s.charAt(i));
        }
        return r;
    }

    /**
     * Create a new list by adding an element to the head of this list.
     */
    public NonEmpty<T> add(T head) {
        return new NonEmpty<T>(head, this);
    }

    /**
     * Create a new list by adding multiple elements to the head of this list.
     */
    public <S extends T> IList<T> addAll(IList<S> l) {
        IList<T> r = this;
        for(IList<S> next = l.reverse(); !next.isEmpty(); next = next.tail()) {
            r = r.add(next.head());
        }
        return r;
    }

    /**
     * Is this list empty?.
     */
    public abstract boolean isEmpty();

    /**
     * Returns Optional.empty() if this list is empty,
     * otherwise it returns an Optional which wraps the non-empty list.
     */
    public abstract Optional<NonEmpty<T>> nonEmpty();

    /**
     * @return the head of this list.
     * @throws UnsupportedOperationException if the list is empty.
     */
    public abstract T head();

    /**
     * @return the tail of this list.
     * @throws UnsupportedOperationException if the list is empty.
     */
    public abstract IList<T> tail();

    /**
     * @return the indexed element of this list.
     * @throws IndexOutOfBoundsException if the index is out of bounds.
     */
    public abstract T get(int index);

    /**
     * Append the contents of this list to a StringBuilder.
     * @param sb the StringBuilder to be appended to
     * @return the StringBuilder
     */
    public abstract StringBuilder append(StringBuilder sb);

    /**
     * List equality.
     * @return true if this list and rhs are equal in terms of their size and elements.
     */
    @Override
    public boolean equals(Object rhs) {
        return this == rhs ||
            (rhs != null &&
                getClass() == rhs.getClass() &&
                equals((IList<T>) rhs));
    }

    /**
     * Type-safe list equality.
     * @return true if this list and rhs are equal in terms of their elements.
     */
    public abstract boolean equals(IList<T> rhs);

    /**
     * Apply one of two functions depending on whether this list is empty or not.
     * @return the r of applying the appropriate function.
     */
    public abstract <S> S match(F<NonEmpty<T>, S> nonEmpty, F<Empty<T>, S> empty);

    /**
     * Create a new list by appending an element to the end of this list.
     * @return concatenated list
     */
    public abstract IList<T> append(IList<T> l);

    /**
     * @return the length of this list.
     */
    public abstract int size();

    /**
     * @return this list in reverse.
     */
    public abstract IList<T> reverse();

    /**
     * Map a function over this list.
     * @return mapped list
     */
    public abstract <U> IList<U> map(F<? super T, ? extends U> f);

    /**
     * FlatMap a function over this list.
     * @return mapped list
     */
    public abstract <U> IList<U> flatMap(F<? super T, IList<? extends U>> f);

    /**
     * Right-fold a function over this list.
     * @return folded r
     */
    public abstract <U> U foldr(F2<T, U, U> f, U z);

    /**
     * Left-fold a function over this list.
     * @return folded r
     */
    public abstract <U> U foldl(F2<U, T, U> f, U z);

    /**
     * Right-fold a function over this non-empty list.
     * @return folded r
     */
    public abstract T foldr1(Op2<T> f);

    /**
     * Left-fold a function over this non-empty list.
     * @return folded r
     */
    public abstract T foldl1(Op2<T> f);

    /**
     * Create a spliterator.
     * @return the spliterator
     */
    public abstract Spliterator<T> spliterator();

    /**
     * Create a Stream onto this list.
     * @return the stream
     */
    public Stream<T> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    /**
     * Create a parallel Stream onto this list.
     * @return the stream
     */
    public Stream<T> parallelStream() {
        return StreamSupport.stream(spliterator(), true);
    }

    /**
     * Create an iterator over this list.
     * @return the iterator
     */
    public abstract Iterator<T> iterator();

    /**
     * Convert to a Java List implementation, albeit an immutable one.
     * @return Java List.
     */
    public abstract List<T> toList();

    public static final class Empty<T> extends IList<T> {
        static final Empty EMPTY = new Empty();

        private Empty() {
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public Optional<NonEmpty<T>> nonEmpty() {
            return Optional.empty();
        }

        @Override
        public T head() {
            throw new UnsupportedOperationException("Cannot take the head of an empty list");
        }

        @Override
        public IList<T> tail() {
            throw new UnsupportedOperationException("Cannot take the tail of an empty list");
        }

        @Override
        public T get(int index) {
            throw new IndexOutOfBoundsException(
                "Index " + index + " out of bounds for an " + size() + " element list");
        }

        @Override
        public String toString() {
            return "[]";
        }

        @Override
        public boolean equals(IList<T> rhs) {
            return rhs.isEmpty();
        }

        @Override
        public StringBuilder append(StringBuilder sb) {
            return sb;
        }

        @Override
        public <S> S match(F<NonEmpty<T>, S> nonEmpty, F<Empty<T>, S> empty) {
            return empty.apply(this);
        }

        @Override
        public IList<T> append(IList<T> l) {
            return l;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public IList<T> reverse() {
            return EMPTY;
        }

        @Override
        public <U> IList<U> map(F<? super T, ? extends U> f) {
            return EMPTY;
        }

        @Override
        public <U> IList<U> flatMap(F<? super T, IList<? extends U>> f) {
            return EMPTY;
        }

        @Override
        public <U> U foldr(F2<T, U, U> f, U z) {
            return z;
        }

        @Override
        public <U> U foldl(F2<U, T, U> f, U z) {
            return z;
        }

        @Override
        public T foldr1(Op2<T> f) {
            throw new UnsupportedOperationException("Cannot call foldr1(f) on an empty list");
        }

        @Override
        public T foldl1(Op2<T> f) {
            throw new UnsupportedOperationException("Cannot call foldl1(f) on an empty list");
        }

        @Override
        public Spliterator<T> spliterator() {
            return new Spliterator<T>() {
                @Override
                public boolean tryAdvance(Consumer<? super T> action) {
                    return false;
                }

                @Override
                public Spliterator<T> trySplit() {
                    return null;
                }

                @Override
                public long estimateSize() {
                    return size();
                }

                @Override
                public int characteristics() {
                    return Spliterator.IMMUTABLE + Spliterator.SIZED;
                }
            };
        }

        @Override
        public Iterator<T> iterator() {

            return new Iterator<T>(){
                @Override
                public boolean hasNext() {
                    return false;
                }

                @Override
                public T next() {
                    throw new NoSuchElementException();
                }
            };
        }

        @Override
        public List<T> toList() {
            return Collections.emptyList();
        }
    }

    public static final class NonEmpty<T> extends IList<T> {

        private final T head;
        private final IList<T> tail;

        NonEmpty(T head, IList<T> tail) {
            this.head = Objects.requireNonNull(head);
            this.tail = Objects.requireNonNull(tail);
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public Optional<NonEmpty<T>> nonEmpty() {
            return Optional.of(this);
        }

        @Override
        public T head() {
            return head;
        }

        @Override
        public IList<T> tail() {
            return tail;
        }

        @Override
        public T get(int index) {
            final Functions.F0<RuntimeException> raiseError =
                () -> new IndexOutOfBoundsException("Index " + index + " out of bounds");

            if (index < 0) {
                throw raiseError.apply();
            } else if (index == 0) {
                return head;
            } else {
                IList<T> next = tail;
                for (int i = 1; i < index; ++i) {
                    if (next.isEmpty()) {
                        throw raiseError.apply();
                    } else {
                        next = ((NonEmpty<T>)next).tail;
                    }
                }
                if (next.isEmpty()) {
                    throw raiseError.apply();
                } else {
                    return ((NonEmpty<T>)next).head;
                }
            }
        }

        @Override
        public String toString() {
            final StringBuilder r = new StringBuilder("[");
            append(r).setCharAt(r.length() - 1, ']');
            return r.toString();
        }

        @Override
        public StringBuilder append(StringBuilder sb) {
            return tail.append(sb.append(head).append(','));
        }

        @Override
        public boolean equals(IList<T> rhs) {
            if (rhs.isEmpty()) {
                return false;
            } else {
                for (T lhs : this) {
                    if (rhs.isEmpty() || !lhs.equals(rhs.head())) {
                        return false;
                    }

                    rhs = rhs.tail();
                }

                return rhs.isEmpty();
            }
        }

        @Override
        public <S> S match(F<NonEmpty<T>, S> nonEmpty, F<Empty<T>, S> empty) {
            return nonEmpty.apply(this);
        }

        @Override
        public IList<T> append(IList<T> l) {
            return new NonEmpty<T>(head, tail.append(l));
        }

        @Override
        public int size() {
            IList<T> pos = this;
            int length = 0;
            while (!pos.isEmpty()) {
                ++length;
                pos = pos.tail();
            }

            return length;
        }

        @Override
        public NonEmpty<T> reverse() {
            IList<T> r = IList.of();
            for (IList<T> n = this; !n.isEmpty(); n = n.tail()) {
                r = r.add(n.head());
            }
            return (NonEmpty<T>)r;
        }

        @Override
        public <U> IList<U> map(F<? super T, ? extends U> f) {
            IList<U> r = nil();
            for (IList<T> n = this; !n.isEmpty(); n = n.tail()) {
                r = r.add(f.apply(n.head()));
            }
            return r.reverse();
        }

        @Override
        public <U> IList<U> flatMap(F<? super T, IList<? extends U>> f) {
            final IList<U> r = nil();
            for (IList<T> n = this; !n.isEmpty(); n = n.tail()) {
                r.addAll(f.apply(n.head()));
            }
            return r;
        }

        @Override
        public <U> U foldr(F2<T, U, U> f, U z) {
            return f.apply(head, tail.foldr(f, z));
        }

        @Override
        public <U> U foldl(F2<U, T, U> f, U z) {
            U r = z;
            for (IList<T> n = this; !n.isEmpty(); n = n.tail()) {
                r = f.apply(r, n.head());
            }
            return r;
        }

        @Override
        public T foldr1(Op2<T> f) {
            return tail.nonEmpty()
                .map(tl -> f.apply(head, tl.foldr1(f)))
                .orElse(head);
        }

        @Override
        public T foldl1(Op2<T> f) {
            return tail.nonEmpty()
                .map(tl -> f.apply(head, tl.foldl1(f)))
                .orElse(head);
        }

        @Override
        public Spliterator<T> spliterator() {
            return Spliterators.spliterator(
                this.iterator(),
                size(),
                Spliterator.IMMUTABLE + Spliterator.SIZED
            );
        }

        @Override
        public Iterator<T> iterator() {
            return new Iterator<T>(){

                IList<T> n = NonEmpty.this;

                @Override
                public boolean hasNext() {
                    return !n.isEmpty();
                }

                @Override
                public T next() {
                    final T head = n.head();
                    n = n.tail();
                    return head;
                }
            };
        }

        @Override
        public List<T> toList() {
            return new ListAdaptor<T>(this);
        }
    }
}

class ListAdaptor<T> extends AbstractSequentialList<T> {

    private final IList<T> impl;
    private final int size;

    ListAdaptor(IList<T> impl) {
        this.impl = impl;
        size = impl.size();
    }

    @Override
    public ListIterator<T> listIterator(int index) {

        return new ListIterator<T>() {

            private IList<T> move(IList<T> node, int count) {
                for (int i = 0; i < count; ++i) {
                    node = node.tail();
                }
                return node;
            }

            private int pos = index;
            private IList<T> node = move(impl, index);

            @Override
            public boolean hasNext() {
                return pos < size;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                } else {
                    final T ret = node.head();
                    node = node.tail();
                    ++pos;
                    return ret;
                }
            }

            @Override
            public boolean hasPrevious() {
                return pos >= 0;
            }

            @Override
            public T previous() {
                if (!hasPrevious()) {
                    throw new NoSuchElementException();
                } else {
                    --pos;
                    node = move(impl, pos);
                    ++pos;
                    return node.head();
                }
            }

            @Override
            public int nextIndex() {
                return pos;
            }

            @Override
            public int previousIndex() {
                return pos - 1;
            }

            @Override
            public void remove() {
                throw modError();
            }

            @Override
            public void set(T t) {
                throw modError();
            }

            @Override
            public void add(T t) {
                throw modError();
            }

            private UnsupportedOperationException modError() {
                return new UnsupportedOperationException("IList can not be modified");
            }
        };
    }

    @Override
    public int size() {
        return size;
    }
}
