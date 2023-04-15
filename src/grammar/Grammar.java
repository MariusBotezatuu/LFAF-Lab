package grammar;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

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

    // to do : something like AABA won't work
    public void toChomskyNormalForm() {
        HashMap<String, String> newProductionRules = new HashMap<String, String>();
        HashMap<String, Integer> nonTerminalOccurences = new HashMap<String, Integer>();
        newProductionRules.putAll(productionRules);
        productionRules.forEach((key, value) -> {
            String letter = key.replaceAll("[^a-zA-Z]", "");
            int number = Integer.parseInt(key.replaceAll("[^0-9]", ""));
            if (value == "Ɛ") {
                if (nonTerminalOccurences.containsKey(letter))
                    nonTerminalOccurences.remove(letter);
                nonTerminalOccurences.put(letter + "1", number - 1);
                newProductionRules.remove(key);
            } else {
                nonTerminalOccurences.put(letter, number);
            }
        });

        newProductionRules.putAll(eliminateEpilsonProductions(newProductionRules, nonTerminalOccurences));

    }

    private HashMap<String, String> eliminateEpilsonProductions(HashMap<String, String> newProductionRules,
            HashMap<String, Integer> nonTerminalOccurences) {
        HashMap<String, String> productionRulesCopy = new HashMap<String, String>();
        productionRulesCopy.putAll(productionRules);

        productionRulesCopy.forEach((key, value) -> {
            String letter = key.replaceAll("[^a-zA-Z]", "");
            int number = Integer.parseInt(key.replaceAll("[^0-9]", ""));
            for (int k = 0; k < value.length(); k++) {
                char c = value.charAt(k);
                if (nonTerminalOccurences.containsKey(c + "1")) {
                    StringBuilder count = new StringBuilder();
                    int count2 = 0;
                    for (int j = 0; j < value.length(); j++) {
                        if (value.charAt(j) == c) {
                            count2++;
                            count.append(Integer.toString(count2));
                        }
                    }

                    HashSet<String> set = new HashSet<>();
                    getPartialPermutations(count.toString(), set);
                    if (count.toString().length() > 1)
                        set.remove(count.toString());
                    String[] array = set.toArray(new String[set.size()]);

                    for (int i = 0; i < array.length - 2; i++) {
                        for (int j = i + 1; j < array.length; j++) {
                            if (!array[i].contains(array[j]) && !array[j].contains(array[i])) {
                                String newPermutation = array[i] + array[j];
                                char[] charArray = newPermutation.toCharArray();
                                Arrays.sort(charArray);
                                String sortedNewPermutation = new String(charArray);
                                set.add(sortedNewPermutation);
                            }
                        }
                    }

                    if (count.toString().length() > 1)
                        set.remove(count.toString());
                    set.add("0");

                    for (String element : set) {
                        count.setLength(0);
                        count2 = 0;
                        for (int j = 0; j < value.length(); j++) {
                            if (value.charAt(j) != c)
                                count.append(value.charAt(j));
                            else {
                                count2++;
                                if (element.contains(Integer.toString(count2))) {
                                    count.append(value.charAt(j));
                                }
                            }
                        }
                        // add count to new production rules
                        String letter2 = letter + "1";
                        final Boolean[] prodExists = { false };
                        // check if production rule already exists

                        newProductionRules.forEach((key3, value3) -> {
                            String letter3 = key3.replaceAll("[^a-zA-Z]", "");
                            int number3 = Integer.parseInt(key3.replaceAll("[^0-9]", ""));
                            if (letter3.equals(letter) && value3.equals(count.toString()))
                                prodExists[0] = true;
                        });
                        if (prodExists[0] != true) {
                            newProductionRules.put(
                                    letter + Integer.toString(
                                            nonTerminalOccurences.get(letter.charAt(0) == c ? letter2 : letter) + 1),
                                    count.toString());
                            nonTerminalOccurences.put(letter.charAt(0) == c ? letter2 : letter,
                                    nonTerminalOccurences.get(letter.charAt(0) == c ? letter2 : letter) + 1);
                        }
                    }

                    break;
                }
            }
        });

        System.out.println("Production rules after removal of ε productions:");
        newProductionRules.forEach((key, value) -> {
            System.out.println(key + "->" + value);
        });

        return newProductionRules;

    }

    private void getPartialPermutations(String sequence, HashSet<String> set) {
        set.add(sequence);
        if (sequence.length() > 1) {
            getPartialPermutations(sequence.substring(0, sequence.length() / 2), set);
            getPartialPermutations(sequence.substring(sequence.length() / 2), set);
        }

    }

}
