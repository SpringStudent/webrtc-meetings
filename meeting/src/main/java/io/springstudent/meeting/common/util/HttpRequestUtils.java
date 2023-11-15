package io.springstudent.meeting.common.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.util.HtmlUtils;
public class HttpRequestUtils {

    /**
     * 对value进行Xss过滤处理
     */
    public static String clean(String value) {
        if (StringUtils.isBlank(value) || value.startsWith("http:") || value.startsWith("https:")) {
            return value;
        }
        String convertValue = convert(value);
        //漏掉 "
        char[] values = convertValue.toCharArray();
        int length = values.length;
        String newValue = "";
        for (int i = 0; i < length; i++) {
            char ch = values[i];
            if ('&' == ch || '"' == ch || '·' == ch || '€' == ch || '£' == ch) {
                newValue += String.valueOf(ch);
            } else if ('−' == ch) {
                newValue += String.valueOf('-');
            } else {
                newValue += HtmlUtils.htmlEscape(String.valueOf(ch));
            }
        }
        if (!newValue.equals(value)) {
            System.out.println(value + " is unsafe,be cleaned:" + newValue);
        }
        return newValue;
    }

    public static String convert(String value) {
        value = value.replaceAll("<", "＜");
        value = value.replaceAll("--", "－－");
        value = value.replaceAll(">", "＞");
        value = value.replaceAll("'", "＇ ");
        value = value.replaceAll("…", "...");
        value = value.replaceAll("·", "\\·");
        value = value.replaceAll("¥", "￥");
        value = value.replaceAll("“", "＂");
        value = value.replaceAll("”", "＂");
        value = value.replaceAll("——", "－－");
        value = value.replaceAll("—", "－");
        value = value.replaceAll("‘", "＇ ");
        value = value.replaceAll("’", "＇ ");
        value = value.replaceAll("=", "＝");
        return value;
    }
}
