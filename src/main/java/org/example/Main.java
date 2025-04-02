package org.example;

import org.example.ui.TerminalUI;
import org.example.service.AppService;
import org.example.dao.MongoDAO;

public class Main {
    public static void main(String[] args) {
        MongoDAO mongoDAO = new MongoDAO();
        TerminalUI terminalUI = new TerminalUI();

        AppService appService = new AppService(terminalUI, mongoDAO);

        appService.start();
    }

}
