# Laboratory work 2: Determinism in Finite Automata. Conversion from NDFA 2 DFA. Chomsky Hierarchy.

### Course: Formal Languages & Finite Automata
### Author: Botezatu Marius, st.gr. FAF-212 (variant 2)

----

## Objectives:

1.  Understand what an automaton is and what it can be used for.
2. Continuing the work in the same repository and the same project, the following need to be added: a. Provide a function in your grammar type/class that could classify the grammar based on Chomsky hierarchy. <br/>
b. For this you can use the variant from the previous lab.
3. According to your variant number (by universal convention it is register ID), get the finite automaton definition and do the following tasks: <br/>
a. Implement conversion of a finite automaton to a regular grammar. <br/>
b. Determine whether your FA is deterministic or non-deterministic. <br/>
c. Implement some functionality that would convert an NDFA to a DFA. <br/>
d. Represent the finite automaton graphically (Optional, and can be considered as a bonus point): <br/>



## Implementation description

### 1. Creating the method that can classify a grammar based on Chomsky hierarchy.
* To the Grammar class I added a method called "classifyGrammar" that returns a string, either "type 3", "type 2", "type 1" or "type 0", depending on what type the grammar is. I stored the grammar production rules in a hash map and the function goes through each row of key/value pairs, checking them for various conditions to determine what type the grammar is not. For example, if the value part, the one that is composed of terminal and nonterminal variables, contains "ε" then type 1 would be set to false. In the end the function goes through every type in descending order and returns the first one that is set to true.

* Part of the code that deals with these conditions:
```
if (part[0].length() > 1) {
                type2 = false;
                type3 = false;
            }

            if (value.contains("ε"))
                type1 = false;

            if (ntv.indexOf(value.charAt(0)) > -1)
                left = true;
            else
                right = true;

            for (int i = 0; i < ntv.length(); i++) {
                sum += countChar(value, ntv.charAt(i));
                int j = value.indexOf(ntv.charAt(i));
                if (j > 0 && value.length() > (j + 1)) {
                    type3 = false;
                }
            }
            if (sum > 1)
                type3 = false;

        });
        if (left == right)
            type3 = false;

```
* Testing with the grammar from the previous lab
```
P={ 
    S → aS
    S → bD
    S → fR
    D → cD
    D → dR
    R → bR
    R → f
    D → d
}
The grammar from the previous lab is of type 3
```
* Testing with other random grammars
```
P={
    S → aSb
    S → baS
    S → cRdd
    S → dL
    R → aL
    L → f
}
The grammar is of type 2
```
```
P={
    S->aSBC 
    BC->B 
    C->a 
    B->b
}
The grammar is of type 1
```
```
P={
    S->aSBC 
    BC->B 
    C->a 
    B->b
    B->ε
}
The grammar is of type 0
```
### 2. Implementing conversion of a finite automaton to a regular grammar.
* To the FiniteAutomaton class I added a method called "toRegularGrammar" that converts the automaton to a regular grammar and then returns an object of type Grammar. The conversion is done by creating a "terminalVariables" array and making it equal to the "alphabet" array, by creating a "nonTerminalVariables" array and making it equal to the "possibleStates" array, except for the last element which represents the final state and is not a nonterminal variable, and by creating a hash map to store the production rules. The automata transitions are stored in an array of objects, where each object has 3 variables: current state, next state and transition label. These are taken and converted into a hash map form.
```
char[] nonTerminalVariables = Arrays.copyOf(possibleStates, possibleStates.length - 1);
        char[] terminalVariables = alphabet.clone();
        String rightSide;
        HashMap<String, String> productionRules = new HashMap<String, String>();
        for (int i = 0; i < transitions.length; i++) {
            String leftSide = String.valueOf(transitions[i].currentState) + (i + 1);
            if (i != 5)
                rightSide = String.valueOf(transitions[i].transitionLabel) + String.valueOf(transitions[i].nextState);
            else
                rightSide = String.valueOf(transitions[i].transitionLabel);

            productionRules.put(leftSide, rightSide);
        }

        Grammar grammar = new Grammar(nonTerminalVariables, terminalVariables, productionRules);
        return grammar;
```
```
FiniteAutomaton automaton2 = new FiniteAutomaton(possibleStates, alphabet, 'S', 'D', transitions);
        Grammar grammar2 = automaton2.toRegularGrammar();

```
### 3. Determining whether the finite automata is deterministic or non-deterministic.
* In the FiniteAutomaton class I added a method called "determineType" that checks whether the finite automata is deterministic, returning true if it is and false if it is not. It goes through the transitions states and checks various things that a deterministic automata should not have. 
* First of all, if any of the transition labels are "ε" it returns false.
```
for (int i = 0; i < transitions.length; i++) {
            if (transitions[i].transitionLabel == 'ε')
                return false;
```
* Second of all, if any 2 transitions have the same current state, the same transition label but different next states then it returns false.
```
if (transitions[j].currentState == transitions[i].currentState
                            && transitions[j].transitionLabel == transitions[i].transitionLabel
                            && transitions[j].nextState != transitions[i].nextState)
                        return false;
```
* Third of all, if at least one possible state does not have transitions for every element in the alphabet then it returns false as well.
```
int count = 0;
        for (int i = 0; i < possibleStates.length; i++) {
            count = 0;
            for (int j = 0; j < alphabet.length; j++) {
                for (int z = 0; z < transitions.length; z++) {
                    if (transitions[z].currentState == possibleStates[i]
                            && transitions[z].transitionLabel == alphabet[j]) {
                        count++;
                        break;
                    }
                }
            }
            if (count != alphabet.length)
                return false;
        }
        return true;
```
* Results from testing with the finite automata from my variant
```
Q = {q0,q1,q2,q3,q4},
∑ = {a,b,c},
F = {q4},
δ(q0,a) = q1,
δ(q1,b) = q2,
δ(q1,b) = q3,
δ(q2,c) = q3,
δ(q3,a) = q3,
δ(q3,b) = q4.
The finite automata is non-deterministic
```
* Results from testing with a deterministic finite automata
```
Q = {q0,q1},
∑ = {a,b,c},
F = {q1},
δ(q0,a) = q1,
δ(q0,b) = q1,
δ(q0,c) = q1,
δ(q1,a) = q0,
δ(q1,b) = q0,
δ(q1,c) = q0.
The finite automata is deterministic
```
* It is also worth mentioning that I do not store the possible states as q0,q1,q2,etc, but rather as S,A,B,etc.
### 4. Converting NDFA to DFA
* In the FiniteAutomaton class I created a method called "toDeterministic" that creates a DFA out of the current NDFA and returns it as a FiniteAutomaton object. The algorithm to do this works in the following way: 
* 2 new array lists are created, one for holding the new set of transitions and one for holding the new possible states.The initial transitions and set of possible states are copied to these but are later modified.
* We go through every transition and look for transitions that have the same current state, same transition label but different next states. When we find them we merge their 2 next states into a string "merge". After reaching the end of the transitions we go through them again and this time if the string "merge" contains the substring current state, the current state is replaced with the "merge" string. Same thing happens for next state. 
* By looking at the current state and next state of every transition we can add those possible states that are not already in the new "possible states" array list.
* Every possible state should have a transition for every specific element in the alphabet. If this is not the case then they are added. If necessary a new state "E" is created, which acts as a trap/dead state.
* In the end all of these parameters are given to a FiniteAutomaton constructor and a DFA object is created and returned by the method.
* This should work for any finite automaton, however, because I hold the final state in a char, there is no way to specify that an automaton has more than 1 final state. Making it possible to add more than 1 final state would require a bit more work. However, it is not necessary in my case because both NDFA/DFA's only have 1 final state.
```
FiniteAutomaton automaton3 = new FiniteAutomaton(DFApossibleStates.toArray(new String[0]), alphabet,
                initialState, finalState, DFAtransitions.toArray(new Transition[DFAtransitions.size()]));
        return automaton3;
```
* My NDFA:
```
Q = {q0,q1,q2,q3,q4},
∑ = {a,b,c},
F = {q4},
δ(q0,a) = q1,
δ(q1,b) = q2,
δ(q1,b) = q3,
δ(q2,c) = q3,
δ(q3,a) = q3,
δ(q3,b) = q4.
```
* The DFA created using this NDFA:
```
Q = S,A,BC,D,E
∑ = a,b,c
F = D
Transitions:
S->aA
A->bBC
BC->cBC
BC->aBC
BC->bD
S->bE
S->cE
A->aE
A->cE
D->aE
D->bE
D->cE
E->aE
E->bE
E->cE
```
* For the previous lab the 3 variables in the Transition class were chars, but to do certain things in this lab I needed them to be strings. Changing them directly would have required me to change some things from the previous lab and I wasn't sure if that was allowed, so I just extended the class. 
### 5. Represent the finite automaton graphically
* To do this I decided to use a combination of java and python. It works in the following way: in the FiniteAutomaton class I created a method that converts the finite automata to a string and returns it.
* This string is then passed into the method "sendToPython", located in the Main class. The method then takes the string and passes it to a python program that I made, located in the python folder. The method also executes the python script, so everything is done automatically.
* The python script, using the string that it is given as well as the library Graphviz, creates a pdf file at the root of the project where the finite automata is represented graphically.
* Results for my NDFA:

![first image](/Reports/images/lab2_image1.png)
* Results for my NDFA converted to a DFA:

![first image](/Reports/images/lab2_image2.png)

## Conclusion 
* As a result of doing this laboratory work I now fully understand how to classify grammar's  based on Chomsky hierarchy, how to convert a finite automaton to a regular grammar, how to convert a NDFA to a DFA and how to determine an automaton type. Furthermore, I've gained some valuable coding knowledge, such as how to work with the python library Graphitz to draw finite automata's and how to execute and pass parameters to a python script in java.

## References
* Course lecture "Regular language. Finite automata"
* https://graphviz.org/
* https://www.youtube.com/watch?v=pnyXgIXpKnc&t=466s
* https://www.youtube.com/watch?v=i-fk9o46oVY
* https://www.baeldung.com/java-working-with-python




















