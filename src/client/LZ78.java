package client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alexander on 15.11.2017.
 */
public class LZ78 {

    private static int getBit(byte[] byteArray, long pos) {
        int value = byteArray[(int) (pos / 8)] + 128;
        return (value >> (7 - pos % 8)) & 1;
    }

    private static void setBit(byte[] byteArray, long position, int v) {
        int value = byteArray[(int) position / 8] + 128;
        if (getBit(byteArray, position) == 1) {
            value = value ^ (1 << (7 - position % 8));
        }
        byteArray[(int) (position / 8)] = (byte) ((value | (v << (7 - position % 8))) - 128);
    }

    private static long bitsToInt(ArrayList<Integer> bits) {
        long value = 0;
        for (int i = 0; i < bits.size(); i++) {
            value += (bits.get(i) << (bits.size() - i - 1));
        }
        return value;
    }

    private static ArrayList intToBits(long value) {
        ArrayList<Integer> bits = new ArrayList<>();
        do {
            bits.add(0, (int) (value & 1));
            value = value >> 1;
        } while (value > 0);

        return bits;
    }

    public static byte[] decompress(byte[] byteArray) {
        int nodesCnt = 0;
        Node root = new Node(nodesCnt++);
        Map<Long, Node> numToNode = new HashMap<>();
        numToNode.put(root.getNum(), root);
        ArrayList<Integer> decodedAtBits = new ArrayList<>();

        long pos = 8;
        long bitsCnt = 8 * byteArray.length;

        int dontUse = byteArray[0] + 128;

        while (pos < bitsCnt - dontUse) {
            int numLen = (int) Math.ceil(Math.log(nodesCnt) / Math.log(2));
            ArrayList<Integer> numAsBits = new ArrayList<>();
            for (int i = 0; i < numLen; i++) {
                numAsBits.add(getBit(byteArray, pos));
                pos++;
            }
            long nodeNum = bitsToInt(numAsBits);
            Node curNode = numToNode.get(nodeNum);
            decodedAtBits.addAll(curNode.getBits());
            if (pos < bitsCnt - dontUse) {
                decodedAtBits.add(getBit(byteArray, pos));
                curNode.put(getBit(byteArray, pos), nodesCnt++);
                curNode = curNode.getCh(getBit(byteArray, pos));
                numToNode.put(curNode.getNum(), curNode);
            }
            pos++;
        }

        byte[] decoded = new byte[(decodedAtBits.size() + 7) / 8];
        for (int i = 0; i < decodedAtBits.size(); i++) {
            setBit(decoded, i, decodedAtBits.get(i));
        }
        return decoded;
    }

    public static byte[] compress(byte[] byteArray) {
        int nodesCnt = 0;
        Node root = new Node(nodesCnt++);
        ArrayList<Integer> comp = new ArrayList<>();
        long counter = 0;

        final long pLength = byteArray.length * 8;

        while (counter < pLength) {
            Node current = root;
            if (counter == 0) {
                comp.add(getBit(byteArray, counter));
                current.put(getBit(byteArray, counter), nodesCnt++);
                counter++;
            }

            while (counter < pLength && current.hasCh(getBit(byteArray, counter))) {
                current = current.getCh(getBit(byteArray, counter));
                counter++;
            }

            ArrayList<Integer> curNodeNumAsBits = intToBits(current.getNum());
            int numLen = (int) Math.ceil(Math.log(nodesCnt) / Math.log(2));
            while (curNodeNumAsBits.size() < numLen) {
                curNodeNumAsBits.add(0, 0);
            }

            comp.addAll(curNodeNumAsBits);
            if (counter < pLength) {
                comp.add(getBit(byteArray, counter));
                current.put(getBit(byteArray, counter), nodesCnt++);
            }
            counter++;
        }

        byte[] compressedAsBytes = new byte[(comp.size() + 7) / 8 + 1];
        int err = (compressedAsBytes.length - 1) * 8 - comp.size();
        comp.addAll(0, intToBits(err));
        for (int i = 0; i < 8 - intToBits(err).size(); i++) {
            comp.add(0, 0);
        }

        for (int i = 0; i < comp.size(); i++) {
            setBit(compressedAsBytes, i, comp.get(i));
        }
        return compressedAsBytes;
    }


}
