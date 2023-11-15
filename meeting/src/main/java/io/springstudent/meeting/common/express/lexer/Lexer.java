package io.springstudent.meeting.common.express.lexer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhouning
 * @date 2023/04/08 10:30
 */
public class Lexer {
    private List<String> list;// 用于存储中序表达式的链表

    public List<String> getInfixList() {
        return list;
    }

    public Lexer(String expression){
        list=new ArrayList<String>();

        // 使用正则表达式分词
        String regExp = "(\\d+(\\.*)\\d*)|(\\+)|(\\-)|(\\*)|(\\/)|(\\()|(\\))";

        Pattern pattern=Pattern.compile(regExp);
        Matcher matcher=pattern.matcher(expression);
        while(matcher.find()){
            list.add(matcher.group(0));
        }
    }

    public void print(){
        for(String str:list){
            System.out.println(str);
        }
    }
}
