package org.example.service;

import org.example.ui.TerminalUI;
import org.example.dao.MongoDAO;

public class AppService {
    TerminalUI terminalUI;
    MongoDAO mongoDAO;
    int op;

    public AppService(TerminalUI terminalUI, MongoDAO mongoDAO) {
        this.terminalUI = terminalUI;
        this.mongoDAO = mongoDAO;
    }

    public void start() {

        do {
            op = terminalUI.showMenu();
            switch (op) {
                case 1:
                    saveDocument();
                    break;
                case 2:
                    obtainDocument();
                    break;
                case 3:
                    deleteDocument();
                    break;
                case 4:
                    exitProgram();
                    return;
            }
        } while (true);
    }

    private void saveDocument() {

    }

    private void obtainDocument() {
    }

    private void deleteDocument() {
        int op = terminalUI.deleteOptions();
    }

    private void exitProgram() {
        terminalUI.exit();
    }
}
