package io.springstudent.meeting.room.service;

import com.gysoft.jdbc.bean.*;
import io.springstudent.meeting.common.util.EmptyUtils;
import io.springstudent.meeting.common.util.SubjectUtils;
import io.springstudent.meeting.room.bean.RoomInfo;
import io.springstudent.meeting.room.dao.RoomBoardDao;
import io.springstudent.meeting.room.dao.RoomDao;
import io.springstudent.meeting.room.dao.RoomHistoryDao;
import io.springstudent.meeting.room.pojo.Room;
import io.springstudent.meeting.room.pojo.RoomBoard;
import io.springstudent.meeting.room.pojo.RoomHistory;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhouning
 * @date 2023/04/20 13:16
 */
@Service
public class RoomServiceImpl implements RoomService {

    @Resource
    private RoomDao roomDao;

    @Resource
    private RoomBoardDao roomBoardDao;

    @Resource
    private RoomHistoryDao roomHistoryDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createRoom() throws Exception {
        Long now = new Date().getTime();
        String roomCode = RandomStringUtils.randomAlphanumeric(9);
        roomDao.save(Room.builder()
                .roomCode(roomCode)
                .roomName("会议室" + roomCode)
                .startTime(now)
                .createUser(SubjectUtils.loginUserName())
                .build());
        roomHistoryDao.save(RoomHistory.builder().username(SubjectUtils.loginUserName()).roomCode(roomCode).action("join").createTime(now).build());
        roomBoardDao.save(RoomBoard.builder().roomCode(roomCode).boardData(null).build());
        return roomCode;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRoomName(String roomCode, String roomName) throws Exception {
        roomDao.updateWithSql(new SQL().update(Room.class).set("roomName", roomName).where("roomCode", roomCode));
    }

    @Override
    public RoomInfo queryRoom(String roomCode) throws Exception {
        RoomInfo roomInfo = roomDao.queryWithSql(RoomInfo.class, new SQL().select("id,roomCode,roomName,startTime,endTime,createUser").from(Room.class).where("roomCode", roomCode)).queryOne();
        if(roomInfo!=null){
            roomInfo.setJoinUser(roomHistoryDao.queryWithSql(String.class, new SQL().select("distinct(username)").from(RoomHistory.class).where("roomCode", roomCode)).queryForList());
            roomInfo.setBoardData(roomBoardDao.queryWithSql(String.class,new SQL().select("boardData").from(RoomBoard.class).where("roomCode", roomCode)).queryObject());
        }
        return roomInfo;
    }

    @Override
    public PageResult<RoomInfo> pageRoom(Page page) throws Exception {
        SQL sql = new SQL().select("id,roomCode,roomName,startTime,endTime,createUser").from(Room.class).orderBy(new Sort("startTime", "desc"));
        PageResult<RoomInfo> pageResult = roomDao.queryWithSql(RoomInfo.class, sql).pageQuery(page);
        if (EmptyUtils.isNotEmpty(pageResult.getList())) {
            Map<String, Set<String>> roomUserMap = roomHistoryDao.queryWithCriteria(new Criteria().in("roomCode",pageResult.getList().stream().map(RoomInfo::getRoomCode).collect(Collectors.toList()))).stream()
                    .collect(Collectors.groupingBy(RoomHistory::getRoomCode, Collectors.mapping(RoomHistory::getUsername, Collectors.toSet())));
            pageResult.getList().forEach(roomInfo -> roomInfo.setJoinUser(new ArrayList<>(roomUserMap.get(roomInfo.getRoomCode()))));
        }
        return pageResult;
    }

}
