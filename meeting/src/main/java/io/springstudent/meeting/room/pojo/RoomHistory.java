package io.springstudent.meeting.room.pojo;

import com.gysoft.jdbc.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhouning
 * @date 2023/04/19 13:13
 */
@Data
@Table(name = "room_history")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomHistory {

    private Integer id;

    private String roomCode;

    private String action;

    private String username;

    private Long createTime;
}
