package lexer;

enum TokenType {
    // Java separators
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACKET, RIGHT_BRACKET, LEFT_BRACE, RIGHT_BRACE, SEMICOLON, COMMA, DOT, QUOTE,
    // Java keywords
    PUBLIC, PRIVATE, STATIC, CLASS, VOID, IF, ELSE, FOR, WHILE, DO, SWITCH, CASE, DEFAULT, BREAK, CONTINUE, RETURN, NEW,
    INT, STRING, BOOLEAN, FLOAT, CHAR,
    // Java operators
    PLUS, MINUS, TIMES, DIVIDE, ASSIGN, EQUALS,
    // Java literals
    INTEGER_LITERAL, BOOLEAN_LITERAL,
    // Java identifiers
    IDENTIFIER,
    // Java comments using "//"
    COMMENT
}