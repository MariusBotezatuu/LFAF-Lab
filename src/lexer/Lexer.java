package lexer;

import java.util.ArrayList;

public class Lexer {

    private int handleIdentifier(String input, int i, ArrayList<Token> result) {
        StringBuilder identifier = new StringBuilder();
        char currentChar = input.charAt(i);
        while (Character.isLetterOrDigit(currentChar) || currentChar == '_') {
            identifier.append(currentChar);
            i++;
            if (i >= input.length()) {
                break;
            }
            currentChar = input.charAt(i);
        }
        result.add(new Token(TokenType.IDENTIFIER, identifier.toString()));
        return i;
    }

    private int handleIntegerLiteral(String input, int i, ArrayList<Token> result) {
        StringBuilder integerLiteral = new StringBuilder();
        char currentChar = input.charAt(i);
        while (Character.isDigit(currentChar)) {
            integerLiteral.append(currentChar);
            i++;
            if (i >= input.length()) {
                break;
            }
            currentChar = input.charAt(i);
        }
        result.add(new Token(TokenType.INTEGER_LITERAL, integerLiteral.toString()));
        return i;
    }

    public ArrayList<Token> lex(String input) {
        ArrayList<Token> result = new ArrayList<>();
        for (int i = 0; i < input.length();) {
            char currentChar = input.charAt(i);
            switch (currentChar) {
                case '(':
                    result.add(new Token(TokenType.LEFT_PAREN, "("));
                    i++;
                    break;
                case '[':
                    result.add(new Token(TokenType.LEFT_BRACKET, "["));
                    i++;
                    break;
                case ']':
                    result.add(new Token(TokenType.RIGHT_BRACKET, "]"));
                    i++;
                    break;
                case ',':
                    result.add(new Token(TokenType.COMMA, ","));
                    i++;
                    break;
                case '.':
                    result.add(new Token(TokenType.DOT, "."));
                    i++;
                    break;
                case '"':
                    result.add(new Token(TokenType.QUOTE, "\""));
                    i++;
                    break;
                case ')':
                    result.add(new Token(TokenType.RIGHT_PAREN, ")"));
                    i++;
                    break;
                case '{':
                    result.add(new Token(TokenType.LEFT_BRACE, "{"));
                    i++;
                    break;
                case '}':
                    result.add(new Token(TokenType.RIGHT_BRACE, "}"));
                    i++;
                    break;
                case ';':
                    result.add(new Token(TokenType.SEMICOLON, ";"));
                    i++;
                    break;
                case '+':
                    result.add(new Token(TokenType.PLUS, "+"));
                    i++;
                    break;
                case '-':
                    result.add(new Token(TokenType.MINUS, "-"));
                    i++;
                    break;
                case '*':
                    result.add(new Token(TokenType.TIMES, "*"));
                    i++;
                    break;
                case '/':
                    if (i + 1 < input.length() && input.charAt(i + 1) == '/') {
                        StringBuilder stringComment = new StringBuilder();
                        i += 2;
                        while (i < input.length() && input.charAt(i) != '\n') {
                            stringComment.append(input.charAt(i));
                            i++;
                        }
                        result.add(new Token(TokenType.COMMENT, stringComment.toString().trim()));
                    } else {
                        result.add(new Token(TokenType.DIVIDE, "/"));
                        i++;
                    }
                    break;
                case '=':
                    if (i + 1 < input.length() && input.charAt(i + 1) == '=') {
                        result.add(new Token(TokenType.EQUALS, "=="));
                        i += 2;
                    } else {
                        result.add(new Token(TokenType.ASSIGN, "="));
                        i++;
                    }
                    break;
                case 'p':
                    if (i + 6 <= input.length() && input.substring(i, i + 6).equals("public")) {
                        result.add(new Token(TokenType.PUBLIC, "public"));
                        i += 6;
                    } else if (i + 7 <= input.length() && input.substring(i, i + 7).equals("private")) {
                        result.add(new Token(TokenType.PRIVATE, "private"));
                        i += 7;
                    } else {
                        i = handleIdentifier(input, i, result);
                    }
                    break;
                case 's':
                    if (i + 6 <= input.length() && input.substring(i, i + 6).equals("static")) {
                        result.add(new Token(TokenType.STATIC, "static"));
                        i += 6;
                    } else if (i + 6 <= input.length() && input.substring(i, i + 6).equals("switch")) {
                        result.add(new Token(TokenType.SWITCH, "switch"));
                        i += 6;
                    } else {
                        i = handleIdentifier(input, i, result);
                    }
                    break;
                case 'S':
                    if (i + 6 <= input.length() && input.substring(i, i + 6).equals("String")) {
                        result.add(new Token(TokenType.STRING, "String"));
                        i += 6;
                    } else {
                        i = handleIdentifier(input, i, result);
                    }
                    break;
                case 'c':
                    if (i + 5 <= input.length() && input.substring(i, i + 5).equals("class")) {
                        result.add(new Token(TokenType.CLASS, "class"));
                        i += 5;
                    } else if (i + 8 <= input.length() && input.substring(i, i + 8).equals("continue")) {
                        result.add(new Token(TokenType.CONTINUE, "continue"));
                        i += 8;
                    } else if (i + 4 <= input.length() && input.substring(i, i + 4).equals("case")) {
                        result.add(new Token(TokenType.CASE, "case"));
                        i += 4;
                    } else if (i + 4 <= input.length() && input.substring(i, i + 4).equals("char")) {
                        result.add(new Token(TokenType.CHAR, "char"));
                        i += 4;
                    } else {
                        i = handleIdentifier(input, i, result);
                    }
                    break;
                case 'v':
                    if (i + 4 <= input.length() && input.substring(i, i + 4).equals("void")) {
                        result.add(new Token(TokenType.VOID, "void"));
                        i += 4;
                    } else {
                        i = handleIdentifier(input, i, result);
                    }
                    break;
                case 'i':
                    if (i + 3 <= input.length() && input.substring(i, i + 3).equals("int")) {
                        result.add(new Token(TokenType.INT, "int"));
                        i += 3;
                    } else if (i + 2 <= input.length() && input.substring(i, i + 2).equals("if")) {
                        result.add(new Token(TokenType.IF, "if"));
                        i += 2;
                    } else {
                        i = handleIdentifier(input, i, result);
                    }
                    break;
                case 'e':
                    if (i + 4 <= input.length() && input.substring(i, i + 4).equals("else")) {
                        result.add(new Token(TokenType.ELSE, "else"));
                        i += 4;
                    } else {
                        i = handleIdentifier(input, i, result);
                    }
                    break;
                case 'f':
                    if (i + 3 <= input.length() && input.substring(i, i + 3).equals("for")) {
                        result.add(new Token(TokenType.FOR, "for"));
                        i += 3;
                    } else if (i + 5 <= input.length() && input.substring(i, i + 5).equals("float")) {
                        result.add(new Token(TokenType.FLOAT, "float"));
                        i += 5;
                    } else if (i + 5 <= input.length() && input.substring(i, i + 5).equals("false")) {
                        result.add(new Token(TokenType.BOOLEAN_LITERAL, "false"));
                        i += 5;
                    } else {
                        i = handleIdentifier(input, i, result);
                    }
                    break;
                case 'w':
                    if (i + 5 <= input.length() && input.substring(i, i + 5).equals("while")) {
                        result.add(new Token(TokenType.WHILE, "while"));
                        i += 5;
                    } else {
                        i = handleIdentifier(input, i, result);
                    }
                    break;
                case 'd':
                    if (i + 2 <= input.length() && input.substring(i, i + 2).equals("do")) {
                        result.add(new Token(TokenType.DO, "do"));
                        i += 2;
                    } else if (i + 7 <= input.length() && input.substring(i, i + 7).equals("default")) {
                        result.add(new Token(TokenType.DEFAULT, "default"));
                        i += 7;
                    } else {
                        i = handleIdentifier(input, i, result);
                    }
                    break;
                case 'b':
                    if (i + 5 <= input.length() && input.substring(i, i + 5).equals("break")) {
                        result.add(new Token(TokenType.BREAK, "break"));
                        i += 5;
                    } else if (i + 7 <= input.length() && input.substring(i, i + 7).equals("boolean")) {
                        result.add(new Token(TokenType.BOOLEAN, "boolean"));
                        i += 7;
                    } else {
                        i = handleIdentifier(input, i, result);
                    }
                    break;
                case 'r':
                    if (i + 6 <= input.length() && input.substring(i, i + 6).equals("return")) {
                        result.add(new Token(TokenType.RETURN, "return"));
                        i += 6;
                    } else {
                        i = handleIdentifier(input, i, result);
                    }
                    break;
                case 'n':
                    if (i + 3 <= input.length() && input.substring(i, i + 3).equals("new")) {
                        result.add(new Token(TokenType.NEW, "new"));
                        i += 3;
                    } else {
                        i = handleIdentifier(input, i, result);
                    }
                    break;
                case 't':
                    if (i + 4 <= input.length() && input.substring(i, i + 4).equals("true")) {
                        result.add(new Token(TokenType.BOOLEAN_LITERAL, "true"));
                        i += 4;
                    } else {
                        i = handleIdentifier(input, i, result);
                    }
                    break;
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    i = handleIntegerLiteral(input, i, result);
                    break;

                default:
                    if (Character.isLetter(currentChar)) {
                        i = handleIdentifier(input, i, result);
                    } else if (Character.isWhitespace(currentChar)) {
                        i++;
                    } else {
                        System.out.println("Error: unrecognized character at position " + i);
                        i++;
                    }
                    break;
            }
        }
        return result;
    }

}