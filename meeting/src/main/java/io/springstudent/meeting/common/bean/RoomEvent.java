package io.springstudent.meeting.common.bean;

import lombok.Data;

/**
 * @author zhouning
 * @date 2023/04/21 10:01
 */
@Data
public class RoomEvent {

    private String eventName;

    private String roomCode;

    private String roomName;

    private String username;
}
