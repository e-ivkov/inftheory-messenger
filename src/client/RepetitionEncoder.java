package client;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Created by ekaterina on 11/8/17.
 */
public class RepetitionEncoder {

    /**
     * Takes the initial message and repeats every byte desired number of times
     * Writes result to result array and returns it
     * @param message to encode
     * @param rep number of times to repeat each byte
     */
    public static byte[] encode(byte[] message, int rep) {
        byte[] result = new byte[message.length * rep];
        int counter = 0;
        for (int i = 0; i < result.length; i += rep) {
            for (int j = 0; j < rep; j++) {
                result[i + j] = message[counter];
            }
            counter++;
        }

        return result;
    }


    /**
     * For every entity in array there is a map<byte, Integer> that holds how many times each byte is in the message,
     * Finds element with maximum frequency and wrote it to the result array
     * @param message to decode
     * @param rep how many times each symbol was repeated
     * @return initial message
     */
    public static byte[] decode(byte[] message, int rep) {
        byte[] result = new byte[message.length / rep];
        int messageCounter = 0;
        for (int i = 0; i < message.length / rep; i++) {
            Map<Byte, Integer> frequency = new HashMap<>();//byte, how many times
            for (int j = 0; j < rep; j++) {
                int repetitions = 0;
                if (frequency.get(message[messageCounter + j] + 1) != null) {
                    repetitions = frequency.get(message[messageCounter + j]);
                }
                frequency.put(message[messageCounter + j], repetitions + 1);
            }
            byte maxSymbol = 0;
            int maxSimilar = 0;
            for (byte symbol : frequency.keySet()) {
                if (maxSimilar < frequency.get(symbol)) {//if more times than before
                    maxSimilar = frequency.get(symbol);
                    maxSymbol = symbol;
                }
            }

            result[i] = maxSymbol;
            messageCounter += rep;
        }
        return result;
    }
}
