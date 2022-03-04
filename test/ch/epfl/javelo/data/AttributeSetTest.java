package ch.epfl.javelo.data;

import org.junit.jupiter.api.Test;

import static ch.epfl.javelo.data.Attribute.HIGHWAY_TRACK;
import static ch.epfl.javelo.data.Attribute.TRACKTYPE_GRADE1;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AttributeSetTest {
    @Test
    void attributeSetIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> {
            new AttributeSet((long) Math.pow(2, 63));
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new AttributeSet((long) Math.pow(2, 62));
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new AttributeSet((long) Math.pow(2, 64) - 1);
        });
    }

    @Test
    void ofAndToStringsWorksNormally() { //obligé d'avoir toString qui marche correctement
        AttributeSet actual1 = AttributeSet.of(Attribute.HIGHWAY_SERVICE);
        AttributeSet expected1 = new AttributeSet(1L);
        assertEquals(expected1.toString(), actual1.toString());

        AttributeSet actual2 = AttributeSet.of(Attribute.HIGHWAY_FOOTWAY, Attribute.HIGHWAY_FOOTWAY);
        AttributeSet expected2 = new AttributeSet(8L);
        assertEquals(expected2.toString(), actual2.toString());

        AttributeSet actual3 = AttributeSet.of(Attribute.HIGHWAY_PATH);
        AttributeSet expected3 = new AttributeSet(16L);
        assertEquals(expected3.toString(), actual3.toString());

        AttributeSet actual4 = AttributeSet.of(Attribute.HIGHWAY_FOOTWAY, Attribute.HIGHWAY_PATH);
        AttributeSet expected4 = new AttributeSet(24L);
        assertEquals(expected4.toString(), actual4.toString());
    }

    @Test
    void containsWorksNormally() {
        AttributeSet set1 = AttributeSet.of(Attribute.ACCESS_NO);
        boolean actual1 = set1.contains(Attribute.ACCESS_NO);
        boolean expected1 = true;
        assertEquals(expected1, actual1);

        AttributeSet set2 = AttributeSet.of(Attribute.HIGHWAY_RESIDENTIAL);
        boolean actual2 = set2.contains(Attribute.ACCESS_YES);
        boolean expected2 = false;
        assertEquals(expected2, actual2);
    }

    @Test
    void toStringWorksNormally() {
        AttributeSet set = AttributeSet.of(TRACKTYPE_GRADE1, HIGHWAY_TRACK);
        assertEquals("{highway=track,tracktype=grade1}", set.toString());
    }

    @Test
    void intersectsWorksNormally() {
        AttributeSet set = AttributeSet.of(Attribute.HIGHWAY_PATH);
        AttributeSet set2 = AttributeSet.of(Attribute.HIGHWAY_PATH, Attribute.LCN_YES, Attribute.ACCESS_NO);
        boolean actual1 = set.intersects(set2);
        boolean expected1 = true;
        assertEquals(expected1, actual1);
    }

    @Test
    void AttributeSetConstructorThrowsOnInvalidCoordinates() {
        //System.out.println(Attribute.COUNT);
        //Y'en a 62
        assertThrows(IllegalArgumentException.class, () -> {
            new AttributeSet(((long)Math.pow(2, 62)));
        });
    }

    @Test
    void FindContainsAttribute (){
        AttributeSet a = new AttributeSet(2);
        var actual = a.contains(HIGHWAY_TRACK);
        var expected = true;
        assertEquals(expected,actual);
    }

    @Test
    void FindContainsNotAttribute (){
        AttributeSet a = new AttributeSet(4);
        var actual = a.contains(HIGHWAY_TRACK);
        var expected = false;
        assertEquals(expected,actual);
    }

    @Test
    void FindAnIntersect(){
        AttributeSet a = new AttributeSet(1);
        AttributeSet b = new AttributeSet(3);
        var actual = a.intersects(b);
        var expected = true;
        assertEquals(expected,actual);
    }

    @Test
    void FindNullIntersect(){
        AttributeSet a = new AttributeSet(1);
        AttributeSet b = new AttributeSet(4);
        var actual = a.intersects(b);
        var expected = false;
        assertEquals(expected,actual);
    }


    @Test
    void GoodStringAttributeSet (){
        AttributeSet set = AttributeSet.of(TRACKTYPE_GRADE1, HIGHWAY_TRACK);
        assertEquals("{highway=track,tracktype=grade1}", set.toString());
    }

    @Test
    void returns0ifNoArguments(){
        AttributeSet set = AttributeSet.of();
        assertEquals(0, set.bits());
    }

}
