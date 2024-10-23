package dev.surya.ruleengine.controllers;

import dev.surya.ruleengine.dtos.RuleCreateRequest;
import dev.surya.ruleengine.exceptions.DuplicateRuleException;
import dev.surya.ruleengine.models.Node;
import dev.surya.ruleengine.models.Rule;
import dev.surya.ruleengine.services.RuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rules")
public class RuleController {
    @Autowired
    private RuleService ruleService;

    @PostMapping("/create")
    public ResponseEntity<?> createRule(@RequestBody RuleCreateRequest request) {
        try {
            Rule rule = ruleService.createRule(request.getRuleString());
            return ResponseEntity.ok(rule);
        } catch (DuplicateRuleException e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/combine")
    public ResponseEntity<Node> combineRules(@RequestBody List<Long> ruleIds) {
        Node combinedAst = ruleService.combineRules(ruleIds);
        return ResponseEntity.ok(combinedAst);
    }

    @PostMapping("/evaluate/{ruleId}")
    public ResponseEntity<Boolean> evaluateRule(@PathVariable Long ruleId, @RequestBody Map<String, Object> data) {
        boolean result = ruleService.evaluateRule(ruleId, data);
        return ResponseEntity.ok(result);
    }
}
