package io.springstudent.meeting.common.express.ruler;

/**
 * @author zhouning
 * @date 2023/04/07 15:42
 */
public class None extends Condition{
    public None(String element) {
        super(element);
    }

    @Override
    String type() {
        return null;
    }
}
