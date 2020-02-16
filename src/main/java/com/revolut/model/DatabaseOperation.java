package com.revolut.model;

import javax.persistence.EntityManager;
import java.util.logging.Logger;

public class DatabaseOperation<T> {

        private static Logger LOG = Logger.getLogger(DatabaseOperation.class.getName());
        private static final Object _LOCK = new Object();

        public T insert(T item) {
            EntityManager entityManager = DatabaseUtil.getEntityManager();
            try {
                entityManager.getTransaction().begin();
                synchronized (_LOCK) {
                    entityManager.persist(item);
                    entityManager.flush();
                }
                entityManager.getTransaction().commit();
            } catch (Exception e) {
                entityManager.getTransaction().rollback();
                throw e;
            } finally {
                entityManager.close();
            }
            return item;
        }

        public T read(Class<T> clazz, int id) {
            EntityManager entityManager = DatabaseUtil.getEntityManager();
            T item = null;
            try {
                item = entityManager.find(clazz, id);
            } finally {
                entityManager.close();
                if (item == null)
                    LOG.warning("No records records were found with given id value !!");
            }
            return item;
        }

    }
