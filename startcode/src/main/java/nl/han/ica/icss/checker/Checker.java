package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;
import java.util.List;


public class Checker {

    private IHANLinkedList<HashMap<String, ExpressionType>> variableTypes;

    public void check(AST ast) {
        variableTypes = new HANLinkedList<>();
        check(ast.root);
    }

    private void check(Stylesheet stylesheet) {
        variableTypes.addFirst(new HashMap<>());

        for (ASTNode child : stylesheet.getChildren()) {
            if (child instanceof VariableAssignment variableAssignment) {
                controlVariableAssignment(variableAssignment);
            } else if (child instanceof Stylerule styleRule) {
                controlStylerule(styleRule);
            }
        }

        variableTypes.removeFirst();
    }

    private void controlStylerule(Stylerule rule) {
        variableTypes.addFirst(new HashMap<>());
        controlBody(rule.body);
        variableTypes.removeFirst();
    }

    private void controlBody(List<ASTNode> body) {
        for (ASTNode node : body) {
            if (node instanceof VariableAssignment va) {
                controlVariableAssignment(va);
            }
        }
    }

    private void controlVariableAssignment(VariableAssignment variableAssignment) {
        ExpressionType expressionType = getExpressionType(variableAssignment.expression);
        variableTypes.getFirst().put(variableAssignment.name.name, expressionType);
    }

    private ExpressionType getExpressionType(Expression expression) {
        if (expression instanceof BoolLiteral) return ExpressionType.BOOL;
        if (expression instanceof ColorLiteral) return ExpressionType.COLOR;
        if (expression instanceof PixelLiteral) return ExpressionType.PIXEL;
        if (expression instanceof PercentageLiteral) return ExpressionType.PERCENTAGE;
        if (expression instanceof ScalarLiteral) return ExpressionType.SCALAR;

        return ExpressionType.UNDEFINED;
    }
}
