package io.springstudent.meeting.common.express.lexer;
import java.util.List;

/**
 * @author zhouning
 * @date 2023/04/08 10:31
 */
public class Tree {
    // 根节点
    private Node root;

    // infixList为分词完毕的中序表达式列表
    public Tree(List<String> infixList){
        root=build(infixList,0,infixList.size());
    }

    // 构建一棵树,从根节点构建起
    private Node build(List<String> list,int start,int end){
        int depth=0;//记录深度，进一层括号加一，退出来减一
        int plusDivideRightmostPos=-1;// 记录最右边的加减号位置
        int multiDivideRightmostPos=-1;// 记录最右边的乘除号位置

        // 操作数
        if(start==end-1){
            // 下标相差一，说明找到的是没有子节点的叶子节点，也即操作数节点
            Node leafNode=new Node(NodeType.Digit,list.get(start));
            return leafNode;
        }
        // 这个循环是为了找括号外最右边的运算符位置
        for(int i=start;i<end;i++){
            String operatorText=list.get(i);// 获得操作符的文字，如果是操作数直接ignore

            if(operatorText.equals("(")){
                depth++;
            }else if(operatorText.equals(")")){
                depth--;
            }else if(operatorText.equals("+") || operatorText.equals("-") ){
                if(depth==0){
                    plusDivideRightmostPos=i;
                }
            }else if(operatorText.equals("*") || operatorText.equals("/") ){
                if(depth==0){
                    multiDivideRightmostPos=i;
                }
            }
        }

        int rightMost=-1;

        if(plusDivideRightmostPos==-1 && multiDivideRightmostPos==-1){
            // 整个算式被多余的括号括起来了,去掉这层多余的括号再做
            return build(list,start+1,end-1);
        }

        // 优先取加减号的位置，因为它的计算优先级最低，应当最后算
        rightMost=plusDivideRightmostPos;

        if(plusDivideRightmostPos==-1 && multiDivideRightmostPos>0){
            // 括号外只有乘除号，如(1+2)*(3+4)，这时只有取乘除号位置，
            rightMost=multiDivideRightmostPos;
        }

        // 如果能走到这里，则最右边括号外的运算符位置已经找到了，可以开始构建节点
        String operator=list.get(rightMost);
        Node nodeOper=new Node(operator);
        // 这里创建的节点都是操作符节点，不是最终的叶子节点
        // 以最右边的操作符为界，分两侧构建左右子节点
        nodeOper.setLeftNode(build(list,start,rightMost));
        nodeOper.setRightNode(build(list,rightMost+1,end));

        // 返回构建完的节点
        return nodeOper;
    }

    // 取二叉树的值
    public float evaluate() throws Exception{
        return this.root.getValue();
    }

    // 后序遍历
    private String postOrder(Node node){
        if(node!=null){
            String s="";

            s+=postOrder(node.getLeftNode());

            s+=postOrder(node.getRightNode());

            s+=node.toString();

            return s;
        }

        return "";
    }

    // 从构建好的二叉树获得后序表达式
    public String getPostfix(){
        return postOrder(root);
    }

    // 中序遍历
    private String inOrder(Node node){
        if(node!=null){
            String s="";

            if(node.getType().equals(NodeType.OP_Plus) || node.getType().equals(NodeType.OP_Minus)){
                s+="(";
            }

            s+=inOrder(node.getLeftNode());

            s+=node.toString();

            s+=inOrder(node.getRightNode());

            if(node.getType().equals(NodeType.OP_Plus) || node.getType().equals(NodeType.OP_Minus)){
                s+=")";
            }

            return s;
        }

        return "";
    }

    // 从构建好的二叉树获得中序表达式
    public String getInfix(){
        String s="";
        s=inOrder(root);

        if(s.startsWith("(") && s.endsWith(")") && (root.getType()==NodeType.OP_Minus || root.getType()==NodeType.OP_Plus) ){
            return s.substring(1, s.length()-1);
        }else{
            return s;
        }

    }
}
