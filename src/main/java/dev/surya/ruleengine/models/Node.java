package dev.surya.ruleengine.models;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
@Setter
@Getter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "nodeType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = OperatorNode.class, name = "operator"),
        @JsonSubTypes.Type(value = OperandNode.class, name = "operand")
})
public abstract class Node {
    // Getter and setter for type
    protected NodeType type;

    public abstract boolean evaluate(Map<String, Object> data);

}