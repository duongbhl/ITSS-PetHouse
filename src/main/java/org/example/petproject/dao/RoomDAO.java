package org.example.petproject.dao;

import org.example.petproject.model.Room;

public class RoomDAO extends BaseDAO<Room, Long> {
    @Override
    protected Class<Room> getEntityClass() {
        return Room.class;
    }
}
