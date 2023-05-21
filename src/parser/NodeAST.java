package parser;

import java.util.ArrayList;

class NodeAST {
    public String type = null;
    public ArrayList<NodeAST> children;
    public String value = null;

    public NodeAST(String type) {
        this.type = type;
        children = new ArrayList<>();
    }

    public NodeAST(String type, String value) {
        this.type = type;
        this.value = value;
        children = new ArrayList<>();
    }

    public NodeAST() {
        children = new ArrayList<>();
    }

    public void addChild(NodeAST child) {
        children.add(child);
    }

    public void addValue(String value) {
        this.value = value;
    }

}
