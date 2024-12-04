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

/**
 *  The {@code TextCompressor} class provides static methods for compressing
 *  and expanding natural language through textfile input.
 *
 *  @author Zach Blick, YOUR NAME HERE
 */
public class TextCompressor {

    private static void compress() {

        // TODO: Complete the compress() method
        // Need some way to determine a standard code length, could include space in the binary tree and not always include it
        // I'll experiment with code lengths, but start with 10.
        // Need to add a check that if the sequence doesn't end in a space, return, or any kind of punctuation to continue sequence with next set.

        BinaryStdOut.close();
    }

    private class BinaryTree() {
        private Node root;

        public BinaryTree() {
            this.root = new Node('s');
        }

        public BinaryTree(char letter) {
            this.root = new Node(letter);
        }

        private String getSequence(String input) {
            String sequence = "";
            Node current = this.root;
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


        // Build the input string into the binary tree and return the pathway
        private String buildSequence(String input) {
            for (int i = 0; i < input.length(); i++) {

            }
        }

    }

    private class Node() {
        private char letter;

        private Node child1;
        private Node child2;

        public Node(char letter) {
            this.letter = letter;
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



    private static void expand() {

        // TODO: Complete the expand() method

        BinaryStdOut.close();
    }

    public static void main(String[] args) {
        if      (args[0].equals("-")) compress();
        else if (args[0].equals("+")) expand();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}
