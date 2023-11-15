package io.springstudent.meeting.chat.service;

import io.springstudent.meeting.chat.bean.ChatInfo;

import java.util.List;

/**
 * @author zhouning
 * @date 2023/04/25 16:15
 */
public interface ChatService {

    List<ChatInfo> chatInfos(String roomCode)throws Exception;
}
