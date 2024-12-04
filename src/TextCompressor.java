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
        // Need some way to determine a standard code length, could include space in the binary tree and not always include it
        // I'll experiment with code lengths, but start with 10.
        // Need to add a check that if the sequence doesn't end in a space, return, or any kind of punctuation to continue sequence with next set.

        BinaryTree tree = new BinaryTree();

        String word = readWord();

        ArrayList<String> binSequence = new ArrayList<>();

        while (word != "") {
            binSequence.add(tree.buildSequence(word));
        }

        tree.compressTree();

        /**
         * Todo: add a way to separate the end of the binary tree and the beginning of the data
         *       add encoding of main data
         */

        BinaryStdOut.close();
    }

    public class Node() {
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

    public class BinaryTree() {
        private TextCompressor.Node root;

        public BinaryTree() {
            this.root = new TextCompressor.Node('s');
        }

        public BinaryTree(char letter) {
            this.root = new TextCompressor.Node(letter);
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
                        return sequence;
                    }
                }
                else {
                    sequence = sequence + "0";
                    next = current.getChild1();
                    if (i + 1 < input.length() && next == null) {
                        current.addChild1(input.charAt(i + 1));
                    }
                    else if (next == null) {
                        return sequence;
                    }
                    i--;
                }
                current = next;
            }
            return sequence;
        }
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

        // TODO: Complete the expand() method
        BinaryTree tree = new BinaryTree(1);


        BinaryStdOut.close();
    }

    public static void main(String[] args) {
        if      (args[0].equals("-")) compress();
        else if (args[0].equals("+")) expand();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}






