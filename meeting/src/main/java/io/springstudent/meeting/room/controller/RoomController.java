package io.springstudent.meeting.room.controller;

import com.gysoft.jdbc.bean.Page;
import com.gysoft.jdbc.bean.PageResult;
import io.springstudent.meeting.room.bean.RoomInfo;
import io.springstudent.meeting.room.pojo.RoomBoardTxt;
import io.springstudent.meeting.room.service.RoomService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhouning
 * @date 2023/04/20 13:21
 */
@RestController
@RequestMapping("/room")
public class RoomController {

    private static final Logger logger = LogManager.getLogger(RoomController.class);

    @Resource
    private RoomService roomService;

    @PostMapping("/createRoom")
    public String createRoom() throws Exception {
        try {
            return roomService.createRoom();
        } catch (Exception e) {
            logger.error("createRoom error", e);
            throw e;
        }
    }

    @PostMapping("/updateRoomName")
    public void updateRoomName(String roomCode, String roomName) throws Exception {
        try {
            roomService.updateRoomName(roomCode, roomName);
        } catch (Exception e) {
            logger.error("updateRoomName error,roomCode={},roomName", roomCode, roomName);
            throw e;
        }
    }

    @GetMapping("/queryRoom")
    public RoomInfo queryRoom(@RequestParam String roomCode) throws Exception {
        try {
            return roomService.queryRoom(roomCode);
        } catch (Exception e) {
            logger.error("queryRoom error,roomCode={}", roomCode, e);
            throw e;
        }
    }

    @PostMapping("/pageRoom")
    public PageResult<RoomInfo> pageRoom(@RequestBody Page page)throws Exception{
        try {
            return roomService.pageRoom(page);
        }catch (Exception e){
            logger.error("pageRoom error,page={}",page,e);
            throw e;
        }
    }

    @GetMapping("/listRoomBoardTxt")
    public List<RoomBoardTxt> listRoomBoardTxt(@RequestParam String roomCode)throws Exception{
        try {
            return roomService.listRoomBoardTxt(roomCode);
        }catch (Exception e){
            logger.error("listRoomBoardTxt error,roomCode={}",roomCode,e);
            throw e;
        }
    }
}
