package io.springstudent.meeting.room.dao;

import com.gysoft.jdbc.dao.EntityDaoImpl;
import io.springstudent.meeting.room.pojo.RoomHistory;
import org.springframework.stereotype.Repository;

/**
 * @author zhouning
 * @date 2023/04/19 13:14
 */
@Repository
public class RoomHistoryDaoImpl extends EntityDaoImpl<RoomHistory, Integer> implements RoomHistoryDao {
}
