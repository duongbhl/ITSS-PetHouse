package dao;

import model.Pet;

public class PetDAO extends BaseDAO<Pet, Long> {
    @Override
    protected Class<Pet> getEntityClass() {
        return Pet.class;
    }
}
