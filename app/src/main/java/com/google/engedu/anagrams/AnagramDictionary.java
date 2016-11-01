package com.google.engedu.anagrams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;

class AnagramDictionary {

    static boolean NEW_GAME_FLAG = false;

    private static final int MIN_NUM_ANAGRAMS = 3;
    private static final int DEFAULT_WORD_LENGTH = 3;
    private static final int MAX_WORD_LENGTH = 7;
    private static int WORD_LENGTH = DEFAULT_WORD_LENGTH;
    private static HashMap<String, ArrayList<String>> lettersToWord = new HashMap<>();
    private static HashMap<Integer, ArrayList<String>> sizeToWords = new HashMap<>();
    private static HashSet<String> wordSet = new HashSet<>();

    AnagramDictionary(InputStream wordListStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(wordListStream));
        String line;
        while((line = in.readLine()) != null) {
            String word = line.trim();
            String sortedWord = sortLetters(word);  //Compute the sorted word value.
            int wordLength = word.length(); //Compute word length.
            addToHMSTWList(wordLength, word);   //Build HM of (wordSize : [List of words of that size])
            addToHMLTWList(sortedWord,word);    //Build HM of (sortedWord : [List of anagrams of sorted word])
            wordSet.add(word);  //Build HS of words.
        }
    }

    private void addToHMLTWList(String sortedWord, String word) {
        ArrayList<String> wordValueList = lettersToWord.get(sortedWord);

        // if list does not exist create it
        if(wordValueList == null) {
            wordValueList = new ArrayList<>();
            wordValueList.add(word);
            lettersToWord.put(sortedWord, wordValueList);
        } else {
            // add if item is not already in list
            if(!wordValueList.contains(word)) wordValueList.add(word);
        }

    }

    private void addToHMSTWList(int wordLength, String word) {
        ArrayList<String> wordValueList = sizeToWords.get(wordLength);

        // if list does not exist create it
        if(wordValueList == null) {
            wordValueList = new ArrayList<>();
            wordValueList.add(word);
            sizeToWords.put(wordLength, wordValueList);
        } else {
            // add if item is not already in list
            if(!wordValueList.contains(word)) wordValueList.add(word);
        }

    }

    private String sortLetters(String word){
        char[] chars = word.toCharArray();
        Arrays.sort(chars);
        return new String(chars);
    }

    boolean isGoodWord(String word, String base) {
        return wordSet.contains(word) && !isSubstring(word,base);
    }

    private boolean isSubstring(String word, String base){
        return word.toLowerCase().contains(base.toLowerCase());
    }

    String pickGoodStarterWord() {
        if (NEW_GAME_FLAG && WORD_LENGTH != MAX_WORD_LENGTH) {
            WORD_LENGTH++;
        }
        ArrayList<String> wordList = sizeToWords.get(WORD_LENGTH);
        int randomIndex = ThreadLocalRandom.current().nextInt(0,wordList.size());

        if (lettersToWord.get(sortLetters(wordList.get(randomIndex))).size() < MIN_NUM_ANAGRAMS) {
            for (String word : wordList) {
                if (lettersToWord.get(sortLetters(word)).size() >= MIN_NUM_ANAGRAMS) {
                    return word;
                }
            }
            WORD_LENGTH++;
            pickGoodStarterWord();
        }
        return wordList.get(randomIndex);
    }

    public ArrayList<String> getAnagrams(String currentWord) {
        ArrayList<String> result = new ArrayList<>();
        String sortedWord = sortLetters(currentWord);
        if (lettersToWord.containsKey(sortedWord)) {
            result.addAll(lettersToWord.get(sortedWord));
        }
        result = removeSubstringsFromAnswers(result, currentWord);
        return result;
    }

    ArrayList<String> getAnagramsWithOneMoreLetter(String currentWord) {
        char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        ArrayList<String> result = new ArrayList<>();
        for (char anAlphabet : alphabet) {
            String sortedWordPlusLetter = sortLetters(currentWord + anAlphabet);
            if (lettersToWord.containsKey(sortedWordPlusLetter)) {
                result.addAll(lettersToWord.get(sortedWordPlusLetter));
            }
        }
        result = removeSubstringsFromAnswers(result, currentWord);
        return result;
    }

    ArrayList<String> getAnagramsWithTwoMoreLetters(String currentWord) {
        char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        ArrayList<String> result = new ArrayList<>();
        for (char anAlphabet : alphabet) {
            for(char anotherAlphabet: alphabet) {
                String sortedWordPlusLetters = sortLetters(currentWord + anAlphabet + anotherAlphabet);
                if (lettersToWord.containsKey(sortedWordPlusLetters)) {
                    result.addAll(lettersToWord.get(sortedWordPlusLetters));
                }
            }
        }
        result = removeSubstringsFromAnswers(result, currentWord);
        return result;
    }

    //TODO: Implement two word anagrams method. 
    ArrayList<String> getTwoWordAnagrams(String currentWords){
        ArrayList<String> twoWordAnagrams = new ArrayList<>();
        return twoWordAnagrams;
    }

    private ArrayList<String> removeSubstringsFromAnswers(ArrayList<String> results, String currentWord){
        ArrayList<String> itemsToRemove = new ArrayList<>();
        boolean MODIFIED_FLAG;
        for (String suspectWord: results) {
            if (isSubstring(suspectWord, currentWord)){
                itemsToRemove.add(suspectWord);
            }
        }
        MODIFIED_FLAG = results.removeAll(itemsToRemove);
        if (MODIFIED_FLAG){
            for (String wordToDelete :itemsToRemove) {
                results.remove(wordToDelete);
            }
        }
        return results;
    }
}

