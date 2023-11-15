package io.springstudent.meeting.chat.controller;

import io.springstudent.meeting.chat.bean.ChatInfo;
import io.springstudent.meeting.chat.service.ChatService;
import io.springstudent.meeting.room.controller.RoomController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhouning
 * @date 2023/04/25 16:15
 */
@RestController
@RequestMapping("/chat")
public class ChatController {

    private static final Logger logger = LogManager.getLogger(RoomController.class);

    @Resource
    private ChatService chatService;

    @GetMapping("/chatInfos")
    public List<ChatInfo> chatInfos(String roomCode)throws Exception{
        try {
            return chatService.chatInfos(roomCode);
        }catch (Exception e){
            logger.error("chatInfos error,roomCode={}",roomCode,e);
            throw e;
        }
    }
}
