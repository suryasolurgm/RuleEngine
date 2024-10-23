package dev.surya.ruleengine.models;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
@Getter
@Setter
public class OperandNode extends Node {
    private String attribute;
    private String comparison;
    private Object value;

    public OperandNode() {
        this.type = NodeType.OPERAND;
    }

    public OperandNode(String attribute, String comparison, Object value) {
        this.type = NodeType.OPERAND;
        this.attribute = attribute;
        this.comparison = comparison;
        this.value = value;
    }

    @Override
    public boolean evaluate(Map<String, Object> data) {
        Object attributeValue = data.get(attribute);
        if (attributeValue == null) {
            throw new IllegalArgumentException("Attribute not found in data: " + attribute);
        }

        if (!(attributeValue instanceof Comparable) || !(value instanceof Comparable)) {
            throw new IllegalArgumentException("Attribute or value is not comparable");
        }

        Comparable comparableAttribute = (Comparable) attributeValue;
        Comparable comparableValue = (Comparable) value;

        int comparisonResult = comparableAttribute.compareTo(comparableValue);

        switch (comparison) {
            case ">":
                return comparisonResult > 0;
            case "<":
                return comparisonResult < 0;
            case ">=":
                return comparisonResult >= 0;
            case "<=":
                return comparisonResult <= 0;
            case "=":
                return comparisonResult == 0;
            case "!=":
                return comparisonResult != 0;
            default:
                throw new IllegalArgumentException("Invalid comparison operator: " + comparison);
        }
    }
}