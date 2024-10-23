package dev.surya.ruleengine.models;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
@Getter
@Setter
public class OperatorNode extends Node {
    private String operator;
    private Node left;
    private Node right;
    public OperatorNode() {
        this.type = NodeType.OPERATOR;
    }
    public OperatorNode(String operator, Node left, Node right) {
        this.type = NodeType.OPERATOR;
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean evaluate(Map<String, Object> data) {
        switch (operator) {
            case "AND":
                return left.evaluate(data) && right.evaluate(data);
            case "OR":
                return left.evaluate(data) || right.evaluate(data);
            default:
                throw new IllegalArgumentException("Invalid operator: " + operator);
        }
    }
}
