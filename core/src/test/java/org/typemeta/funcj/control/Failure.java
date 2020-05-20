package org.typemeta.funcj.control;

import java.util.Objects;

final class Failure extends Exception {

    Failure(String msg) {
        super(msg);
    }

    @Override
    public String toString() {
        return "Error{msg='" + getMessage() + "\'}";
    }

    @Override
    public boolean equals(Object rhs) {
        if (this == rhs) {
            return true;
        } else if (rhs == null || !(rhs instanceof Failure)) {
            return false;
        } else {
            final Failure rhsT = (Failure) rhs;
            return Objects.equals(getMessage(), rhsT.getMessage());
        }
    }
}
