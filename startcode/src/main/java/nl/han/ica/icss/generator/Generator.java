package nl.han.ica.icss.generator;


import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;

public class Generator {

    public String generate(AST ast) {
        StringBuilder result = new StringBuilder(); // Maak een nieuwe StringBuilder aan, omdat String niet veranderbaar is.

        for (ASTNode child : ast.root.getChildren()) {
            if (child instanceof Stylerule stylerule) {
                createStylerule(stylerule, result);
            }
        }
        return result.toString(); //Zet om naar String
    }

    private void createStylerule(Stylerule stylerule, StringBuilder result) {
        result.append(stylerule.selectors.get(0).toString()); // Selector naar string
        result.append(" {\n"); // Na Selector open curly bracket

        for (ASTNode node : stylerule.body) {
            if (node instanceof Declaration declaration) {
                createDeclaration(declaration, result);
            }
        }

        result.append("}\n\n"); //Sluit de stylerule met curly bracket
    }

    private void createDeclaration(Declaration declaration, StringBuilder result) {
        result.append("  ") //2 spaties
                .append(declaration.property.name) // Iets van background-color
                .append(": ") //Na het definiëren van de property name background-color:
                .append(getLiteralValue(declaration.expression)) // Voeg expression toe
                .append(";\n"); // Sluit de declaration

    }

    private String getLiteralValue(Expression expression) {
        if (expression instanceof PixelLiteral pixelLiteral) {
            return pixelLiteral.value + "px"; //Return px bij PixelLiteral
        } else if (expression instanceof PercentageLiteral percentageLiteral){
            return percentageLiteral.value + "%";
        } else if (expression instanceof ColorLiteral colorLiteral) {
            return colorLiteral.value; //return value van color ex. #ffffff
        }

        return ""; //return niks
    }
}
