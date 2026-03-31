package nl.han.ica.icss.generator;


import nl.han.ica.icss.ast.AST;
import nl.han.ica.icss.ast.ASTNode;
import nl.han.ica.icss.ast.Stylerule;

public class Generator {

	public String generate(AST ast) {
		StringBuilder result = new StringBuilder(); // Maak een nieuwe StringBuilder aan, omdat String niet veranderbaar is.

		for (ASTNode child : ast.root.getChildren()){
			if (child instanceof Stylerule stylerule) {
				// Er moet wat gebeuren
			}
		}
        return result.toString(); //Zet om naar String
	}

}
