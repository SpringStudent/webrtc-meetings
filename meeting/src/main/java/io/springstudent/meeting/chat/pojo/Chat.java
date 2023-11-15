package io.springstudent.meeting.chat.pojo;

import com.gysoft.jdbc.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhouning
 * @date 2023/04/19 13:08
 */
@Data
@Table(name = "chat")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Chat {

    private Integer id;

    private Integer type;

    private String content;

    private String sender;

    private Long sendTime;

    private String roomCode;
}
