package io.springstudent.meeting.room.pojo;

import com.gysoft.jdbc.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhouning
 * @date 2023/04/24 9:19
 */
@Data
@Table(name = "room_board")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomBoard {

    private Integer id;

    private String roomCode;

    private String boardData;
}
