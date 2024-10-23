package dev.surya.ruleengine.repositories;

import dev.surya.ruleengine.models.Rule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RuleRepository extends JpaRepository<Rule, Long> {
    // You can add custom query methods here if needed
    Optional<Rule> findByRuleString(String ruleString);
}
