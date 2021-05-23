package sample.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Отвечает за обмен между клиентом и сервером (обслуживает клиента)
 */
public class ClientHandler {
    private MyServer server;
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    private  String name;

    public String getName() {
        return name;
    }

    public ClientHandler(MyServer server, Socket socket) {
        try {
            this.socket = socket;
            this.server = server;
            this.inputStream = new DataInputStream(socket.getInputStream());
            this.outputStream = new DataOutputStream(socket.getOutputStream());
            this.name = "";
            
            new Thread(() -> {
                try {
                    authentification();
                    if (!this.socket.isClosed()) {
                        readMessages();
                    }
                } catch (IOException exception) {
                    exception.printStackTrace();
                } finally {
                    closeConnection();
                }
            }).start();
            
        } catch (IOException ex) {
            System.out.println("Проблема при создании клиента");
        }
    }

    // /auth login pass - хотим отправлять такое сообщение в потоке для авторизации
    private void authentification() throws IOException {
        long currentTimeMillis = System.currentTimeMillis();
        while (true) {
            if (inputStream.available() != 0) {
                currentTimeMillis = System.currentTimeMillis();
                String message = inputStream.readUTF();
                if (message.startsWith(ChatConstants.AUTH_COMMAND)) {
                    String[] parts = message.split("\\s+"); // разбивает строку по пробелам на массив строк длиной 3
                    String nick = server.getAuthService().getNickByLoginAndPass(parts[1], parts[2]);
                    if (nick != null) { // Проверяем корректность логина + пароля
                        if (!server.isNickBusy(nick)) { // Проверяем, что в чате нет человека с таким именем
                            sendMsg(ChatConstants.AUTH_OK + " " + nick);
                            name = nick;
                            server.subscribe(this);
                            server.broadcastMessage(name, name + " вошел в чат");
                            return;
                        } else {
                            sendMsg("Ник уже используется");
                        }
                    } else {
                        sendMsg("Неверные логин/пароль");
                    }
                }
            } else if (System.currentTimeMillis() - currentTimeMillis > (long) 15000) {
                closeStreamAndSocket();
                System.out.println("Соединение завершено по таймауту");
                return;
            }
        }
    }

    public void sendMsg(String message) {
        try {
            outputStream.writeUTF(message);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void readMessages() throws IOException {
        while (true) {
            String messageFromClient = inputStream.readUTF();
            System.out.println("от " + name + ": " + messageFromClient);
            if (messageFromClient.equals(ChatConstants.STOP_WORD)) {
                return;
            }
            server.broadcastMessage(name, "[" + name + "]: " + messageFromClient);
        }
    }
    public void closeConnection() {
        server.unsubscribe(this);
        server.broadcastMessage(name, name + " вышел из чата");
        closeStreamAndSocket();
    }

    private void closeStreamAndSocket() {
        try {
            inputStream.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        try {
            outputStream.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
