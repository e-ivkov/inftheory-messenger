package client;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * Created by ekaterina on 11/7/17.
 */
public class Messenger {
    private Socket socket;

    public Messenger(Listener<Byte[]> listener) throws IOException {
        InetAddress host = InetAddress.getLocalHost();
        socket = new Socket(host.getHostName(), 5600);

        DataInputStream socketInput = new DataInputStream(
                new BufferedInputStream(socket.getInputStream()));

        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    int length = socketInput.readInt();
                    if (length > 0) {
                        System.out.println("Getting message");
                        byte[] inMessage = new byte[length];
                        socketInput.readFully(inMessage, 0, inMessage.length);
                        System.out.println("Got message: "+inMessage.length);
                        listener.receive(toObjects(inMessage));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    Byte[] toObjects(byte[] bytesPrim) {
        Byte[] bytes = new Byte[bytesPrim.length];
        Arrays.setAll(bytes, n -> bytesPrim[n]);
        return bytes;
    }

    public void sendMessageByte(byte[] message) {
        try {
            //
            // Create a connection to the server socket on the server application
            //


            //
            // Send a message to the client application
            //
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            try {

                dos.writeInt(message.length);
                dos.write(message);
                dos.flush();
            } catch (IOException ioe) {
                System.out.println("Sending error: " + ioe.getMessage());
            }


        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
