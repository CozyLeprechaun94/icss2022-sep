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
        variableTypes.addFirst(new HashMap<>()); //Globale scope aanmaken

        //Loop door de stylesheet en verwerk variabelen en stylerules
        for (ASTNode child : stylesheet.getChildren()) {
            if (child instanceof VariableAssignment variableAssignment) {
                controlVariableAssignment(variableAssignment);
            } else if (child instanceof Stylerule styleRule) {
                controlStylerule(styleRule);
            }
        }

        variableTypes.removeFirst(); //Opruimen
    }

    // Nieuwe scope aanmaken voor de stylerule
    private void controlStylerule(Stylerule rule) {
        variableTypes.addFirst(new HashMap<>());
        controlBody(rule.body);
        variableTypes.removeFirst();
    }

    // Loop door de body heen en controleer alle nodes
    private void controlBody(List<ASTNode> body) {
        for (ASTNode node : body) {
            if (node instanceof VariableAssignment variableAssignment) {
                controlVariableAssignment(variableAssignment);
            } else if (node instanceof Declaration declaration) {
                controlDeclaration(declaration);
            } else if (node instanceof IfClause ifClause) {
                controlIfClause(ifClause);
            }
        }
    }

    //Controleer of de value met de property klopt
    private void controlDeclaration(Declaration declaration) {
        getExpressionType(declaration.expression);
    }

    // Sla het type van de variabelen op in de huidige scope
    private void controlVariableAssignment(VariableAssignment variableAssignment) {
        ExpressionType expressionType = getExpressionType(variableAssignment.expression);
        variableTypes.getFirst().put(variableAssignment.name.name, expressionType);
    }

    // Check of de variabele gedeclareerd zijn, zoek in alle scopes
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


    private void controlIfClause(IfClause ifClause) {
        // Controleer als de IF een Boolean bevat
        if (getExpressionType(ifClause.getConditionalExpression()) != ExpressionType.BOOL) {
            ifClause.setError("Condition must be of type bool!");
        }

        variableTypes.addFirst(new HashMap<>()); //Scope bovenop de stack zetten als een blok ingaat
        controlBody(ifClause.body);
        variableTypes.removeFirst(); // Scope opruimen wanneer je het if blok verlaat

        if (ifClause.elseClause != null) {
            variableTypes.addFirst(new HashMap<>());
            controlBody(ifClause.elseClause.body);
            variableTypes.removeFirst();
        }
    }

    private ExpressionType controlOperation(Operation operation) {
        ExpressionType leftHandSide = getExpressionType(operation.lhs);
        ExpressionType rightHandSide = getExpressionType(operation.rhs);

        //Geen kleuren in operation
        if (leftHandSide == ExpressionType.COLOR || rightHandSide == ExpressionType.COLOR) {
            operation.setError("Can't use colors with operations!");
            return ExpressionType.COLOR;
        }

        // Minimaal een scalar
        if (operation instanceof MultiplyOperation) {
            if (leftHandSide != ExpressionType.SCALAR && rightHandSide != ExpressionType.SCALAR) {
                operation.setError("Only one scalar type can be used!");

                return ExpressionType.UNDEFINED;
            }
            return leftHandSide != ExpressionType.SCALAR ? leftHandSide : rightHandSide;
        }

        // Zelfde type bij optellen en aftrekken
        if (operation instanceof AddOperation || operation instanceof SubtractOperation) {
            if (leftHandSide != rightHandSide) {
                operation.setError("Operations must be of the same literal type!");
                return ExpressionType.UNDEFINED;
            }
            return leftHandSide;
        }

        return ExpressionType.UNDEFINED;
    }

    //Geef de expressie type terug
    private ExpressionType getExpressionType(Expression expression) {
        if (expression instanceof BoolLiteral) return ExpressionType.BOOL;
        if (expression instanceof ColorLiteral) return ExpressionType.COLOR;
        if (expression instanceof PixelLiteral) return ExpressionType.PIXEL;
        if (expression instanceof PercentageLiteral) return ExpressionType.PERCENTAGE;
        if (expression instanceof ScalarLiteral) return ExpressionType.SCALAR;

        // Variabelen opzoeken in scope
        if (expression instanceof VariableReference variableReference) {
            return lookForVariable(variableReference.name, variableReference);
        }

        //Controleer operation
        if (expression instanceof Operation operation) {
            return controlOperation(operation);
        }

        return ExpressionType.UNDEFINED;
    }
}
