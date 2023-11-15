package io.springstudent.meeting.common.express.ruler;

/**
 * @author zhouning
 * @date 2023/04/07 13:40
 */
public class And extends Condition {
    public And(String element) {
        super(element);
    }

    @Override
    public String type() {
        return "And";
    }

}
