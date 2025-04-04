package org.example.service;

import org.example.model.UserFile;
import org.example.model.Version;
import org.example.ui.TerminalUI;
import org.example.dao.MongoDAO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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
        List<UserFile> files = mongoDAO.getAllFiles();
        if (files.isEmpty()) {
            terminalUI.showMessage("No hay ficheros guardados.");
            return;
        }

        terminalUI.listFiles(files);
        int fileChoice = terminalUI.chooseFile(files.size());
        UserFile selectedFile = files.get(fileChoice - 1);

        terminalUI.listVersions(selectedFile);
        int versionChoice = terminalUI.chooseVersion(selectedFile.getVersions().length);
        Version selectedVersion = selectedFile.getVersions()[versionChoice - 1];

        String recoveryPath = terminalUI.askRecoveryPath();
        Path path = Paths.get(recoveryPath);

        if (Files.exists(path)) {
            if (!terminalUI.confirmOverwrite(recoveryPath)) {
                terminalUI.showMessage("Operación cancelada. No se recuperó el fichero.");
                return;
            }
        }

        try {
            byte[] data = selectedVersion.getData().getData();
            Files.write(path, data);
            terminalUI.showMessage("Fichero recuperado con éxito en: " + recoveryPath);
        } catch (IOException e) {
            terminalUI.showMessage("Error al recuperar el fichero: " + e.getMessage());
        }
    }

    private void deleteDocument() {
        int deleteOption = terminalUI.deleteOptions();
        List<UserFile> files = mongoDAO.getAllFiles();

        if (files.isEmpty()) {
            terminalUI.showMessage("No hay ficheros para eliminar.");
            return;
        }

        // Opción 1: Eliminar fichero completo
        if (deleteOption == 1) {
            terminalUI.listFiles(files);
            int fileChoice = terminalUI.chooseFile(files.size());
            // la lista empieza en 0
            UserFile fileToDelete = files.get(fileChoice - 1);

            if (terminalUI.confirmDeletion(fileToDelete.getFileName())) {
                long deletedCount = mongoDAO.deleteFile(fileToDelete.getId());
                if (deletedCount > 0) {
                    terminalUI.showMessage("Fichero '" + fileToDelete.getFileName() + "' eliminado correctamente.");
                } else {
                    terminalUI.showMessage("No se pudo eliminar el fichero.");
                }
            } else {
                terminalUI.showMessage("Operación cancelada.");
            }
        }
        // Opción 2: Eliminar una versión específica
        else if (deleteOption == 2) {
            terminalUI.listFiles(files);
            int fileChoice = terminalUI.chooseFile(files.size());
            UserFile selectedFile = files.get(fileChoice - 1);

            Version[] versions = selectedFile.getVersions();
            if (versions.length == 0) {
                terminalUI.showMessage("El fichero no tiene versiones para eliminar.");
                return;
            }

            terminalUI.listVersions(selectedFile);
            int versionChoice = terminalUI.chooseVersion(versions.length);
            Version versionToDelete = versions[versionChoice - 1];

            if (terminalUI.confirmDeletionVersion(selectedFile.getFileName(), versionToDelete.getVersion())) {
                mongoDAO.removeVersionFromFile(selectedFile.getId(), versionToDelete.getVersion());
                terminalUI.showMessage("Versión " + versionToDelete.getVersion() + " eliminada correctamente del fichero '" + selectedFile.getFileName() + "'.");
            } else {
                terminalUI.showMessage("Operación cancelada.");
            }
        }
        // Opción 3: Volver atrás
        else {
            terminalUI.showMessage("Volviendo al menú principal.");
        }
    }

    private void exitProgram() {
        terminalUI.exit();
    }
}
