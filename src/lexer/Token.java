package lexer;

public class Token {
    public final TokenType t;
    public final String c;

    public Token(TokenType t, String c) {
        this.t = t;
        this.c = c;
    }

    public String toString() {
        return "Token(" + t + ", " + c + " )";
    }
}
