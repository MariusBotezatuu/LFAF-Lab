package grammar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import automaton.FiniteAutomaton;
import automaton.Transition;

@FunctionalInterface
interface originalGrammar {
    HashMap<String, String> apply();
}

public class Grammar {
    private char[] nonTerminalVariables;
    private String[] longerNonTerminalVariables;
    private char[] terminalVariables;
    private String word = "";
    private boolean type1 = true, type2 = true, type3 = true, left = false, right = false;
    HashMap<String, String> productionRules = new HashMap<String, String>();

    public Grammar(char[] nonTerminalVariables, char[] terminalVariables, HashMap<String, String> productionRules) {
        this.nonTerminalVariables = nonTerminalVariables.clone();
        this.terminalVariables = terminalVariables.clone();
        this.productionRules.putAll(productionRules);
    }

    public Grammar(String[] longerNonTerminalVariables, char[] terminalVariables,
            HashMap<String, String> productionRules) {
        this.longerNonTerminalVariables = longerNonTerminalVariables.clone();
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

    private void reorderProductionRules(HashMap<String, String> productionRulesToOrder) {
        int[] nonTerminalCount = new int[nonTerminalVariables.length];
        HashMap<String, String> productionRulesCopy = new HashMap<String, String>();
        HashMap<Character, Integer> nonTerminalPositions = new HashMap<Character, Integer>();
        for (int i = 0; i < nonTerminalVariables.length; i++) {
            nonTerminalPositions.put(nonTerminalVariables[i], i);
        }

        productionRulesCopy.putAll(productionRulesToOrder);
        productionRulesToOrder.forEach((key, value) -> {
            String letter = key.replaceAll("[^a-zA-Z]", "");
            int number = Integer.parseInt(key.replaceAll("[^0-9]", ""));
            number += 200000000;
            productionRulesCopy.remove(key);
            productionRulesCopy.put(letter + number, value);
        });

        productionRulesToOrder.clear();
        productionRulesToOrder.putAll(productionRulesCopy);

        productionRulesToOrder.forEach((key, value) -> {
            String letter = key.replaceAll("[^a-zA-Z]", "");
            productionRulesCopy.remove(key);
            nonTerminalCount[nonTerminalPositions.get(letter.charAt(0))]++;
            productionRulesCopy.put(letter + nonTerminalCount[nonTerminalPositions.get(letter.charAt(0))], value);
        });

        productionRulesToOrder.clear();
        productionRulesToOrder.putAll(productionRulesCopy);
    }

    private void updateNonTerminalArray(HashMap<String, String> newProductionRules) {
        HashSet<String> nonTerminalSet = new HashSet<String>();

        newProductionRules.forEach((key, value) -> {
            String letter;
            if (!key.contains("."))
                letter = key.replaceAll("[^a-zA-Z]", "");
            else
                letter = key;
            nonTerminalSet.add(letter);
        });

        longerNonTerminalVariables = nonTerminalSet.toArray(new String[0]);
    }

    private void applyChomskyNormalFormStep(originalGrammar step, HashMap<String, String> newProductionRules) {
        HashMap<String, String> newProductionRulesCopy = new HashMap<String, String>();

        newProductionRulesCopy.putAll(step.apply());
        newProductionRules.clear();
        newProductionRules.putAll(newProductionRulesCopy);
    }

    public Grammar toChomskyNormalForm() {
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
        updateNonTerminalArray(newProductionRules);

        Grammar chomskyNormalFormGrammar = new Grammar(longerNonTerminalVariables, terminalVariables,
                newProductionRules);
        System.out.print("VN: {");
        for (int i = 0; i < longerNonTerminalVariables.length; i++) {
            if (i != longerNonTerminalVariables.length - 1)
                System.out.print(longerNonTerminalVariables[i] + ",");
            else
                System.out.println(longerNonTerminalVariables[i] + "}");
        }
        System.out.print("VT: {");
        for (int i = 0; i < terminalVariables.length; i++) {
            if (i != terminalVariables.length - 1)
                System.out.print(terminalVariables[i] + ",");
            else
                System.out.println(terminalVariables[i] + "}");
        }

        return chomskyNormalFormGrammar;
    }

    private HashMap<String, String> obtainChomskyNormalForm(HashMap<String, String> newProductionRules) {
        int[] xValue = new int[1];
        int[] yValue = new int[1];
        HashMap<String, String> newNonTerminals = new HashMap<String, String>();
        HashSet<Character> terminalSet = new HashSet<Character>();
        HashMap<String, String> newProductionRulesCopy = new HashMap<String, String>();
        for (int i = 0; i < terminalVariables.length; i++)
            terminalSet.add(terminalVariables[i]);
        xValue[0] = 1;
        yValue[0] = 1;
        newProductionRulesCopy.putAll(newProductionRules);

        newProductionRules.forEach((key, value) -> {
            String newValue = value.replaceAll("\\.\\d+", "");
            String key2;
            int terminalPosition = 0;

            while ((newValue.length() != 1) && (newValue.length() > 2 || (terminalSet.contains(newValue.charAt(0))
                    || terminalSet.contains(newValue.charAt(1))))) {

                if (terminalSet.contains(newValue.charAt(0))) {
                    if (newNonTerminals.containsValue(Character.toString(newValue.charAt(0)))) {
                        for (Map.Entry<String, String> entry : newNonTerminals.entrySet()) {
                            if (entry.getValue().equals(Character.toString(newValue.charAt(0)))) {
                                key2 = entry.getKey();
                                value = key2 + value.substring(1);
                                break;
                            }
                        }
                    } else {
                        newNonTerminals.put("Y." + yValue[0], Character.toString(newValue.charAt(0)));
                        value = "Y." + yValue[0] + value.substring(1);
                        yValue[0]++;
                    }
                }

                int dotIndex = value.indexOf(".");
                if (dotIndex != -1) {
                    for (int i = dotIndex + 1; i < value.length(); i++) {
                        char c = value.charAt(i);
                        if (Character.isDigit(c)) {
                            terminalPosition = i;
                            break;
                        }
                    }
                } else
                    terminalPosition = 0;

                terminalPosition++;
                if (terminalSet.contains(value.charAt(terminalPosition))) {
                    if (newNonTerminals.containsValue(Character.toString(value.charAt(terminalPosition)))) {
                        for (Map.Entry<String, String> entry : newNonTerminals.entrySet()) {
                            if (entry.getValue().equals(Character.toString(value.charAt(terminalPosition)))) {
                                key2 = entry.getKey();
                                value = value.substring(0, terminalPosition) + key2
                                        + value.substring(terminalPosition + 1);
                                break;
                            }
                        }
                    } else {
                        newNonTerminals.put("Y." + yValue[0], Character.toString(value.charAt(terminalPosition)));
                        value = value.substring(0, terminalPosition) + "Y." + yValue[0]
                                + value.substring(terminalPosition + 1);
                        yValue[0]++;
                    }
                }

                newValue = value.replaceAll("\\.\\d+", "");

                if (Character.isUpperCase(newValue.charAt(0)) && Character.isUpperCase(newValue.charAt(1))
                        && newValue.length() > 2 && Character.isUpperCase(newValue.charAt(2))) {
                    Boolean reachedFirstString = false;
                    Boolean reachedSecondString = false;
                    int end = 0;
                    String firstString = "";
                    String secondString = "";
                    for (int i = 0; i < value.length(); i++) {
                        end = i;
                        if (value.charAt(i) >= 'A' && value.charAt(i) <= 'Z') {
                            if (!reachedFirstString) {
                                reachedFirstString = true;
                                firstString = Character.toString(value.charAt(i));
                            } else {
                                if (!reachedSecondString) {
                                    reachedSecondString = true;
                                    secondString = Character.toString(value.charAt(i));
                                } else {
                                    end--;
                                    break;
                                }
                            }
                        } else {
                            if (!reachedSecondString) {
                                firstString = firstString + Character.toString(value.charAt(i));
                            } else
                                secondString = secondString + Character.toString(value.charAt(i));
                        }
                    }

                    if (newNonTerminals.containsValue(firstString + secondString)) {
                        for (Map.Entry<String, String> entry : newNonTerminals.entrySet()) {
                            if (entry.getValue().equals(firstString + secondString)) {
                                key2 = entry.getKey();
                                if (end + 1 >= value.length())
                                    value = key2;
                                else
                                    value = key2 + value.substring(end + 1);
                                break;
                            }
                        }
                    } else {
                        newNonTerminals.put("X." + xValue[0], firstString + secondString);
                        if (end + 1 >= value.length())
                            value = "X." + xValue[0];
                        else
                            value = "X." + xValue[0] + value.substring(end + 1);
                        xValue[0]++;
                    }

                }

                newValue = value.replaceAll("\\.\\d+", "");

                if (Character.isUpperCase(newValue.charAt(0)) && Character.isUpperCase(newValue.charAt(1))
                        && newValue.length() > 2 && !Character.isUpperCase(newValue.charAt(2))) {
                    int index = value.indexOf(newValue.charAt(2));
                    if (newNonTerminals.containsValue(Character.toString(newValue.charAt(2)))) {
                        for (Map.Entry<String, String> entry : newNonTerminals.entrySet()) {
                            if (entry.getValue().equals(Character.toString(newValue.charAt(2)))) {
                                key2 = entry.getKey();
                                value = value.substring(0, index) + key2 + value.substring(index + 1);
                                break;
                            }
                        }
                    } else {
                        newNonTerminals.put("Y." + yValue[0], Character.toString(newValue.charAt(2)));
                        value = value.substring(0, index) + "Y." + yValue[0] + value.substring(index + 1);
                        yValue[0]++;
                    }

                }

                newProductionRulesCopy.put(key, value);
                newValue = value.replaceAll("\\.\\d+", "");
            }
        });
        newProductionRulesCopy.putAll(newNonTerminals);
        newProductionRules.clear();
        newProductionRules.putAll(newProductionRulesCopy);

        System.out.println("Chomsky Normal Form Production Rules:");
        newProductionRules.forEach((key, value) -> {
            System.out.println(key + "->" + value);
        });

        return newProductionRules;

    }

    private HashMap<String, String> eliminateInaccessibleSymbols(HashMap<String, String> newProductionRules,
            HashMap<String, Integer> nonTerminalOccurences) {
        HashSet<Character> terminalSet = new HashSet<Character>();
        HashSet<Character> inaccessibleSymbols = new HashSet<Character>();
        HashSet<Character> accesibleSymbols = new HashSet<Character>();
        HashSet<Character> accesibleSymbolsCopy = new HashSet<Character>();
        accesibleSymbols.add('S');
        accesibleSymbolsCopy.addAll(accesibleSymbols);
        for (int i = 0; i < terminalVariables.length; i++)
            terminalSet.add(terminalVariables[i]);
        for (int i = 0; i < nonTerminalVariables.length; i++)
            inaccessibleSymbols.add(nonTerminalVariables[i]);

        for (int i = 0; i < nonTerminalVariables.length; i++) {
            accesibleSymbols.clear();
            accesibleSymbols.addAll(accesibleSymbolsCopy);
            for (Character element : accesibleSymbols) {
                newProductionRules.forEach((key, value) -> {
                    String letter = key.replaceAll("[^a-zA-Z]", "");
                    if (letter.charAt(0) == element) {
                        for (int j = 0; j < value.length(); j++) {
                            if (!terminalSet.contains(value.charAt(j))) {
                                accesibleSymbolsCopy.add(value.charAt(j));
                                inaccessibleSymbols.remove(value.charAt(j));
                            }
                        }
                    }
                });
            }
        }

        HashMap<String, String> newProductionRulesCopy = new HashMap<String, String>();
        newProductionRulesCopy.putAll(newProductionRules);
        newProductionRules.forEach((key, value) -> {
            String letter = key.replaceAll("[^a-zA-Z]", "");
            if (inaccessibleSymbols.contains(letter.charAt(0)))
                newProductionRulesCopy.remove(key);
            else {
                for (int i = 0; i < value.length(); i++) {
                    if (inaccessibleSymbols.contains(value.charAt(i))) {
                        newProductionRulesCopy.remove(key);
                        break;
                    }
                }
            }
        });
        newProductionRules.clear();
        newProductionRules.putAll(newProductionRulesCopy);
        reorderProductionRules(newProductionRules);

        System.out.println("Production rules after removal of inaccessible symbols:");
        newProductionRules.forEach((key, value) -> {
            System.out.println(key + "->" + value);
        });

        return newProductionRules;
    }

    private HashMap<String, String> eliminateNonProductiveSymbols(HashMap<String, String> newProductionRules,
            HashMap<String, Integer> nonTerminalOccurences) {
        boolean[] hasTerminalVariable = new boolean[nonTerminalVariables.length];
        HashSet<String> productiveSymbols = new HashSet<>();
        HashSet<Character> terminalSet = new HashSet<Character>();
        for (int i = 0; i < terminalVariables.length; i++)
            terminalSet.add(terminalVariables[i]);

        newProductionRules.forEach((key, value) -> {
            String letter = key.replaceAll("[^a-zA-Z]", "");
            if (value.length() == 1) {
                for (int i = 0; i < terminalVariables.length; i++) {
                    if (terminalVariables[i] == value.charAt(0)) {
                        for (int j = 0; j < nonTerminalVariables.length; j++) {
                            if (nonTerminalVariables[j] == letter.charAt(0)) {
                                hasTerminalVariable[j] = true;
                            }
                        }
                    }
                }
            }
        });

        newProductionRules.forEach((key, value) -> {
            Boolean isProductive = true;
            String letter = key.replaceAll("[^a-zA-Z]", "");

            for (int i = 0; i < value.length(); i++) {
                if (!terminalSet.contains(value.charAt(i))) {
                    for (int j = 0; j < nonTerminalVariables.length; j++) {
                        if (nonTerminalVariables[j] == value.charAt(i) && hasTerminalVariable[j] != true) {
                            isProductive = false;
                        }
                    }
                }
            }
            if (isProductive)
                productiveSymbols.add(letter);
        });

        HashMap<String, String> newProductionRulesWithoutNonProductive = new HashMap<String, String>();
        newProductionRulesWithoutNonProductive.putAll(newProductionRules);
        newProductionRules.forEach((key, value) -> {
            String letter = key.replaceAll("[^a-zA-Z]", "");
            if (!productiveSymbols.contains(letter))
                newProductionRulesWithoutNonProductive.remove(key);
            for (int i = 0; i < value.length(); i++) {
                if (!terminalSet.contains(value.charAt(i))
                        && !productiveSymbols.contains(Character.toString(value.charAt(i))))
                    newProductionRulesWithoutNonProductive.remove(key);
            }
        });

        newProductionRules.clear();
        newProductionRules.putAll(newProductionRulesWithoutNonProductive);
        reorderProductionRules(newProductionRules);

        System.out.println("Production rules after removal of non-productive symbols:");
        newProductionRules.forEach((key, value) -> {
            System.out.println(key + "->" + value);
        });

        return newProductionRules;
    }

    private HashMap<String, String> eliminateUnitProductions(HashMap<String, String> newProductionRules,
            HashMap<String, Integer> nonTerminalOccurences) {
        HashMap<String, String> newProductionRulesWithoutUnit = new HashMap<String, String>();
        newProductionRulesWithoutUnit.putAll(newProductionRules);

        for (int i = 0; i < nonTerminalVariables.length; i++) {
            newProductionRules.forEach((key, value) -> {
                String letter = key.replaceAll("[^a-zA-Z]", "");
                int number = Integer.parseInt(key.replaceAll("[^0-9]", ""));
                if (value.length() == 1 && Character.isUpperCase(value.charAt(0))) {
                    newProductionRules.forEach((key1, value1) -> {
                        String letter1 = key1.replaceAll("[^a-zA-Z]", "");
                        int number1 = Integer.parseInt(key1.replaceAll("[^0-9]", ""));
                        if (letter1.equals(value)) {
                            final Boolean[] prodExists = { false };
                            newProductionRulesWithoutUnit.forEach((key2, value2) -> {
                                String letter2 = key2.replaceAll("[^a-zA-Z]", "");
                                int number2 = Integer.parseInt(key2.replaceAll("[^0-9]", ""));
                                if (letter2.equals(letter) && value2.equals(value1)) {
                                    prodExists[0] = true;
                                }
                            });
                            if (prodExists[0] == false) {
                                if (newProductionRulesWithoutUnit.get(key).length() == 1
                                        && Character.isUpperCase(newProductionRulesWithoutUnit.get(key).charAt(0))) {
                                    newProductionRulesWithoutUnit.put(key, value1);
                                } else {
                                    int count = nonTerminalOccurences.get(letter);
                                    count++;
                                    newProductionRulesWithoutUnit.put(letter + Integer.toString(count), value1);
                                    nonTerminalOccurences.put(letter, count);
                                }
                            }
                        }
                    });
                }
            });
            newProductionRules.clear();
            newProductionRules.putAll(newProductionRulesWithoutUnit);
        }

        newProductionRulesWithoutUnit.forEach((key, value) -> {
            String letter = key.replaceAll("[^a-zA-Z]", "");
            int number = Integer.parseInt(key.replaceAll("[^0-9]", ""));
            if (value.length() == 1 && Character.isUpperCase(value.charAt(0))) {
                newProductionRules.remove(key);
                nonTerminalOccurences.put(letter, nonTerminalOccurences.get(letter) - 1);
            }
        });

        reorderProductionRules(newProductionRules);

        System.out.println("Production rules after removal of unit productions:");
        newProductionRules.forEach((key, value) -> {
            System.out.println(key + "->" + value);
        });

        return newProductionRules;
    }

    private HashMap<String, String> eliminateEpilsonProductions(HashMap<String, String> newProductionRules,
            HashMap<String, Integer> nonTerminalOccurences) {
        HashMap<String, String> productionRulesCopy = new HashMap<String, String>();
        productionRulesCopy.putAll(productionRules);

        for (int q = 0; q < nonTerminalVariables.length; q++) {
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

                            String letter2 = letter + "1";
                            final Boolean[] prodExists = { false };

                            newProductionRules.forEach((key3, value3) -> {
                                String letter3 = key3.replaceAll("[^a-zA-Z]", "");
                                int number3 = Integer.parseInt(key3.replaceAll("[^0-9]", ""));
                                if (letter3.equals(letter) && value3.equals(count.toString()))
                                    prodExists[0] = true;
                            });
                            if (prodExists[0] != true && !count.toString().isEmpty()) {
                                newProductionRules.put(
                                        letter + Integer.toString(
                                                nonTerminalOccurences.get(letter.charAt(0) == c ? letter2 : letter)
                                                        + 1),
                                        count.toString());
                                nonTerminalOccurences.put(letter.charAt(0) == c ? letter2 : letter,
                                        nonTerminalOccurences.get(letter.charAt(0) == c ? letter2 : letter) + 1);
                            }
                        }
                        break;
                    }
                }
            });
        }

        HashMap<String, Integer> newNonTerminalOccurences = new HashMap<String, Integer>();
        newNonTerminalOccurences.putAll(nonTerminalOccurences);
        nonTerminalOccurences.forEach((key, value) -> {
            String letter = key.replaceAll("[^a-zA-Z]", "");
            if (key.length() > 1) {
                newNonTerminalOccurences.remove(key);
                newNonTerminalOccurences.put(letter, value);
            }
        });

        nonTerminalOccurences.clear();
        nonTerminalOccurences.putAll(newNonTerminalOccurences);
        reorderProductionRules(newProductionRules);

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
