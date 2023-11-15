package io.springstudent.meeting.common.express.ruler;

/**
 * @author zhouning
 * @date 2023/04/07 13:40
 */
public class Or extends Condition {
    public Or(String element) {
        super(element);
    }

    @Override
    public String type() {
        return "Or";
    }
}
