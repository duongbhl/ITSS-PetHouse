package org.example.petproject.dao;

import org.example.petproject.model.Service;

public class ServiceDAO extends BaseDAO<Service, Long> {
    @Override
    protected Class<Service> getEntityClass() {
        return Service.class;
    }
}
