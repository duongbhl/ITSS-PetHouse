package dao;

import model.Room;

public class RoomDAO extends BaseDAO<Room, Long> {
    @Override
    protected Class<Room> getEntityClass() {
        return Room.class;
    }
}
