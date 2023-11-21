package io.springstudent.meeting.common.signal;

import cn.hutool.core.date.DateUtil;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.gysoft.jdbc.bean.Criteria;
import com.gysoft.jdbc.bean.SQL;
import io.springstudent.meeting.chat.dao.ChatDao;
import io.springstudent.meeting.chat.pojo.Chat;
import io.springstudent.meeting.common.bean.RoomEvent;
import io.springstudent.meeting.common.util.EmptyUtils;
import io.springstudent.meeting.room.dao.RoomBoardDao;
import io.springstudent.meeting.room.dao.RoomBoardTxtDao;
import io.springstudent.meeting.room.dao.RoomDao;
import io.springstudent.meeting.room.dao.RoomHistoryDao;
import io.springstudent.meeting.room.pojo.Room;
import io.springstudent.meeting.room.pojo.RoomBoard;
import io.springstudent.meeting.room.pojo.RoomBoardTxt;
import io.springstudent.meeting.room.pojo.RoomHistory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;


/**
 * @author zhouning
 * @date 2023/04/18 13:39
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class SignalListener implements ISignalListener {

    @Resource
    private SocketIOServer socketIOServer;
    @Resource
    private ChatDao chatDao;
    @Resource
    private RoomHistoryDao roomHistoryDao;
    @Resource
    private RoomDao roomDao;
    @Resource
    private RoomBoardDao roomBoardDao;
    @Resource
    private RoomBoardTxtDao roomBoardTxtDao;

    @Override
    @OnConnect
    public void onConnect(SocketIOClient client) throws Exception {
        String roomCode = roomCode(client);
        String username = username(client);
        String msg = username + "加入会议室";
        Long now = new Date().getTime();
        client.joinRoom(roomCode);
        socketIOServer.getRoomOperations(roomCode).sendEvent("joinRoom", username, msg);
        roomHistoryDao.save(RoomHistory.builder().username(username).roomCode(roomCode).action("join").createTime(now).build());
        chatDao.save(Chat.builder().roomCode(roomCode).type(1).sender(username).sendTime(now).content(msg).build());
        if (socketIOServer.getRoomOperations(roomCode).getClients().size() == 1) {
            roomDao.updateWithSql(new SQL().update(Room.class).set("endTime", null).where("roomCode", roomCode));
        }
    }

    @Override
    @OnDisconnect
    public void onDisconnect(SocketIOClient client) throws Exception {
        String roomCode = roomCode(client);
        String username = username(client);
        String msg = username + "离开会议室";
        Long now = new Date().getTime();
        client.leaveRoom(roomCode);
        socketIOServer.getRoomOperations(roomCode).sendEvent("leaveRoom", username, msg);
        roomHistoryDao.save(RoomHistory.builder().username(username(client)).roomCode(roomCode).action("leave").createTime(now).build());
        chatDao.save(Chat.builder().roomCode(roomCode).type(1).sender(username).sendTime(now).content(msg).build());
        if (EmptyUtils.isEmpty(socketIOServer.getRoomOperations(roomCode).getClients())) {
            roomDao.updateWithSql(new SQL().update(Room.class).set("endTime", new Date().getTime()).where("roomCode", roomCode));
        }
    }

    @Override
    @OnEvent("roomUser")
    public void roomUser(SocketIOClient client) throws Exception {
        String roomCode = roomCode(client);
        List<String> roomUsers = new ArrayList<>();
        socketIOServer.getRoomOperations(roomCode).getClients().forEach(new Consumer<SocketIOClient>() {
            @Override
            public void accept(SocketIOClient client) {
                roomUsers.add(username(client));
            }
        });
        System.out.println(roomUsers);
        client.sendEvent("roomUser", roomUsers);
    }

    @Override
    @OnEvent("sendMsg")
    public void sendMsg(SocketIOClient client, String msg) throws Exception {
        String roomCode = roomCode(client);
        String username = username(client);
        Long now = new Date().getTime();
        socketIOServer.getRoomOperations(roomCode(client)).sendEvent("receiveMsg", username, DateUtil.format(new Date(), "HH:mm:ss"), msg);
        chatDao.save(Chat.builder().roomCode(roomCode).type(0).sender(username).sendTime(now).content(msg).build());
    }

    @Override
    @OnEvent("meetingEvent")
    public void meetingEvent(SocketIOClient client, RoomEvent roomEvent) throws Exception {
        String roomCode = roomCode(client);
        String username = username(client);
        Long now = new Date().getTime();
        String eventName = roomEvent.getEventName();
        if (eventName.equals("editName")) {
            String msg = username + "修改会议室名称为" + roomEvent.getRoomName();
            socketIOServer.getRoomOperations(roomCode(client)).sendEvent("meetingEvent_" + roomEvent.getEventName(), roomEvent.getRoomName(), msg);
            chatDao.save(Chat.builder().roomCode(roomCode).type(1).sender(username).sendTime(now).content(msg).build());
        }
    }

    @Override
    @OnEvent("drawLine")
    public void drawLine(SocketIOClient client, int nowX, int nowY, int x, int y, String color, int drawSize) throws Exception {
        String roomCode = roomCode(client);
        socketIOServer.getRoomOperations(roomCode).sendEvent("drawLine", client, nowX, nowY, x, y, color, drawSize);
    }

    @Override
    @OnEvent("drawText")
    public void drawText(SocketIOClient client, String id, String txt, int x, int y) throws Exception {
        String roomCode = roomCode(client);
        socketIOServer.getRoomOperations(roomCode).sendEvent("drawText", client, txt, x, y);
        roomBoardTxtDao.save(RoomBoardTxt.builder().roomCode(roomCode).id(id).txt(txt).x(x).y(y).build());
    }

    @Override
    @OnEvent("drawSqr")
    public void drawSqr(SocketIOClient client, int x, int y, int width, int height) throws Exception {
        String roomCode = roomCode(client);
        socketIOServer.getRoomOperations(roomCode).sendEvent("drawSqr", client, x, y, width,height);
    }

    @Override
    @OnEvent("reDrawText")
    public void reDrawText(SocketIOClient client, String id, String txt, int x, int y, int width, int height) throws Exception {
        String roomCode = roomCode(client);
        roomBoardTxtDao.updateWithSql(new SQL().update(RoomBoardTxt.class).set("txt", txt).set("x", x).set("y", y).where("id", id));
        socketIOServer.getRoomOperations(roomCode).sendEvent("reDrawText", client, txt, x, y, width, height);
    }

    @Override
    @OnEvent("storeBoard")
    public void storeBoard(SocketIOClient client, String boardData) throws Exception {
        String roomCode = roomCode(client);
        roomBoardDao.updateWithSql(new SQL().update(RoomBoard.class).set("boardData", boardData).where("roomCode", roomCode));
    }

    @Override
    @OnEvent("clearBoard")
    public void clearBoard(SocketIOClient client) throws Exception {
        String roomCode = roomCode(client);
        roomBoardDao.updateWithSql(new SQL().update(RoomBoard.class).set("boardData", null).where("roomCode", roomCode));
        roomBoardTxtDao.deleteWithCriteria(new Criteria().where("roomCode",roomCode));
        socketIOServer.getRoomOperations(roomCode).sendEvent("clearBoard", client);
    }

    @Override
    @OnEvent("getBoard")
    public void getBoard(SocketIOClient client) throws Exception {
        String roomCode = roomCode(client);
        RoomBoard roomBoard = roomBoardDao.queryOne(new Criteria().where("roomCode", roomCode));
        if (roomBoard != null) {
            client.sendEvent("getBoard", roomBoard.getBoardData());
        }
    }

    private String roomCode(SocketIOClient client) {
        return client.getHandshakeData().getSingleUrlParam("roomCode");
    }

    private String username(SocketIOClient client) {
        return client.getHandshakeData().getSingleUrlParam("username");
    }
}
