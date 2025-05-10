package dao;

import model.Service;

public class ServiceDAO extends BaseDAO<Service, Long> {
    @Override
    protected Class<Service> getEntityClass() {
        return Service.class;
    }
}
