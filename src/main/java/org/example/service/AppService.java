package org.example.service;

import org.example.model.UserFile;
import org.example.model.Version;
import org.example.ui.TerminalUI;
import org.example.dao.MongoDAO;

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

            /*  Revisar esto
                Que pasa si no existe una version en un fichero
                (no deberia pasar porque cuando se crea un fichero, se crea con una version)
             */
            terminalUI.listVersions(selectedFile);
            int versionChoice = terminalUI.chooseVersion(versions.length);
            Version versionToDelete = versions[versionChoice - 1];

            /*
                Cambiar esto a que, para eliminar una version, en lugar de poner "SI"
                se deba poner explicitamente la version que se esta borrando, similar
                al metodo de confirmDeletion(), pero solamente con la version
            */
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
