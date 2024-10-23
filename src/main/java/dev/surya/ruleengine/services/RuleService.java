package dev.surya.ruleengine.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.surya.ruleengine.exceptions.DuplicateRuleException;
import dev.surya.ruleengine.models.*;
import dev.surya.ruleengine.repositories.RuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RuleService {
    @Autowired
    private RuleRepository ruleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public Rule createRule(String ruleString) throws DuplicateRuleException {
        // Check if a rule with the same string already exists
        if (isDuplicateRule(ruleString)) {
            throw new DuplicateRuleException("A rule with the same content already exists: " + ruleString);
        }

        Node ast = parseRuleString(ruleString);
        String astJson = convertAstToJson(ast);
        Rule rule = new Rule("Rule_" + System.currentTimeMillis(), ruleString, astJson);
        return ruleRepository.save(rule);
    }
    private boolean isDuplicateRule(String ruleString) {
        return ruleRepository.findByRuleString(ruleString).isPresent();
    }

    public Node combineRules(List<Long> ruleIds) {
        List<Rule> rules = ruleRepository.findAllById(ruleIds);
        List<Node> asts = rules.stream()
                .map(rule -> convertJsonToAst(rule.getAstJson()))
                .collect(Collectors.toList());
        return combineAsts(asts);
    }

    public boolean evaluateRule(Long ruleId, Map<String, Object> data) {
        Rule rule = ruleRepository.findById(ruleId)
                .orElseThrow(() -> new IllegalArgumentException("Rule not found"));
        Node ast = convertJsonToAst(rule.getAstJson());
        return ast.evaluate(data);
    }

    private Node parseRuleString(String ruleString) {
        System.out.println("Parsing rule string: " + ruleString);
        List<String> tokens = tokenize(ruleString);
        System.out.println("Tokenized: " + tokens);
        return buildAST(tokens);
    }

    private List<String> tokenize(String ruleString) {
        List<String> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();
        boolean inQuotes = false;

        for (char c : ruleString.toCharArray()) {
            if (c == '\'' || c == '"') {
                inQuotes = !inQuotes;
                currentToken.append(c);
            } else if (!inQuotes && (c == '(' || c == ')' || c == ' ')) {
                if (currentToken.length() > 0) {
                    tokens.add(currentToken.toString());
                    currentToken = new StringBuilder();
                }
                if (c != ' ') {
                    tokens.add(String.valueOf(c));
                }
            } else {
                currentToken.append(c);
            }
        }

        if (currentToken.length() > 0) {
            tokens.add(currentToken.toString());
        }

        return tokens;
    }

    private Node buildAST(List<String> tokens) {
        Stack<Node> nodeStack = new Stack<>();
        Stack<String> operatorStack = new Stack<>();

        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);
            System.out.println("Processing token: " + token);
            if (token.equals("(")) {
                operatorStack.push(token);
            } else if (token.equals(")")) {
                while (!operatorStack.peek().equals("(")) {
                    processOperator(nodeStack, operatorStack.pop());
                }
                operatorStack.pop(); // Remove "("
            } else if (isOperator(token)) {
                while (!operatorStack.isEmpty() && precedence(operatorStack.peek()) >= precedence(token)) {
                    processOperator(nodeStack, operatorStack.pop());
                }
                operatorStack.push(token);
            } else {
                // Handle operand
                String attribute = token;
                String operator = tokens.get(++i);
                String value = tokens.get(++i);
                nodeStack.push(createOperandNode(attribute, operator, value));
            }
            System.out.println("Node stack: " + nodeStack);
            System.out.println("Operator stack: " + operatorStack);
        }

        while (!operatorStack.isEmpty()) {
            processOperator(nodeStack, operatorStack.pop());
        }

        return nodeStack.pop();
    }
    private Node createOperandNode(String attribute, String operator, String value) {
        System.out.println("Creating operand node for: " + attribute + " " + operator + " " + value);
        return new OperandNode(attribute, operator, parseValue(value));
    }

    private int precedence(String operator) {
        if (operator.equals("AND")) return 2;
        if (operator.equals("OR")) return 1;
        return 0;
    }

    private void processOperator(Stack<Node> nodeStack, String operator) {
        Node right = nodeStack.pop();
        Node left = nodeStack.pop();
        nodeStack.push(new OperatorNode(operator, left, right));
    }
    private boolean isOperator(String token) {
        return token.equals("AND") || token.equals("OR");
    }


    private Object parseValue(String value) {
        value = value.trim();
        if (value.startsWith("'") && value.endsWith("'")) {
            return value.substring(1, value.length() - 1);
        } else if (value.matches("\\d+")) {
            return Integer.parseInt(value);
        } else if (value.matches("\\d+\\.\\d+")) {
            return Double.parseDouble(value);
        } else {
            return value;
        }
    }




    private Node combineAsts(List<Node> asts) {
        if (asts == null || asts.isEmpty()) {
            throw new IllegalArgumentException("No rules to combine");
        }

        Node combined = asts.get(0);
        for (int i = 1; i < asts.size(); i++) {
            combined = new OperatorNode("AND", combined, asts.get(i));
        }
        return combined;
    }

    public String convertAstToJson(Node ast) {
        try {
            return objectMapper.writeValueAsString(ast);
        } catch (Exception e) {
            throw new RuntimeException("Error converting AST to JSON", e);
        }
    }

    public Node convertJsonToAst(String json) {
        try {
            return objectMapper.readValue(json, Node.class);
        } catch (Exception e) {
            throw new RuntimeException("Error converting JSON to AST", e);
        }
    }


    public Rule saveRule(Rule combinedRule) {
        return ruleRepository.save(combinedRule);
    }

    public void deleteAllRules() {
        ruleRepository.deleteAll();
    }
}