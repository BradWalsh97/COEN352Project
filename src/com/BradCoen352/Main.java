package com.BradCoen352;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        clearConsole();
        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Hello, welcome to the COEN352 project. " +
                "Lets start with which part you'd like to evaluate.\n" +
                "Please Ensure that your dictionary and input text files are stored in the InputFileDirectory folder.\n" +
                "Insert an integer value for the part you'd like to chose: ");
        String part = userInput.readLine();
        clearConsole();
        if (part.equals("1")) {
            System.out.println("\n*****Part 1*****");
            partOne();
        } else if (part.equals("2")) {
            System.out.println("\n*****Part 2*****");
            partTwo();
        } else if (part.equals("22")) {
            System.out.println("\n*****Secret Part 2*****");
            partTwo_two();
        } else
            System.out.println("\nGoodbye!");
    }

    //source for three write functions: https://stackoverflow.com/questions/1062113/fastest-way-to-write-huge-data-in-text-file-java
    private static void writeRaw(List<String> records) throws IOException {
        //File file = File.createTempFile("repeated", ".txt");
        File file = new File("repeated.txt");
        try {
            FileWriter writer = new FileWriter(file);
            System.out.print("Writing raw... ");
            write(records, writer);
        } finally {
            // comment this out if you want to inspect the files afterward
            //file.delete();
        }
    }

    private static void writeBuffered(List<String> records, int bufSize, String path) throws IOException {
        File file = new File(path);
        try {
            FileWriter writer = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(writer, bufSize);

            // System.out.print("Writing buffered (buffer size: " + bufSize + ")... ");
            write(records, bufferedWriter);
        } finally {
            // comment this out if you want to inspect the files afterward
            //file.delete();
        }
    }

    private static void write(List<String> records, Writer writer) throws IOException {
        long start = System.currentTimeMillis();
        for (String record : records) {
            writer.write(record);
        }
        writer.flush();
        writer.close();
        long end = System.currentTimeMillis();
        // System.out.println((end - start) / 1000f + " seconds");
    }

    //source: https://stackoverflow.com/questions/2979383/java-clear-the-console
    private static void clearConsole() throws IOException, InterruptedException {
        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
    }

    private static void partOne() throws IOException {
        /**Create timers used to assess code execution time**/
        final long startTime;
        final long endTime;
        final long createBSTTime;
        final long endCreateBSTTime;
        final long loadInputTextTime;
        final long endLoadInputText;
        final long loadHashMap;
        final long endLoadHashMap;
        final long makeMinHeapTime;
        final long endMakeMinHeapTime;
        final long writeFrequenciesTextFile;
        final long endWriteFrequenciesTextFile;
        final long writeRepeatedTextFile;
        final long endWriteRepeatedTextFile;
        long wordCount = 0;
        /**End timer setup**/

        //Get dictionary & input file paths
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter the name of the dictionary: ");
        Path currentRelativePath = Paths.get("");
        String dictionaryName = currentRelativePath.toAbsolutePath().toString() + "\\InputFileDirectory\\" + scan.next();
        System.out.println("Path: " + dictionaryName);
        System.out.print("\nEnter the name of the input File: ");
        String inputFileName = currentRelativePath.toAbsolutePath().toString() + "\\InputFileDirectory\\" + scan.next();
        System.out.println("Path: " + inputFileName);

        startTime = System.currentTimeMillis();
        createBSTTime = System.currentTimeMillis();
        MyBST dictionary = new MyBST();//a Binary search tree to hold the dictionary of words
        //MyRedBlackBST secondDictionary = new MyRedBlackBST<>(); //todo: get rid of all RDTree stuff

        /****Get dictionary and shuffle it****/
        //BufferedReader dictionaryBuffer = new BufferedReader(new FileReader("dictionary.txt"), 8192 * 4); //todo: get this from command line from user

        String[] lineWords;
        ArrayList<String> allDictionaryWords;
        String line;
        lineWords = null;
        allDictionaryWords = new ArrayList<String>();
        try (BufferedReader dictionaryBuffer = new BufferedReader(new FileReader(dictionaryName), 8192 * 4)) {

            //put every word into lineWords. each word is to be lower case and all non-alphabetical characters are removed
            while ((line = dictionaryBuffer.readLine()) != null) {
                lineWords = line.replaceAll("(?:[^a-zA-Z -]|(?<=\\w)-(?!\\S))", " ").toLowerCase().split("\\s+"); //make to lower and split on space
                allDictionaryWords.addAll(Arrays.asList(lineWords));
            }
            dictionaryBuffer.close();
        } catch (Exception e) {
            System.out.println("There is an issue with your dictionary. Please ensure your file exists, can be opened, isn't already open," +
                    "and isn't null");
            System.out.println(e.getCause());
            System.exit(1);
        }

        //first check if lineWords is empty. If it is that means our dictionary is empty and we have a problem
        if (lineWords == null) throw new RuntimeException("Dictionary is null, please provide a non-empty dictionary");
        //Shuffle allDictionaryWords Then insert every word into the BST with a default value of 0
        Collections.shuffle(allDictionaryWords);
        /****Now that dictionary is shuffled, we add it to our BST****/
        for (String word : allDictionaryWords) {
            dictionary.put(word, 0);
        }
        endCreateBSTTime = System.currentTimeMillis();

        /**Now that we have the dictionary, we want to load the input text ****/
        loadInputTextTime = System.currentTimeMillis();
        //BufferedReader inputTextBuffer = new BufferedReader(new FileReader("/InputFilesPartA/0.txt"), 8192 * 4);
        ArrayList<String> allInputWords;
        String inputLine = "";
        String[] inputLineWords = null;
        allInputWords = new ArrayList<String>();
        String tmpStringForInput;
        try (BufferedReader inputTextBuffer = new BufferedReader(new FileReader(inputFileName), 8 * 1024 * 1024)) {
            while ((inputLine = inputTextBuffer.readLine()) != null) {
                inputLineWords = inputLine.replaceAll("(?:[^a-zA-Z -]|(?<=\\w)-(?!\\S))", " ").toLowerCase().split("\\s+");
                allInputWords.addAll(Arrays.asList(inputLineWords));
            }
            if (inputLineWords == null) throw new RuntimeException("Input file is null, please provide non-empty file");
            inputTextBuffer.close();
        } catch (Exception e) {
            System.out.println("There is an issue with your input file. Please ensure your file exists, can be opened, isn't already open," +
                    "and isn't null");
            System.out.println(e.getCause());
            System.exit(1);
        }
        wordCount = allInputWords.size();
        endLoadInputText = System.currentTimeMillis();

        /****Once dictionary is created, we create a priority queue (minHeap) to store the amount of times a word
         is found in the file.****/
        loadHashMap = System.currentTimeMillis();
        HashMap<String, Integer> frequencyMap = new HashMap<>();
        for (String word : allInputWords) {
            if(word.equals("s")||word.equals(""))
                continue;
            //old method
//            if (dictionary.contains(word)) {
//                //if(secondDictionary.contains(word)){//todo: Compare performance of rb-tree vs bst and chose the best
//                if (frequencyMap.containsKey(word)) {
//                    frequencyMap.put(word, frequencyMap.get(word) + 1);
//                } else frequencyMap.put(word, 1);
//            }
            //maybe faster method?
            if (frequencyMap.containsKey(word)) //already been found -> update value
                frequencyMap.put(word, frequencyMap.get(word) + 1);
            else if (dictionary.contains(word)) //not been found yet but in dictionary, create entry with default value = 1
                frequencyMap.put(word, 1);
        }
        endLoadHashMap = System.currentTimeMillis();
        //TreeMap<String, Integer> frequencyTree = new TreeMap<>();


        //now, the hashmap should have a value for all words found in the input file and dictionary.
        //we're going to get all the keys and values and put them into the minHeap
        makeMinHeapTime = System.currentTimeMillis();
        frequencyPriorityQueue frequencyHeap = new frequencyPriorityQueue(250); //assume default size of 250
        repeatedPriorityQueue repeatedHeap = new repeatedPriorityQueue(250);
        for (Map.Entry<String, Integer> entry : frequencyMap.entrySet()) {
            frequencyHeap.insert(entry.getKey(), entry.getValue());
            repeatedHeap.insert(entry.getKey(), entry.getValue());
            //System.out.println("Node " + ":\n  Word: " + entry.getKey() + "\n  Frequency: " + entry.getValue());
        }
        //now show it in the terminal as well as write to file (uncomment for debugging)
//        PrintWriter frequenciesWriter = new PrintWriter("frequencies.txt", StandardCharsets.UTF_8);
//        for (int i = 0; i < frequencyMap.size(); i++) {
//            //System.out.println(frequencyHeap.popMin());
//            MySpecialPriorityQueue.Node tmp = frequencyHeap.popMin();
//            System.out.println("Node " + i + ":\n  Word: " + tmp.getKey() + "\n  Frequency: " + tmp.getValue());
//            frequenciesWriter.println(tmp.getKey() + " " + tmp.getValue());
//        }

        endMakeMinHeapTime = System.currentTimeMillis();


        /**Make array that has all frequencies of certain size and then sort them alphabetically**/
//        int maxFrequency;
//        int nextFrequency = 2;
//        int i = 0;
//        MySpecialPriorityQueue.Node tmpNode;
//        ArrayList<sortingNode> sortedList = new ArrayList<>();
//        ArrayList<sortingNode> subArray = new ArrayList<>();
//
//        for (int j = 0; j < frequencyHeap.size(); j++) {
//            while(i < nextFrequency){
//                tmpNode = frequencyHeap.popMin();
//                i = tmpNode.getValue();
//
//
//            }
//       }


        /***Create the first txt (frequencies.txt) and insert the respective data)***/
        writeFrequenciesTextFile = System.currentTimeMillis();
        try (PrintWriter frequenciesWriter = new PrintWriter(currentRelativePath.toAbsolutePath().toString() + "\\OutputFiles\\frequencies.txt", StandardCharsets.UTF_8)) {
            long numberOfWords = 0;
            for (int i = 0; i < frequencyMap.size(); i++) {
                frequencyPriorityQueue.Node tmp = frequencyHeap.popMin();
                frequenciesWriter.println(tmp.getKey() + " " + tmp.getValue());
                numberOfWords += tmp.getValue();
            }
            frequenciesWriter.close();
        } catch (Exception e) {
            System.out.println("There is an issue with your frequencies.txt. Please ensure your file isn't already open," +
                    "and isn't null");
            System.out.println(e.getCause());
            System.exit(1);
        }
        endWriteFrequenciesTextFile = System.currentTimeMillis();

        /***Create the second txt (repeated.txt) and insert the respective data)***/
        /* Since this type of file can be rather large, we're going to
         *
         */
        writeRepeatedTextFile = System.currentTimeMillis();
//        PrintWriter repeatedWriter = new PrintWriter(currentRelativePath.toAbsolutePath().toString() + "\\OutputFiles\\repeated.txt", StandardCharsets.UTF_8);
//        String bigLine = "";
//        if (numberOfWords < 500000) {
//            for (int i = 0; i < frequencyMap.size(); i++) {
//                String tmpString = "";
//                repeatedPriorityQueue.Node tmp = repeatedHeap.popMin();
//                int value = tmp.getValue();
//                String word = tmp.getKey();
//                for (int j = 0; j < value / 2; j++) { //if we write the word two times we'll cut the loops by two
//                    tmpString += word + " " + word + " ";
//                }
//                if (value % 2 == 1)//if its an odd number, write an extra word to it
//                    tmpString += word + " ";
//                //repeatedWriter.println(tmpString + " ");
//                bigLine += bigLine;
//            }
//            repeatedWriter.write(bigLine);
//            repeatedWriter.close();
//        }
//        else{
        List<String> repeatedWordsList = new ArrayList<>();
        for (int i = 0; i < frequencyMap.size(); i++) {
            repeatedPriorityQueue.Node tmpNode = repeatedHeap.popMin();
            String word = tmpNode.getKey();
            for (int j = 0; j < tmpNode.getValue(); j++) {
                repeatedWordsList.add(word + " ");
            }

        }
        //writeRaw(repeatedWordsList);
        writeBuffered(repeatedWordsList, 4194304, currentRelativePath.toAbsolutePath().toString() + "\\OutputFiles\\repeated.txt");
//        }
        endWriteRepeatedTextFile = System.currentTimeMillis();
        endTime = System.currentTimeMillis();
        System.out.println("Create Dictionary Time:         " + (endCreateBSTTime - createBSTTime) + "ms -> " + (endCreateBSTTime - createBSTTime) / 1000f + " seconds");
        System.out.println("Load Input Text Time:           " + (endLoadInputText - loadInputTextTime) + "ms -> " + (endLoadInputText - loadInputTextTime) / 1000f + " seconds");
        System.out.println("Create HashMap Time:            " + (endLoadHashMap - loadHashMap) + "ms -> " + (endLoadHashMap - loadHashMap) / 1000f + " seconds");
        System.out.println("Load MinHeap Time:              " + (endMakeMinHeapTime - makeMinHeapTime) + "ms -> " + (endMakeMinHeapTime - makeMinHeapTime) / 1000f + " seconds");
        System.out.println("Write Frequency Text File Time: " + (endWriteFrequenciesTextFile - writeFrequenciesTextFile) + "ms -> " + (endWriteFrequenciesTextFile - writeFrequenciesTextFile) / 1000f + " seconds");
        System.out.println("Write Repeated Text File Time:  " + (endWriteRepeatedTextFile - writeRepeatedTextFile) + "ms -> " + (endWriteRepeatedTextFile - writeRepeatedTextFile) / 1000f + " seconds");
        System.out.println("Total Execution Time:           " + (endTime - startTime) + "ms -> " + (endTime - startTime) / 1000f + " seconds");
        System.out.println("Word Count:                     " + wordCount);

        if (true) {//this if statement is to measure the performance of the program. Set true if you want to record execution time
            try (PrintWriter performanceWriter = new PrintWriter(currentRelativePath.toAbsolutePath().toString() + "\\OutputFiles\\performance.csv", StandardCharsets.UTF_8)) {
                performanceWriter.println("Word Count:                           ," + wordCount);
                performanceWriter.println("Create Dictionary Time:               ," + (endCreateBSTTime - createBSTTime) + "ms -> " + (endCreateBSTTime - createBSTTime) / 1000f + " seconds");
                performanceWriter.println("Load Input Text Time:                 ," + (endLoadInputText - loadInputTextTime) + "ms -> " + (endLoadInputText - loadInputTextTime) / 1000f + " seconds");
                performanceWriter.println("Create HashMap Time:                  ," + (endLoadHashMap - loadHashMap) + "ms -> " + (endLoadHashMap - loadHashMap) / 1000f + " seconds");
                performanceWriter.println("Load MinHeap Time:                    ," + (endMakeMinHeapTime - makeMinHeapTime) + "ms -> " + (endMakeMinHeapTime - makeMinHeapTime) / 1000f + " seconds");
                performanceWriter.println("Write Frequency Text File Time:       ," + (endWriteFrequenciesTextFile - writeFrequenciesTextFile) + "ms -> " + (endWriteFrequenciesTextFile - writeFrequenciesTextFile) / 1000f + " seconds");
                performanceWriter.println("Write Repeated Text File Time:        ," + (endWriteRepeatedTextFile - writeRepeatedTextFile) + "ms -> " + (endWriteRepeatedTextFile - writeRepeatedTextFile) / 1000f + " seconds");
                performanceWriter.println("Total Execution Time:                 ," + (endTime - startTime) + "ms -> " + (endTime - startTime) / 1000f + " seconds");

                performanceWriter.close();
            } catch (Exception e) {
                System.out.println("There is an issue with your performance.csv. Please ensure your file can be opened or isn't already open.");
                System.out.println(e.getCause());
                System.exit(1);
            }
        }
    }

    private static void partTwo() throws IOException {
        /**Create timers used to assess code execution time**/
        final long startTime;
        final long endTime;
        final long createBackupDictionary;
        final long endCreateBackupDictionary;
        final long createBSTTime;
        final long endCreateBSTTime;
        final long loadInputTextTime;
        final long endLoadInputText;
        final long loadHashMap;
        final long endLoadHashMap;
        final long makeMinHeapTime;
        final long endMakeMinHeapTime;
        final long writeFrequenciesTextFile;
        final long endWriteFrequenciesTextFile;
        final long writeRepeatedTextFile;
        final long endWriteRepeatedTextFile;
        final long writeCorrectedWordsTextFile;
        final long endWriteCorrectedWordsTextFile;
        long wordCount = 0;
        /**End timer setup**/

        //Get dictionary & input file paths
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter the name of the dictionary: ");
        Path currentRelativePath = Paths.get("");
        String dictionaryName = currentRelativePath.toAbsolutePath().toString() + "\\InputFileDirectory\\" + scan.next();
        System.out.println("Path: " + dictionaryName);
        System.out.print("\nEnter the name of the input File: ");
        String inputFileName = currentRelativePath.toAbsolutePath().toString() + "\\InputFileDirectory\\" + scan.next();
        System.out.println("Path: " + inputFileName);

        /*Before loading the user provided files, we're going to make a second dictionary which will allow us to make
         *better predictions of if only a single word in a double word input is correct. The logic will be
         * explained at that part of the algorithm
         */
        startTime = System.currentTimeMillis();
        createBackupDictionary = System.currentTimeMillis();
//        HashMap<String, Boolean> backupDictionary = new HashMap<>();
//        BufferedReader backupDictionaryBuffer = new BufferedReader(new FileReader("words_alpha.txt"), 5000 * 1024);
//        String backDicLine;
//        String[] backupLineWords = null;
//        while ((backDicLine = backupDictionaryBuffer.readLine()) != null) {
//            //backupLineWords = backDicLine.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
//            backupDictionary.put(backDicLine, false);
//        }
        endCreateBackupDictionary = System.currentTimeMillis();

        createBSTTime = System.currentTimeMillis();
        MyBST dictionary = new MyBST(); //a Binary search tree to hold the dictionary of words

        /****Get dictionary and shuffle it****/
        String[] lineWords;
        ArrayList<String> allDictionaryWords;
        String line;
        lineWords = null;
        allDictionaryWords = new ArrayList<String>();
        try (BufferedReader dictionaryBuffer = new BufferedReader(new FileReader(dictionaryName), 8192 * 4)) {

            //put every word into lineWords. each word is to be lower case and all non-alphabetical characters are removed
            while ((line = dictionaryBuffer.readLine()) != null) {
                lineWords = line.replaceAll("(?:[^a-zA-Z -]|(?<=\\w)-(?!\\S))", " ").toLowerCase().split("\\s+"); //make to lower and split on space
                allDictionaryWords.addAll(Arrays.asList(lineWords));
            }
            dictionaryBuffer.close();
        } catch (Exception e) {
            System.out.println("There is an issue with your dictionary. Please ensure your file exists, can be opened, isn't already open," +
                    "and isn't null");
            System.out.println(e.getCause());
            System.exit(1);
        }

        //first check if lineWords is empty. If it is that means our dictionary is empty and we have a problem
        if (lineWords == null)
            throw new RuntimeException("Looks like your dictionary is null, make sure its ok.");
        //Shuffle allDictionaryWords Then insert every word into the BST with a default value of 0
        Collections.shuffle(allDictionaryWords);
        /****Now that dictionary is shuffled, we add it to our BST****/
        for (String word : allDictionaryWords) {
            dictionary.put(word, 0);
            //secondDictionary.put(word, 0);
            //secondDictionary.put(word, 0);
        }
        endCreateBSTTime = System.currentTimeMillis();

        /**Now that we have the dictionary, we want to load the input text ****/
        loadInputTextTime = System.currentTimeMillis();
        ArrayList<String> allInputWords;
        String inputLine;
        String[] inputLineWords = null;
        allInputWords = new ArrayList<String>();
        try (BufferedReader inputTextBuffer = new BufferedReader(new FileReader(inputFileName), 8 * 1024 * 1024)) {
            while ((inputLine = inputTextBuffer.readLine()) != null) {
                //inputLineWords = inputLine.replaceAll("[^a-zA-Z\\- ]", " ").toLowerCase().split("\\s+");
                inputLineWords = inputLine.replaceAll("(?:[^a-zA-Z -]|(?<=\\w)-(?!\\S))", " ").toLowerCase().split("\\s+");
                allInputWords.addAll(Arrays.asList(inputLineWords));
            }
            if (inputLineWords == null) throw new RuntimeException("Looks like your input file is null, please make sure its ok");
            inputTextBuffer.close();
        } //todo: try catch if file is there
        catch (Exception e) {
            System.out.println("There seems to be an issue with you're Input file. Please ensure it can be opened, isn't currently opened, " +
                    "and isn't null. The following error was found: ");
            System.out.println(e.getMessage());
            System.exit(1);
        }
        endLoadInputText = System.currentTimeMillis();

        /**Input text is loaded and read to be read. We now need to search for the words**/
        loadHashMap = System.currentTimeMillis();
        /* The way this is going to work is a few thing. Every time a word is found in the dictionary, we're going to
         * Add it to a hashmap which will serve as the first reference of a dictionary.
         * If the word is found in the bst dictionary or the hashmap dictionary we are going to increment it in the
         * frequency map. If its not found we're going to do some further analysis. Finally, if after the analysis
         * nothing is found we're going to move onto the next word.
         */
        HashMap<String, Integer> frequencyMap = new HashMap<>();
        List<String> fixedWordList = new ArrayList<>();
        //wordCount = allInputWords.size();
        for (String word : allInputWords) {
            if(word.equals("s"))
                continue;
            if (frequencyMap.containsKey(word)) //already been found -> update value
                frequencyMap.put(word, frequencyMap.get(word) + 1);
            else if (dictionary.contains(word)) {//not been found yet but exists, create entry with default value = 1
                frequencyMap.put(word, 1);
            } else {
                //word may exists but may be missing space between two words
                //The average length of the word in the provided dictionary is 5.4. Thus, we're going to try to
                //split the word at its fifth letter. Average of large dictionary is 9.4 so test the two and see
                //Todo: compare 5 and 9 as cut number

                //first, see if its a word thats just not in the dictionary. If it is ie: dictionary has single word,
                //but not the plural. Break;
//                if(backupDictionary.containsKey(word))
//                    break;
                /*scrap the above stuff and go with this instead. Instead of looking for a word such as "a" I will
                 *make the first substring the first two letters in the word and scan until I find a word in either of
                 *The strings. I prioritize finding two strings, if I find just one I'll keep looking.
                 *While this will affect the performance of the application I am justifying the computational
                 *expense to hopefully get more true to life readings.
                 */
                String subStringLeft = "";
                String subStringRight = "";
                boolean goodWordFound = false;
                boolean moreThanOneWord = false;
                ArrayList<String> wordsFound = new ArrayList<>();
                if (word.length() == 2) {
                    //This is a special case where the word might be two a's for example.
                    //This should not happen and if it does it'll be considered a typo since there is
                    //generally no sentence in the english language that requires two a's or I's.
                    //Thus, split and check if they're words and if theyre equal just count them once
                    //else case should not occur generally so just exit. This is because words like 'as' should already
                    //have been covered above unless the word isnt in the dictionary.
                    subStringLeft = word.substring(0, 1);
                    subStringRight = word.substring(1, 2);
                    boolean subStringLeftBool = dictionary.contains(subStringLeft);
                    boolean subStringRightBool = dictionary.contains(subStringRight);
                    if (subStringLeft.equals(subStringRight) && subStringLeftBool && subStringRightBool) {//todo: make code == text above
                        if (frequencyMap.containsKey(subStringLeft))
                            frequencyMap.put(subStringLeft, frequencyMap.get(subStringLeft) + 1);
                        else
                            frequencyMap.put(subStringLeft, 1);
                        if (frequencyMap.containsKey(subStringRight))
                            frequencyMap.put(subStringRight, frequencyMap.get(subStringRight) + 1);
                        else
                            frequencyMap.put(subStringRight, 1);
                    }
                } else {
                    String tmpSingleWord = null;
                    boolean freqMapSubStringLeft;
                    boolean freqMapSubStringRight;
                    boolean dictionarySubStringLeft;
                    boolean dictionarySubStringRight;
                    int i = 0, j = 0;
                    subStringLeft = word.substring(0, i);
                    subStringRight = word.substring(i);
                    for (i = 0, j = 0; i < word.length(); i++) {
                        dictionarySubStringLeft = false;
                        dictionarySubStringRight = false;
                        freqMapSubStringLeft = frequencyMap.containsKey(subStringLeft);
                        if(!freqMapSubStringLeft)
                            dictionarySubStringLeft = dictionary.contains(subStringLeft);
                        freqMapSubStringRight = frequencyMap.containsKey(subStringRight);
                        if(!freqMapSubStringRight)
                            dictionarySubStringRight = dictionary.contains(subStringRight);

                        if ((freqMapSubStringLeft && freqMapSubStringRight) //Only two words which exist together case
                                || (dictionarySubStringLeft && dictionarySubStringRight)) {
                            if (freqMapSubStringLeft)
                                frequencyMap.put(subStringLeft, frequencyMap.get(subStringLeft) + 1);
                            else
                                frequencyMap.put(subStringLeft, 1);
                            if (freqMapSubStringRight)
                                frequencyMap.put(subStringRight, frequencyMap.get(subStringRight) + 1);
                            else
                                frequencyMap.put(subStringRight, 1);
                            if (!moreThanOneWord)
                                fixedWordList.add(word + ", " + subStringLeft + " " + subStringRight);//add to fixed word list
                            else {
                                wordsFound.add(subStringLeft);
                                wordsFound.add(subStringRight);
                            }
                            //wordCount+=2;
                            //goodWordFound = true;

                            break;
                        } else if (freqMapSubStringLeft || dictionarySubStringLeft) {//more than two words case
                            String tmp = subStringLeft;
                            j = i + 1;
                            int storeJ = i - 1;
                            for (; j < word.length(); j++) {
                                tmp = subStringLeft + word.substring(i - 1, j);
                                boolean tmpBool = frequencyMap.containsKey(tmp);
                                if ((tmpBool || dictionary.contains(tmp)) && !tmp.equals(subStringLeft)) {
                                    if (tmpBool)
                                        frequencyMap.put(tmp, frequencyMap.get(tmp) + 1);
                                    else
                                        frequencyMap.put(tmp, 1);

                                    wordsFound.add(tmp);
                                    goodWordFound = true;
                                    moreThanOneWord = true;
                                    i = j;
                                    break;
                                }
                            }
                            if (j == word.length()) {//this is the case where the word initially found was a correct word
                                j = storeJ;
                                goodWordFound = true;
                                moreThanOneWord = true;
                                wordsFound.add(subStringLeft);
                            }
                        }
                        subStringLeft = word.substring(j, i);
                        subStringRight = word.substring(i);
                    }
                    //now we should have all of our found words in the array list so we're going to add them into one
                    //corrected word line
                    if (goodWordFound) {
                        String wordsFoundLine = word + ", ";
                        for (int k = 0; k < wordsFound.size(); k++) {
                            wordsFoundLine += wordsFound.get(k) + " ";
                            wordCount += wordsFound.size();
                        }
                        fixedWordList.add(wordsFoundLine);
                    }
                    goodWordFound = false;


                    //If we're at this point, that means that only a single word was found. Thus, we add it to
                    //our fixed word list
//                    if (tmpSingleWord != null && !doubleWordFlag)
//                        fixedWordList.add(word + ", " + tmpSingleWord);
//                    doubleWordFlag = false;
                }
            }
        }
        endLoadHashMap = System.currentTimeMillis();

        //now, the hashmap should have a value for all words found in the input file and dictionary.
        //we're going to get all the keys and values and put them into the minHeap
        makeMinHeapTime = System.currentTimeMillis();
        frequencyPriorityQueue frequencyHeap = new frequencyPriorityQueue(250); //assume default size of 250
        repeatedPriorityQueue repeatedHeap = new repeatedPriorityQueue(250);
        for (Map.Entry<String, Integer> entry : frequencyMap.entrySet()) {
            frequencyHeap.insert(entry.getKey(), entry.getValue());
            repeatedHeap.insert(entry.getKey(), entry.getValue());
            //System.out.println("Node " + ":\n  Word: " + entry.getKey() + "\n  Frequency: " + entry.getValue());
        }
        //now show it in the terminal as well as write to file (uncomment for debugging)
//        PrintWriter frequenciesWriter = new PrintWriter("frequencies.txt", StandardCharsets.UTF_8);
//        for (int i = 0; i < frequencyMap.size(); i++) {
//            //System.out.println(frequencyHeap.popMin());
//            MySpecialPriorityQueue.Node tmp = frequencyHeap.popMin();
//            System.out.println("Node " + i + ":\n  Word: " + tmp.getKey() + "\n  Frequency: " + tmp.getValue());
//            frequenciesWriter.println(tmp.getKey() + " " + tmp.getValue());
//        }

        endMakeMinHeapTime = System.currentTimeMillis();

        /***Create the first txt (frequencies.txt) and insert the respective data)***/
        writeFrequenciesTextFile = System.currentTimeMillis();
        PrintWriter frequenciesWriter = new PrintWriter(currentRelativePath.toAbsolutePath().toString() + "\\OutputFiles\\frequencies.txt", StandardCharsets.UTF_8);
        // long numberOfWords = 0;
        for (int i = 0; i < frequencyMap.size(); i++) {
            frequencyPriorityQueue.Node tmp = frequencyHeap.popMin();
            frequenciesWriter.println(tmp.getKey() + " " + tmp.getValue());
            //numberOfWords += tmp.getValue();
        }
        frequenciesWriter.close();
        endWriteFrequenciesTextFile = System.currentTimeMillis();

        /***Create the second txt (repeated.txt) and insert the respective data)***/
        writeRepeatedTextFile = System.currentTimeMillis();
        PrintWriter repeatedWriter = new PrintWriter(currentRelativePath.toAbsolutePath().toString() + "\\OutputFiles\\repeated.txt", StandardCharsets.UTF_8);
        String bigLine = "";
        List<String> repeatedWordsList = new ArrayList<>();
        for (int i = 0; i < frequencyMap.size(); i++) {
            repeatedPriorityQueue.Node tmpNode = repeatedHeap.popMin();
            String word = tmpNode.getKey();
            for (int j = 0; j < tmpNode.getValue(); j++) {
                repeatedWordsList.add(word + " ");
            }

        }
        //writeRaw(repeatedWordsList);
        writeBuffered(repeatedWordsList, 4194304, currentRelativePath.toAbsolutePath().toString() + "\\OutputFiles\\repeated.txt");
//        }
        endWriteRepeatedTextFile = System.currentTimeMillis();


        /***Create the Third txt (corrected_words_detected.txt) and insert respective data ***/
        writeCorrectedWordsTextFile = System.currentTimeMillis();
        writeBuffered(fixedWordList, 4194304, currentRelativePath.toAbsolutePath().toString() + "\\OutputFiles\\corrected_words_detected.txt");
        endWriteCorrectedWordsTextFile = System.currentTimeMillis();

        endTime = System.currentTimeMillis();
        System.out.println("Create Dictionary Time:               " + (endCreateBSTTime - createBSTTime) + "ms -> " + (endCreateBSTTime - createBSTTime) / 1000f + " seconds");
        //System.out.println("Create Backup Dictionary Time         " + (endCreateBackupDictionary - createBackupDictionary) + "ms -> " + (endCreateBackupDictionary - createBackupDictionary) / 1000f + " seconds");
        System.out.println("Load Input Text Time:                 " + (endLoadInputText - loadInputTextTime) + "ms -> " + (endLoadInputText - loadInputTextTime) / 1000f + " seconds");
        System.out.println("Create HashMap Time:                  " + (endLoadHashMap - loadHashMap) + "ms -> " + (endLoadHashMap - loadHashMap) / 1000f + " seconds");
        System.out.println("Load MinHeap Time:                    " + (endMakeMinHeapTime - makeMinHeapTime) + "ms -> " + (endMakeMinHeapTime - makeMinHeapTime) / 1000f + " seconds");
        System.out.println("Write Frequency Text File Time:       " + (endWriteFrequenciesTextFile - writeFrequenciesTextFile) + "ms -> " + (endWriteFrequenciesTextFile - writeFrequenciesTextFile) / 1000f + " seconds");
        System.out.println("Write Repeated Text File Time:        " + (endWriteRepeatedTextFile - writeRepeatedTextFile) + "ms -> " + (endWriteRepeatedTextFile - writeRepeatedTextFile) / 1000f + " seconds");
        System.out.println("Write Corrected Words Text File Time: " + (endWriteCorrectedWordsTextFile - writeCorrectedWordsTextFile) + "ms -> " + (endWriteCorrectedWordsTextFile - writeCorrectedWordsTextFile) / 1000f + " seconds");
        System.out.println("Total Execution Time:                 " + (endTime - startTime) + "ms -> " + (endTime - startTime) / 1000f + " seconds");
        //System.out.println("Word Count:                           " + wordCount);

        if (true) {//this if statement is to measure the performance of the program. Set true if you want to record execution time
            try (PrintWriter performanceWriter = new PrintWriter(currentRelativePath.toAbsolutePath().toString() + "\\OutputFiles\\performance.csv", StandardCharsets.UTF_8)) {
                //performanceWriter.println("Word Count:                           ," + wordCount);
                performanceWriter.println("Create Dictionary Time:               ," + (endCreateBSTTime - createBSTTime) + "ms -> " + (endCreateBSTTime - createBSTTime) / 1000f + " seconds");
                //performanceWriter.println("Create Backup Dictionary Time         ," + (endCreateBackupDictionary - createBackupDictionary) + "ms -> " + (endCreateBackupDictionary - createBackupDictionary) / 1000f + " seconds");
                performanceWriter.println("Load Input Text Time:                 ," + (endLoadInputText - loadInputTextTime) + "ms -> " + (endLoadInputText - loadInputTextTime) / 1000f + " seconds");
                performanceWriter.println("Create HashMap Time:                  ," + (endLoadHashMap - loadHashMap) + "ms -> " + (endLoadHashMap - loadHashMap) / 1000f + " seconds");
                performanceWriter.println("Load MinHeap Time:                    ," + (endMakeMinHeapTime - makeMinHeapTime) + "ms -> " + (endMakeMinHeapTime - makeMinHeapTime) / 1000f + " seconds");
                performanceWriter.println("Write Frequency Text File Time:       ," + (endWriteFrequenciesTextFile - writeFrequenciesTextFile) + "ms -> " + (endWriteFrequenciesTextFile - writeFrequenciesTextFile) / 1000f + " seconds");
                performanceWriter.println("Write Repeated Text File Time:        ," + (endWriteRepeatedTextFile - writeRepeatedTextFile) + "ms -> " + (endWriteRepeatedTextFile - writeRepeatedTextFile) / 1000f + " seconds");
                performanceWriter.println("Write Corrected Words Text File Time: ," + (endWriteCorrectedWordsTextFile - writeCorrectedWordsTextFile) + "ms -> " + (endWriteCorrectedWordsTextFile - writeCorrectedWordsTextFile) / 1000f + " seconds");
                performanceWriter.println("Total Execution Time:                 ," + (endTime - startTime) + "ms -> " + (endTime - startTime) / 1000f + " seconds");

                performanceWriter.close();
            } catch (Exception e) {
                System.out.println("There is an issue with your performance.csv . Please ensure your file exists, can be opened, isn't already open," +
                        "and isn't null");
                System.out.println(e.getCause());
                System.exit(1);
            }

        }
    }

    private static void partTwo_two() throws IOException {
        /**Create timers used to assess code execution time**/
        final long startTime;
        final long endTime;
        final long createBackupDictionary;
        final long endCreateBackupDictionary;
        final long createBSTTime;
        final long endCreateBSTTime;
        final long loadInputTextTime;
        final long endLoadInputText;
        final long loadHashMap;
        final long endLoadHashMap;
        final long makeMinHeapTime;
        final long endMakeMinHeapTime;
        final long writeFrequenciesTextFile;
        final long endWriteFrequenciesTextFile;
        final long writeRepeatedTextFile;
        final long endWriteRepeatedTextFile;
        final long writeCorrectedWordsTextFile;
        final long endWriteCorrectedWordsTextFile;
        long wordCount = 0;
        /**End timer setup**/

        //Get dictionary & input file paths
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter the name of the dictionary: ");
        Path currentRelativePath = Paths.get("");
        String dictionaryName = currentRelativePath.toAbsolutePath().toString() + "\\InputFileDirectory\\" + scan.next();
        System.out.println("Path: " + dictionaryName);
        System.out.print("\nEnter the name of the input File: ");
        String inputFileName = currentRelativePath.toAbsolutePath().toString() + "\\InputFileDirectory\\" + scan.next();
        System.out.println("Path: " + inputFileName);

        /*Before loading the user provided files, we're going to make a second dictionary which will allow us to make
         *better predictions of if only a single word in a double word input is correct. The logic will be
         * explained at that part of the algorithm
         */
        startTime = System.currentTimeMillis();
        createBackupDictionary = System.currentTimeMillis();
        HashMap<String, Boolean> backupDictionary = new HashMap<>();
        BufferedReader backupDictionaryBuffer = new BufferedReader(new FileReader("words_alpha.txt"), 5000 * 1024);
        String backDicLine;
        String[] backupLineWords = null;
        while ((backDicLine = backupDictionaryBuffer.readLine()) != null) {
            //backupLineWords = backDicLine.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
            backupDictionary.put(backDicLine, false);
        }
        endCreateBackupDictionary = System.currentTimeMillis();

        createBSTTime = System.currentTimeMillis();
        MyBST dictionary = new MyBST(); //a Binary search tree to hold the dictionary of words

        /****Get dictionary and shuffle it****/
        String[] lineWords;
        ArrayList<String> allDictionaryWords;
        String line;
        lineWords = null;
        allDictionaryWords = new ArrayList<String>();
        try (BufferedReader dictionaryBuffer = new BufferedReader(new FileReader(dictionaryName), 8192 * 4)) {

            //put every word into lineWords. each word is to be lower case and all non-alphabetical characters are removed
            while ((line = dictionaryBuffer.readLine()) != null) {
                lineWords = line.replaceAll("[^a-zA-Z ]", " ").toLowerCase().split("\\s+"); //make to lower and split on space
                allDictionaryWords.addAll(Arrays.asList(lineWords));
            }
            dictionaryBuffer.close();
        } catch (Exception e) {
            System.out.println("There is an issue with your dictionary. Please ensure your file exists, can be opened, isn't already open," +
                    "and isn't null");
            System.out.println(e.getCause());
            System.exit(1);
        }

        //first check if lineWords is empty. If it is that means our dictionary is empty and we have a problem
        if (lineWords == null)
            throw new RuntimeException("There is an issue with your dictionary, please ensure it is" +
                    "not empty, it exists, or isn't currently opened. ");
        //Shuffle allDictionaryWords Then insert every word into the BST with a default value of 0
        Collections.shuffle(allDictionaryWords);
        /****Now that dictionary is shuffled, we add it to our BST****/
        for (String word : allDictionaryWords) {
            dictionary.put(word, 0);
            //secondDictionary.put(word, 0);
            //secondDictionary.put(word, 0);
        }
        endCreateBSTTime = System.currentTimeMillis();

        /**Now that we have the dictionary, we want to load the input text ****/
        loadInputTextTime = System.currentTimeMillis();
        ArrayList<String> allInputWords;
        String inputLine;
        String[] inputLineWords = null;
        allInputWords = new ArrayList<String>();
        try (BufferedReader inputTextBuffer = new BufferedReader(new FileReader(inputFileName), 8 * 1024 * 1024)) {
            while ((inputLine = inputTextBuffer.readLine()) != null) {
                inputLineWords = inputLine.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
                allInputWords.addAll(Arrays.asList(inputLineWords));
            }
            if (inputLineWords == null) throw new RuntimeException("Input file is null, please provide non-empty file");
            inputTextBuffer.close();
        } //todo: try catch if file is there
        catch (Exception e) {
            System.out.println("There seems to be an issue with you're Input file. Please ensure it can be opened, isn't currently opened, " +
                    "and isn't null. The following error was found: ");
            System.out.println(e.getMessage());
            System.exit(1);
        }
        endLoadInputText = System.currentTimeMillis();

        /**Input text is loaded and read to be read. We now need to search for the words**/
        loadHashMap = System.currentTimeMillis();
        /* The way this is going to work is a few thing. Every time a word is found in the dictionary, we're going to
         * Add it to a hashmap which will serve as the first reference of a dictionary.
         * If the word is found in the bst dictionary or the hashmap dictionary we are going to increment it in the
         * frequency map. If its not found we're going to do some further analysis. Finally, if after the analysis
         * nothing is found we're going to move onto the next word.
         */
        HashMap<String, Integer> frequencyMap = new HashMap<>();
        List<String> fixedWordList = new ArrayList<>();
        wordCount = allInputWords.size();
        for (String word : allInputWords) {
            if (frequencyMap.containsKey(word)) //already been found -> update value
                frequencyMap.put(word, frequencyMap.get(word) + 1);
            else if (dictionary.contains(word)) {//not been found yet but exists, create entry with default value = 1
                frequencyMap.put(word, 1);
            } else {//word may exists but is missing space between two words
                //The average length of the word in the provided dictionary is 5.4. Thus, we're going to try to
                //split the word at its fifth letter. Average of large dictionary is 9.4 so test the two and see
                //Todo: compare 5 and 9 as cut number

                /*scrap the above stuff and go with this instead. Instead of looking for a word such as "a" I will
                 *make the first substring the first two letters in the word and scan until I find a word in either of
                 *The strings. I prioritize finding two strings, if I find just one I'll keep looking.
                 *While this will affect the performance of the application I am justifying the computational
                 *expense to hopefully get more true to life readings.
                 */
                String subStringLeft = "";
                String subStringRight = "";
                boolean doubleWordFlag = false;
                if (word.length() == 2) {
                    //This is a special case where the word might be two a's for example.
                    //This should not happen and if it does it'll be considered a typo since there is
                    //generally no sentence in the english language that requires two a's or I's.
                    //Thus, split and check if they're words and if theyre equal just count them once
                    //else case should not occur generally so just exit. This is because words like 'as' should already
                    //have been covered above unless the word isnt in the dictionary.
                    subStringLeft = word.substring(0, 1);
                    subStringRight = word.substring(1, 2);
                    boolean subStringLeftBool = dictionary.contains(subStringLeft);
                    boolean subStringRightBool = dictionary.contains(subStringRight);
                    if (subStringLeft.equals(subStringRight) && subStringLeftBool && subStringRightBool) {//todo: make code == text above
                        if (frequencyMap.containsKey(subStringLeft))
                            frequencyMap.put(subStringLeft, frequencyMap.get(subStringLeft) + 1);
                        else
                            frequencyMap.put(subStringLeft, 1);
                        if (frequencyMap.containsKey(subStringRight))
                            frequencyMap.put(subStringRight, frequencyMap.get(subStringRight) + 1);
                        else
                            frequencyMap.put(subStringRight, 1);
                    }
                } else {
                    String tmpSingleWord = null; //todo: ask if can use second dictionary to compare if only one word gets found to make sure its reasonable.
                    for (int i = 0; i < word.length(); i++) {
                        /* Here, we prioritize findind two words. Thus, if initially we find two words we're going to
                         * just stick with that as that is the most likely gramatically and contextually correct
                         * solution. If we find just one, then we're going to store the single word but keep looping
                         * through the list to see if there are two correct words later in the list.
                         * Again, this is done to try to find the most correct solution at the sacrifice of time.
                         */
                        subStringLeft = word.substring(0, i);
                        subStringRight = word.substring(i);
                        boolean freqMapSubStringLeft = frequencyMap.containsKey(subStringLeft);
                        boolean freqMapSubStringRight = frequencyMap.containsKey(subStringRight);
                        boolean dictionarySubStringLeft = dictionary.contains(subStringLeft);
                        boolean dictionarySubStringRight = dictionary.contains(subStringRight);
                        if ((freqMapSubStringLeft && freqMapSubStringRight)
                                || (dictionarySubStringLeft && dictionarySubStringRight)) {
                            if (freqMapSubStringLeft)
                                frequencyMap.put(subStringLeft, frequencyMap.get(subStringLeft) + 1);
                            else
                                frequencyMap.put(subStringLeft, 1);
                            if (freqMapSubStringRight)
                                frequencyMap.put(subStringRight, frequencyMap.get(subStringRight) + 1);
                            else
                                frequencyMap.put(subStringRight, 1);
                            fixedWordList.add(word + ", " + subStringLeft + " " + subStringRight);//add to fixed word list
                            doubleWordFlag = true;

                            break; //break for loop as noted above todo: check if break all loops or just this one
                        } else if ((freqMapSubStringLeft || dictionarySubStringLeft) && !backupDictionary.containsKey(word)) {
                            if (backupDictionary.containsKey(subStringRight))
                                tmpSingleWord = subStringLeft;
//                            if(subStringLeft.length() != 1)
//                                tmpSingleWord = subStringLeft;
//                            else if(backupDictionary.containsKey(word)){
//                                /*
//                                 * If this case is reached, this means that the detected word is either "a" or "I".
//                                 * Due to how common these letters are, if a word is not found in the dictionary but
//                                 * contains "a" or "I" there is a possibility of a false positive on the word correction
//                                 * To mitigate this risk, we are going to check to see if the entire word is in the
//                                 * extensive backupDictionary. If it is, this signals a false positive. Thus, we reject
//                                 * the word and move on. The same applies for the 'else if' below.

//                                 */
//                                break;
//                            }
//                            else
                            //tmpSingleWord = subStringLeft;
                        } else if ((freqMapSubStringRight || dictionarySubStringRight) && !backupDictionary.containsKey(word)) {
                            if (backupDictionary.containsKey(subStringLeft))
                                tmpSingleWord = subStringRight;
//                            if(subStringRight.length() != 1)
//                                tmpSingleWord = subStringRight;
//                            else if(backupDictionary.containsKey(word))
//                                break;
//                            else
                            // tmpSingleWord = subStringRight;
                        }
//                        else if(!freqMapSubStringRight && !freqMapSubStringLeft && !dictionarySubStringLeft && !dictionarySubStringRight)
//                            break;
                    }
                    //If we're at this point, that means that only a single word was found. Thus, we add it to
                    //our fixed word list
                    if (tmpSingleWord != null && !doubleWordFlag)
                        fixedWordList.add(word + ", " + tmpSingleWord);
                    doubleWordFlag = false;
                }
            }
        }
        endLoadHashMap = System.currentTimeMillis();

        //now, the hashmap should have a value for all words found in the input file and dictionary.
        //we're going to get all the keys and values and put them into the minHeap
        makeMinHeapTime = System.currentTimeMillis();
        frequencyPriorityQueue frequencyHeap = new frequencyPriorityQueue(250); //assume default size of 250
        repeatedPriorityQueue repeatedHeap = new repeatedPriorityQueue(250);
        for (Map.Entry<String, Integer> entry : frequencyMap.entrySet()) {
            frequencyHeap.insert(entry.getKey(), entry.getValue());
            repeatedHeap.insert(entry.getKey(), entry.getValue());
            //System.out.println("Node " + ":\n  Word: " + entry.getKey() + "\n  Frequency: " + entry.getValue());
        }
        //now show it in the terminal as well as write to file (uncomment for debugging)
//        PrintWriter frequenciesWriter = new PrintWriter("frequencies.txt", StandardCharsets.UTF_8);
//        for (int i = 0; i < frequencyMap.size(); i++) {
//            //System.out.println(frequencyHeap.popMin());
//            MySpecialPriorityQueue.Node tmp = frequencyHeap.popMin();
//            System.out.println("Node " + i + ":\n  Word: " + tmp.getKey() + "\n  Frequency: " + tmp.getValue());
//            frequenciesWriter.println(tmp.getKey() + " " + tmp.getValue());
//        }

        endMakeMinHeapTime = System.currentTimeMillis();


        /**Make array that has all frequencies of certain size and then sort them alphabetically**/
//        int maxFrequency;
//        int nextFrequency = 2;
//        int i = 0;
//        MySpecialPriorityQueue.Node tmpNode;
//        ArrayList<sortingNode> sortedList = new ArrayList<>();
//        ArrayList<sortingNode> subArray = new ArrayList<>();
//
//        for (int j = 0; j < frequencyHeap.size(); j++) {
//            while(i < nextFrequency){
//                tmpNode = frequencyHeap.popMin();
//                i = tmpNode.getValue();
//
//
//            }
//       }


        /***Create the first txt (frequencies.txt) and insert the respective data)***/
        writeFrequenciesTextFile = System.currentTimeMillis();
        PrintWriter frequenciesWriter = new PrintWriter(currentRelativePath.toAbsolutePath().toString() + "\\OutputFiles\\frequencies.txt", StandardCharsets.UTF_8);
        // long numberOfWords = 0;
        for (int i = 0; i < frequencyMap.size(); i++) {
            frequencyPriorityQueue.Node tmp = frequencyHeap.popMin();
            frequenciesWriter.println(tmp.getKey() + " " + tmp.getValue());
            //numberOfWords += tmp.getValue();
        }
        frequenciesWriter.close();
        endWriteFrequenciesTextFile = System.currentTimeMillis();

        /***Create the second txt (repeated.txt) and insert the respective data)***/
        /* Since this type of file can be rather large, we're going to
         *
         */
        writeRepeatedTextFile = System.currentTimeMillis();
        PrintWriter repeatedWriter = new PrintWriter(currentRelativePath.toAbsolutePath().toString() + "\\OutputFiles\\repeated.txt", StandardCharsets.UTF_8);
        String bigLine = "";
//        if (numberOfWords < 500000) {
//            for (int i = 0; i < frequencyMap.size(); i++) {
//                String tmpString = "";
//                repeatedPriorityQueue.Node tmp = repeatedHeap.popMin();
//                int value = tmp.getValue();
//                String word = tmp.getKey();
//                for (int j = 0; j < value / 2; j++) { //if we write the word two times we'll cut the loops by two
//                    tmpString += word + " " + word + " ";
//                }
//                if (value % 2 == 1)//if its an odd number, write an extra word to it
//                    tmpString += word + " ";
//                //repeatedWriter.println(tmpString + " ");
//                bigLine += bigLine;
//            }
//            repeatedWriter.write(bigLine);
//            repeatedWriter.close();
//        }
//        else{
        List<String> repeatedWordsList = new ArrayList<>();
        for (int i = 0; i < frequencyMap.size(); i++) {
            repeatedPriorityQueue.Node tmpNode = repeatedHeap.popMin();
            String word = tmpNode.getKey();
            for (int j = 0; j < tmpNode.getValue(); j++) {
                repeatedWordsList.add(word + " ");
            }

        }
        //writeRaw(repeatedWordsList);
        writeBuffered(repeatedWordsList, 4194304, currentRelativePath.toAbsolutePath().toString() + "\\OutputFiles\\repeated.txt");
//        }
        endWriteRepeatedTextFile = System.currentTimeMillis();


        /***Create the Third txt (corrected_words_detected.txt) and insert respective data ***/
        writeCorrectedWordsTextFile = System.currentTimeMillis();
        PrintWriter correctedWordsWriter = new PrintWriter("corrected_words_detected.txt", StandardCharsets.UTF_8);
        for (String fixedWord : fixedWordList)
            correctedWordsWriter.write(fixedWord + System.lineSeparator());
        correctedWordsWriter.close();
        endWriteCorrectedWordsTextFile = System.currentTimeMillis();

        endTime = System.currentTimeMillis();
        System.out.println("Create Dictionary Time:               " + (endCreateBSTTime - createBSTTime) + "ms -> " + (endCreateBSTTime - createBSTTime) / 1000f + " seconds");
        System.out.println("Create Backup Dictionary Time         " + (endCreateBackupDictionary - createBackupDictionary) + "ms -> " + (endCreateBackupDictionary - createBackupDictionary) / 1000f + " seconds");
        System.out.println("Load Input Text Time:                 " + (endLoadInputText - loadInputTextTime) + "ms -> " + (endLoadInputText - loadInputTextTime) / 1000f + " seconds");
        System.out.println("Create HashMap Time:                  " + (endLoadHashMap - loadHashMap) + "ms -> " + (endLoadHashMap - loadHashMap) / 1000f + " seconds");
        System.out.println("Load MinHeap Time:                    " + (endMakeMinHeapTime - makeMinHeapTime) + "ms -> " + (endMakeMinHeapTime - makeMinHeapTime) / 1000f + " seconds");
        System.out.println("Write Frequency Text File Time:       " + (endWriteFrequenciesTextFile - writeFrequenciesTextFile) + "ms -> " + (endWriteFrequenciesTextFile - writeFrequenciesTextFile) / 1000f + " seconds");
        System.out.println("Write Repeated Text File Time:        " + (endWriteRepeatedTextFile - writeRepeatedTextFile) + "ms -> " + (endWriteRepeatedTextFile - writeRepeatedTextFile) / 1000f + " seconds");
        System.out.println("Write Corrected Words Text File Time: " + (endWriteCorrectedWordsTextFile - writeCorrectedWordsTextFile) + "ms -> " + (endWriteCorrectedWordsTextFile - writeCorrectedWordsTextFile) / 1000f + " seconds");
        System.out.println("Total Execution Time:                 " + (endTime - startTime) + "ms -> " + (endTime - startTime) / 1000f + " seconds");
        System.out.println("Word Count:                           " + wordCount);

        if (true) {//this if statement is to measure the performance of the program. Set true if you want to record execution time
            try (PrintWriter performanceWriter = new PrintWriter(currentRelativePath.toAbsolutePath().toString() + "\\OutputFiles\\performance.csv", StandardCharsets.UTF_8)) {
                performanceWriter.println("Word Count:                           ," + wordCount);
                performanceWriter.println("Create Dictionary Time:               ," + (endCreateBSTTime - createBSTTime) + "ms -> " + (endCreateBSTTime - createBSTTime) / 1000f + " seconds");
                performanceWriter.println("Create Backup Dictionary Time         ," + (endCreateBackupDictionary - createBackupDictionary) + "ms -> " + (endCreateBackupDictionary - createBackupDictionary) / 1000f + " seconds");
                performanceWriter.println("Load Input Text Time:                 ," + (endLoadInputText - loadInputTextTime) + "ms -> " + (endLoadInputText - loadInputTextTime) / 1000f + " seconds");
                performanceWriter.println("Create HashMap Time:                  ," + (endLoadHashMap - loadHashMap) + "ms -> " + (endLoadHashMap - loadHashMap) / 1000f + " seconds");
                performanceWriter.println("Load MinHeap Time:                    ," + (endMakeMinHeapTime - makeMinHeapTime) + "ms -> " + (endMakeMinHeapTime - makeMinHeapTime) / 1000f + " seconds");
                performanceWriter.println("Write Frequency Text File Time:       ," + (endWriteFrequenciesTextFile - writeFrequenciesTextFile) + "ms -> " + (endWriteFrequenciesTextFile - writeFrequenciesTextFile) / 1000f + " seconds");
                performanceWriter.println("Write Repeated Text File Time:        ," + (endWriteRepeatedTextFile - writeRepeatedTextFile) + "ms -> " + (endWriteRepeatedTextFile - writeRepeatedTextFile) / 1000f + " seconds");
                performanceWriter.println("Write Corrected Words Text File Time: ," + (endWriteCorrectedWordsTextFile - writeCorrectedWordsTextFile) + "ms -> " + (endWriteCorrectedWordsTextFile - writeCorrectedWordsTextFile) / 1000f + " seconds");
                performanceWriter.println("Total Execution Time:                 ," + (endTime - startTime) + "ms -> " + (endTime - startTime) / 1000f + " seconds");

                performanceWriter.close();
            } catch (Exception e) {
                System.out.println("There is an issue with your performance.csv . Please ensure your file exists, can be opened, isn't already open," +
                        "and isn't null");
                System.out.println(e.getCause());
                System.exit(1);
            }

        }
    }

    void createRedBlackBST(MyRedBlackBST dictionary) throws IOException {

        final long startTime = System.currentTimeMillis(); //start time of timer used to test execution time
        final long endTime, startTime2, endTime2;

        //To see which method is faster, I will try using a buffer reader and split on each space to load the
        //dictionary. Then I'll use a scanner and see which is faster
        /****Start Buffered Reader Method ****/
        //make a buffer with the dictionary as the filename and an 8KB size (since all provided are 2KB)
        BufferedReader dictionaryBuffer = new BufferedReader(new FileReader("dictionary.txt"), 8192 * 4); //todo: change to this to test real dictionary
        //BufferedReader dictionaryBuffer = new BufferedReader(new FileReader("words_alpha.txt"), 8192 * 2048);
        //final long endTime = System.currentTimeMillis();
        String line;// = dictionaryBuffer.readLine();
        while ((line = dictionaryBuffer.readLine()) != null) {
            line = line.toLowerCase();
            String[] lineWords = line.split(" ");//split line into words when space detected
            for (String lineWord : lineWords) {
                dictionary.put(lineWord, 0); //key is word, value is zero since haven't read input text yet
                //System.out.println(lineWords[i]);
            }
        }
        dictionaryBuffer.close();
        //final long endTime = System.currentTimeMillis(); //test buffer load time into dictionary

        /****End Buffered Reader Method ****/

        //Scanner method not as good as buffered reader method
        /****Start Scanner Method****
         //Scanner dictionaryScanner = new Scanner(new File("dictionary.txt"));
         Scanner dictionaryScanner = new Scanner(new File("words_alpha.txt"));
         //final long endTime = System.currentTimeMillis(); //used to determine load time of scanner
         while (dictionaryScanner.hasNext()) {
         String line = dictionaryScanner.nextLine();
         String[] tmpString = line.split(" ");
         for (String s : tmpString) {
         dictionary.put(s, 0);
         //System.out.println(s);
         }

         }
         final long endTime = System.currentTimeMillis(); //user to determine time to load scanner and put into dictionary
         System.out.println("Time:" + (endTime - startTime) + "ms");
         ****End Scanner Method****/


        /**now that the dictionary is stored in a BST, we want to load a input file and search for the items
         * We established that the BufferedReader is faster than the scanner so we're going to use that for the input
         * Files as well
         */
        int lenthOfText = 0;
        BufferedReader inputTextBuffer = new BufferedReader(new FileReader("0.txt"), 104857600);
        while ((line = inputTextBuffer.readLine()) != null) {
            String[] lineWords = line.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
            //line = line.toLowerCase();
            //lenthOfText += line.length();
            //String[] lineWords = line.split(" ");
            for (String lineWord : lineWords) {
                if (dictionary.contains(lineWord)) {
                    // System.out.println("Lineword: " + lineWord);
                    // System.out.println("Value pre-increment: " + dictionary.get(lineWord));
                    dictionary.incrementValue(lineWord);
                    // System.out.println("Value post-increment: " + dictionary.get(lineWord));
                    //break;
                }

            }

        }
        endTime = System.currentTimeMillis();
        System.out.println("Time:" + (endTime - startTime) + "ms");
        System.out.println("BST Size: " + dictionary.size());
        System.out.println("Length of Text: " + lenthOfText);


        //todo:

        //print all items in tree
//        for (int i = 0; i < dictionary.size(); i ++){
//            System.out.println("Key: " + dictionary.select(i) + "\n"
//                             + "Value: " + dictionary.get(dictionary.select(i))+ "\n");
//        }

    }

    public static class sortingNode {
        private int frequency;
        private String word;
        private char firstLetterOfWord;

        sortingNode(int frequency, String word, char firstLetterOfWord) {
            this.firstLetterOfWord = firstLetterOfWord;
            this.frequency = frequency;
            this.word = word;
        }

        sortingNode() {
            this.firstLetterOfWord = 'z';
            this.frequency = 0;
            this.word = "";
        }

        public int getFrequency() {
            return frequency;
        }

        public void setFrequency(int frequency) {
            this.frequency = frequency;
        }

        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }

        public char getFirstLetterOfWord() {
            return firstLetterOfWord;
        }

        public void setFirstLetterOfWord(char firstLetterOfWord) {
            this.firstLetterOfWord = firstLetterOfWord;
        }
    }
}







