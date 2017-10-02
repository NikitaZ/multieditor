package org.multieditor.data.fixture;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.lang.reflect.Field;

/**
 * This is a base class for test fixture classes.
 * This fixture initializes persistence provider to in-memory database,
 * instantiates controllers and binds them together.
 */
public class TestFixtureBase {

    protected EntityManager em;

    protected EntityTransaction tx;

    public TestFixtureBase() {
        this(new TestConnector());
    }

    public TestFixtureBase(TestConnector connector) {
        em = connector.getEntityManager();
    }

    public static void inject(Object destination, Object obj, String fieldName) {
        try {
            Field field = destination.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(destination, obj);
        } catch (NoSuchFieldException e) {
            System.err.println("NoSuchFieldException: " + fieldName + " on " + destination.getClass().getName());
        } catch (IllegalAccessException e) {
            System.err.println("IllegalAccessException: " + fieldName + " could not be set on " + destination.getClass().getName());
        }
    }

    public void injectEntityManager(Object ejb) {
        inject(ejb, em, "em");
    }

    public void finish() {
        if (em != null) {
            em.close();
        }
    }

    public EntityManager getEntityManager() {
        return em;
    }

    public void beginTransaction() {
        tx = em.getTransaction();
        tx.begin();
    }

    public void commitTransaction() {
        tx.commit();
    }

    public void rollbackTransaction() {
        tx.rollback();
    }
}
