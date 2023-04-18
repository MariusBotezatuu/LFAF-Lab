# Laboratory work 4: Chomsky Normal Form.

### Course: Formal Languages & Finite Automata

### Author: Botezatu Marius, st.gr. FAF-212 (variant 2)

---

## Objectives:

1. Learn about Chomsky Normal Form.
2. Get familiar with the approaches of normalizing a grammar.
3. Implement a method for normalizing an input grammar by the rules of CNF.

## Implementation description

### 1. Creating the main method.

- First of all, I represent the production rules in a hash map where both the key and value is a string. The key is what is on the left hand side of the production rule, so the nonterminal symbol, and the value is what is on the right hand side of the rule, so the nonterminal/terminal variables.
- Because the keys in a hash map have to be unique, but production rules can have the same left part multiple times I also add a number in the key to differentiate them. For example, here is my variant.

```
HashMap<String, String> productionRules2 = new HashMap<String, String>();

        productionRules2.put("S1", "aB");
        productionRules2.put("A1", "B");
        productionRules2.put("S2", "bA");
        productionRules2.put("A2", "b");
        productionRules2.put("A3", "aD");
        productionRules2.put("A4", "AS");
        productionRules2.put("A5", "bAAB");
        productionRules2.put("A6", "Ɛ");
        productionRules2.put("B1", "b");
        productionRules2.put("B2", "bS");
        productionRules2.put("C1", "AB");
        productionRules2.put("D1", "BB");

        char[] nonTerminalVariables2 = { 'S', 'A', 'B', 'C', 'D' };
        char[] terminalVariables2 = { 'a', 'b' };
        Grammar variant2Grammar = new Grammar(nonTerminalVariables2, terminalVariables2, productionRules2);
```

- In the Grammar class I added a method called "toChomskyNormalForm". This serves as the entry point for the process of converting a grammar to Chomsky Normal Form. It takes a Grammar object as a parameter and calls other methods on this object's production rules to bring them to the desired form. These methods are described in detail in the next few sections. The production rules are not modified directly but instead the method creates a copy of them and performs the operations on this copy. This is because I wanted the original Grammar object to remain the same and for the "toChomskyNormalForm" method to return a new Grammar object, one with updated fields.
- To avoid duplicate code I decided to create a method called "applyChomskyNormalFormStep". This method takes in the production rules as a parameter, as well as what method we want to pass the production rules to as a second parameter and the method basically calls the second method for us.

```
private void applyChomskyNormalFormStep(originalGrammar step, HashMap<String, String> newProductionRules) {
        HashMap<String, String> newProductionRulesCopy = new HashMap<String, String>();

        newProductionRulesCopy.putAll(step.apply());
        newProductionRules.clear();
        newProductionRules.putAll(newProductionRulesCopy);
    }

```

- Calling the various methods, which all take in production rules and return a new set of production rules, and are all in the "Grammar" class.

```
 originalGrammar f = () -> eliminateEpilsonProductions(newProductionRules, nonTerminalOccurences);
        applyChomskyNormalFormStep(f, newProductionRules);
        f = () -> eliminateUnitProductions(newProductionRules, nonTerminalOccurences);
        applyChomskyNormalFormStep(f, newProductionRules);
        f = () -> eliminateNonProductiveSymbols(newProductionRules, nonTerminalOccurences);
        applyChomskyNormalFormStep(f, newProductionRules);
        f = () -> eliminateInaccessibleSymbols(newProductionRules, nonTerminalOccurences);
        applyChomskyNormalFormStep(f, newProductionRules);
        f = () -> obtainChomskyNormalForm(newProductionRules);
        applyChomskyNormalFormStep(f, newProductionRules);
```

- This entry method also creates a hash map called "nonTerminalOccurences" which tracks how many occurences of a specific nonterminal variable there are on the left side of the production rules. Also, if that production rule contains "Ɛ" then I add "1" to the key. This is useful for the next steps where we eliminate empty string productions and add new rules.
- The methods are all designed to work for any grammar, not just my variant, with a few minor restrictions. Firstly, nonterminal variables have to be between 'A' and 'Z'. Secondly, terminal variables cannot be numbers or a dot.

### 2. Eliminating Ɛ productions.

- I added a method called "eliminateEpilsonProductions". It iterates through the production rule hash map and through every character of the map's values. If it finds a character that is nonterminal and has a rule with "Ɛ" somewhere in the map, it goes till the end of the value string and counts how many times that character appears, creating a string out of this. For example, if there is a production rule "A->Ɛ" and "B->AACA", then the string would be "123", if "A" appears twice, it would just be "12". After this I find all of the permutations that are possible with the characters of that string that are smaller then the length of the string and put them in a hash set. For "123" the possible permutations would be "12","23","13","1","2","3", for "12" the possible permutations would be just "1" and "2".
- Using the elements of this hash set we can then add new production rules as if by eliminating "Ɛ". Using the previous example "B->AACA", we would go through the hash set and see "12", therefore only keep the first and second "A" character and eliminate the third one. Then we can add "B->AAC" to the rules. Next we would see "23", so we would remove the first "A", keep the second and third and add "B->ACA" to the rules, and so on.
- After finishing we leave the rule and move on to the next one. This will work in a lot of situations but not all. For instance, what if in our previous example "B->AACA", we also had "C->Ɛ"? After dealing with "A->Ɛ" we leave the rule and ignore "C". To fix this we iterate over the production rules multiple times.
- Naturally, the rules that simply have "Ɛ" on the right side are all removed.
- Results after eliminating Ɛ productions on my variant.

```
Production rules after removal of ε productions:
S3->b
D1->BB
C1->B
B1->bS
C2->AB
B2->b
A1->bB
A2->bAB
A3->B
A4->b
A5->bAAB
A6->S
A7->aD
A8->AS
S1->aB
S2->bA
```

### 3. Eliminating unit productions.

- I created a method called "eliminateUnitProductions". It goes through the production rules and if it finds a rule where the right side of the rule consists of just 1 nonterminal character, it iterates though the production rules again and replaces the terminal character on the right side with all of the rules that that terminal character has.
- Because there can be multiple layers of unit productions, for example "S->A","A->B","B->C","C->D","D->a", this process is repeated multiple times.
- Naturally, in the end if there are any rules left of the form "A->B", they are removed.
- Results after eliminating unit productions on my variant.

```
Production rules after removal of unit productions:
S3->b
D1->BB
C1->AB
B1->b
C2->bS
B2->bS
C3->b
A1->bA
A2->aD
A3->AS
A4->bB
A5->bAB
A6->bAAB
A7->aB
A8->bS
A9->b
S1->aB
S2->bA
```

### 4. Eliminating non-productive symbols.

- I created a method called "eliminateNonProductiveSymbols". It works by going through the production rules and finding out which nonterminal variable has at least 1 rule where the right side consists entirely of terminal variables. These results are stored in a boolean array, where the first position is for the first nonterminal variable, the second for the second nonterminal variable and so on.
- It then goes through the rules again and looks at every character on the right side string, the value part of the hash map. Using the array created previously, it checks if at least one of the characters in the string is terminal or if at least one nonterminal character in the string has access to a terminal character, according to the array. If it does, the nonterminal symbol on the left part of the rule is added into a set.
- In the end, the nonterminal symbols that are not in the set are non-productive symbols, so we go through the rules one more time and remove the rules that contain a nonproductive symbol either on the left or right side.
- Results after eliminating non-productive symbols on my variant.

```
Production rules after removal of non-productive symbols:
S3->b
D1->BB
C1->bS
B1->bS
C2->AB
B2->b
C3->b
A1->b
A2->aB
A3->bS
A4->bA
A5->aD
A6->bAB
A7->bAAB
A8->AS
A9->bB
S1->aB
S2->bA
```

### 4. Eliminating inaccessible symbols.

- I created a method called "eliminateInaccessibleSymbols". This method has a set of accessible symbols. Initially only "S" is part of it. We go through the production rules and check only those where the nonterminal on the left side is part of the set. We look at the characters on the right side one by one and if we encounter another nonterminal variable that is not in the set we add it.
- This process is repeated multiple times.
- After finishing, the nonterminal variables that are not in the set are the inaccessible one's. So all we have to do is go through the production rules and delete those where the left side consists of an inaccessible symbol.
- Results after eliminating inaccessible symbols on my variant.

```
Production rules after removal of inaccessible symbols:
S3->b
D1->BB
B1->b
B2->bS
A1->bB
A2->bAAB
A3->AS
A4->b
A5->aB
A6->aD
A7->bAB
A8->bS
A9->bA
S1->aB
S2->bA
```

### 5. Obtaining the Chomsky Normal Form.

- I created a method called "obtainChomskyNormalForm". It works by iterating over the production rules and bringing every rule to the Chomsky Normal Form one by one. It creates a new hash map in which we'll store the new rules.
- After reaching a rule it only looks at the first 2 or 3 elements on the right side and keeps doing the described operations until the right side consists of either 2 nonterminal variables or 1 terminal variable. If there are no 2 elements and it is just 1, that means that it is just 1 terminal variable and we leave it alone and move on to the next rule. If the first element is a terminal variable and the second a nonterminal, or vice versa, we check our new hash map to see if it already contains a value equal to the terminal. If it does, we just swap the terminal with the key from the new hash map that has that specific value. If it doesn't, we create a new entry in the hash map where the key is "Y." + a specific number and the value is the terminal variable. The terminal is then swapped with the key.
- Entries like "Y.1","Y.2","Y.3", etc, are new nonterminal variables that have 1 rule each with just 1 terminal on the right side.
- If the first 2 elements are nonterminals and the third is also a nonterminal then we check the hash map for a value that is equal to the first nonterminal + second nonterminal. If it exists we swap these 2 with the key from the hash map that is equal to them combined. If not, we create a new entry in the hash map where the key this time is "X." + a specific number and the value is nonterminal 1 + nonterminal 2. These are then swapped with the key.
- Entries like "X.1", "X.2","X.3", etc, are new nonterminal variables that have 1 rule each with 2 other nonterminal variables on the right side.
- If the first 2 elements are nonterminals and the third is not a nonterminal but a terminal then we check the hash map to see if it contains the value of the 3rd element. If it does, we swap it with the key that contains that value. If not, we add it to the map with the key "Y." + a specific number and the value equal to the terminal and then swap the 3rd element with the key.
- Again, this process keeps going until the right side of the rule is normalized. So we normalize every rule 1 by 1.
- In the end the hash map that we created that contains keys like "Y.1" and "X.1" is added to the production rules.
- Results after obtaining the Chomsky Normal Form on my variant.

```
Chomsky Normal Form Production Rules:
S3->b
D1->BB
B1->b
B2->Y.1S
A1->Y.1B
A2->X.2B
A3->AS
A4->b
A5->Y.2B
A6->Y.2D
A7->X.1B
A8->Y.1S
A9->Y.1A
X.1->Y.1A
Y.2->a
Y.1->b
X.2->X.1A
S1->Y.2B
S2->Y.1A
VN: {A,B,S,D,X.1,Y.2,Y.1,X.2}
VT: {a,b}
```

- As I've mentioned previously, these algorithms are designed to work for every grammar, so here are some examples of it solving other random variants.

- Variant 12, which looks like this.

```
S->A
A->aX
A->bX
X->Ɛ
X->BX
X->b
B->AD
D->aD
D->a
C->Ca
```

```
Chomsky Normal Form Production Rules:
D1->a
D2->Y.1D
B1->AD
A1->Y.1X
A2->Y.2X
A3->a
A4->b
X1->AD
X2->BX
X3->b
Y.2->b
Y.1->a
VN: {A,B,D,X,Y.2,Y.1}
VT: {a,b}
```

- Variant 17, which looks like this.

```
S->aA
S->AC
A->a
A->ASC
A->BC
A->aD
B->b
B->bA
C->Ɛ
C->BA
E->aB
D->abC
```

```
Chomsky Normal Form Production Rules:
S3->BC
S4->Y.1D
S5->Y.1A
S6->AC
S7->a
S8->X.1C
S9->b
C1->BA
A1->BC
A2->Y.2A
A3->b
A4->AS
A5->X.1C
A6->a
A7->Y.1D
X.1->AS
Y.2->b
Y.1->a
X.2->Y.1Y.2
D1->X.2C
D2->Y.1Y.2
B1->b
B2->Y.2A
S1->AS
S2->Y.2A
VN: {A,B,S,C,D,X.1,Y.2,Y.1,X.2}
VT: {a,b}
```

- Variant 25, which looks like this.

```
S->bA
S->BC
A->a
A->aS
A->bCaCa
B->A
B->bS
B->bCAa
C->Ɛ
C->AB
D->AB
```

```
Chomsky Normal Form Production Rules:
S3->X.1Y.2
S4->a
S5->Y.2S
A1->a
A2->Y.2S
A3->X.1Y.2
X.1->Y.1Y.2
Y.2->a
Y.1->b
X.2->Y.1A
S1->X.2Y.2
S2->Y.1A
VN: {A,S,X.1,Y.2,Y.1,X.2}
VT: {a,b}
```

- A slightly more difficult grammar that I came up with.

```
VN = {S,A,C,D,B}
VT = {a,b,#,$}
Production rules:
S->BCCACA
S->aaD#$
A->Ɛ
A->$###BCAD
A->DDDD
C->Ɛ
C->$$$A$$$
C->ABCDaba
D->a
D->$
D->CCaabCC
B->bb
```

```
Chomsky Normal Form Production Rules:
A1->X.20D
X.35->X.34Y.3
A2->X.24D
X.36->X.35C
A3->X.25D
X.37->X.31C
X.1->Y.1Y.1
X.30->Y.2Y.2
X.3->X.2A
X.31->X.30Y.3
X.2->X.1Y.1
X.32->CC
X.5->X.4Y.1
X.33->X.32Y.2
X.4->X.3Y.1
X.34->X.33Y.2
X.7->X.6D
X.6->BC
X.9->X.8Y.3
X.8->X.7Y.2
B1->Y.3Y.3
X.24->X.23D
X.25->X.18A
X.26->CY.2
X.27->X.26Y.2
X.28->X.27Y.3
X.29->X.28C
X.20->X.19A
X.21->X.2Y.1
X.22->X.21Y.1
X.23->DD
C1->X.5Y.1
C2->X.9Y.2
C3->X.14Y.2
X.13->X.12Y.2
C4->X.22Y.1
X.14->X.13Y.3
X.15->Y.1Y.4
X.16->X.15Y.4
X.17->X.16Y.4
X.18->X.17B
X.19->X.18C
Y.2->a
Y.1->$
X.10->AB
X.11->X.10C
Y.4->#
Y.3->b
X.12->X.11D
D10->X.29C
D11->X.31C
D1->a
D2->$
D3->X.36C
D4->X.34Y.3
D5->X.37C
D6->X.28C
D7->X.30Y.3
D8->X.27Y.3
D9->X.35C
VN: {A,B,C,D,X.35,X.13,X.36,X.14,X.37,X.15,X.16,X.17,X.18,X.19,X.1,X.30,X.3,X.31,Y.2,X.2,X.32,Y.1,X.10,X.5,X.33,X.11,Y.4,X.4,X.34,Y.3,X.12,X.7,X.6,X.9,X.8,X.24,X.25,X.26,X.27,X.28,X.29,X.20,X.21,X.22,X.23}
VT: {a,b,#,$}
```

## Conclusion

- As a result of doing this laboratory work I now fully understand what Chosmky Normal Form actually is and how we can properly bring a grammar to this form by eliminating epilson productions, eliminating unit productions, eliminating nonproductive/inaccessible symbols and performing various operations to ensure that every rule is either like "A->AB", where "A","B" are nonterminal variables, or like "A->a", where "A" is a nonterminal variable and "a" is a terminal variable. I've also figured out how to implement all of these steps in code and how we can design the algorithms in such a way so that they work for any given grammar.
