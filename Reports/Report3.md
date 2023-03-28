# Laboratory work 3: Lexer & Scanner.
### Course: Formal Languages & Finite Automata
### Author: Botezatu Marius, st.gr. FAF-212 (variant 2)

----

## Objectives:

1.  Understand what lexical analysis is.
2. Get familiar with the inner workings of a lexer/scanner/tokenizer.
3. Implement a sample lexer and show how it works.


## Implementation description

### 1. Creating the various token types.

* I created a new folder called "lexer" that holds all of the classes needed to implement the sample lexer.
* I created a class called "TokenType" that enumerates all of the different token types that my lexer recognizes. 

```
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
```

### 2. Creating the token class.
* I created a class called "Token" that has 2 variables: one of type TokenType, which holds the token type and another of type string, which holds the lexeme. The lexeme is the actual text of the token, the sequence of characters that matches the pattern for the token type [1]. This class also has a method called "toString" that takes in the variables of the class and returns a string of the format "Token(token type, lexeme)".
```
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

```

### 3. Creating the actual lexer.
* I created a class called "Lexer". The main method of this class is "lex" which takes in an input string and returns an ArrayList of Token objects. It uses a switch statement to iterate through each character of the input string and adds a Token object to the result ArrayList for each recognized token. The class also has 2 more methods, "handleIdentifier" and "handleIntegerLiteral" which are helper methods that are called by the lex method to handle the recognition of identifiers and integer literals, respectively. The beginning of the lex method can be seen below.
```
 public ArrayList<Token> lex(String input) {
        ArrayList<Token> result = new ArrayList<>();
        for (int i = 0; i < input.length();) {
            char currentChar = input.charAt(i);
            switch (currentChar) {
```

### 4. Example of the lexer execution.
* The "lexer" folder also contains an "input.txt" file where we write what we want the lexer to scan. The contents of this file are read in the "Main" class and then stored as a string. After creating a "Lexer" object the "lex" method is called and this string is passed to it. 
* After returning the ArrayList of token's we go through each token in the list and call the "toString" method to print the token to the screen as a string.
```
String input = new String(Files.readAllBytes(Paths.get("src/lexer/input.txt")));

        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.lex(input);
        for (Token t : tokens) {
            System.out.println(t.toString());
        }
```
* With the following contents in the input.txt file
```
class Lamp {
  
  // stores the value for light
  // true if light is on
  // false if light is off
  boolean isOn;

  // method to turn on the light
  void turnOn() {
    isOn = true;

  }

  // method to turnoff the light
  void turnOff() {
    isOn = false;
  }
}
```
* The lexer creates the following tokens
```
Token(CLASS, class )
Token(IDENTIFIER, Lamp )
Token(LEFT_BRACE, { )
Token(COMMENT, stores the value for light )
Token(COMMENT, true if light is on )
Token(COMMENT, false if light is off )
Token(BOOLEAN, boolean )
Token(IDENTIFIER, isOn )
Token(SEMICOLON, ; )
Token(COMMENT, method to turn on the light )
Token(VOID, void )
Token(IDENTIFIER, turnOn )
Token(LEFT_PAREN, ( )
Token(RIGHT_PAREN, ) )
Token(LEFT_BRACE, { )
Token(IDENTIFIER, isOn )
Token(ASSIGN, = )
Token(BOOLEAN_LITERAL, true )
Token(SEMICOLON, ; )
Token(RIGHT_BRACE, } )
Token(COMMENT, method to turnoff the light )
Token(VOID, void )
Token(IDENTIFIER, turnOff )
Token(LEFT_PAREN, ( )
Token(RIGHT_PAREN, ) )
Token(LEFT_BRACE, { )
Token(IDENTIFIER, isOn )
Token(ASSIGN, = )
Token(BOOLEAN_LITERAL, false )
Token(SEMICOLON, ; )
Token(RIGHT_BRACE, } )
Token(RIGHT_BRACE, } )
```

## Conclusion 
* As a result of doing this laboratory work I now fully understand what a lexer is, how exactly it breaks down a string into tokens and what a token actually consists of. Besides understanding the theoretical part I also understood how one can implement a sample lexer in code. All of this knowledge has taken me one step further to understanding how a compiler works. 

## References
[1] [Lexical analysis](https://en.wikipedia.org/wiki/Lexical_analysis)





















