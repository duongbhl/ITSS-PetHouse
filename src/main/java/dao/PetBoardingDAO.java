package dao;

import model.PetBoarding;

public class PetBoardingDAO extends BaseDAO<PetBoarding, Long> {
    @Override
    protected Class<PetBoarding> getEntityClass() {
        return PetBoarding.class;
    }
}
