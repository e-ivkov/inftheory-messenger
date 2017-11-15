package client;

/**
 * Project: Messenger FX
 *
 * @author Егор Ивков
 * @since 07.11.2017
 */
public enum MessageType {
    FILE, TEXT;

    public static byte getByteValue(MessageType messageType){
        for (int i = 0; i < values().length; i++) {
            if(values()[i].equals(messageType)){
                return (byte)i;
            }
        }
        return -1;
    }

    public static MessageType fromByte(byte b){
        return values()[b];
    }
}
