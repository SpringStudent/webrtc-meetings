package io.springstudent.meeting.common.signal;

import com.corundumstudio.socketio.SocketIOClient;
import io.springstudent.meeting.common.bean.RoomEvent;

/**
 * @author zhouning
 * @date 2023/04/21 8:57
 */
public interface ISignalListener {

    void onConnect(SocketIOClient client) throws Exception;

    void onDisconnect(SocketIOClient client) throws Exception;

    void roomUser(SocketIOClient client) throws Exception;

    void sendMsg(SocketIOClient client, String msg) throws Exception;

    void meetingEvent(SocketIOClient client, RoomEvent roomEvent) throws Exception;

    void drawLine(SocketIOClient client, int nowX, int nowY, int x, int y, String color, int drawSize) throws Exception;

    void drawText(SocketIOClient client,String id,String txt, int x, int y) throws Exception;

    void reDrawText(SocketIOClient client,String id,String txt, int x, int y,int width,int height) throws Exception;

    void storeBoard(SocketIOClient client, String boardData) throws Exception;

    void clearBoard(SocketIOClient client) throws Exception;

    void getBoard(SocketIOClient client)throws Exception;
}
