package fr.thomasleberre.chat.server;

import fr.thomasleberre.chat.server.database.MemoryDatabase;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server extends Application {
    private final HashMap<Integer, Socket> clients = new HashMap<>();
    private ServerSocket server;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("view/fxml/root.fxml"));

        Scene scene = new Scene(root, 1280, 720);

        primaryStage.setTitle("Chat Server");
        primaryStage.setScene(scene);
//        primaryStage.show();

        startServer();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        server.close();
    }

    public static void main(String[] args) {
        System.out.println("Started");
        launch(args);
    }

    private void startServer() {
        try {
            server = new ServerSocket(45678);

            new Thread(() -> {
                while (true) {
                    try {
                        Socket client = server.accept();
                        new Thread(new ClientProcessing(client, clients, new MemoryDatabase())).start();
                    } catch (IOException e) {
                        if (server.isClosed()) {
                            break;
                        }
                        System.out.println("Error with sockets");
                    }
                }
            }).start();
        } catch (IOException e) {
            System.out.println("Error while opening ServerSocket");
        }
    }
}
