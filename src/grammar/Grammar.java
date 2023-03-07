package grammar;

import java.util.HashMap;
import automaton.FiniteAutomaton;
import automaton.Transition;

public class Grammar {
    private char[] nonTerminalVariables;
    private char[] terminalVariables;
    private String word = "";
    private boolean type1 = true, type2 = true, type3 = true, left = false, right = false;
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

    public int countChar(String str, char c) {
        int count = 0;

        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == c)
                count++;
        }
        return count;
    }

    public String classifyGrammar() {
        productionRules.forEach((key, value) -> {
            int sum = 0;
            String[] part = key.split("(?<=\\D)(?=\\d)");
            String ntv = String.valueOf(nonTerminalVariables);
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

        if (type3 == true)
            return "type 3";
        if (type2 == true)
            return "type 2";
        if (type1 == true)
            return "type 1";

        return "type 0";

    }

}
