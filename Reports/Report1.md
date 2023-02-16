# Laboratory work 1: Intro to formal languages. Regular grammars. Finite Automata.

### Course: Formal Languages & Finite Automata
### Author: Botezatu Marius, FAF-212, variant 2

----


## Objectives:

* Understand what a language is and what it needs to have in order to be considered a formal one.
* Create a local && remote repository of a VCS hosting service.
* Choose a programming language.
* Create a separate folder where you will be keeping the report.
* According to your variant number get the grammar definition.
* Implement a type/class for your grammar.
* Add one function that would generate 5 valid strings from the language expressed by your given grammar.
* Implement some functionality that would convert and object of type Grammar to one of type Finite Automaton.
* For the Finite Automaton, please add a method that checks if an input string can be obtained via the state transition from it.


## Implementation description

* First of all, the terminal and nonterminal symbols are stored in arrays and the production rules are stored using a hash map. Because there can be multiple rules for the same initial state, for example S->aS, S->bS, and hash maps cannot contain the same key value twice I decided to distinguish the rules by marking them as S1,S2,etc. 


```
HashMap<String, String> productionRules = new HashMap<String, String>();
        productionRules.put("S1", "aS");
        productionRules.put("S2", "bS");
        productionRules.put("S3", "cR");
        productionRules.put("S4", "dL");
        productionRules.put("R5", "dL");
        productionRules.put("R6", "e");
        productionRules.put("L7", "fL");
        productionRules.put("L8", "eL");
        productionRules.put("L9", "d");
        char[] nonTerminalVariables = { 'S', 'R', 'L' };
        char[] terminalVariables = { 'a', 'b', 'c', 'd', 'e', 'f' };
```
These are then given to the grammar object as constructor parameters.

* The Grammar class has 2 main methods. The first is for generating a valid string from the language expressed by my given grammar. It works by recursively calling itself and randomly choosing valid rules for the states that it is in until it reaches a final state, where it then returns the generated string. 
* The second is for converting the grammar to an object of type finite automaton. It works by creating a FiniteAutomaton object and passing in the necessary attributes as constructor parameters, necessary attributes include: set of states, alphabet, transition set, initial states and accepting states.
* An array of transition objects is used to create the transition set, where each object has 3 variables: initial state, next state and transition label. All of the other parameters passed into the FiniteAutomaton constructor are stored either as char or array's of char.
```
public FiniteAutomaton(char[] possibleStates, char[] alphabet, char initialState, char finalState,
        Transition[] transitions) {
        this.possibleStates = possibleStates.clone();
        this.alphabet = alphabet.clone();
        this.transitions = transitions.clone();
        this.initialState = initialState;
        this.finalState = finalState;
    }
```
* The FiniteAutomaton class has a method that checks whether an input string can be obtained by the created automaton. It works by initiating an initial state and going through each element of the string individually, checking whether there are any possible transitions that would make the appearance of that element possible while modifying the state accordingly. In the end if the transition is not possible or if the algorithm does not end in a final state the method returns false, otherwise it returns true. 


## Conclusions / Screenshots / Results
The created program is able to successfully generate valid strings by the given grammar, to convert the grammar to a finite automaton and to use this finite automaton to check whether a specific string can be obtained by it. Below is an example of the program generating 5 strings and checking whether 2 different strings belong to the language.

## Practical Results
![first image](/Reports/images/lab1_image1.png)


![second image](/Reports/images/lab1_image2.png)

