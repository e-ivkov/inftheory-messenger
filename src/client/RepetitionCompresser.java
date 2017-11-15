package client;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RepetitionCompresser {

    public RepetitionCompresser() {
    }

    /**
     * creates array of RGB values of image
     *
     * @param path to the image file
     * @return array of RGB values of each pixel, first two elements of array - width and height
     * @throws InterruptedException
     */
    public int[] readPixels(String path) throws InterruptedException, IOException {
        Path p = Paths.get(path);
        byte[] data = Files.readAllBytes(p);
        int[] result = bytesToInts(data);
        return result;
    }

    /**
     * takes array of integers and using repetition transforms it to two dimensional array:
     * first column - element of array, second - number of times it was repeated(not in the whole array, but in sequence)
     * then writes this array to array of bytes
     *
     * @param pixels
     * @throws IOException
     */
    public byte[] compressPic(int[] pixels) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        int count = 1; //counting number of repetitions
        int index = 1;
        int[][] array = new int[pixels.length + 1][2];

        array[0][0] = pixels.length;
        array[0][1] = 0;


        for (int i = 0; i < pixels.length - 1; i++) {
            if (pixels[i] == pixels[i + 1] && i != pixels.length - 2) {
                count++;
            } else if (pixels[i] == pixels[i + 1] && i == pixels.length - 2) {
                array[index][1] = count + 1;
                array[index][0] = pixels[i];
                index++;
            } else if (pixels[i] != pixels[i + 1] && i == pixels.length - 2) {
                array[index][1] = count;
                array[index][0] = pixels[i];
                index++;
                array[index][1] = 1;
                array[index][0] = pixels[i + 1];
                index++;
            } else {
                array[index][1] = count;
                array[index][0] = pixels[i];
                index++;
                count = 1;
            }
        }

        //writing array to binary file
        for (int i = 0; i < index; i++) {
            for (int j = 0; j < 2; j++) {
                dos.writeInt(array[i][j]);
            }
        }

        dos.flush();
        dos.close();

        return baos.toByteArray();
    }

    /**
     * reading from binary file and converting it to array of integers
     *
     * @return array of integers(RGB values in this case)
     */
    public byte[] decompress(byte[] array) throws IOException {
        int[] bytes = bytesToInts(array);

        int length = bytes[0];

        int[][] repetitionArray = new int[length][2];
        int count = 2;

        //reading all integers from binary file
        for (int i = 0; i < repetitionArray.length; i++) {
            for (int j = 0; j < 2; j++) {
                try {
                    repetitionArray[i][j] = bytes[count];
                    count++;
                } catch (Exception e) {
                    break;
                }
            }
        }

        int[] pixels = new int[length];
        count = 0;

        //converting it to an array of pixels
        for (int i = 0; i < pixels.length; i++) {
            if (repetitionArray[i][1] >= 1) {
                while (repetitionArray[i][1] > 0) {
                    pixels[count++] = repetitionArray[i][0];
                    repetitionArray[i][1]--;
                }
            }
        }

        return intsToBytes(pixels);
    }

    public int[] bytesToInts(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        IntBuffer intBuffer = byteBuffer.asIntBuffer();
        int[] result = new int[intBuffer.limit()];
        intBuffer.get(result);
        return result;
    }

    public byte[] intsToBytes(int[] ints) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(ints.length * Integer.BYTES);
        IntBuffer intBuffer = byteBuffer.asIntBuffer();
        intBuffer.put(ints);
        return byteBuffer.array();
    }
}
