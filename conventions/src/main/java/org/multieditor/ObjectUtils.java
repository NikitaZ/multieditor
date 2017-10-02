package org.multieditor;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Utility methods to handle null/empty strings and collections
 */
public class ObjectUtils {
    private ObjectUtils() {
    }

    public static String emptyIfNull(String value) {
        return value == null ? "" : value;
    }

    public static <T> List<T> emptyIfNull(List<T> list) {
        return list == null ? Collections.<T>emptyList() : list;
    }

    public static <T> Set<T> emptyIfNull(Set<T> set) {
        return set == null ? Collections.<T>emptySet() : set;
    }

    public static <K, V> Map<K, V> emptyIfNull(Map<K, V> map) {
        return map == null ? Collections.<K, V>emptyMap() : map;
    }

    public static <T> boolean isEmpty(Collection<T> collection) {
        return collection == null || collection.isEmpty();
    }

    public static <K, V> boolean isEmpty(Map<K, V> map) {
        return map == null || map.isEmpty();
    }

    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static String removeCR(String s) {
        return s != null ? s.replace("\r", "") : null;
    }

    /**
     * Safe to use in case of null-argument
     */
    public static String safeTrim(String str) {
        // standard trim() removes '\n', '\r', '\t' as well as ' '
        return str != null ? str.trim() : null;
    }

    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static <T> List<T> getAsList(T[] values) {
        if (values == null || values.length == 0) {
            return Collections.emptyList();
        } else {
            return Arrays.asList(values);
        }
    }
}
