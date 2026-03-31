package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
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
            } else if (node instanceof Declaration decl) {
                controlDeclaration(decl);
            }
        }
    }

    private void controlDeclaration(Declaration declaration) {
        getExpressionType(declaration.expression);
    }

    private void controlVariableAssignment(VariableAssignment variableAssignment) {
        ExpressionType expressionType = getExpressionType(variableAssignment.expression);
        variableTypes.getFirst().put(variableAssignment.name.name, expressionType);
    }

    private ExpressionType lookForVariable(String name, ASTNode node) {
        for (int i = 0; i < variableTypes.getSize(); i++) {
            HashMap<String, ExpressionType> scope = variableTypes.get(i);
            if (scope.containsKey(name)) {
                return scope.get(name);
            }
        }
        node.setError("Variable " + name + " is not declared!");
        return ExpressionType.UNDEFINED;
    }

    private ExpressionType controlOperation(Operation operation) {
        ExpressionType leftHandSight = getExpressionType(operation.lhs);
        ExpressionType rightHandSight = getExpressionType(operation.rhs);

        if (operation instanceof MultiplyOperation) {
            if (leftHandSight != ExpressionType.SCALAR && rightHandSight != ExpressionType.SCALAR) {
                operation.setError("Only one scalar type can be used!");

                return ExpressionType.UNDEFINED;
            }
            return leftHandSight != ExpressionType.SCALAR ? leftHandSight : rightHandSight;
        }

        if (operation instanceof AddOperation || operation instanceof SubtractOperation) {
            if (leftHandSight != rightHandSight) {
                operation.setError("Operations must be of the same literal type!");
                return ExpressionType.UNDEFINED;
            }
            return leftHandSight;
        }

        return ExpressionType.UNDEFINED;
    }

    private ExpressionType getExpressionType(Expression expression) {
        if (expression instanceof BoolLiteral) return ExpressionType.BOOL;
        if (expression instanceof ColorLiteral) return ExpressionType.COLOR;
        if (expression instanceof PixelLiteral) return ExpressionType.PIXEL;
        if (expression instanceof PercentageLiteral) return ExpressionType.PERCENTAGE;
        if (expression instanceof ScalarLiteral) return ExpressionType.SCALAR;

        if (expression instanceof VariableReference variableReference) {
            return lookForVariable(variableReference.name, variableReference);
        }

        if (expression instanceof Operation operation) {
            return controlOperation(operation);
        }

        return ExpressionType.UNDEFINED;
    }
}
