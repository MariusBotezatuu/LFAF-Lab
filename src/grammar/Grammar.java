package grammar;

import java.util.HashMap;
import automaton.FiniteAutomaton;
import automaton.Transition;

public class Grammar {
    private char[] nonTerminalVariables;
    private char[] terminalVariables;
    private String word = "";
    HashMap<String, String> productionRules = new HashMap<String, String>();

    public Grammar(char[] nonTerminalVariables, char[] terminalVariables, HashMap<String, String> productionRules) {
        this.nonTerminalVariables = nonTerminalVariables.clone();
        this.terminalVariables = terminalVariables.clone();
        this.productionRules.putAll(productionRules);
    }

    public String generateString() {
        String vn, p1, p2;
        int min = 0, max = 0, randomInt;

        if (word.length() == 0) {
            vn = "S";
        } else {
            vn = String.valueOf(word.charAt(word.length() - 1));
        }

        switch (vn) {
            case "S":
                min = 1;
                max = 4;
                break;
            case "R":
                min = 5;
                max = 6;
                break;
            case "L":
                min = 7;
                max = 9;
                break;
        }

        randomInt = (int) Math.floor(Math.random() * (max - min + 1) + min);
        p1 = vn + randomInt;
        p2 = productionRules.get(p1);

        if (word.length() == 0) {
            word = p2;
        } else {
            word = word.substring(0, word.length() - 1);
            word += p2;
        }

        if (p2.length() == 1)
            return word;
        else
            word = generateString();

        return word;
    }

    public void releaseWord() {
        word = "";
    }

    public FiniteAutomaton toFiniteAutomaton() {
        char[] possibleStates = new char[nonTerminalVariables.length + 1];
        Transition[] transitions = new Transition[9];

        System.arraycopy(nonTerminalVariables, 0, possibleStates, 0, nonTerminalVariables.length);
        possibleStates[nonTerminalVariables.length] = 'F';

        transitions[0] = new Transition(possibleStates[0], possibleStates[0], terminalVariables[0]);
        transitions[1] = new Transition(possibleStates[0], possibleStates[0], terminalVariables[1]);
        transitions[2] = new Transition(possibleStates[0], possibleStates[1], terminalVariables[2]);
        transitions[3] = new Transition(possibleStates[0], possibleStates[2], terminalVariables[3]);
        transitions[4] = new Transition(possibleStates[1], possibleStates[2], terminalVariables[3]);
        transitions[5] = new Transition(possibleStates[1], possibleStates[3], terminalVariables[4]);
        transitions[6] = new Transition(possibleStates[2], possibleStates[2], terminalVariables[5]);
        transitions[7] = new Transition(possibleStates[2], possibleStates[2], terminalVariables[4]);
        transitions[8] = new Transition(possibleStates[2], possibleStates[3], terminalVariables[3]);

        FiniteAutomaton finiteAutomaton1 = new FiniteAutomaton(possibleStates, terminalVariables, possibleStates[0],
                possibleStates[3], transitions);

        return finiteAutomaton1;
    }

}
