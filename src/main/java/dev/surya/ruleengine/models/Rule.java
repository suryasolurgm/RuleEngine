package dev.surya.ruleengine.models;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;


@Entity
@Table(name = "rules")
@Getter
@Setter
public class Rule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rule_name", nullable = false)
    private String ruleName;
    @Column(name = "rule_string", nullable = false, unique = true)
    private String ruleString;

    @Type(JsonType.class)
    @Column(name = "ast_json", columnDefinition = "json")
    private String astJson;

    // Constructors, getters, and setters
    public Rule() {}

    public Rule(String ruleName, String ruleString, String astJson) {
        this.ruleName = ruleName;
        this.ruleString = ruleString;
        this.astJson = astJson;
    }

    // Getters and setters
    // ...
}
