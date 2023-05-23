# Laboratory work 5: Parser & Building an Abstract Syntax Tree.

### Course: Formal Languages & Finite Automata

### Author: Botezatu Marius, st.gr. FAF-212 (variant 2)

---

## Objectives:

1. Get familiar with parsing, what it is and how it can be programmed.
2. Get familiar with the concept of AST.
3. Implement the necessary data structures for an AST that could be used for the text you have processed in the 3rd lab work.
4. Implement a simple parser program that could extract the syntactic information from the input text.

## Implementation description

### 1. Creating the parser.

- The lexer from the 3rd lab produces an array list of token objects. Each token is composed of a token type object and a string which holds the lexeme, the sequence of characters that matches the pattern for the token type.

```
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

```

- The lexer recognizes the following token types

```
package lexer;

public enum TokenType {
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

- It is for a simplified version of the java language. So there are some restrictions even on the keywords that are available. For instance, even though the keyword "class" is available, you cannot define a class inside another class.

- I created a new package called "parser" and put a new class called "Parser" into it. When creating a new instance of this class one must pass in the array list of tokens as a parameter to the constructor.

```
public Parser(ArrayList<Token> tokens) {
        this.tokens = tokens;
        this.currentTokenIndex = 0;
        this.currentToken = tokens.get(0);
        root = new NodeAST("Program");
    }
```

- To actually parse through the tokens I created a public method called "parse". It works by going through the list of tokens one by one, analyzing them and calling various other private methods depending on what the token type is. For example, if the token type is "IF", the method "ifStatement" would be called. This method would know that an if statement should be expected, so it will try to match the current token with "IF", and then with a left parenthesis, and then it would call a different method that will deal with the actual condition of the if statement. After this it will try to match the current token with a right parenthesis. Next a method that deals with the expression of the statement will be called, so if the condition is satisfied, and so on.

- During the parsing phase if something does not go as expected the parser will return an error. There are 3 different errors that it can produce. Examples of each are shown below.

- Input string:

```
class Lamp {
  int turnoff){

  }
}
```

- Result:

```
Exception in thread "main" java.lang.RuntimeException: Syntax error: expected LEFT_PAREN but found RIGHT_PAREN
```

- Input string:

```
class Lamp {
  public2 int test;
}
```

- Result:

```
Exception in thread "main" java.lang.RuntimeException: Syntax error: unexpected token INTEGER_LITERAL
```

- Input string:

```
class Lamp {
  public boolean isOn;

    // method to turn on the light
  void turnOn(int i1, boolean c) {
    isOn = true;
    int i;

}
```

- Result:

```
Exception in thread "main" java.lang.RuntimeException: Syntax error: missing token RIGHT_BRACE
```

### 2. Building the AST.

- In the parser package I added a new class called "NodeAST". This class has 3 fields: a type, a value and an array list of other NodeAST objects called "children". An instance of this class would represent a node in the AST.
- Basically, the type is the type of node it represents. For instance, "ClassDeclaration". The value is the value of the node, if it has any. For instance, for our class declaration example the value could be the class name. The children array list holds the children nodes of the current node. If we were to take our class declaration example again, the children nodes could be "FieldDeclaration" and "MethodDeclaration".
- I modified my parser class to build the AST as it is parsing. Firstly, I added a NodeAST field called "root". This is the root of the tree and is needed to print the AST to the screen later on. The type of the root node is "Program".
- Secondly, I modified the methods in the "Parser" class so that most of them return an NodeAST object and most of them add NodeAST objects to the object that they return, after receiving objects by other methods. To understand this better we can take an example. If we take the following simple input string:

```
class Lamp {
  public boolean isOn;
  private int seconds;
}
```

- The root node would be of type "Program". It will add a "ClassDeclaration" node to it's children array list. The "ClassDeclaration" node will have a value of "Lamp" and an array list of 2 "FieldDeclaration" children. Each "FieldDeclaration" node will have a value representing the name of the field and 2 children respectively, one of type "AccessModifier" and another of type "FieldType".
- Naturally, the AST that is constructed will not contain unnecessary information such as spaces, parentheses and braces.

```
AST:
Program
  ClassDeclaration
  Lamp
    FieldDeclaration
    isOn
      AccessModifier
      public
      FieldType
      boolean
    FieldDeclaration
    seconds
      AccessModifier
      private
      FieldType
      int
```

- To print the AST created to the screen I added a public method called "showAST" to the "parser" class. This method starts with the root node and iterates through it's children, trying to go as deep as possible in the branch. I've shown an example above but here is a slightly more complicated AST.
- Input string:

```
class Lamp {

  // stores the value for light
  // true if light is on
  // false if light is off
  public boolean isOn;
  private int test;
  private static int test2;

  // method to turn on the light
  void turnOn(int i1, boolean c) {
    isOn = true;
    int i;

    i=1+2;
    if (isOn == false)
      i = 2;
    else i = 1;
    i = 5+2+1;


  }

  // method to turnoff the light
  int turnOff() {
    isOn = false;
    int j;
    j=0;
    while (i == 0)
      j=j+1;
  return (j+10*1)+4;
  }
}

class test{
  void f(){
    char marius;
  }
}
```

- AST created:

```
AST:
Program
  ClassDeclaration
  Lamp
    FieldDeclaration
    isOn
      AccessModifier
      public
      FieldType
      boolean
    FieldDeclaration
    test
      AccessModifier
      private
      FieldType
      int
    FieldDeclaration
    test2
      AccessModifier
      private
      StaticModifier
      static
      FieldType
      int
    MethodDeclaration
    turnOn
      Parameters
        Parameter
        int
          i1
        Parameter
        boolean
          c
      MethodBody
          AssignmentStatement
          isOn=
            Expression
            true
          MethodFieldDeclaration
          i
            FieldType
            int
          AssignmentStatement
          i=
            Expression
            1
              Operator
              +
              Expression
              2
          IfStatement
            Expression
            isOn
              Operator
              ==
              Expression
              false
              AssignmentStatement
              i=
                Expression
                2
            ElseStatement
                AssignmentStatement
                i=
                  Expression
                  1
          AssignmentStatement
          i=
            Expression
            5
              Operator
              +
              Expression
              2
                Operator
                +
                Expression
                1
    MethodDeclaration
    turnOff
      MethodBody
          AssignmentStatement
          isOn=
            Expression
            false
          MethodFieldDeclaration
          j
            FieldType
            int
          AssignmentStatement
          j=
            Expression
            0
          WhileStatement
            Expression
            i
              Operator
              ==
              Expression
              0
              AssignmentStatement
              j=
                Expression
                j
                  Operator
                  +
                  Expression
                  1
          ReturnStatement
              Expression
              j
                Operator
                +
                Expression
                10
                  Operator
                  *
                  Expression
                  1
              Operator
              +
              Expression
              4
  ClassDeclaration
  test
    MethodDeclaration
    f
      MethodBody
          MethodFieldDeclaration
          marius
            FieldType
            char
```

## Conclusion

- As a result of doing this laboratory work I now understand the second step in the compilation process, which is parsing through the tokens created by the lexer and performing syntactical analysis. I've also understood what an AST is and how it can be created during the parsing process. Besides the theory, I've also studied and figured out how to use all of this knowledge to actually code a parser for a simplified version of the java programming language.
