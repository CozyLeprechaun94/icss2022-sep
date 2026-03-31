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

public class Evaluator implements Transform {

    private IHANLinkedList<HashMap<String, Literal>> variableValues;

    public Evaluator() {
        variableValues = new HANLinkedList<>();
    }

    @Override
    public void apply(AST ast) {
        variableValues = new HANLinkedList<>();

        for (ASTNode child : ast.root.getChildren()) {
            if (child instanceof VariableAssignment va) {
                applyVariableAssignment(va);
            } else if (child instanceof Stylerule rule) {
                applyStylerule(rule);
            }
        }

        variableValues.removeFirst();
    }

    private void applyVariableAssignment(VariableAssignment variableAssignment){
        Literal value = valueExpression(variableAssignment.expression);
        variableValues.getFirst().put(variableAssignment.name.name, value);
    }

    private void applyStylerule(Stylerule stylerule){
        // empty
    }


    private Literal valueExpression(Expression expression) {
        if (expression instanceof Literal literal) return literal;

        return null;
    }
}
