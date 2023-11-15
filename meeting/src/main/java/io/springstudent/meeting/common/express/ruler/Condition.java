package io.springstudent.meeting.common.express.ruler;

/**
 * @author zhouning
 * @date 2023/04/07 13:25
 */
public abstract class Condition {
    protected String element;

    public Condition(String element) {
        this.element = element;
    }

    @Override
    public String toString() {
        return "Condition{" +
                "element='" + element + '\'' +
                '}';
    }

    abstract String type();

}
