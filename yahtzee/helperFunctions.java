package com.yahtzee;

import java.util.*;
import java.util.stream.Collectors;

public class helperFunctions {

    // Checks if all elements in the list are the same
    public static boolean allSame(List<Integer> v) {
        for (int i = 1; i < v.size(); i++) {
            if (!v.get(i).equals(v.get(0))) {
                return false;
            }
        }
        return true;
    }

    // Checks if the elements in the list form a sequence
    public static boolean allSequence(List<Integer> v) {
        List<Integer> sortedV = new ArrayList<>(v);
        Collections.sort(sortedV);
        for (int i = 1; i < sortedV.size(); i++) {
            if (sortedV.get(i) != sortedV.get(i - 1) + 1) {
                return false;
            }
        }
        return true;
    }

    // Checks if the list contains a sequence of a given length
    public static boolean containsSequence(List<Integer> v, int sequenceLength) {
        if (v.size() < sequenceLength) {
            return false;
        }
        for (int i = 0; i <= v.size() - sequenceLength; i++) {
            List<Integer> subV = v.subList(i, i + sequenceLength);
            if (allSequence(subV)) {
                return true;
            }
        }
        return false;
    }

    // Checks if the list contains a sequence of exactly 5 consecutive numbers
    public static boolean fiveSequence(List<Integer> v) {
        return containsSequence(v, 5);
    }

    // Checks if the list contains a sequence of exactly 4 consecutive numbers
    public static boolean fourSequence(List<Integer> v) {
        return containsSequence(v, 4);
    }

    // Checks if the list contains at least 'n' occurrences of any number
    public static boolean atleastNSame(List<Integer> v, int n) {
        Map<Integer, Integer> counts = new HashMap<>();
        for (int i : v) {
            counts.put(i, counts.getOrDefault(i, 0) + 1);
        }
        for (int count : counts.values()) {
            if (count >= n) {
                return true;
            }
        }
        return false;
    }

    // Checks if the list contains at least 4 of the same number
    public static boolean atleastFourSame(List<Integer> v) {
        return atleastNSame(v, 4);
    }

    // Checks if the list contains at least 3 of the same number
    public static boolean atleastThreeSame(List<Integer> v) {
        return atleastNSame(v, 3);
    }

    // Checks if the list contains the number 'n'
    public static boolean containsN(List<Integer> v, int n) {
        return v.contains(n);
    }

    // Returns the first sequence of a given length found in the list
    public static List<Integer> getSequence(List<Integer> v, int sequenceLength) {
        List<Integer> sortedV = new ArrayList<>(v);
        Collections.sort(sortedV);
        for (int i = 0; i <= sortedV.size() - sequenceLength; i++) {
            List<Integer> subV = sortedV.subList(i, i + sequenceLength);
            if (allSequence(subV)) {
                return subV;
            }
        }
        return new ArrayList<>();
    }

    // Returns the sum of all elements in the list
    public static int sum(List<Integer> v) {
        return v.stream().mapToInt(Integer::intValue).sum();
    }

    // Returns how many times 'n' appears in the list
    public static int countN(List<Integer> v, int n) {
        return (int) v.stream().filter(i -> i == n).count();
    }

    // Returns a list containing only the unique elements from the input list
    public static List<Integer> getUnique(List<Integer> v) {
        return v.stream().distinct().collect(Collectors.toList());
    }

    // Returns the number of unique elements in the list
    public static int countUnique(List<Integer> v) {
        return getUnique(v).size();
    }

    // Returns the highest count of any number in the list
    public static int maxCount(List<Integer> v) {
        Map<Integer, Integer> counts = new HashMap<>();
        for (int i : v) {
            counts.put(i, counts.getOrDefault(i, 0) + 1);
        }
        return counts.values().stream().max(Integer::compare).orElse(0);
    }

    // Returns the total number of repeated elements in the list
    public static int numRepeats(List<Integer> v) {
        Map<Integer, Integer> counts = new HashMap<>();
        for (int i : v) {
            counts.put(i, counts.getOrDefault(i, 0) + 1);
        }
        return counts.values().stream().mapToInt(count -> count > 1 ? count - 1 : 0).sum();
    }

    // Returns the length of the longest sequence of consecutive numbers in the list
    public static int longestSequenceLength(List<Integer> v) {
        if (v.isEmpty()) {
            return 0;
        }

        int longest = 0;
        int current = 1;
        List<Integer> sortedV = getUnique(v);
        Collections.sort(sortedV);

        for (int i = 1; i < sortedV.size(); i++) {
            if (sortedV.get(i) == sortedV.get(i - 1) + 1) {
                current++;
            } else {
                longest = Math.max(longest, current);
                current = 1;
            }
        }

        return Math.max(longest, current);
    }

    // Checks if the list represents a full house (three of one number and two of
    // another)
    public static boolean fullHouse(List<Integer> v) {
        Map<Integer, Integer> counts = new HashMap<>();
        for (int i : v) {
            counts.put(i, counts.getOrDefault(i, 0) + 1);
        }
        boolean hasTwo = false;
        boolean hasThree = false;
        for (int count : counts.values()) {
            if (count == 2) {
                hasTwo = true;
            } else if (count == 3) {
                hasThree = true;
            }
        }
        return hasTwo && hasThree;
    }

    // Splits a string into tokens based on a delimiter
    public static List<String> split(String s, char delimiter) {
        return Arrays.asList(s.split(String.valueOf(delimiter)));
    }

    // Joins a list of strings into a single string with a given delimiter
    public static String join(List<String> v, char delimiter) {
        return String.join(String.valueOf(delimiter), v);
    }

    // Trims whitespace from both ends of a string
    public static String trim(String s) {
        return s.trim();
    }

    // Generates all possible combinations of 'numDice' dice values
    public static List<List<Integer>> getDicePermutation(int numDice) {
        List<List<Integer>> combinations = new ArrayList<>();
        List<Integer> combination = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            combination.add(i);
            combinations.add(new ArrayList<>(combination));
            combination.remove(combination.size() - 1);
        }

        for (int i = 1; i < numDice; i++) {
            List<List<Integer>> newCombinations = new ArrayList<>();
            for (List<Integer> c : combinations) {
                for (int j = 1; j <= 6; j++) {
                    List<Integer> newCombination = new ArrayList<>(c);
                    newCombination.add(j);
                    newCombinations.add(newCombination);
                }
            }
            combinations = newCombinations;
        }

        return combinations;
    }

    // Recursively generates all combinations of 'n' dice
    public static void generateCombinations(int n, int start, List<Integer> current, List<List<Integer>> result) {
        if (n == 0) {
            result.add(new ArrayList<>(current));
            return;
        }

        for (int i = start; i <= 6; i++) {
            current.add(i);
            generateCombinations(n - 1, i, current, result);
            current.remove(current.size() - 1);
        }
    }

    // Wrapper for generating combinations of dice rolls
    public static List<List<Integer>> diceCombinations(int n) {
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> current = new ArrayList<>();
        generateCombinations(n, 1, current, result);
        return result;
    }

    // Generates a random boolean value
    public static boolean randomBool() {
        Random random = new Random();
        return random.nextBoolean();
    }

    // Function to return the reversed version of a given list.
    public static <T> List<T> reversed(List<T> v) {
        List<T> reversedV = new ArrayList<>(v);
        Collections.reverse(reversedV);
        return reversedV;
    }

    // Function to return the intersection of two lists (common elements).
    public static <T> List<T> intersection(List<T> v1, List<T> v2) {
        Map<T, Integer> countMap = new HashMap<>();
        List<T> result = new ArrayList<>();

        for (T element : v1) {
            countMap.put(element, countMap.getOrDefault(element, 0) + 1);
        }

        for (T element : v2) {
            if (countMap.getOrDefault(element, 0) > 0) {
                result.add(element);
                countMap.put(element, countMap.get(element) - 1);
            }
        }

        return result;
    }

    // // Function to compute the difference between two lists (elements in v1 but
    // not in v2).
    // public static <T> List<T> difference(List<T> v1, List<T> v2) {
    // List<T> sortedV1 = new ArrayList<>(v1);
    // Collections.sort(sortedV1);
    // List<T> sortedV2 = new ArrayList<>(v2);
    // Collections.sort(sortedV2);

    // List<T> result = new ArrayList<>();
    // int i = 0, j = 0;

    // while (i < sortedV1.size() && j < sortedV2.size()) {
    // if (sortedV1.get(i).compareTo(sortedV2.get(j)) < 0) {
    // result.add(sortedV1.get(i));
    // i++;
    // } else if (sortedV1.get(i).equals(sortedV2.get(j))) {
    // i++;
    // j++;
    // } else {
    // j++;
    // }
    // }

    // while (i < sortedV1.size()) {
    // result.add(sortedV1.get(i));
    // i++;
    // }

    // return result;
    // }

    // Function to compute the difference between two lists (elements in v1 but not
    // in v2).
    public static <T extends Comparable<T>> List<T> difference(List<T> v1, List<T> v2) {
        List<T> sortedV1 = new ArrayList<>(v1);
        Collections.sort(sortedV1);
        List<T> sortedV2 = new ArrayList<>(v2);
        Collections.sort(sortedV2);

        List<T> result = new ArrayList<>();
        int i = 0, j = 0;

        while (i < sortedV1.size() && j < sortedV2.size()) {
            if (sortedV1.get(i).compareTo(sortedV2.get(j)) < 0) {
                result.add(sortedV1.get(i));
                i++;
            } else if (sortedV1.get(i).equals(sortedV2.get(j))) {
                i++;
                j++;
            } else {
                j++;
            }
        }

        while (i < sortedV1.size()) {
            result.add(sortedV1.get(i));
            i++;
        }

        return result;
    }

    // Function to check if a list contains a specific element.
    public static <T> boolean contains(List<T> v, T element) {
        return v.contains(element);
    }

    // Function to concatenate two lists.
    public static <T> List<T> concatenate(List<T> v1, List<T> v2) {
        List<T> result = new ArrayList<>(v1);
        result.addAll(v2);
        return result;
    }

    // Function to check if two lists contain the same elements, regardless of
    // order.
    public static <T> boolean unorderedEqual(List<T> v1, List<T> v2) {
        if (v1.size() != v2.size()) {
            return false;
        }

        Map<T, Integer> counts = new HashMap<>();
        for (T element : v1) {
            counts.put(element, counts.getOrDefault(element, 0) + 1);
        }
        for (T element : v2) {
            counts.put(element, counts.getOrDefault(element, 0) - 1);
        }

        for (int count : counts.values()) {
            if (count != 0) {
                return false;
            }
        }

        return true;
    }

    // Function to check if the second list (v2) is a subset of the first list (v1).
    public static <T> boolean subset(List<T> v1, List<T> v2) {
        Map<T, Integer> counts = new HashMap<>();
        for (T element : v1) {
            counts.put(element, counts.getOrDefault(element, 0) + 1);
        }
        for (T element : v2) {
            counts.put(element, counts.getOrDefault(element, 0) - 1);
        }

        for (int count : counts.values()) {
            if (count < 0) {
                return false;
            }
        }

        return true;
    }
}