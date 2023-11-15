package io.springstudent.meeting.common.express.lexer;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @author zhouning
 * @date 2023/04/08 10:30
 */
public class Entry {

    public static void main(String[] args) throws Exception{
        // 取得用户输入的表达式
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String rawExpression = null;
        System.out.print("请输入算术表达式:");
        rawExpression = br.readLine();

        // 得到合法的算术表达式
        String expression="";
        for(int i=0;i<rawExpression.length();i++){
            // 拿到表达式的每个字符
            char c=rawExpression.charAt(i);
            if(Character.isDigit(c) || c=='+' || c=='-' || c=='*' || c=='/' || c=='(' || c==')' || c=='.'){
                expression+=c;
            }else{
                System.out.print(" "+c+"不是合法的算术表达式字符.");
                System.exit(0);
            }
        }

        // 送去解析
        Lexer p=new Lexer(expression);
        p.print();
        Tree t=new Tree(p.getInfixList());
        try {
            System.out.println("算式表达式"+expression+"的计算结果为:"+t.evaluate());
            System.out.println("其后序表达式为:"+t.getPostfix());
            System.out.println("从二叉树重新组建的中序表达式为"+t.getInfix());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
