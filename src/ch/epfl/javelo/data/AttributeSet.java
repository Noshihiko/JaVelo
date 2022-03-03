package ch.epfl.javelo.data;

import java.util.StringJoiner;

import static ch.epfl.javelo.Preconditions.checkArgument;
import static ch.epfl.javelo.data.Attribute.ALL;

public record AttributeSet(long bits) {
    public AttributeSet {
        checkArgument((bits>>Attribute.COUNT) == 0);
    }

    public static AttributeSet of(Attribute... attributes) {
        long seq = 0;
        for (int i=0; i< attributes.length; ++i) {
            long mask = 1L << attributes[i].ordinal();
            seq = (mask | seq);
        }
        return new AttributeSet(seq);
    }

    public boolean contains(Attribute attribute) {
        boolean check;
        if (((1L << attribute.ordinal()) & this.bits)!=0) {
            check = true;
        }
        else check = false;
        return check;
    }

    public boolean intersects(AttributeSet that) {
        boolean check;
        if ((that.bits & this.bits)!=0) {
            check = true;
        }
        else check = false;
        return check;
    }

    public String toString() {
        StringJoiner M = new StringJoiner(", ", "{", "}");
        for (int i=0; i< Attribute.COUNT; ++i) {
            if (this.contains(ALL.get(i))) {
                M.add(ALL.get(i).keyValue());
            }
        }
        return M.toString();
    }
}