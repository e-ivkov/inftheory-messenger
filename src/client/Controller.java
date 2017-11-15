package client;

import com.sun.xml.internal.ws.util.ByteArrayBuffer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;

public class Controller {

    @FXML
    TextField textField;
    @FXML
    TextArea textArea;
    @FXML
    Stage primaryStage;
    @FXML
    ComboBox<String> compressionBox;
    @FXML
    ComboBox<String> encodingBox;

    private Messenger messenger;

    public void setMessenger(Messenger messenger) {
        this.messenger = messenger;
    }

    @FXML
    public void onEnter(ActionEvent ae){
        handleSend(ae);

    }

    @FXML
    private void handleSendFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt", "*.rtf"),
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.bmp"),
                new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.aiff"),
                new FileChooser.ExtensionFilter("Binary Files", "*.bin"));
        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile != null) {
            byte[] info = encode(compress(selectedFile));
            byte[] header = generateFileHeader(selectedFile, info.length);
            sendMessage(header, info);
            println("File sent: " + selectedFile.getName());
        }
    }

    @FXML
    private void handleSend(ActionEvent event) {
        println("You: " + textField.getText());
        String text = textField.getText();
        byte[] info = encode(compressText(text));
        byte[] header = generateTextHeader(info.length);
        sendMessage(header, info);
        textField.clear();
    }

    /**
     * prints text in TextArea
     *
     * @param text to be printed
     */
    private void println(String text) {
        textArea.setText(text + "\n" + textArea.getText());
    }

    private byte[] generateTextHeader(int len) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(baos);
        try {
            dataOutputStream.write(MessageType.getByteValue(MessageType.TEXT));
            dataOutputStream.write(Encoding.getByteValue(Encoding.fromString(encodingBox.getValue())));
            dataOutputStream.write(Compression.getByteValue(Compression.fromString(compressionBox.getValue())));
            dataOutputStream.writeInt(len);
            dataOutputStream.writeUTF("not a file");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }

    /**
     * generates message header for files
     *
     * @param file to be sent
     * @param len  length of compressed and encoded file
     * @return bytes of header
     */
    private byte[] generateFileHeader(File file, int len) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(baos);
        try {
            dataOutputStream.write(MessageType.getByteValue(MessageType.FILE));
            dataOutputStream.write(Encoding.getByteValue(Encoding.fromString(encodingBox.getValue())));
            dataOutputStream.write(Compression.getByteValue(Compression.fromString(compressionBox.getValue())));
            dataOutputStream.writeInt(len);
            dataOutputStream.writeUTF(file.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }

    /**
     * @param file for which we want to know extension
     * @return extension of a file without dot
     */
    private String getExtension(File file) {
        String extension = "";
        int i = file.getName().lastIndexOf('.');
        if (i > 0) {
            extension = file.getName().substring(i + 1);
        }
        return extension;
    }

    /**
     * @param file to be compressed
     * @return bytes of the compressed file
     */
    private byte[] compress(File file) {
        try {
            switch (Compression.fromString(compressionBox.getValue())) {
                case Repetition:
                    RepetitionCompresser repetitionCompresser = new RepetitionCompresser();
                    return repetitionCompresser.compressPic(repetitionCompresser.bytesToInts(Files.readAllBytes(file.toPath())));
                case ShannonFano:
                    return ShannonFano.compress(Files.readAllBytes(file.toPath()));
                case Huffman:
                    return Huffman.compress(Files.readAllBytes(file.toPath()));
            }
        } catch (Exception e) {
            println("Compression exception");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param text to be compressed
     * @return bytes of compressed text
     */
    private byte[] compressText(String text) {
        return text.getBytes();
    }

    /**
     * Sends message with this header and content with the use of Messenger class
     *
     * @param header header of the message
     * @param info   content of the message
     */
    private void sendMessage(byte[] header, byte[] info) {
        try {
            ByteArrayBuffer buffer = new ByteArrayBuffer();
            DataOutputStream socketOutput = new DataOutputStream(buffer);
            socketOutput.write(header);
            socketOutput.write(info);
            messenger.sendMessageByte(buffer.getRawData());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * called when file is received by Messenger
     *
     * @param file file which is received
     */
    public void receiveFile(File file) {
        println("File received: " + file.getPath());
    }

    /**
     * called when text is received by Messenger
     *
     * @param text text which is received
     */
    public void receiveText(String text) {
        println("Other: " + text);
    }

    /**
     * one int = four bytes
     *
     * @param ints to be converted to bytes
     * @return array of bytes converted from ints
     */
    public byte[] intsToBytes(int[] ints) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(ints.length * Integer.BYTES);
        IntBuffer intBuffer = byteBuffer.asIntBuffer();
        intBuffer.put(ints);
        return byteBuffer.array();
    }

    /**
     * @param content to be transmitted
     * @return encoded content for transmission
     */
    private byte[] encode(byte[] content) {
        try {
            switch (Encoding.fromString(encodingBox.getValue())) {
                case Hamming:
                    return HammingEncoder.encodeArr(content);
                case Parity:
                    return ParityEncoder.encodeArr(content);
                case Repetition:
                    return RepetitionEncoder.encode(content,3);
            }
        } catch (Exception e) {
            println("Encoding exception");
            e.printStackTrace();
        }
        return null;
    }
}
