package dev.surya.ruleengine;

import dev.surya.ruleengine.exceptions.DuplicateRuleException;
import dev.surya.ruleengine.models.Node;
import dev.surya.ruleengine.models.OperatorNode;
import dev.surya.ruleengine.models.Rule;
import dev.surya.ruleengine.services.RuleService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RuleEngineApplicationTests {

    @Autowired
    private RuleService ruleService;

    private String rule1;
    private String rule2;

    @BeforeEach
    void setUp() {
        rule1 = "((age > 30 AND department = 'Sales') OR (age < 25 AND department = 'Marketing')) AND (salary > 50000 OR experience > 5)";
        rule2 = "((age > 30 AND department = 'Marketing')) AND (salary > 20000 OR experience > 5)";
    }
    @AfterEach
    void tearDown() {
        ruleService.deleteAllRules();
    }
    @Test
    void testCreateRule() throws Exception {
        Rule createdRule1 = ruleService.createRule(rule1);
        assertNotNull(createdRule1);
        assertEquals(rule1, createdRule1.getRuleString());

        Rule createdRule2 = ruleService.createRule(rule2);
        assertNotNull(createdRule2);
        assertEquals(rule2, createdRule2.getRuleString());
    }

    @Test
    void testCombineRules() throws Exception {
        Rule createdRule1 = ruleService.createRule(rule1);
        Rule createdRule2 = ruleService.createRule(rule2);

        Node combinedNode = ruleService.combineRules(Arrays.asList(createdRule1.getId(), createdRule2.getId()));
        assertNotNull(combinedNode);
        assertTrue(combinedNode instanceof OperatorNode);
        assertEquals("AND", ((OperatorNode) combinedNode).getOperator());
    }

    @Test
    void testEvaluateRule() throws Exception {
        Rule createdRule1 = ruleService.createRule(rule1);

        // Test case that should evaluate to true
        Map<String, Object> data1 = new HashMap<>();
        data1.put("age", 35);
        data1.put("department", "Sales");
        data1.put("salary", 60000);
        data1.put("experience", 3);
        assertTrue(ruleService.evaluateRule(createdRule1.getId(), data1));

        // Test case that should evaluate to false
        Map<String, Object> data2 = new HashMap<>();
        data2.put("age", 28);
        data2.put("department", "Marketing");
        data2.put("salary", 45000);
        data2.put("experience", 2);
        assertFalse(ruleService.evaluateRule(createdRule1.getId(), data2));
    }

    @Test
    void testCombinedRuleEvaluation() throws Exception {
        Rule createdRule1 = ruleService.createRule(rule1);
        Rule createdRule2 = ruleService.createRule(rule2);

        Node combinedNode = ruleService.combineRules(Arrays.asList(createdRule1.getId(), createdRule2.getId()));
        String combinedJson = ruleService.convertAstToJson(combinedNode);
        Rule combinedRule = new Rule("CombinedRule", "Combined Rule", combinedJson);
        combinedRule = ruleService.saveRule(combinedRule);

        // Test case that should satisfy rule1 but not rule2
        Map<String, Object> data1 = new HashMap<>();
        data1.put("age", 35);
        data1.put("department", "Sales");
        data1.put("salary", 60000);
        data1.put("experience", 3);
        assertFalse(ruleService.evaluateRule(combinedRule.getId(), data1));

        // Test case that should satisfy both rule1 and rule2
        Map<String, Object> data2 = new HashMap<>();
        data2.put("age", 35);
        data2.put("department", "Marketing");
        data2.put("salary", 60000);
        data2.put("experience", 6);
        assertFalse(ruleService.evaluateRule(combinedRule.getId(), data2));
    }

    @Test
    void testDuplicateRuleCreation() {
        assertDoesNotThrow(() -> ruleService.createRule(rule1));
        assertThrows(DuplicateRuleException.class, () -> ruleService.createRule(rule1));
    }

    @Test
    void testAdditionalRuleCombination() throws Exception {
        String rule3 = "age >= 18 AND (department = 'HR' OR department = 'Finance')";
        Rule createdRule1 = ruleService.createRule(rule1);
        Rule createdRule2 = ruleService.createRule(rule2);
        Rule createdRule3 = ruleService.createRule(rule3);

        Node combinedNode = ruleService.combineRules(Arrays.asList(createdRule1.getId(), createdRule2.getId(), createdRule3.getId()));
        assertNotNull(combinedNode);
        assertTrue(combinedNode instanceof OperatorNode);
        assertEquals("AND", ((OperatorNode) combinedNode).getOperator());

        String combinedJson = ruleService.convertAstToJson(combinedNode);
        Rule combinedRule = new Rule("CombinedRule", "Combined Rule", combinedJson);
        combinedRule = ruleService.saveRule(combinedRule);

        // Test case that should satisfy all three rules
        Map<String, Object> data = new HashMap<>();
        data.put("age", 35);
        data.put("department", "HR");
        data.put("salary", 60000);
        data.put("experience", 6);
        assertFalse(ruleService.evaluateRule(combinedRule.getId(), data));

        // Test case that should not satisfy the combined rule
        data.put("age", 17);
        assertFalse(ruleService.evaluateRule(combinedRule.getId(), data));
    }
}
