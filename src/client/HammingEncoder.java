package client;

import java.util.*;

class HammingEncoder {
    public static BitSet encode(byte bt) {
        BitSet in = BitSet.valueOf(new byte[] { bt });
        int[] a = new int[8];

        for(int i = 0; i < 8; i++) {
            a[i] = in.get(7 - i)?1:0;
        }

        int b[] = generateCode(a);
        BitSet res = new BitSet(16);
        for (int i = 0; i < 12; i++)
            res.set(i, b[i] == 1);
        res.set(15, true);
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
        byte[] res = new byte[in.length / 2];
        for (int i = 0; i < res.length; i++) {
            byte[] tmp = new byte[2];
            tmp[0] = in[i * 2];
            tmp[1] = in[i * 2 + 1];
            BitSet x = new BitSet().valueOf(tmp);
            res[i] = decode(x);
        }

        return res;
    }

    public static void makeErr(BitSet in){
        in.set(5, true);
    }

    static int[] generateCode(int a[]) {
        // We will return the array 'b'.
        int b[];

        // We find the number of parity bits required:
        int i = 0, parity_count = 4, j = 0, k = 0;

        // Length of 'b' is length of original data (a) + number of parity bits.
        b = new int[a.length + parity_count];

        // Initialize this array with '2' to indicate an 'unset' value in parity bit locations:

        for (i = 1; i <= b.length; i++) {
            if (Math.pow(2, j) == i) {
                // Found a parity bit location.
                // Adjusting with (-1) to account for array indices starting from 0 instead of 1.

                b[i - 1] = 2;
                j++;
            } else {
                b[k + j] = a[k++];
            }
        }
        for (i = 0; i < parity_count; i++) {
            // Setting even parity bits at parity bit locations:

            b[((int) Math.pow(2, i)) - 1] = getParity(b, i);
        }
        return b;
    }

    static int getParity(int b[], int power) {
        int parity = 0;
        for (int i = 0; i < b.length; i++) {
            if (b[i] != 2) {
                // If 'i' doesn't contain an unset value,
                // We will save that index value in k, increase it by 1,
                // Then we convert it into binary:

                int k = i + 1;
                String s = Integer.toBinaryString(k);

                // Now if the bit at the 2^(power) location of the binary value of index is 1,
                // Then we need to check the value stored at that location.
                // Checking if that value is 1 or 0, we will calculate the parity value.

                int x = ((Integer.parseInt(s)) / ((int) Math.pow(10, power))) % 10;
                if (x == 1) {
                    if (b[i] == 1) {
                        parity = (parity + 1) % 2;
                    }
                }
            }
        }
        return parity;
    }

    static byte decode(BitSet in) {
        int[] a = new int[12];
        for(int i = 0; i < 12; i++) {
            a[i] = in.get(i)?1:0;
        }

        int parity_count = 4;

        int power;
        // We shall use the value stored in 'power' to find the correct bits to check for parity.

        int parity[] = new int[parity_count];
        // 'parity' array will store the values of the parity checks.

        String syndrome = new String();
        // 'syndrome' string will be used to store the integer value of error location.

        for (power = 0; power < parity_count; power++) {
            // We need to check the parities, the same number of times as the number of parity bits added.

            for (int i = 0; i < a.length; i++) {
                // Extracting the bit from 2^(power):

                int k = i + 1;
                String s = Integer.toBinaryString(k);
                int bit = ((Integer.parseInt(s)) / ((int) Math.pow(10, power))) % 10;
                if (bit == 1) {
                    if (a[i] == 1) {
                        parity[power] = (parity[power] + 1) % 2;
                    }
                }
            }
            syndrome = parity[power] + syndrome;
        }
        // This gives us the parity check equation values.
        // Using these values, we will now check if there is a single bit error and then correct it.

        int error_location = Integer.parseInt(syndrome, 2);
        if(error_location != 0) {
            a[error_location-1] = (a[error_location-1]+1)%2;
        }

        // Finally, we shall extract the original data from the received (and corrected) code:
        power = parity_count-1;
        BitSet res = new BitSet(8);
        int k = 0;
        for(int i=a.length ; i > 0 ; i--) {
            if(Math.pow(2, power) != i) {
                res.set(k, a[i - 1] != 0);
                k++;
            }
            else {
                power--;
            }
        }
        byte[] ret = res.toByteArray();
        return ret.length > 0 ? ret[0] : 0;
    }

}