package org.example.service;

import org.example.dao.TerminalUI;
import org.example.ui.MongoDAO;

public class AppService {
    TerminalUI terminalUI;
    MongoDAO mongoDAO;

    public AppService(TerminalUI terminalUI, MongoDAO mongoDAO) {
        this.terminalUI = terminalUI;
        this.mongoDAO = mongoDAO;
    }

    public void start() {
        while (true) {

        }
    }
}
