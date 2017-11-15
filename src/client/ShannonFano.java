package client;
import java.lang.reflect.Array;
import java.util.*;

public class ShannonFano
{
    private static class Symbol {
        char symbol;
        int frequency;

        Symbol(char symbol, int frequency) {
            this.symbol = symbol;
            this.frequency = frequency;
        }
    }
    public static byte[] compress(byte[] input2)
    {
        String input = toString(input2);
        HashMap<Character, Integer> frequency = new HashMap<>();
        for (char symbol:input.toCharArray())
        {
            frequency.merge(symbol, 1, Integer::sum);
        }
        Symbol[] symbols = new Symbol[frequency.size()];
        int symbols_number = 0;
        for (Character symbol:frequency.keySet())
        {
            symbols[symbols_number++] = new Symbol(symbol, frequency.get(symbol));
        }
        Arrays.sort(symbols, (a, b) -> Integer.compare(-a.frequency, -b.frequency));
        HashMap<Character, String> codes = new HashMap<>();
        Stack<int[]> bounds = new Stack<>();
        bounds.push(new int[]{0, frequency.size() - 1});
        while(!bounds.empty())
        {
            int lowerBound = bounds.peek()[0];
            int upperBound = bounds.peek()[1];
            int left = lowerBound;
            int right = upperBound;
            int leftSum = 0;
            int rightSum = 0;
            bounds.pop();
            while (left <= right)
            {
                if(leftSum < rightSum)
                {
                    leftSum+=symbols[left].frequency;
                    codes.merge(symbols[left].symbol, "0", String::concat);
                    left++;
                }else{
                    rightSum+=symbols[right].frequency;
                    codes.merge(symbols[right].symbol,"1", String::concat);
                    right--;
                }
            }
            if(right + 1 < upperBound)
            {
                bounds.push(new int[]{right + 1, upperBound});
            }
            if(left - 1 > lowerBound)
            {
                bounds.push(new int[]{lowerBound, left - 1});
            }
        }
        String output = Integer.toString(codes.size());
        output+=" ";
        for (HashMap.Entry<Character, String> entry: codes.entrySet())
        {
            output = output.concat(entry.getValue() + " " +entry.getKey() + " ");
        }
        String output2 = "";
        for (Character symbol:input.toCharArray())
        {
            output2 = output2.concat(codes.get(symbol));
        }
        byte remainder = (byte)(output2.length()%8);
        for (int i = 0; i < 8 - remainder; i++)
        {
            output2 += '0';
        }
        output += toChar(toString((char)(8-remainder)));
        for (int i = 0; i < output2.length(); i += 8)
        {
            output += toChar(output2.substring(i,i+8));
        }
        return toByteArray(output);
    }

    public static byte[] decompress(byte[] input2)
    {
        String input = toString(input2);
        int cursor = 0;
        StringBuilder alphabetSize = new StringBuilder();
        while (Character.isDigit(input.charAt(cursor)))
        {
            alphabetSize.append(input.charAt(cursor++));
        }
        int asize = Integer.parseInt(alphabetSize.toString());
        HashMap<String, Character> codes = new HashMap<>();
        for(int i = 0; i < asize; i++)
        {
            cursor++;
            StringBuilder code = new StringBuilder();
            while (Character.isDigit(input.charAt(cursor)))
            {
                code.append(input.charAt(cursor++));
            }
            cursor++;
            codes.put(code.toString(), input.charAt(cursor++));
        }
        String need = input.substring(cursor+1);
        String out2 = "";
        String rem = toString(need.charAt(0));
        int remainder = 0;
        for (int i = 0; i < 8; i++)
        {
            remainder *= 2;
            remainder += (rem.charAt(i) == '1' ? 1 : 0);
        }
        for (int i = 1; i < need.length(); i++)
        {
            out2= out2.concat(toString(need.charAt(i)));
        }
        int tmp = remainder-128;
        out2 = out2.substring(0,out2.length()-tmp);

        StringBuilder output = new StringBuilder();
        StringBuilder nextWord = new StringBuilder();
        input = out2;
        int inputSize = input.length();
        cursor = 0;
        while (cursor < inputSize)
        {
            nextWord.append(input.charAt(cursor++));
            if(codes.containsKey(nextWord.toString()))
            {
                output.append(codes.get(nextWord.toString()));
                nextWord = new StringBuilder();
            }
        }
        String tmp2 = output.toString();
        return (toByteArray(tmp2));
    }
    public static byte[] toByteArray(String s) {
        byte[] bytes = new byte[s.length()];
        for (int i = 0; i < s.length(); i++) {
            bytes[i] = (byte)s.charAt(i);
        }
        return bytes;
    }

    public static String toString(byte[] bytes) {
        char[] chars = new char[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            chars[i] = (char)bytes[i];
        }
        return new String(chars);
    }
    public static char toChar(String s)
    {
        byte ret = 0;
        for (int i = 0; i < 8; i++)
        {
            ret *= 2;
            ret += (s.charAt(i) == '1' ? 1 : 0);
        }
        return (char)(ret+128);
    }
    public static String toString (char q)
    {
        byte remain = (byte) q;
        int remainder = (int)remain + 128;
        String rem = "";
        while (remainder > 0) {
            int v = remainder%2;
            rem = (v == 1 ? '1' : '0') + rem;
            remainder /= 2;
        }
        while (rem.length() < 8) {
            rem = '0' + rem;
        }
        return rem;
    }
}

