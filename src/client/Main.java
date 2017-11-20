package client;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.nio.ByteBuffer;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("client.fxml"));
        Parent root = loader.load();
        Controller controller = loader.getController();
        primaryStage.setTitle("Student Secret Messenger");
        primaryStage.setScene(new Scene(root, 774, 522));
        primaryStage.setResizable(false);
        controller.compressionBox.setItems(FXCollections.observableArrayList(Compression.toStringArray()));
        controller.compressionBox.getSelectionModel().selectFirst();
        controller.encodingBox.setItems(FXCollections.observableArrayList(Encoding.toStringArray()));
        controller.encodingBox.getSelectionModel().selectFirst();
        //tells Messenger what to do when something is received
        Messenger messenger = new Messenger(new Listener<Byte[]>() {
            @Override
            public void receive(Byte[] message) {
                DataInputStream stream = new DataInputStream(new ByteArrayInputStream(toBytes(message)));
                try {
                    //reading header
                    MessageType messageType = MessageType.fromByte(stream.readByte());
                    Encoding encoding = Encoding.fromByte(stream.readByte());
                    Compression compression = Compression.fromByte(stream.readByte());
                    int len = stream.readInt();
                    String fileName = stream.readUTF();
                    //reading content
                    byte[] bytes = new byte[len];
                    stream.readFully(bytes);
                    byte[] info = decompress(decode(bytes, encoding), messageType, compression);
                    switch (messageType) {
                        case TEXT:
                            controller.receiveText(new String(info));
                            break;
                        default:
                            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
                            fileOutputStream.write(info);
                            fileOutputStream.close();
                            controller.receiveFile(new File(fileName));
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        controller.setMessenger(messenger);
        primaryStage.show();
    }

    /**
     * convers bytes primitive type to Objects
     * @param bytes to be converted
     * @return Byte objects
     */
    public byte[] toBytes(Byte[] bytes){
        byte[] out = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            out[i] = bytes[i];
        }
        return out;
    }

    /**
     * @param bytes of String
     * @return decoded null terminated string from array of bytes
     */
    public String getStrFromBytes(byte[] bytes){
        int l = 0;
        for(int i=0; i< bytes.length; ++i) {
            if (bytes[i] == 0) {
                l = i;
                break;
            }
        }
        byte[] str = new byte[l+1];
        System.arraycopy(bytes, 0, str, 0, l);
        return new String(str);
    }

    /**
     *
     * @param compressed bytes of compressed contents of the file
     * @param messageType type of the message
     * @return bytes of decompressed contents of the file
     */
    public byte[] decompress(byte[] compressed, MessageType messageType, Compression compression){
        try {
            if(messageType.equals(MessageType.TEXT))
                return compressed;
            switch (compression) {
                case ShannonFano:
                    return ShannonFano.decompress(compressed);
                case Repetition:
                    RepetitionCompresser repetitionCompresser = new RepetitionCompresser();
                    return repetitionCompresser.decompress(compressed);
                case Huffman:
                    return Huffman.decompress(compressed);
                case LZ78:
                    return LZ78.decompress(compressed);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param encoded encoded for transmission bytes
     * @return decoded bytes
     */
    public byte[] decode(byte[] encoded, Encoding encoding){
        switch (encoding){
            case Hamming:
                return HammingEncoder.decodeArr(encoded);
            case Repetition:
                return RepetitionEncoder.decode(encoded,3);
            case Parity:
                return ParityEncoder.decodeArr(encoded);
        }
        return null;
    }

    /**
     * int = 4 bytes
     * @param bytes array of bytes to be converted
     * @return int array converted from byte array
     */
    public int[] bytesToInts(byte[] bytes){
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        return byteBuffer.asIntBuffer().array();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
