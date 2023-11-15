package io.springstudent.meeting.chat.dao;

import com.gysoft.jdbc.dao.EntityDaoImpl;
import io.springstudent.meeting.chat.pojo.Chat;
import org.springframework.stereotype.Repository;

/**
 * @author zhouning
 * @date 2023/04/19 13:10
 */
@Repository
public class ChatDaoImpl extends EntityDaoImpl<Chat,Integer> implements ChatDao {
}
