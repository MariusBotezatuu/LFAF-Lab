package parser;

import lexer.Token;
import lexer.TokenType;
import java.util.ArrayList;

public class Parser {
    private ArrayList<Token> tokens;
    private int currentTokenIndex;
    private Token currentToken;
    public NodeAST root;

    public Parser(ArrayList<Token> tokens) {
        this.tokens = tokens;
        this.currentTokenIndex = 0;
        this.currentToken = tokens.get(0);
        root = new NodeAST("Program");
    }

    private void advance() {
        currentTokenIndex++;
        if (currentTokenIndex < tokens.size()) {
            currentToken = tokens.get(currentTokenIndex);
        }
        if (currentTokenIndex > tokens.size())
            throw new RuntimeException("Syntax error: missing token " + currentToken.t);

    }

    private void match(TokenType expectedType) {
        if (currentToken.t == expectedType) {
            advance();
        } else {
            throw new RuntimeException("Syntax error: expected " + expectedType +
                    " but found " + currentToken.t);
        }
    }

    public void parse() {
        ignoreComments();
        compilationUnit();
    }

    private void ignoreComments() {
        while (currentTokenIndex < tokens.size() && tokens.get(currentTokenIndex).t == TokenType.COMMENT) {
            currentTokenIndex += 1;
            currentToken = tokens.get(currentTokenIndex);
        }
    }

    private void compilationUnit() {
        while (currentTokenIndex < tokens.size()) {
            root.addChild(classDeclaration());
        }
    }

    private NodeAST classDeclaration() {
        NodeAST classNode;
        match(TokenType.CLASS);
        classNode = new NodeAST("ClassDeclaration", currentToken.c);
        match(TokenType.IDENTIFIER);
        match(TokenType.LEFT_BRACE);

        while (currentToken.t != TokenType.RIGHT_BRACE) {
            ignoreComments();
            if (currentToken.t == TokenType.PUBLIC ||
                    currentToken.t == TokenType.PRIVATE ||
                    currentToken.t == TokenType.STATIC) {
                classNode.addChild(fieldDeclaration());
            } else if (currentToken.t == TokenType.INT || currentToken.t == TokenType.STRING
                    || currentToken.t == TokenType.BOOLEAN
                    || currentToken.t == TokenType.FLOAT ||
                    currentToken.t == TokenType.CHAR || currentToken.t == TokenType.VOID) {
                classNode.addChild(methodDeclaration());
            } else {
                throw new RuntimeException("Syntax error: unexpected token " + currentToken.t);
            }
        }
        match(TokenType.RIGHT_BRACE);
        return classNode;
    }

    private NodeAST fieldDeclaration() {
        NodeAST fieldNode = new NodeAST("FieldDeclaration");
        NodeAST child = accessModifier();
        if (child.type != null)
            fieldNode.addChild(child);
        child = staticModifier();
        if (child.type != null)
            fieldNode.addChild(child);
        fieldNode.addChild(fieldType());
        fieldNode.addValue(currentToken.c);
        match(TokenType.IDENTIFIER);
        match(TokenType.SEMICOLON);
        return fieldNode;
    }

    private NodeAST methodFieldDeclaration() {
        NodeAST methodFieldDeclarationNode = new NodeAST("MethodFieldDeclaration");
        methodFieldDeclarationNode.addChild(fieldType());
        methodFieldDeclarationNode.addValue(currentToken.c);
        match(TokenType.IDENTIFIER);
        match(TokenType.SEMICOLON);
        return methodFieldDeclarationNode;
    }

    private NodeAST fieldType() {
        NodeAST fieldTypeNode;
        if (currentToken.t == TokenType.INT || currentToken.t == TokenType.STRING || currentToken.t == TokenType.BOOLEAN
                || currentToken.t == TokenType.FLOAT ||
                currentToken.t == TokenType.CHAR) {
            fieldTypeNode = new NodeAST("FieldType", currentToken.c);
            match(currentToken.t);
        } else {
            throw new RuntimeException("Syntax error: unexpected token " + currentToken.t);
        }
        return fieldTypeNode;
    }

    private NodeAST accessModifier() {
        NodeAST accessModifierNode;
        if (currentToken.t == TokenType.PUBLIC ||
                currentToken.t == TokenType.PRIVATE) {
            accessModifierNode = new NodeAST("AccessModifier", currentToken.c);
            match(currentToken.t);
        } else
            accessModifierNode = new NodeAST();
        return accessModifierNode;
    }

    private NodeAST staticModifier() {
        NodeAST staticModifierNode;
        if (currentToken.t == TokenType.STATIC) {
            staticModifierNode = new NodeAST("StaticModifier", currentToken.c);
            match(TokenType.STATIC);
        } else
            staticModifierNode = new NodeAST();
        return staticModifierNode;
    }

    private NodeAST methodDeclaration() {
        NodeAST methodDeclarationNode;
        match(currentToken.t);
        methodDeclarationNode = new NodeAST("MethodDeclaration", currentToken.c);
        match(TokenType.IDENTIFIER);
        match(TokenType.LEFT_PAREN);

        if (currentToken.t != TokenType.RIGHT_PAREN) {
            methodDeclarationNode.addChild(parameters());
        }

        match(TokenType.RIGHT_PAREN);
        methodDeclarationNode.addChild(methodBody());
        return methodDeclarationNode;
    }

    private NodeAST parameters() {
        NodeAST parametersNode = new NodeAST("Parameters");
        parametersNode.addChild(parameter());

        while (currentToken.t == TokenType.COMMA) {
            match(TokenType.COMMA);
            parametersNode.addChild(parameter());
        }
        return parametersNode;
    }

    private NodeAST parameter() {
        NodeAST parameterNode;
        if (currentToken.t == TokenType.INT || currentToken.t == TokenType.STRING
                || currentToken.t == TokenType.BOOLEAN
                || currentToken.t == TokenType.FLOAT ||
                currentToken.t == TokenType.CHAR) {
            parameterNode = new NodeAST("Parameter", currentToken.c);
            match(currentToken.t);
        } else
            throw new RuntimeException("Syntax error: unexpected token " + currentToken.t);
        NodeAST parameterNodeIdentifier = new NodeAST(currentToken.c);
        parameterNode.addChild(parameterNodeIdentifier);
        match(TokenType.IDENTIFIER);
        return parameterNode;
    }

    private NodeAST methodBody() {
        NodeAST methodBodyNode = new NodeAST("MethodBody");
        match(TokenType.LEFT_BRACE);

        while (currentToken.t != TokenType.RIGHT_BRACE) {
            methodBodyNode.addChild(statement());
        }

        match(TokenType.RIGHT_BRACE);
        return methodBodyNode;
    }

    private NodeAST statement() {
        NodeAST statementNode = new NodeAST("Statement");
        if (currentToken.t == TokenType.IDENTIFIER) {
            statementNode.addChild(assignmentStatement());
        } else if (currentToken.t == TokenType.INT || currentToken.t == TokenType.STRING
                || currentToken.t == TokenType.BOOLEAN
                || currentToken.t == TokenType.FLOAT ||
                currentToken.t == TokenType.CHAR) {
            statementNode.addChild(methodFieldDeclaration());
        } else if (currentToken.t == TokenType.IF) {
            statementNode.addChild(ifStatement());
        } else if (currentToken.t == TokenType.WHILE) {
            statementNode.addChild(whileStatement());
        } else if (currentToken.t == TokenType.RETURN) {
            statementNode.addChild(returnStatement());
        } else {
            throw new RuntimeException("Syntax error: unexpected token " + currentToken.t);
        }
        return statementNode;
    }

    private NodeAST assignmentStatement() {
        NodeAST assignmentStatementNode = new NodeAST("AssignmentStatement", currentToken.c + "=");
        match(TokenType.IDENTIFIER);
        match(TokenType.ASSIGN);
        assignmentStatementNode.addChild(parseExpression());
        match(TokenType.SEMICOLON);
        return assignmentStatementNode;
    }

    private NodeAST ifStatement() {
        NodeAST ifNode = new NodeAST("IfStatement");
        match(TokenType.IF);
        match(TokenType.LEFT_PAREN);
        ifNode.addChild(parseExpression());
        match(TokenType.RIGHT_PAREN);
        ifNode.addChild(statement());

        if (currentToken.t == TokenType.ELSE) {
            ifNode.addChild(elseStatement());
        }
        return ifNode;
    }

    private NodeAST elseStatement() {
        NodeAST elseStatementNode = new NodeAST("ElseStatement");
        match(TokenType.ELSE);
        elseStatementNode.addChild(statement());
        return elseStatementNode;
    }

    private NodeAST whileStatement() {
        NodeAST whileStatementNode = new NodeAST("WhileStatement");
        match(TokenType.WHILE);
        match(TokenType.LEFT_PAREN);
        whileStatementNode.addChild(parseExpression());
        match(TokenType.RIGHT_PAREN);
        whileStatementNode.addChild(statement());
        return whileStatementNode;
    }

    private NodeAST returnStatement() {
        NodeAST returnStatementNode = new NodeAST("ReturnStatement");
        match(TokenType.RETURN);

        if (currentToken.t != TokenType.SEMICOLON) {
            returnStatementNode.addChild(parseExpression());
        }

        match(TokenType.SEMICOLON);
        return returnStatementNode;
    }

    private NodeAST parseExpression() {
        NodeAST parseExpressionNode;
        if (currentToken.t == TokenType.IDENTIFIER) {
            parseExpressionNode = new NodeAST("Expression", currentToken.c);
            match(TokenType.IDENTIFIER);
        } else if (currentToken.t == TokenType.INTEGER_LITERAL ||
                currentToken.t == TokenType.BOOLEAN_LITERAL) {
            parseExpressionNode = new NodeAST("Expression", currentToken.c);
            match(currentToken.t);
        } else if (currentToken.t == TokenType.LEFT_PAREN) {
            match(TokenType.LEFT_PAREN);
            parseExpressionNode = new NodeAST("Expression");
            parseExpressionNode.addChild(parseExpression());
            match(TokenType.RIGHT_PAREN);
        } else {
            throw new RuntimeException("Syntax error: unexpected token " + currentToken.t);
        }

        while (isMultiplicativeOperator()) {
            NodeAST multiplicativeOperationNode = new NodeAST("Operator", currentToken.c);
            parseExpressionNode.addChild(multiplicativeOperationNode);
            match(currentToken.t);
            parseExpressionNode.addChild(parseExpression());
        }

        while (isAdditiveOperator()) {
            NodeAST AdditiveOperationNode = new NodeAST("Operator", currentToken.c);
            parseExpressionNode.addChild(AdditiveOperationNode);
            match(currentToken.t);
            parseExpressionNode.addChild(parseExpression());
        }

        if (isRelationalOperator()) {
            NodeAST relationalOperationNode = new NodeAST("Operator", currentToken.c);
            parseExpressionNode.addChild(relationalOperationNode);
            match(currentToken.t);
            parseExpressionNode.addChild(parseExpression());
        }
        return parseExpressionNode;
    }

    private boolean isRelationalOperator() {
        TokenType type = currentToken.t;
        return type == TokenType.EQUALS;
    }

    private boolean isAdditiveOperator() {
        TokenType type = currentToken.t;
        return type == TokenType.PLUS || type == TokenType.MINUS;
    }

    private boolean isMultiplicativeOperator() {
        TokenType type = currentToken.t;
        return type == TokenType.TIMES || type == TokenType.DIVIDE;
    }

    private void showASTChildren(NodeAST n, int i) {
        for (NodeAST c : n.children) {
            if (!c.type.equals("Statement") && !(c.type.equals("Expression") && c.value == null)) {
                for (int j = 0; j < i; j++)
                    System.out.print("  ");
                System.out.println(c.type);
                if (c.value != null) {
                    for (int j = 0; j < i; j++)
                        System.out.print("  ");
                    System.out.println(c.value);
                }
            }
            if (c.children.size() > 0) {
                int i1 = i + 1;
                showASTChildren(c, i1);

            }
        }
    }

    public void showAST() {
        System.out.println(root.type);
        for (NodeAST n : root.children) {
            if (!n.type.equals("Statement") && !(n.type.equals("Expression") && n.value == null)) {
                System.out.print("  ");
                System.out.println(n.type);
                if (n.value != null) {
                    System.out.print("  ");
                    System.out.println(n.value);
                }
            }
            if (n.children.size() > 0)
                showASTChildren(n, 2);
        }
    }
}