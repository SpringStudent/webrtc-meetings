package io.springstudent.meeting.room.dao;

import com.gysoft.jdbc.dao.EntityDaoImpl;
import io.springstudent.meeting.room.pojo.RoomBoard;
import org.springframework.stereotype.Repository;

/**
 * @author zhouning
 * @date 2023/04/24 9:19
 */
@Repository
public class RoomBoardDaoImpl extends EntityDaoImpl<RoomBoard, Integer> implements RoomBoardDao{
}
