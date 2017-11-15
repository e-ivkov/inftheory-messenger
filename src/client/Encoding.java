package client;

/**
 * Project: Messenger FX
 *
 * @author Егор Ивков
 * @since 14.11.2017
 */
public enum Encoding {
    Hamming, Parity, Repetition;

    public static byte getByteValue(Encoding e){
        for (int i = 0; i < values().length; i++) {
            if(values()[i].equals(e)){
                return (byte)i;
            }
        }
        return -1;
    }

    public static Encoding fromByte(byte b){
        return values()[b];
    }

    public static String[] toStringArray(){
        String[] arr = new String[values().length];
        for (int i = 0; i < values().length; i++) {
            arr[i] = values()[i].toString();
        }
        return arr;
    }

    public static Encoding fromString(String value) throws Exception{
        for (int i = 0; i < values().length; i++) {
            if(values()[i].toString().equals(value)){
                return values()[i];
            }
        }
        throw new Exception("Encoding type not found");
    }
}
