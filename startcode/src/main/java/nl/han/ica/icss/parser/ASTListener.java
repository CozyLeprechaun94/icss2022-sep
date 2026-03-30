package nl.han.ica.icss.parser;

import nl.han.ica.datastructures.HANStack;
import nl.han.ica.datastructures.IHANStack;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
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

	@Override
	public void enterVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
		VariableAssignment varAssignment = new VariableAssignment();
		currentContainer.peek().addChild(varAssignment);
		currentContainer.push(varAssignment);
	}

	@Override
	public void exitVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
		currentContainer.pop();
	}

	@Override
	public void enterVariableReference(ICSSParser.VariableReferenceContext ctx) {
		var reference = new VariableReference(ctx.getText());
		currentContainer.peek().addChild(reference);
		currentContainer.push(reference);

	}

//	@Override
//	public void exitVariableReference(ICSSParser.VariableReferenceContext ctx) {
//		currentContainer.pop();
//	}

	@Override
	public void enterStylerule(ICSSParser.StyleruleContext ctx) {
		Stylerule stylerule = new Stylerule();
		currentContainer.peek().addChild(stylerule);
		currentContainer.push(stylerule);
	}

	@Override
	public void exitStylerule(ICSSParser.StyleruleContext ctx) {
		currentContainer.pop();
	}

	@Override
	public void enterSelector(ICSSParser.SelectorContext ctx) {
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
		Declaration declaration = new Declaration();
		declaration.property = new PropertyName(ctx.propertyName().getText());
		currentContainer.peek().addChild(declaration);
		currentContainer.push(declaration);
	}

	@Override
	public void exitDeclaration(ICSSParser.DeclarationContext ctx) {
		currentContainer.pop();
	}

	@Override
	public void enterLiteral(ICSSParser.LiteralContext ctx) {
		Literal literal;
		if (ctx.COLOR() != null) {
			literal = new ColorLiteral(ctx.getText());
		} else if (ctx.PIXELSIZE() != null) {
			literal = new PixelLiteral(Integer.parseInt(ctx.getText().replace("px", "")));
		} else if (ctx.TRUE() != null) {
			literal = new BoolLiteral(ctx.getText());
		} else if (ctx.FALSE() != null) {
			literal = new BoolLiteral(ctx.getText());
		} else if (ctx.SCALAR() != null) {
			literal = new ScalarLiteral(Integer.parseInt(ctx.getText()));
		} else if (ctx.PERCENTAGE() != null) {
			literal = new PercentageLiteral(Integer.parseInt(ctx.getText().replace("%", "")));
		} else {
			throw new RuntimeException("Unrecognized literal: " + ctx.getText());
		}
		currentContainer.peek().addChild(literal);
	}
}