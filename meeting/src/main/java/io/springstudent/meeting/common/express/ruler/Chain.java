package io.springstudent.meeting.common.express.ruler;

import lombok.Data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhouning
 * @date 2023/04/07 13:24
 */
@Data
public class  Chain {

    private Entry head;

    private Entry tail;

    public static final Pattern CONDITION_PATTERN = Pattern.compile("[&,|]");

    public Chain(String str) {
        final Matcher matcher = CONDITION_PATTERN.matcher(str);
        int start = 0;
        String type = null;
        while (matcher.find()) {
            String separator = matcher.group();
            String content = str.substring(start, matcher.start());
            start = matcher.end();

            buildChain(type, content);
            type = separator;
        }
        buildChain(type, str.substring(start));
    }

    private void buildChain(String separator, String content) {
        Entry entry = new Entry();
        if (head == null) {
            entry.setCondition(new None(content));
            head = entry;
            tail = entry;
        } else {
            tail.next = entry;
            tail = entry;
        }
        if ("&".equals(separator)) {
            entry.setCondition(new And(content));
        } else if ("|".equals(separator)) {
            entry.setCondition(new Or(content));
        }
    }

    public boolean loop(Content content) {
        Entry next = head;
        boolean result = content.compare(head.condition);
        while (next.getNext() != null) {
            next = next.getNext();
            if (next.getCondition().type().equals("Or")) {
                result = result || content.compare(next.condition);
            } else if (next.getCondition().type().equals("And")) {
                result = result && content.compare(next.condition);
            }
        }
        return result;
    }

    @Data
    private static class Entry {
        private Entry next;
        private Condition condition;
    }

}
