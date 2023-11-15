package io.springstudent.meeting.common.express.ruler;

import lombok.Data;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhouning
 * @date 2023/04/07 13:25
 */
@Data
public class Content {

    private List<Object> elements;

    public Content(Object... eles) {
        elements = Arrays.stream(eles).collect(Collectors.toList());
    }

    public boolean compare(Condition condition){
        return this.elements.contains(condition.element);
    }
}
