package org.example.petproject.dao;

import org.example.petproject.model.Pet;

public class PetDAO extends BaseDAO<Pet, Long> {
    @Override
    protected Class<Pet> getEntityClass() {
        return Pet.class;
    }
}
