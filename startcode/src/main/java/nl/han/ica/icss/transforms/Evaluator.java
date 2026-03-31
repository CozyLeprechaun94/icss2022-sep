package nl.han.ica.icss.transforms;

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

public class Evaluator implements Transform {

    private IHANLinkedList<HashMap<String, Literal>> variableValues;

    public Evaluator() {
        variableValues = new HANLinkedList<>();
    }

    @Override
    public void apply(AST ast) {
        variableValues = new HANLinkedList<>();
        variableValues.addFirst(new HashMap<>());

        for (ASTNode child : ast.root.getChildren()) {
            if (child instanceof VariableAssignment va) {
                applyVariableAssignment(va);
            } else if (child instanceof Stylerule rule) {
                applyStylerule(rule);
            }
        }

        variableValues.removeFirst();
    }

    private void applyVariableAssignment(VariableAssignment variableAssignment) {
        Literal value = valueExpression(variableAssignment.expression);
        variableValues.getFirst().put(variableAssignment.name.name, value);
    }

    private void applyStylerule(Stylerule stylerule) {
        variableValues.addFirst(new HashMap<>());
        valueBody(stylerule.body);
        variableValues.removeFirst();
    }

    private void applyDeclaration(Declaration declaration) {
        declaration.expression = valueExpression(declaration.expression);
    }

    private void valueBody(List<ASTNode> body) {
        for (ASTNode node : body) {
            if (node instanceof VariableAssignment variableAssignment) {
                applyVariableAssignment(variableAssignment);
            } else if (node instanceof Declaration declaration) {
                applyDeclaration(declaration);
            }
        }
    }

    private Literal lookupVariable(String name) {
        for (int i = 0; i < variableValues.getSize(); i++) {
            if (variableValues.get(i).containsKey(name)) {
                return variableValues.get(i).get(name);
            }
        }
        return null;
    }

    private Literal valueOperation(Operation operation, Literal leftHandSight, Literal rightHandSight) {
        if (operation instanceof AddOperation) {
            if (leftHandSight instanceof PixelLiteral left && rightHandSight instanceof PixelLiteral right) {
                return new PixelLiteral(left.value + right.value);
            }
            if (leftHandSight instanceof PercentageLiteral left && rightHandSight instanceof PercentageLiteral right) {
                return new PercentageLiteral(left.value + right.value);
            }
            if (leftHandSight instanceof ScalarLiteral left && rightHandSight instanceof ScalarLiteral right) {
                return new ScalarLiteral(left.value + right.value);
            }
        }

        if (operation instanceof SubtractOperation) {
            if (leftHandSight instanceof PixelLiteral left && rightHandSight instanceof PixelLiteral right) {
                return new PixelLiteral(left.value - right.value);
            }
            if (leftHandSight instanceof PercentageLiteral left && rightHandSight instanceof PercentageLiteral right) {
                return new PercentageLiteral(left.value - right.value);
            }
            if (leftHandSight instanceof ScalarLiteral left && rightHandSight instanceof ScalarLiteral right) {
                return new ScalarLiteral(left.value - right.value);
            }
        }

        if (operation instanceof MultiplyOperation) {
            if (leftHandSight instanceof ScalarLiteral left) {
                if (rightHandSight instanceof PixelLiteral right)
                    return new PixelLiteral(left.value * right.value);
                if (rightHandSight instanceof PercentageLiteral right)
                    return new PercentageLiteral(left.value * right.value);
                if (rightHandSight instanceof ScalarLiteral right)
                    return new ScalarLiteral(left.value * right.value);
            }
            if (rightHandSight instanceof ScalarLiteral right) {
                if (leftHandSight instanceof PixelLiteral left)
                    return new PixelLiteral(left.value * right.value);
                if (leftHandSight instanceof PercentageLiteral left)
                    return new PercentageLiteral(left.value * right.value);
            }
        }
        return null;
    }

    private Literal valueExpression(Expression expression) {
        if (expression instanceof Literal literal) return literal;

        if (expression instanceof VariableReference variableReference) {
            return lookupVariable(variableReference.name);
        }

        if (expression instanceof Operation operation) {
            Literal leftHandSight = valueExpression(operation.lhs);
            Literal rightHandSight = valueExpression(operation.rhs);
            return valueOperation(operation, leftHandSight, rightHandSight);
        }

        return null;
    }
}
