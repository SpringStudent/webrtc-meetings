package io.springstudent.meeting.room.dao;

import com.gysoft.jdbc.dao.EntityDaoImpl;
import io.springstudent.meeting.room.pojo.RoomBoardTxt;
import org.springframework.stereotype.Repository;

/**
 * @author zhouning
 * @date 2023/11/18 14:18
 */
@Repository
public class RoomBoardTxtDaoImpl  extends EntityDaoImpl<RoomBoardTxt,String> implements RoomBoardTxtDao{
}
