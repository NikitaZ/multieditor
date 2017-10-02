package org.multieditor.data.entity;

import org.multieditor.ObjectUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.multieditor.ObjectUtils.isEmpty;

/**
 * This interface allows to manipulate entities with a unique name property in a universal way.
 */
public interface NamedEntity {
    String getName();

    public static Set<String> convertNamedEntitiesToNamesSet(
            Collection<? extends NamedEntity> entities) {
        if (isEmpty(entities)) {
            return Collections.emptySet();
        }

        // LinkedHashSet - to preserve the order
        Set<String> concise = new LinkedHashSet<>(entities.size());
        for (NamedEntity entity : entities) {
            concise.add(entity.getName());
        }
        return concise;
    }

    public static List<String> convertNamedEntitiesToNamesList(Collection<? extends NamedEntity> entities) {
        if (isEmpty(entities)) {
            return Collections.emptyList();
        }

        List<String> concise = new ArrayList<>(entities.size());
        for (NamedEntity entity : entities) {
            concise.add(entity.getName());
        }
        return concise;
    }

    /**
     * Note that if entities is empty an empty map is returned which cannot be modified!
     */
    public static <T extends NamedEntity> Map<String, T> convertNamedEntitiesToMap(Collection<T> entities) {
        if (ObjectUtils.isEmpty(entities)) {
            return Collections.emptyMap();
        }

        Map<String, T> map = new LinkedHashMap<>();
        for (T entity : entities) {
            map.put(entity.getName(), entity);
        }
        return map;
    }


    public static final Comparator<NamedEntity> COMPARATOR = new Comparator<NamedEntity>() {
        public int compare(NamedEntity o1, NamedEntity o2) {
            return o1.getName().compareTo(o2.getName());
        }
    };

    public static final Comparator<NamedEntity> REVERSE_COMPARATOR = Collections.reverseOrder(COMPARATOR);
}