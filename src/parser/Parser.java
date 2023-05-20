package parser;

import lexer.Token;
import lexer.TokenType;
import java.util.ArrayList;

public class Parser {
    private ArrayList<Token> tokens;
    private int currentTokenIndex;
    private Token currentToken;
    public Node root;

    public Parser(ArrayList<Token> tokens) {
        this.tokens = tokens;
        this.currentTokenIndex = 0;
        this.currentToken = tokens.get(0);
    }

    private void advance() {
        currentTokenIndex++;
        if (currentTokenIndex < tokens.size()) {
            currentToken = tokens.get(currentTokenIndex);
        }
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
            classDeclaration();
        }
    }

    private void classDeclaration() {
        match(TokenType.CLASS);
        match(TokenType.IDENTIFIER);
        match(TokenType.LEFT_BRACE);

        while (currentToken.t != TokenType.RIGHT_BRACE) {
            ignoreComments();
            if (currentToken.t == TokenType.PUBLIC ||
                    currentToken.t == TokenType.PRIVATE ||
                    currentToken.t == TokenType.STATIC) {
                fieldDeclaration();
            } else if (currentToken.t == TokenType.INT || currentToken.t == TokenType.STRING
                    || currentToken.t == TokenType.BOOLEAN
                    || currentToken.t == TokenType.FLOAT ||
                    currentToken.t == TokenType.CHAR || currentToken.t == TokenType.VOID) {
                methodDeclaration();
            } else {
                throw new RuntimeException("Syntax error: unexpected token " + currentToken.t);
            }
        }

        match(TokenType.RIGHT_BRACE);
    }

    private void fieldDeclaration() {
        accessModifier();
        staticModifier();
        fieldType();
        match(TokenType.IDENTIFIER);
        match(TokenType.SEMICOLON);
    }

    private void methodFieldDeclaration() {
        fieldType();
        match(TokenType.IDENTIFIER);
        match(TokenType.SEMICOLON);
    }

    private void fieldType() {
        if (currentToken.t == TokenType.INT || currentToken.t == TokenType.STRING || currentToken.t == TokenType.BOOLEAN
                || currentToken.t == TokenType.FLOAT ||
                currentToken.t == TokenType.CHAR) {
            match(currentToken.t);
        } else {
            throw new RuntimeException("Syntax error: unexpected token " + currentToken.t);
        }
    }

    private void accessModifier() {
        if (currentToken.t == TokenType.PUBLIC ||
                currentToken.t == TokenType.PRIVATE) {
            match(currentToken.t);
        }
    }

    private void staticModifier() {
        if (currentToken.t == TokenType.STATIC) {
            match(TokenType.STATIC);
        }
    }

    private void methodDeclaration() {
        match(currentToken.t);
        match(TokenType.IDENTIFIER);
        match(TokenType.LEFT_PAREN);

        if (currentToken.t != TokenType.RIGHT_PAREN) {
            parameters();
        }

        match(TokenType.RIGHT_PAREN);
        methodBody();
    }

    private void parameters() {
        parameter();

        while (currentToken.t == TokenType.COMMA) {
            match(TokenType.COMMA);
            parameter();
        }
    }

    private void parameter() {
        if (currentToken.t == TokenType.INT || currentToken.t == TokenType.STRING
                || currentToken.t == TokenType.BOOLEAN
                || currentToken.t == TokenType.FLOAT ||
                currentToken.t == TokenType.CHAR)
            match(currentToken.t);
        else
            throw new RuntimeException("Syntax error: unexpected token " + currentToken.t);
        match(TokenType.IDENTIFIER);
    }

    private void methodBody() {
        match(TokenType.LEFT_BRACE);

        while (currentToken.t != TokenType.RIGHT_BRACE) {
            statement();
        }

        match(TokenType.RIGHT_BRACE);
    }

    private void statement() {
        if (currentToken.t == TokenType.IDENTIFIER) {
            assignmentStatement();
        } else if (currentToken.t == TokenType.INT || currentToken.t == TokenType.STRING
                || currentToken.t == TokenType.BOOLEAN
                || currentToken.t == TokenType.FLOAT ||
                currentToken.t == TokenType.CHAR) {
            methodFieldDeclaration();
        } else if (currentToken.t == TokenType.IF) {
            ifStatement();
        } else if (currentToken.t == TokenType.WHILE) {
            whileStatement();
        } else if (currentToken.t == TokenType.RETURN) {
            returnStatement();
        } else {
            throw new RuntimeException("Syntax error: unexpected token " + currentToken.t);
        }
    }

    private void assignmentStatement() {
        match(TokenType.IDENTIFIER);
        match(TokenType.ASSIGN);
        parseExpression();
        match(TokenType.SEMICOLON);
    }

    private void ifStatement() {
        match(TokenType.IF);
        match(TokenType.LEFT_PAREN);
        parseExpression();
        match(TokenType.RIGHT_PAREN);
        statement();

        if (currentToken.t == TokenType.ELSE) {
            match(TokenType.ELSE);
            statement();
        }
    }

    private void whileStatement() {
        match(TokenType.WHILE);
        match(TokenType.LEFT_PAREN);
        parseExpression();
        match(TokenType.RIGHT_PAREN);
        statement();
    }

    private void returnStatement() {
        match(TokenType.RETURN);

        if (currentToken.t != TokenType.SEMICOLON) {
            parseExpression();
        }

        match(TokenType.SEMICOLON);
    }

    private void parseExpression() {
        if (currentToken.t == TokenType.IDENTIFIER) {
            match(TokenType.IDENTIFIER);
        } else if (currentToken.t == TokenType.INTEGER_LITERAL ||
                currentToken.t == TokenType.BOOLEAN_LITERAL) {
            match(currentToken.t);
        } else if (currentToken.t == TokenType.LEFT_PAREN) {
            match(TokenType.LEFT_PAREN);
            parseExpression();
            match(TokenType.RIGHT_PAREN);
        } else {
            throw new RuntimeException("Syntax error: unexpected token " + currentToken.t);
        }

        while (isMultiplicativeOperator()) {
            match(currentToken.t);
            parseExpression();
        }

        while (isAdditiveOperator()) {
            match(currentToken.t);
            parseExpression();
        }

        if (isRelationalOperator()) {
            match(currentToken.t);
            parseExpression();
        }
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
}