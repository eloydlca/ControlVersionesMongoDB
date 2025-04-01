package org.example;

import org.example.dao.TerminalUI;
import org.example.service.AppService;
import org.example.ui.MongoDAO;

public class Main {
    public static void main(String[] args) {
        TerminalUI terminalUI = new TerminalUI();
        MongoDAO mongoDAO = new MongoDAO();

        AppService appService = new AppService(terminalUI, mongoDAO);

        appService.start();
    }

}
