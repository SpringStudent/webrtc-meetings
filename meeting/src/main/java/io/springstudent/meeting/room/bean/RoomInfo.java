package io.springstudent.meeting.room.bean;

import lombok.Data;

import java.util.List;

/**
 * @author zhouning
 * @date 2023/04/20 13:23
 */
@Data
public class RoomInfo {

    private String id;

    private String roomCode;

    private String roomName;

    private Long startTime;

    private Long endTime;

    private String createUser;

    private List<String> joinUser;

    private String boardData;
}
