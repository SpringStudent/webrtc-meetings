package io.springstudent.meeting.room.pojo;

import com.gysoft.jdbc.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhouning
 * @date 2023/11/18 14:16
 */
@Data
@Table(name = "room_board_txt")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomBoardTxt {

    private String id;

    private String roomCode;

    private String txt;

    private Integer x;

    private Integer y;
}
