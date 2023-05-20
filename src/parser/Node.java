package parser;

import java.util.ArrayList;

class Node {
    public String type;
    public ArrayList<Node> children;
    public String value;

    public Node(String type) {
        this.type = type;
    }

    public Node(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public void addChild(Node child) {
        children.add(child);
    }

}
