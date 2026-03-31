package nl.han.ica.icss.generator;


import nl.han.ica.icss.ast.AST;
import nl.han.ica.icss.ast.ASTNode;
import nl.han.ica.icss.ast.Selector;
import nl.han.ica.icss.ast.Stylerule;

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

    private StringBuilder createStylerule(Stylerule stylerule, StringBuilder result) {
        result.append(stylerule.selectors.toString());
        for (ASTNode node : stylerule.body) {
            if(node instanceof Selector selector) {
                result.append(selector.toString());
            }
        }

        result.toString();
        return result;
    }
}
