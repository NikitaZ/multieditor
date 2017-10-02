package org.multieditor.web;

import org.multieditor.data.entity.NamedEntity;
import org.multieditor.data.info.multieditor.UserAccountSummary;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This class makes UserAccounts map available to JSF.
 * An instance of this class will be created for you automatically, the first
 * time your application evaluates a value binding expression or method binding
 * expression that references a managed bean using this class.
 */
@Named
@ApplicationScoped
public class UserMapperBean {

    private static final long CACHE_LIFE_TIME_MS = 60 * 1000;
    private final AtomicReference<Map<String, UserAccountSummary>> ref = new AtomicReference<>();

    @Inject
    @Named("ServiceBean")
    private transient ServiceBean serviceBean;

    private volatile long lastUpdate = 0;

    private List<UserAccountSummary> getEntities() {
        return serviceBean.getUserService().findAll();
    }

    /**
     * Given an object representing a domain name, return corresponding domain object
     *
     * @param key domain name to convert to a real domain.
     * @return null, if domain was not found.
     */
    public UserAccountSummary get(String key) {
        if (key == null) {
            return null;
        }

        return getCachedMap().get(key);
    }


    private Map<String, UserAccountSummary> constructEntityMap() {
        Collection<UserAccountSummary> users = getEntities();
        return NamedEntity.convertNamedEntitiesToMap(users);
    }

    private Map<String, UserAccountSummary> getCachedMap() {
        if ((ref.get() == null) || (System.currentTimeMillis() - lastUpdate > CACHE_LIFE_TIME_MS)) {
            ref.set(constructEntityMap());
            lastUpdate = System.currentTimeMillis();
        }
        return ref.get();
    }
}