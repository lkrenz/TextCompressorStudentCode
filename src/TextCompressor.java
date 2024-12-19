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
 *  @author Zach Blick, Liam Krenz
 */
public class TextCompressor {

    // Compresses an inputted string using LZW compression
    private void compress() {
        TST codes = instantiateTST();

        // Variables for compression
        int codeLength = 12;
        int currentCode = 257;
        int maxCodes = 1 << codeLength;

        // Loads full file into string
        String sequence = BinaryStdIn.readString();

        // Writes in the code length used
        BinaryStdOut.write(codeLength);

        int i = 0;
        String current;
        String newSequence;

        // Iterates through the sequence using LZW compression
        while (i < sequence.length()) {

            // Finds the longest matching prefix for the current char
            current = codes.getLongestPrefix(sequence, i);

            // If there is no prefix, add the char to codes
            // While chars are already added, this handles possible special characters
            if (current.isEmpty()) {
                char character = sequence.charAt(i);
                codes.insert(character + "", character);
                current = character + "";
            }

            // Writes the current code in
            BinaryStdOut.write(codes.lookup(current), codeLength);

            // Iterates to the next sequence
            i += current.length();

            // Breaks if end of file is reached
            if (i >= sequence.length()) {
                break;
            }

            // Finds and adds new code
            newSequence = codes.getLongestPrefix(sequence, i);

            // Handles edge case by adding first char to start of sequence
            if (newSequence.isEmpty()) {
                newSequence = sequence.charAt(i) + "";
                codes.insert(newSequence, sequence.charAt(i));
            }

            // Adds new code if there is space
            if (currentCode < maxCodes) {
                codes.insert(current + newSequence.charAt(0), currentCode);
                currentCode++;
            }
        }
        BinaryStdOut.close();
    }

    // Expands compressed binary file using LZW
    private void expand() {

        // Variables and objects used in expansion
        int codeLength = BinaryStdIn.readInt();
        int maxCodes = 1 << codeLength;
        String[] codePairs = new String[maxCodes];

        // Adds char codes into codePairs
        for (int i = 0; i < 256; i++) {
            codePairs[i] = i + "";
        }

        // Reads in initial values
        int currentCode = BinaryStdIn.readInt(codeLength);
        String currentString = codePairs[currentCode];

        // Writes out first code
        BinaryStdOut.write(currentString);

        int codeNum = 257;

        // Iterates until the file is empty
        while (!BinaryStdIn.isEmpty()) {

            // Reads next code
            int lookAhead = BinaryStdIn.readInt(codeLength);

            String forwardString;

            // If the lookAhead code exists, the new string is that code
            if (codePairs[lookAhead] != null) {
                forwardString = codePairs[lookAhead];
            }
            else {
                // This handles the edge case by adding the first char back and making the new code
                forwardString = currentString + currentString.charAt(0);
            }

            // Writes the new code and if there are codes left, adds the new code in
            BinaryStdOut.write(forwardString);
            if (codeNum < maxCodes) {
                codePairs[codeNum] = currentString + forwardString.charAt(0);
                codeNum++;
            }
            currentString = forwardString;
        }
        BinaryStdOut.close();
    }

    // Instantiates a new TST with char values
    public TST instantiateTST() {
        TST codes = new TST();
        for (int i = 0; i < 256; i++) {
            codes.insert(Character.toString((char) i), i);
        }
        return codes;
    }

    public static void main(String[] args) {

        TextCompressor compress = new TextCompressor();

        if      (args[0].equals("-")) compress.compress();
        else if (args[0].equals("+")) compress.expand();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}