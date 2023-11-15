package io.springstudent.meeting.room.dao;

import com.gysoft.jdbc.dao.EntityDaoImpl;
import io.springstudent.meeting.room.pojo.Room;
import org.springframework.stereotype.Repository;

/**
 * @author zhouning
 * @date 2023/04/19 13:12
 */
@Repository
public class RoomDaoImpl extends EntityDaoImpl<Room, Integer> implements RoomDao{
}
