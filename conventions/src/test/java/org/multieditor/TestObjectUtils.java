package org.multieditor;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestObjectUtils {

    @Test
    public void testEmptyIfNullList() {
        List<String> list = null;

        Assert.assertEquals(ObjectUtils.emptyIfNull(list), Collections.<String>emptyList());
        list = new ArrayList<>();

        Assert.assertEquals(ObjectUtils.emptyIfNull(list), list);

        list.add("some string");

        Assert.assertEquals(ObjectUtils.emptyIfNull(list), list);
    }

    @Test
    public void testEmptyIfNullSet() {
        Set<String> set = null;

        Assert.assertEquals(ObjectUtils.emptyIfNull(set), Collections.<String>emptySet());

        set = new LinkedHashSet<>(2);

        Assert.assertEquals(ObjectUtils.emptyIfNull(set), set);

        set.add("some string");

        Assert.assertEquals(ObjectUtils.emptyIfNull(set), set);
    }

    @Test
    public void testEmptyIfNullMap() {
        Map<String, String> map = null;

        Assert.assertEquals(ObjectUtils.emptyIfNull(map), Collections.<String, String>emptyMap());

        map = new HashMap<>(2);

        Assert.assertEquals(ObjectUtils.emptyIfNull(map), map);

        map.put("some key", "some value");

        Assert.assertEquals(ObjectUtils.emptyIfNull(map), map);
    }

    @Test
    public void testEmptyIfNullString() {
        String str = null;

        Assert.assertEquals(ObjectUtils.emptyIfNull(str), "");

        str = "";

        Assert.assertEquals(ObjectUtils.emptyIfNull(str), str);

        str = "   ";

        Assert.assertEquals(ObjectUtils.emptyIfNull(str), str);

        str = "abc";

        Assert.assertEquals(ObjectUtils.emptyIfNull(str), str);
    }

    @Test
    public void testIsEmptyCollection() {
        List<String> list = null;

        Assert.assertTrue(ObjectUtils.isEmpty(list));

        list = new ArrayList<>();

        Assert.assertTrue(ObjectUtils.isEmpty(list));

        list.add("x");

        Assert.assertFalse(ObjectUtils.isEmpty(list));
    }

    @Test
    public void testIsEmptyMap() {
        Map<Integer, String> map = null;

        Assert.assertTrue(ObjectUtils.isEmpty(map));

        map = new LinkedHashMap<>();

        Assert.assertTrue(ObjectUtils.isEmpty(map));

        map.put(1, "x");

        Assert.assertFalse(ObjectUtils.isEmpty(map));
    }

    @Test
    public void testIsEmptyString() {
        String str = null;

        Assert.assertTrue(ObjectUtils.isEmpty(str));

        str = "";

        Assert.assertTrue(ObjectUtils.isEmpty(str));

        str = "  ";

        Assert.assertFalse(ObjectUtils.isEmpty(str));

        str = "a";

        Assert.assertFalse(ObjectUtils.isEmpty(str));
    }

    @Test
    public void testIsBlank() {
        String str = null;

        Assert.assertTrue(ObjectUtils.isBlank(str));

        str = "";

        Assert.assertTrue(ObjectUtils.isBlank(str));

        str = " ";

        Assert.assertTrue(ObjectUtils.isBlank(str));

        str = "  \n  ";

        Assert.assertTrue(ObjectUtils.isBlank(str));

        str = "  \t  ";

        Assert.assertTrue(ObjectUtils.isBlank(str));

        str = "a";

        Assert.assertFalse(ObjectUtils.isBlank(str));

        str = "  a  ";

        Assert.assertFalse(ObjectUtils.isBlank(str));
    }

    @Test
    public void testGetAsList() {
        Integer[] intArr = null;
        Assert.assertEquals(ObjectUtils.getAsList(intArr), Collections.<Integer>emptyList());

        intArr = new Integer[0];
        Assert.assertEquals(ObjectUtils.getAsList(intArr), Collections.<Integer>emptyList());

        intArr = new Integer[3];
        Assert.assertTrue(ObjectUtils.getAsList(intArr).size() == 3);
    }

    @Test
    public void testSafeTrim() {
        Assert.assertEquals("abc", ObjectUtils.safeTrim("\r\n\t abc\n\r "));
        Assert.assertEquals("", ObjectUtils.safeTrim("  \r  \n \t  \n  \r "));
        Assert.assertEquals(null, ObjectUtils.safeTrim(null));
        Assert.assertEquals("a\n\t\rb", ObjectUtils.safeTrim("\r\n\t a\n\t\rb\n\r "));
    }

    @Test
    public void testRemoveCR() {
        Assert.assertEquals("abc", ObjectUtils.removeCR("abc"));
        Assert.assertEquals(null, ObjectUtils.removeCR(null));
        Assert.assertEquals("abc", ObjectUtils.removeCR("\ra\rb\rc\r"));
    }
}