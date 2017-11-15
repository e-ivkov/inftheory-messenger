package client;

/**
 * Project: Messenger FX
 *
 * @author Егор Ивков
 * @since 14.11.2017
 */
public enum Compression {
    ShannonFano, Repetition, Huffman;

    public static byte getByteValue(Compression e){
        for (int i = 0; i < values().length; i++) {
            if(values()[i].equals(e)){
                return (byte)i;
            }
        }
        return -1;
    }

    public static String[] toStringArray(){
        String[] arr = new String[values().length];
        for (int i = 0; i < values().length; i++) {
            arr[i] = values()[i].toString();
        }
        return arr;
    }

    public static Compression fromString(String value) throws Exception{
        for (int i = 0; i < values().length; i++) {
            if(values()[i].toString().equals(value)){
                return values()[i];
            }
        }
        throw new Exception("Compression type not found");
    }

    public static Compression fromByte(byte b){
        return values()[b];
    }
}
