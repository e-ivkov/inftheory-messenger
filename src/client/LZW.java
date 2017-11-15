package client;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;

public class LZW {
    private static final int L = 4096;
    private static final int W = 12;
    private static String ans = new String();
    private static int counter = 0;
    private static HashMap<Integer, String> compressTable = new HashMap<Integer, String>();
    private static HashMap<String, Integer> compressTable2 = new HashMap<>();
    private static HashMap<Integer, String> decompressTable = new HashMap<Integer, String>();
    private static HashMap<String, Integer> decompressTable2 = new HashMap<>();
    private static Set<Map.Entry<Integer, String>> entrySet = compressTable.entrySet();
    private static int buffer;
    private static int NumberOfRemainingBits;

/*    private Integer getKey(String in) {
        String desiredObject = new String(in);
        for (Map.Entry<Integer, String> pair : entrySet) {
            if (desiredObject.equals(pair.getValue())) {
                return pair.getKey();
            }
        }
        return null;
    } */

    public void makeTable(HashMap table1, HashMap table2) {
        for (counter = 0; counter < 256; counter++) {
            table1.put(counter, String.valueOf(counter));
            table2.put(String.valueOf(counter), counter);
        }
    }

    public void write() {
        System.out.println(compressTable);
        System.out.println(compressTable2);
    }

    public byte[] mkByte(FileInputStream in) throws IOException {
        int count = 0;
        byte[] res = new byte[count];
        while (in.available() > 0) {
            res = Arrays.copyOf(res, res.length + 1);
            int i = in.read();
            byte b = (byte) i;
            res[count] = b;
            count++;
        }
        return res;
    }

    public Integer findInteger(String in, HashMap<String, Integer> table) {
        return table.get(in);
    }

    public String findStr(Integer in, HashMap<Integer, String> table) {
        return table.get(in);
    }

    public String firstInt(String in) {
        String res;
        try{
            res = in.substring(0, 3);}
        catch (StringIndexOutOfBoundsException err){
            try {
                res = in.substring(0, 2);
            }
            catch (StringIndexOutOfBoundsException err2){
                res = in.substring(0,1);
            }

        }
        return res;
    }


    public byte[] compress(byte[] by) throws IOException {
        int pastInt, currentInt;
        int point = 0;
        String past, current;
        String pC;
        byte[] res = new byte[by.length * 4];

        FileOutputStream mk = new FileOutputStream("temp.bin");
        for (int i = 0; i < by.length; i++) {
            mk.write(by[i]);
        }
        mk.close();
        FileInputStream in = new FileInputStream("temp.bin");
        makeTable(compressTable, compressTable2);
        pastInt = in.read();
        currentInt = in.read();
        past = String.valueOf(pastInt);
        current = String.valueOf(currentInt);
        do {
            pC = past + " " + current;
            if (!compressTable.containsValue(pC)) {
                if (counter < L) {
                    compressTable.put(counter, pC);
                    compressTable2.put(pC, counter);
                }

                byte[] bytes = ByteBuffer.allocate(4).putInt(Integer.valueOf(findInteger(past, compressTable2))).array();
                for (byte b : bytes) {
                    res[point] = b;
                    point++;
                }

                counter++;

//                out.write(Integer.valueOf(find(past)));
                pastInt = currentInt;
                past = String.valueOf(pastInt);
                currentInt = in.read();
                current = String.valueOf(currentInt);
            } else {
                past = pC;
                currentInt = in.read();
                current = String.valueOf(currentInt);
            }
        } while (pastInt >= 0);
        System.out.println(ans);
        return res;
    }

    public byte[] decompress(byte[] info) throws IOException {
        //VARIABLES
        Integer pastInt, currentInt;
        String pastWord, currentWord;
        String past, current, pC;
        Integer count = 0;
        int k = 0;
        int point = 0;
        byte[] res = new byte[info.length];
        currentInt = null;
        //INITIALIZATION
        makeTable(decompressTable, decompressTable2);

        k = 0;
        int it = ((info[k] & 0xFF) << 24) | ((info[k + 1] & 0xFF) << 16)
                | ((info[k + 2] & 0xFF) << 8) | (info[k + 3] & 0xFF);
        currentInt = it;
        currentWord = findStr(it, decompressTable);
        String strArr[] = currentWord.split(" ");
        int numArr[] = new int[strArr.length];
        for (int i = 0; i < strArr.length; i++) {
            byte[] bytes = ByteBuffer.allocate(4).putInt(Integer.valueOf(strArr[i])).array();
            for (byte b : bytes) {
                res[point] = b;
                point++;
            }
        }

        for (int j = 1; j < info.length; j++) {
            pastInt = currentInt;
            pastWord = currentWord;
            k = j * 4;
            currentInt = ((info[k] & 0xFF) << 24) | ((info[k + 1] & 0xFF) << 16)
                    | ((info[k + 2] & 0xFF) << 8) | (info[k + 3] & 0xFF);
            currentWord = findStr(currentInt, decompressTable);
            String strArr1[] = currentWord.split(" ");
            int numArr1[] = new int[strArr1.length];
            for (int i = 0; i < strArr1.length; i++) {
                byte[] bytes = ByteBuffer.allocate(4).putInt(Integer.valueOf(strArr1[i])).array();
                for (byte b : bytes) {
                    res[point] = b;
                    point++;
                }
            }
            if(j < L) {
                past = pastWord;
                current = firstInt(currentWord);
                pC = past + " " + current;
                decompressTable.put(j, pC);
            }
        }
        return res;
    }
}