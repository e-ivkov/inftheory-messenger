package client;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Created by ekaterina on 11/8/17.
 */
public class RepetitionEncoder {

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


    public static byte[] decode(byte[] message, int rep) {
        byte[] result = new byte[message.length / rep];
        int messageCounter = 0;
        for (int i = 0; i < message.length / rep; i++) {
            int priority;
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
