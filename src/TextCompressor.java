/******************************************************************************
 *  Compilation:  javac TextCompressor.java
 *  Execution:    java TextCompressor - < input.txt   (compress)
 *  Execution:    java TextCompressor + < input.txt   (expand)
 *  Dependencies: BinaryIn.java BinaryOut.java
 *  Data files:   abra.txt
 *                jabberwocky.txt
 *                shakespeare.txt
 *                virus.txt
 *
 *  % java DumpBinary 0 < abra.txt
 *  136 bits
 *
 *  % java TextCompressor - < abra.txt | java DumpBinary 0
 *  104 bits    (when using 8-bit codes)
 *
 *  % java DumpBinary 0 < alice.txt
 *  1104064 bits
 *  % java TextCompressor - < alice.txt | java DumpBinary 0
 *  480760 bits
 *  = 43.54% compression ratio!
 ******************************************************************************/

import java.util.ArrayList;

/**
 *  The {@code TextCompressor} class provides static methods for compressing
 *  and expanding natural language through textfile input.
 *
 *  @author Zach Blick, YOUR NAME HERE
 */
public class TextCompressor {

    private void compress() {
        TST codes = new TST();
        int codeLength = 12;
        int currentCode = 257;
        int maxCodes = 1;
        for (int i = 0; i < codeLength; i++) {
            maxCodes *= 2;
        }
        String sequence = BinaryStdIn.readString();
        codes.insert((char)127 + "", 127);
        BinaryStdOut.write(codeLength);


        // For every sequence, get the longest matching prefix
        // If there is no longest matching prefix, add the current char to the TST
        // Write the code of the current prefix to the binary file
        // Find the next longest matching prefix and add the first letter to create a new code
        // Add that code to the TST and move on
        // Add the delete key as a prefix immediately and use it to check if the end of the file is reached

        int i = 0;
        String current = "";
        String newSequence = "";
        while (true) {
            current = codes.getLongestPrefix(sequence, i);
            if (current.isEmpty()) {
                char character = sequence.charAt(i);
                codes.insert((char)character +"", character);
                current += character;
            }
            BinaryStdOut.write(codes.lookup(current), codeLength);
            i += current.length();

            // Todo: add a check for end of string
            newSequence = codes.getLongestPrefix(sequence, i);

            // Shouldn't need to check if it's below 257 as no other condition exists
            if (newSequence.isEmpty()) {
                newSequence = sequence.charAt(i) + "";
                codes.insert(newSequence, sequence.charAt(i));
            }
            if (newSequence.length() == 1 && newSequence.equals((char)127 + "")) {
                BinaryStdOut.close();
                return;
            }
            if (currentCode < maxCodes) {
                newSequence = current + newSequence.charAt(0);

                codes.insert(newSequence, currentCode);
            }
        }

//        // Need some way to determine a standard code length, could include space in the binary tree and not always include it
//        // I'll experiment with code lengths, but start with 10.
//        // Need to add a check that if the sequence doesn't end in a space, return, or any kind of punctuation to continue sequence with next set.
//
//        BinaryTree tree = new BinaryTree();
//
//        String word = readWord();
//
//        ArrayList<String> binSequence = new ArrayList<>();
//
//        while (word != "") {
//            binSequence.add(tree.buildSequence(word));
//        }
//
//        BinaryStdOut.write(binSequence.size());
//
//        tree.compressTree();
//
//        for (int i = 0; i < binSequence.size(); i++) {
//            String sequence = tree.getSequence(binSequence.get(i));
//            for (int j = 0; j < sequence.length(); j++) {
//                BinaryStdOut.write(sequence.charAt(i) == '1');
//            }
//        }
//
//        BinaryStdOut.close();
    }

    public static class Node {
        private char letter;

        // Child 1 is rejected
        private Node child1;

        // Child 2 is accepted letter
        private Node child2;

        public Node(char letter) {
            this.letter = letter;
        }

        public Node(int i) {
            boolean hasChild1 = BinaryStdIn.readBoolean();
            boolean hasChild2 = BinaryStdIn.readBoolean();
            this.letter = BinaryStdIn.readChar();
            if (hasChild1) {
                child1 = new Node(1);
            }
            if (hasChild2) {
                child2 = new Node(1);
            }
        }

        // Node compressed data goes has child1, has child2, own letter, child1 data, child2 data
        public void compressNode() {
            String sequence = "";
            if (child1 != null) {
                BinaryStdOut.write(true);
            }
            else {
                BinaryStdOut.write(false);
            }
            if (child2 != null) {
                BinaryStdOut.write(true);
            }
            else {
                BinaryStdOut.write(false);
            }

            BinaryStdOut.write(letter);
            if (child1 != null) {
                child1.compressNode();
            }
            if (child2 != null) {
                child2.compressNode();
            }
        }

        public void addChild1(char letter) {
            this.child1 = new Node(letter);
        }

        public void addChild2(char letter) {
            this.child2 = new Node(letter);
        }

        public Node getChild1() {
            return child1;
        }

        public Node getChild2() {
            return child2;
        }

        public char getLetter() {
            return this.letter;
        }
    }

    public class BinaryTree {
        private TextCompressor.Node root;

        public BinaryTree() {
            this.root = new Node('s');
        }

        public BinaryTree(char letter) {
            this.root = new Node(letter);
        }

        public BinaryTree(int i) {
            this.root = new Node(1);
        }

        private String getSequence(String input) {
            String sequence = "";
            TextCompressor.Node current = this.root;
            for (int i = 0; i < input.length(); i++) {

                // 0 is reject character
                if (input.charAt(i) == '0') {
                    current = root.getChild1();
                }
                else {
                    sequence = sequence + current.getLetter();
                    current = current.getChild2();
                }
                if (current == null) {
                    return sequence;
                }
            }
            return sequence;
        }

        private void compressTree() {
            root.compressNode();
        }

        // Build the input string into the binary tree and return the pathway
        // Todo ensure the main method adds periods/spaces to the sequence
        private String buildSequence(String input) {
            TextCompressor.Node current = this.root;
            TextCompressor.Node next;
            String sequence = "";
            for (int i = 0; i < input.length(); i++) {
                if (current.getLetter() == input.charAt(i)) {
                    sequence = sequence + "1";
                    next = current.getChild2();
                    if (i + 1 < input.length() && next == null) {
                        current.addChild2(input.charAt(i + 1));
                    }
                    else if (next == null) {
                        break;
                    }
                }
                else {
                    sequence = sequence + "0";
                    next = current.getChild1();
                    if (i + 1 < input.length() && next == null) {
                        current.addChild1(input.charAt(i + 1));
                    }
                    else if (next == null) {
                        break;
                    }
                    i--;
                }
                current = next;
            }

            return padSequence(sequence, 10);
        }
    }
    private static String padSequence(String sequence, int divisor) {
        while (sequence.length() % divisor != 0) {
            sequence = sequence + "0";
        }
        return sequence;
    }

    private static String readWord() {
        String sequence = "";
        char letter = BinaryStdIn.readChar();
        while (letter != ' ' && !BinaryStdIn.isEmpty()) {
            letter = BinaryStdIn.readChar();
            sequence = sequence + letter;
        }
        return sequence;
    }


    private void expand() {

        TST codes = new TST();
        int codeLength = BinaryStdIn.readInt();
        String sequence = "";
        int numCodes = 1;
        for (int i = 0; i < codeLength; i++) {
            numCodes *= 2;
        }
        int codeNum = 257;
        String[] codePairs = new String[numCodes];

        // Get the current code and convert it to a string through looking it up.
        // If the code returns nothing and is below 256, add the char value and add the char to the sequence
        // If the code still doesn't exist, add the first char to the sequence
        // Look to the next code and add the current string + the first char as a code
        // Instant return if code 127 is encountered

        int currentCode = BinaryStdIn.readInt(codeLength);
        int lookAhead = -1;
        String forwardString;
        while (true) {
            if (codePairs[currentCode] == null && currentCode < 257) {
                codePairs[currentCode] = (char)currentCode + "";
            }
            BinaryStdOut.write(codePairs[currentCode]);
            lookAhead = BinaryStdIn.readInt(codeLength);
            if (codePairs[lookAhead] == null) {
                if (lookAhead < 257) {
                    forwardString = codePairs[currentCode] + (char)lookAhead;
                }
                else {
                    forwardString = codePairs[currentCode] + codePairs[currentCode].charAt(0);
                }
            }
            else if (lookAhead == 127) {
                BinaryStdOut.close();
                return;
            }
            else {
                forwardString = codePairs[currentCode] + codePairs[lookAhead].charAt(0);
            }
            codes.insert(forwardString, codeNum);
            codeNum++;
            currentCode = lookAhead;
        }






//        int numWords = BinaryStdIn.readInt();
//        // TODO: Complete the expand() method
//        BinaryTree tree = new BinaryTree(1);
//        String sequence;
//
//        for (int i = 0; i < numWords; i++) {
//            while (true) {
//                String binary = getSequence(10);
//                sequence = tree.getSequence(binary);
//                if (sequence.charAt(sequence.length() - 1) == ' ') {
//                    break;
//                }
//            }
//            BinaryStdOut.write(sequence);
//        }
//
    }

    private String getSequence(int sequenceLength) {
        String sequence = "";
        for (int i = 0; i < sequenceLength; i++) {
            if (BinaryStdIn.readBoolean()) {
                sequence = sequence + "1";
            }
            else {
                sequence = sequence + "0";
            }
        }
        return sequence;
    }

    public static void main(String[] args) {

        TextCompressor compress = new TextCompressor();

        if      (args[0].equals("-")) compress.compress();
        else if (args[0].equals("+")) compress.expand();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}