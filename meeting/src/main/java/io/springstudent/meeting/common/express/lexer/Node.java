package io.springstudent.meeting.common.express.lexer;

/**
 * @author zhouning
 * @date 2023/04/08 10:31
 */
public class Node {
    private NodeType type;
    private float value;
    private Node leftNode;// 左节点
    private Node rightNode;// 右节点

    public Node(){
        type=NodeType.Undifined;
        value=0.0f;
        leftNode=null;
        rightNode=null;
    }

    public Node(String nodeTypeText){
        if(nodeTypeText.equals("+")){
            this.type=NodeType.OP_Plus;
        }else if(nodeTypeText.equals("-")){
            this.type=NodeType.OP_Minus;
        }else if(nodeTypeText.equals("*")){
            this.type=NodeType.OP_Multi;
        }else if(nodeTypeText.equals("/")){
            this.type=NodeType.OP_Divide;
        }else{
            this.type=NodeType.Undifined;
        }

        value=0.0f;
        leftNode=null;
        rightNode=null;
    }

    public Node(NodeType type){
        this.type=type;
        value=0.0f;
        leftNode=null;
        rightNode=null;
    }

    public Node(NodeType type,String str){
        this.type=type;
        this.value=Float.valueOf(str);
        leftNode=null;
        rightNode=null;
    }

    public Node(NodeType type,float value,Node leftNode,Node rightNode){
        this.type=type;
        this.value=value;
        this.leftNode=leftNode;
        this.rightNode=rightNode;
    }

    public Node(NodeType type,Node leftNode,Node rightNode){
        this.type=type;
        this.value=0;
        this.leftNode=leftNode;
        this.rightNode=rightNode;
    }

    public float getValue() throws Exception{
        if(this.type==NodeType.Digit){
            return value;
        }else if(this.type==NodeType.OP_Divide){
            return leftNode.getValue()/rightNode.getValue();
        }else if(this.type==NodeType.OP_Minus){
            return leftNode.getValue()-rightNode.getValue();
        }else if(this.type==NodeType.OP_Multi){
            return leftNode.getValue()*rightNode.getValue();
        }else if(this.type==NodeType.OP_Plus){
            return leftNode.getValue()+rightNode.getValue();
        }else{
            throw new Exception("Not initialize");
        }
    }

    public void setLeftNode(Node leftNode) {
        this.leftNode = leftNode;
    }

    public void setRightNode(Node rightNode) {
        this.rightNode = rightNode;
    }

    public Node getLeftNode() {
        return leftNode;
    }

    public Node getRightNode() {
        return rightNode;
    }

    public String toString(){
        if(this.type==NodeType.Digit){
            return String.valueOf(value)+" ";
        }else if(this.type==NodeType.OP_Divide){
            return "/ ";
        }else if(this.type==NodeType.OP_Minus){
            return "- ";
        }else if(this.type==NodeType.OP_Multi){
            return "* ";
        }else if(this.type==NodeType.OP_Plus){
            return "+ ";
        }else{
            return "? ";
        }
    }

    public NodeType getType() {
        return type;
    }

    public void setType(NodeType type) {
        this.type = type;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
