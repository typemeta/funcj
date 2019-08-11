package org.typemeta.funcj.codec.avro.schema;

import org.apache.avro.Schema;
import org.typemeta.funcj.codec.utils.CodecException;

import java.util.*;

public abstract class SchemaMerge {
    
    public static Schema merge(Schema lhs, Schema rhs) {

        switch (lhs.getType()) {
            case STRING:
            case BYTES:
            case INT:
            case LONG:
            case FLOAT:
            case DOUBLE:
            case BOOLEAN:
            case NULL:
                if (lhs.getType() == rhs.getType()) {
                    return lhs;
                } else {
                    break;
                }
            case ENUM:
            case FIXED:
            case RECORD:
                if (lhs.equals(rhs)) {
                    return lhs;
                }
                break;
            case ARRAY:
            case MAP:
                if (lhs.getValueType().equals(rhs.getValueType())) {
                    return lhs;
                } else {
                    break;
                }
            case UNION:
                final List<Schema> subSchemas = new ArrayList<>(lhs.getTypes());
                if (rhs.getType() == Schema.Type.UNION) {
                    subSchemas.addAll(rhs.getTypes());
                } else {
                    subSchemas.add(rhs);
                }
                return Schema.createUnion(subSchemas);
            default:
                throw new CodecException("Unexpected schema type - " + lhs.getType());
        }

        return Schema.createUnion(lhs, rhs);
    }
}
