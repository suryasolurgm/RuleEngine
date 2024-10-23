package dev.surya.ruleengine.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RuleCreateRequest {
    private String ruleString;

    // Getter and setter
    public String getRuleString() {
        return ruleString;
    }

    public void setRuleString(String ruleString) {
        this.ruleString = ruleString;
    }
}
