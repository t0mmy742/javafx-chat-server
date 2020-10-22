package fr.thomasleberre.chat.server.database;

import java.util.HashMap;

public class MemoryDatabase implements IDatabase {
    private final HashMap<String, Integer> clients = new HashMap<>();
    private int clientsNumber = 0;

    @Override
    public int getClientId(String name) {
        if (clients.containsKey(name)) {
            return clients.get(name);
        } else {
            clients.put(name, ++clientsNumber);
            return clientsNumber;
        }
    }
}
