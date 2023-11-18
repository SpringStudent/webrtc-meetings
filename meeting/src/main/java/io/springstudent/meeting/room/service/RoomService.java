package io.springstudent.meeting.room.service;

import com.gysoft.jdbc.bean.Page;
import com.gysoft.jdbc.bean.PageResult;
import io.springstudent.meeting.room.bean.RoomInfo;
import io.springstudent.meeting.room.pojo.RoomBoardTxt;

import java.util.List;

/**
 * @author zhouning
 * @date 2023/04/20 13:16
 */
public interface RoomService {

    String createRoom()throws Exception;

    void updateRoomName(String roomCode,String roomName)throws Exception;

    RoomInfo queryRoom(String roomCode)throws Exception;

    PageResult<RoomInfo> pageRoom(Page page)throws Exception;

    List<RoomBoardTxt> listRoomBoardTxt(String roomCode)throws Exception;
}
