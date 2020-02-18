package com.revolut.model;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.logging.Logger;

public class DatabaseOperation<T> {

        private static Logger LOG = Logger.getLogger(DatabaseOperation.class.getName());
        private static final Object _LOCK = new Object();

        public List<T> insert(List<T> items) {
            EntityManager entityManager = DatabaseUtil.getEntityManager();
            try {
                entityManager.getTransaction().begin();
                synchronized (_LOCK) {
                    for (T item : items) {
                        entityManager.merge(item);
                        entityManager.flush();
                    }
                }
                entityManager.getTransaction().commit();
            } catch (Exception e) {
                entityManager.getTransaction().rollback();
                throw e;
            } finally {
                entityManager.close();
            }
            return items;
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
