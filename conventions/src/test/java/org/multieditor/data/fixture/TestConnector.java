package org.multieditor.data.fixture;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class TestConnector {

    private EntityManagerFactory emf;

    public TestConnector() {
        try {
            Class.forName("org.h2.Driver").newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        emf = Persistence.createEntityManagerFactory("testDB");
    }

    public void finish() {
        if (emf != null) {
            emf.close();
        }
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

}
