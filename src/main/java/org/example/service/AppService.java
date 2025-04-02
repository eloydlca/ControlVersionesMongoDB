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
                    saveDocument(terminalUI);
                    break;
                case 2:
                    obtainDocument(terminalUI);
                    break;
                case 3:
                    deleteDocument(terminalUI);
                    break;
                case 4:
                    exitProgram(terminalUI);
                    return;
            }
        } while (true);
    }

    private void saveDocument(TerminalUI terminalUI) {
    }

    private void obtainDocument(TerminalUI terminalUI) {
    }

    private void deleteDocument(TerminalUI terminalUI) {
        int op = terminalUI.deleteOptions();
    }

    private void exitProgram(TerminalUI terminalUI) {
        terminalUI.exit();
    }
}
