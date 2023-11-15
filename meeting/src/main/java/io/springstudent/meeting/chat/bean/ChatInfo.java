package io.springstudent.meeting.chat.bean;

import lombok.Data;

/**
 * @author zhouning
 * @date 2023/04/25 16:17
 */
@Data
public class ChatInfo {
    private Integer type;
    private String content;
    private String sender;
    private String sendTime;
    private String roomCode;
}
