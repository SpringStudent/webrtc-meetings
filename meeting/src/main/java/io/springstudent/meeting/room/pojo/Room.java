package io.springstudent.meeting.room.pojo;

import com.gysoft.jdbc.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhouning
 * @date 2023/04/19 13:11
 */
@Data
@Table(name = "room")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Room {

    private Integer id;

    private String roomCode;

    private String roomName;

    private Long startTime;

    private Long endTime;

    private String createUser;
}
