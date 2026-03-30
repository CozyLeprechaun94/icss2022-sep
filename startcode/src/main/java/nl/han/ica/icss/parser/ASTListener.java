package nl.han.ica.icss.parser;

import nl.han.ica.datastructures.HANStack;
import nl.han.ica.datastructures.IHANStack;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;

import nl.han.ica.icss.parser.ICSSParser;
import nl.han.ica.icss.parser.ICSSBaseListener;

/**
 * This class extracts the ICSS Abstract Syntax Tree from the Antlr Parse tree.
 */
public class ASTListener extends ICSSBaseListener {

	//Accumulator attributes:
	private final AST ast;

	//Use this to keep track of the parent nodes when recursively traversing the ast
	private IHANStack<ASTNode> currentContainer;

	public ASTListener(AST ast) {
		this.ast = ast;
		currentContainer = new HANStack<>();
	}

	public ASTListener() {
		ast = new AST();
		currentContainer = new HANStack<>();
		currentContainer.push(ast.root);
	}

	public AST getAST() {
		return ast;
	}

//	@Override
//	public void enterStylesheet(gen.ICSSParser.StylesheetContext ctx) {
//		currentContainer.push(new Stylesheet());
//		ast.setRoot(new Stylesheet());
//	}

//	@Override
//	public void exitStylesheet(gen.ICSSParser.StylesheetContext ctx) {
//		currentContainer.pop();
//	}

	@Override
	public void enterStylerule(ICSSParser.StyleruleContext ctx) {
		currentContainer.push(new Stylerule());
	}

	@Override
	public void exitStylerule(ICSSParser.StyleruleContext ctx) {
		Stylerule stylerule = (Stylerule) currentContainer.pop();
		currentContainer.peek().addChild(stylerule);
	}

	@Override
	public void exitSelector(ICSSParser.SelectorContext ctx) {
		Selector selector;
		if (ctx.CLASS_IDENT() != null) {
			selector = new ClassSelector(ctx.CLASS_IDENT().getText());
		} else if (ctx.ID_IDENT() != null) {
			selector = new IdSelector(ctx.ID_IDENT().getText());
		} else {
			selector = new TagSelector(ctx.LOWER_IDENT().getText());
		}
		currentContainer.peek().addChild(selector);
	}

	@Override
	public void enterDeclaration(ICSSParser.DeclarationContext ctx) {
		currentContainer.push(new Declaration());
	}

	@Override
	public void exitDeclaration(ICSSParser.DeclarationContext ctx) {
		Declaration declaration = (Declaration) currentContainer.pop();
		currentContainer.peek().addChild(declaration);
	}

	@Override
	public void exitPropertyName(ICSSParser.PropertyNameContext ctx) {
		currentContainer.peek().addChild(new PropertyName(ctx.getText()));
	}

	@Override
	public void exitExpression(ICSSParser.ExpressionContext ctx) {
		Expression expr;
		if (ctx.COLOR() != null) {
			expr = new ColorLiteral(ctx.COLOR().getText());
		} else {
			expr = new PixelLiteral(ctx.PIXELSIZE().getText());
		}
		currentContainer.peek().addChild(expr);
	}

	@Override
	public void enterVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
		currentContainer.push(new VariableAssignment());
	}
}