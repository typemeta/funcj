package org.typemeta.funcj.control;

import java.util.Objects;

class Error extends Exception {

    Error(String msg) {
        super(msg);
    }

    @Override
    public String toString() {
        return "Error{msg='" + getMessage() + "\'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Error error = (Error) o;
        return Objects.equals(getMessage(), error.getMessage());
    }
}
