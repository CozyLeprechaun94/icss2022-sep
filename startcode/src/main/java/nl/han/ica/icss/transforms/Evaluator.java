package nl.han.ica.icss.transforms;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.ArrayList;
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
        // Globale scope aanmaken
        variableValues.addFirst(new HashMap<>());
        List<ASTNode> toDelete = new ArrayList<>();

        // Loop door de stylesheet heen en verwerk variabelen en stylerules
        for (ASTNode child : ast.root.getChildren()) {
            if (child instanceof VariableAssignment variableAssignment) {
                applyVariableAssignment(variableAssignment);
                toDelete.add(variableAssignment);
            } else if (child instanceof Stylerule rule) {
                applyStylerule(rule);
            }
        }

        variableValues.removeFirst(); //Opruimen
        ast.root.getChildren().removeAll(toDelete); //Verwijder variabelen die niet in de CSS horen.
    }

    // Sla variabelen op
    private void applyVariableAssignment(VariableAssignment variableAssignment) {
        Literal value = valueExpression(variableAssignment.expression);
        variableValues.getFirst().put(variableAssignment.name.name, value); // Pak de bovenste scope
    }

    //Verwerk de body en maak een nieuwe scope, zodat variabelen binnen de stylerule niet buiten de scope beschikbaar zijn
    private void applyStylerule(Stylerule stylerule) {
        variableValues.addFirst(new HashMap<>());
        valueBody(stylerule.body);
        variableValues.removeFirst();
    }

    //vervang expression door literal
    private void applyDeclaration(Declaration declaration) {
        declaration.expression = valueExpression(declaration.expression);
    }

    private void applyIfClause(IfClause ifClause, List<ASTNode> body) {
        Literal condition = valueExpression(ifClause.conditionalExpression);

        if (condition instanceof BoolLiteral bool) {
            if (bool.value) {
                // evalueer dan voeg op juiste positie
                valueBody(ifClause.body);
                int index = body.indexOf(ifClause); // Houd index bij zodat we weten op welke positie wij zitten voor het invoegen
                body.remove(ifClause);
                body.addAll(index, ifClause.body);
            } else {
                if (ifClause.elseClause != null) {
                    // evalueer dan voeg op juiste positie
                    valueBody(ifClause.elseClause.body);
                    int index = body.indexOf(ifClause);
                    body.remove(ifClause);
                    body.addAll(index, ifClause.elseClause.body);
                } else {
                    // Als er geen else is, verwijder element
                    body.remove(ifClause);
                }
            }
        }
    }

    private void valueBody(List<ASTNode> body) {
        List<ASTNode> toDelete = new ArrayList<>();
        List<ASTNode> copy = new ArrayList<>(body); // Een kopie om te houden wat er wel niet gedaan is.

        for (ASTNode node : copy) {
            if (node instanceof VariableAssignment variableAssignment) {
                applyVariableAssignment(variableAssignment);
                toDelete.add(node);
            } else if (node instanceof IfClause ifClause) {
                applyIfClause(ifClause, body);
            }
        }

        body.removeAll(toDelete);

        for (ASTNode node : new ArrayList<>(body)) {
            if (node instanceof Declaration declaration) {
                applyDeclaration(declaration);
            }
        }
    }

    // Zoek variabel op van boven naar beneden in alle scopes
    private Literal lookupVariable(String name) {
        for (int i = 0; i < variableValues.getSize(); i++) {
            if (variableValues.get(i).containsKey(name)) {
                return variableValues.get(i).get(name);
            }
        }
        return null;
    }

    private Literal valueOperation(Operation operation, Literal leftHandSide, Literal rightHandSide) {
        if (operation instanceof AddOperation) {
            if (leftHandSide instanceof PixelLiteral left && rightHandSide instanceof PixelLiteral right) {
                return new PixelLiteral(left.value + right.value);
            }
            if (leftHandSide instanceof PercentageLiteral left && rightHandSide instanceof PercentageLiteral right) {
                return new PercentageLiteral(left.value + right.value);
            }
            if (leftHandSide instanceof ScalarLiteral left && rightHandSide instanceof ScalarLiteral right) {
                return new ScalarLiteral(left.value + right.value);
            }
        }

        if (operation instanceof SubtractOperation) {
            if (leftHandSide instanceof PixelLiteral left && rightHandSide instanceof PixelLiteral right) {
                return new PixelLiteral(left.value - right.value);
            }
            if (leftHandSide instanceof PercentageLiteral left && rightHandSide instanceof PercentageLiteral right) {
                return new PercentageLiteral(left.value - right.value);
            }
            if (leftHandSide instanceof ScalarLiteral left && rightHandSide instanceof ScalarLiteral right) {
                return new ScalarLiteral(left.value - right.value);
            }
        }

        // Een operation moet scalar zijn
        if (operation instanceof MultiplyOperation) {
            if (leftHandSide instanceof ScalarLiteral left) {
                if (rightHandSide instanceof PixelLiteral right)
                    return new PixelLiteral(left.value * right.value);
                if (rightHandSide instanceof PercentageLiteral right)
                    return new PercentageLiteral(left.value * right.value);
                if (rightHandSide instanceof ScalarLiteral right)
                    return new ScalarLiteral(left.value * right.value);
            }
            if (rightHandSide instanceof ScalarLiteral right) {
                if (leftHandSide instanceof PixelLiteral left)
                    return new PixelLiteral(left.value * right.value);
                if (leftHandSide instanceof PercentageLiteral left)
                    return new PercentageLiteral(left.value * right.value);
            }
        }
        return null;
    }

    // Bereken waarde van expression en geef Literal terug
    private Literal valueExpression(Expression expression) {
        if (expression instanceof Literal literal) return literal;

        // variabelen opzoeken in scope
        if (expression instanceof VariableReference variableReference) {
            return lookupVariable(variableReference.name);
        }

        // beide kanten uitrekenen en dan operatie uitvoeren
        if (expression instanceof Operation operation) {
            Literal leftHandSide = valueExpression(operation.lhs);
            Literal rightHandSide = valueExpression(operation.rhs);
            return valueOperation(operation, leftHandSide, rightHandSide);
        }

        return null;
    }
}
