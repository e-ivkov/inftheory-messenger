package client;
 import com.sun.org.apache.xpath.internal.SourceTree;

import java.util.*;

class ParityEncoder {
    public static BitSet encode(byte bt) {
        BitSet in = BitSet.valueOf(new byte[] { bt });
        BitSet res = new BitSet(15);
        boolean an = false;
        for (int i = 0; i < 8; i++) {
            res.set(i, in.get(i));
            an ^= in.get(i);
        }
        res.set(8, an);
        res.set(12, true);
        return res;
    }

    public static byte[] encodeArr(byte[] in){
        byte[] res = new byte[in.length * 2];
        for (int i = 0; i < in.length; i++) {
            BitSet tmp = encode(in[i]);
            byte[] x = tmp.toByteArray();
            res[i * 2] = x[0];
            res[i * 2 + 1] = x[1];
        }

        return res;
    }

    public static byte[] decodeArr(byte[] in){
        int numErr = 0, maxErr = 10;
        byte[] res = new byte[in.length / 2];
        for (int i = 0; i < res.length; i++) {
            byte[] tmp = new byte[2];
            tmp[0] = in[i * 2];
            tmp[1] = in[i * 2 + 1];
            BitSet x = new BitSet().valueOf(tmp);
            res[i] = tmp[0];
            if(!check(x)){
                numErr++;
            }
        }
        if(numErr < maxErr)
            return res;
        else
            return null;
    }

    public static void makeErr(BitSet in){
        in.set(5, true);
    }

    static boolean check(BitSet in) {
        boolean res = false;
        for(int i = 0; i < 8; i++) {
            res ^= in.get(i);
        }
        return res == in.get(8);
    }

}