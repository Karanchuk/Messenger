package sample;

import sample.Server.ChatConstants;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {

    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private Controller controller;

    public Client(Controller controller) {
        this.controller = controller;
        try {
            openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openConnection() throws IOException {
        this.socket = new Socket(ChatConstants.HOST, ChatConstants.PORT);
        this.inputStream = new DataInputStream(this.socket.getInputStream());
        this.outputStream = new DataOutputStream(this.socket.getOutputStream());

        (new Thread(() -> {
            try {
                // auth
                while (true) {
                    String strFromServer = inputStream.readUTF();
                    if (strFromServer.equals(ChatConstants.AUTH_OK)) {
                        break;
                    }
                    showMessage(strFromServer);
                }

                // read
                while (true) {
                    String strFromServer = inputStream.readUTF();
                    if (strFromServer.equalsIgnoreCase("/end")) {
                        break;
                    }
                    showMessage(strFromServer);
                }
            } catch (IOException ex) {
                System.out.println("Connection closed");
            }
        })).start();

    }

    public void closeConnection() {
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showMessage(String message) {
        controller.appendToTextArea(message);
    }

    public void sendMessage(String message) {
        if (!message.trim().isEmpty()) {
            try {
                outputStream.writeUTF(message);
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error sending message");
            }
        }
    }

}
