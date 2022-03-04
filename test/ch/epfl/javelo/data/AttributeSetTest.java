package ch.epfl.javelo.data;

import org.junit.jupiter.api.Test;

import static ch.epfl.javelo.data.Attribute.HIGHWAY_TRACK;
import static ch.epfl.javelo.data.Attribute.TRACKTYPE_GRADE1;
import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void attributeSetConstructorWorksFine(){
        AttributeSet set1 = new AttributeSet(0b1000L);
        AttributeSet set2 = new AttributeSet(0b0001000000000000000000000000000000000000000000000000000000000000L);
        assertThrows(IllegalArgumentException.class, () -> {
            new AttributeSet(0b1000000000000000000000000000000000000000000000000000000000000000L);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new AttributeSet(0b0100000000000000000000000000000000000000000000000000000000000000L);
        });
    }

    @Test
    void ofWorksOnKnownValues(){
        AttributeSet set1 = AttributeSet.of(Attribute.HIGHWAY_SERVICE,Attribute.HIGHWAY_TRACK);
        AttributeSet set2 = AttributeSet.of(Attribute.HIGHWAY_SERVICE,Attribute.HIGHWAY_TRACK, Attribute.HIGHWAY_FOOTWAY, Attribute.HIGHWAY_UNCLASSIFIED);
        AttributeSet set3 = AttributeSet.of(Attribute.RCN_YES,Attribute.LCN_YES );
        AttributeSet emptySet = AttributeSet.of();


        assertEquals(0b0000000000000000000000000000000000000000000000000000000000000011L,set1.bits());
        assertEquals(0b0000000000000000000000000000000000000000000000000000000000101011L,set2.bits());
        assertEquals(0,emptySet.bits());
        assertEquals(0b0000000000000000000000000000000000000000000000000000000000000000L,emptySet.bits());
        assertEquals(0b0011000000000000000000000000000000000000000000000000000000000000L, set3.bits());


    }

    @Test
    void containsWorksOnKnownValues(){
        AttributeSet set1 = new AttributeSet(0b0010000000000000000000000000000000000000000000000000000000000000L);
        AttributeSet set2 = new AttributeSet(0b0010000000000000000000000000000000000000000000000000000111110000L);
        assertTrue(set1.contains(Attribute.LCN_YES));
        assertFalse(set1.contains(Attribute.ONEWAY_BICYCLE_NO));
        assertFalse(set1.contains(Attribute.HIGHWAY_CYCLEWAY));
        assertTrue(set2.contains(Attribute.LCN_YES));
        assertTrue(set2.contains(Attribute.HIGHWAY_PATH));
        assertTrue(set2.contains(Attribute.HIGHWAY_UNCLASSIFIED));
        assertTrue(set2.contains(Attribute.HIGHWAY_SECONDARY));
        assertTrue(set2.contains(Attribute.HIGHWAY_TERTIARY));
        assertTrue(set2.contains(Attribute.HIGHWAY_STEPS));
        assertFalse(set2.contains(Attribute.ONEWAY_BICYCLE_NO));
        assertFalse(set2.contains(Attribute.HIGHWAY_CYCLEWAY));
    }

    @Test
    void intersectsWorksOnKnownValues(){
        AttributeSet set1 = new AttributeSet(0b0010000000000000000000000000000000000000000000000000000000000000L);
        AttributeSet set2 = new AttributeSet(0b0010000000000001111111111111110000000000000000000000000111110000L);
        AttributeSet set3 = new AttributeSet(0b0000000000000000000000000000000000000000000000000000000000000000L);
        AttributeSet set4 = new AttributeSet(0b0001111111111110000000000000001111111111111111111111111000001111L);
        AttributeSet set5 = new AttributeSet(0);
        assertTrue(set1.intersects(set2));
        assertFalse(set2.intersects(set3));
        assertFalse(set3.intersects(set1));
        assertFalse(set4.intersects(set1));
        assertFalse(set4.intersects(set2));
        assertFalse(set4.intersects(set3));
        assertTrue(set4.intersects(set4));
        assertFalse(set5.intersects(set1));
        assertFalse(set5.intersects(set2));
        assertFalse(set5.intersects(set3));
        assertFalse(set5.intersects(set4));
    }

}
