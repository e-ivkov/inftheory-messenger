package client;

import java.io.*;
import java.util.PriorityQueue;

public class Huffman {
    private static final int AlphabetSize = 256; //ASCII alphabet size

    private Huffman() {
    }//Just default constructor

    public static byte[] compress(byte[] bytes) {
        //Reads a sequence of 8-bit bytes from standard input; compresses them
        //using Huffman codes with an 8-bit alphabet; and writes the results
        //to standard output.
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        byte[] input = bytes;

        int[] freq = new int[AlphabetSize];
        for (int i = 0; i < input.length; i++) {
            //System.out.println("Round " + i);
            System.out.println(input[i]+128);
            freq[input[i]+128]++;
            //System.out.println(freq[input[i]]);
        }
        Node root = buildTree(freq);// building Huffman tree
        String[] st = new String[AlphabetSize];// build code table
        buildCode(st, root, "");
        try {
            writeTree(root, dos);// writing tree for decoder

            // print number of bytes in original uncompressed message

            dos.writeInt(input.length);

            for (int i = 0; i < input.length; i++) {// using Huffman algorithm code to encode the given input
                String code = st[input[i]+128];
                for (int j = 0; j < code.length(); j++) {
                    if (code.charAt(j) == '0') {
                        dos.writeBoolean(false);
                    } else if (code.charAt(j) == '1') {
                        dos.writeBoolean(true);
                    } else throw new IllegalStateException("Inappropriate status (state)");
                }
            }
            dos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }

    private static Node buildTree(int[] freq) {// the creation of the the Huffman tree with given frequencies
        PriorityQueue<Node> pq = new PriorityQueue<Node>();// creating priority queue with singleton trees
        for (int i = 0; i < AlphabetSize; i++)
            if (freq[i] > 0)
                pq.add(new Node((byte)(i-128), freq[i], null, null));

        if (pq.size() == 1) {// in case there is only one character with a nonzero frequency
            if (freq[0] == 0) pq.add(new Node((byte)0, 0, null, null));
            else pq.add(new Node((byte)1, 0, null, null));
        }
        while (pq.size() > 1) {//finding and merging 2 smallest trees
            Node left = pq.remove();
            Node right = pq.remove();
            Node parent = new Node((byte)0, left.freq + right.freq, left, right);
            pq.add(parent);
        }
        return pq.remove();
    }

    private static void buildCode(String[] st, Node x, String s) {//creating a table with symbols and their encodings
        if (!x.isLeaf()) {
            buildCode(st, x.left, s + '0');
            buildCode(st, x.right, s + '1');
        } else {
            st[x.b + 128] = s;
        }
    }

    private static void writeTree(Node x, DataOutputStream dos) throws IOException {//writing the bitstring tree to usual output file
        if (x.isLeaf()) {
            dos.writeBoolean(true);
            dos.writeByte(x.b + 128);
            return;
        }
        dos.writeBoolean(false);
        writeTree(x.left, dos);
        writeTree(x.right, dos);
    }

    public static byte[] decompress(byte[] compressed) {
        //Reads a sequence of bits that represents a Huffman-compressed message from
        //standard input; expands them; and writes the results to standard output.

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(compressed));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            Node root = readTree(dis);// reading from input in Huffman tree
            int length = dis.readInt();//amount of bytes, that we need to write

            for (int i = 0; i < length; i++) { // decode using the Huffman tree
                Node x = root;
                while (!x.isLeaf()) {
                    boolean bit = dis.readBoolean();
                    if (bit) x = x.right;
                    else x = x.left;
                }
                baos.write(x.b + 128);
            }
            dis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }

    private static Node readTree(DataInputStream dis) throws IOException {
        boolean isLeaf = dis.readBoolean();
        if (isLeaf) {
            return new Node(dis.readByte(), -1, null, null);
        } else {
            return new Node((byte)0, -1, readTree(dis), readTree(dis));
        }
    }

    private static class Node implements Comparable<Node> {// Node of the tree in Huffman algorithm
        private final byte b;
        private final int freq;
        private final Node left, right;

        Node(byte b, int freq, Node left, Node right) {
            this.b = b;
            this.freq = freq;
            this.left = left;
            this.right = right;
        }

        private boolean isLeaf() {//Is this node a leaf node or not
            assert ((left == null) && (right == null)) || ((left != null) && (right != null));
            return (left == null) && (right == null);
        }

        public int compareTo(Node that) {
            return this.freq - that.freq;
        } //returning the differences between 2 nodes
    }


}