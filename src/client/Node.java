package client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alexander on 16.11.2017.
 */
public class Node {
        private Map<Integer, Node> map = new HashMap<>();
        private int b;
        private Node pBit;
        private long nodeNum = 0;

        Node(int num) {
            this.pBit = null;
            this.nodeNum = num;
        }

        private Node(Node par, int b, int num) {
            this.pBit = par;
            this.b = b;
            this.nodeNum = num;
        }

        public void put(int b, int num) {
            map.put(b, new Node(this, b, num));
        }

        public Node getCh(int b) {
            return map.get(b);
        }

        public boolean hasCh(int b) {
            return map.containsKey(b);
        }

        public long getNum() {
            return this.nodeNum;
        }

        public int getB() {
            return this.b;
        }

        public ArrayList getBits() {
            ArrayList<Integer> bits = new ArrayList<>();
            Node curNode = this;
            while (curNode.pBit != null) {
                bits.add(0, curNode.getB());
                curNode = curNode.pBit;
            }
            return bits;
        }
    }

