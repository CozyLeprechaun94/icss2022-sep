package nl.han.ica.icss.generator;


import nl.han.ica.icss.ast.*;

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
        result.append("{\n"); // Na Selector open curly bracet

        for (ASTNode node : stylerule.body) {
            if(node instanceof Declaration declaration) {
                createDeclaration(declaration, result);
            }
        }

        result.append("}\n\n"); //Sluit de stylerule met curly bracet
    }

    private void createDeclaration(Declaration declaration, StringBuilder result) {
        result.append(declaration.property.name) // Iets van background-color
                .append(": ") //Na het definiëren van de property name background-color:
                .append(declaration.expression) // Voeg expression toe
                .append(";\n"); // Sluit de declaration

    }
}
