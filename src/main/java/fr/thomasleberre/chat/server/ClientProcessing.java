package fr.thomasleberre.chat.server;

import fr.thomasleberre.chat.server.database.IDatabase;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;

public class ClientProcessing implements Runnable {
    private final Socket socket;
    private final HashMap<Integer, Socket> clients;
    private final IDatabase database;
    private STATE state = STATE.INIT;

    private static final byte ETX = 3;
    private static final byte EOT = 4;

    private enum STATE {
        INIT,
        READY,
        END
    }

    public ClientProcessing(Socket socket, HashMap<Integer, Socket> clients, IDatabase database) {
        this.socket = socket;
        this.clients = clients;
        this.database = database;
    }

    @Override
    public void run() {
        BufferedInputStream reader;

        while (!socket.isClosed()) {
            try {
                reader = new BufferedInputStream(socket.getInputStream());

                int stream;
                StringBuilder message = new StringBuilder();
                byte[] bufferRead;
                int sendToClientId = 0;
                while (true) {
                    bufferRead = new byte[4096];
                    stream = reader.read(bufferRead);
                    message.append(new String(bufferRead, 0, stream));
                    if (sendToClientId == 0 && state == STATE.READY) {
                        sendToClientId = Integer.parseInt(message.substring(0, 1));
                        message.deleteCharAt(0);
                    }
                    if (bufferRead[stream - 1] == ETX) {
                        message.deleteCharAt(message.length() - 1);
                        break;
                    } else if (bufferRead[0] == EOT) {
                        socket.close();
                        break;
                    }
                }
                if (message.toString().equals("\4")) {
                    break;
                }

                if (state == STATE.INIT) {
                    clients.put(database.getClientId(message.toString()), socket);
                    state = STATE.READY;
                } else if (state == STATE.READY) {
                    Socket client = clients.get(sendToClientId);
                    if (client != null) {
                        OutputStream out = client.getOutputStream();
                        out.write(message.toString().getBytes());
                        out.write(ETX);
                        out.flush();
                    }

                    System.out.println(message);
                }
            } catch (IOException e) {
                System.out.println("Error while reading message");
                break;
            }
        }
    }
}
