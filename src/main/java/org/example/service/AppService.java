package org.example.service;

import org.bson.types.Binary;
import org.example.model.UserFile;
import org.example.model.Version;
import org.example.ui.TerminalUI;
import org.example.dao.MongoDAO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
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

    // metodo para gestionar el guardado del documento
    private void saveDocument() {
        while (true) {
            String filePath = terminalUI.writeFilePath();
            File file = new File(filePath);
            //verificar si la ruta es correcta
            if (file.exists()) {
                String fileName = getFileName(file);
                String fileExtension = getFileExtension(file);
                Binary fileBinary = getBinaryFromFIle(file);

                UserFile userFile = mongoDAO.getFileByFileNameAndExtension(fileName, fileExtension);

                // ver si el fichero existe en la base de datos
                // Si
                if (userFile != null ) {
                    // mostrar las opciones
                    String response = terminalUI.showSaveOptionsFileExist(fileName);

                    // En caso de querer añadir una nueva version
                    if (response.equals("NEW_VERSION")) {
                        UserFile userFileFromDB = mongoDAO.getFileByFileNameAndExtension(fileName, fileExtension);

                        // obtener la version mas reciente en base al atributo version (el mas grande)
                        Version lastVersion = Arrays.stream(userFileFromDB.getVersions()).max((v1,v2) -> Float.compare(v1.getVersion(), v2.getVersion())).orElse(null);
                        terminalUI.showMessage("Ultima version: " + lastVersion.getVersion());
                        terminalUI.showMessage("Descripción de la version: " + lastVersion.getVersionDescription() + "\n");

                        String fileDescription = terminalUI.showNewVersionDescriptionOption();
                        float fileVersion = terminalUI.showNewVersionVersionOption();

                        Version newVersion = new Version(fileVersion, fileDescription, fileBinary);

                        mongoDAO.addVersionToFile(userFileFromDB.getId(), newVersion);
                        terminalUI.showMessage("Se ha guardado la nueva version: "+ fileVersion + " del fichero " + fileName);
                        return;
                    } else if (response.equals("OVERWRITE")) {
                        boolean confirmacion = terminalUI.confirmOverWriteFile(userFile);

                        if (confirmacion) {
                            String fileDescription = terminalUI.showNewVersionDescriptionOption();
                            float fileVersion = terminalUI.showNewVersionVersionOption();

                            Version[] version = {new Version(fileVersion, fileDescription, fileBinary)};

                            UserFile newFile = new UserFile(userFile.getId(), fileName, fileExtension, version );
                            mongoDAO.updateFile(newFile);
                            terminalUI.showMessage("Fichero: " + fileName + "." + fileExtension + ", version: "+ fileVersion + ", Se ha guardado con exito");
                            return;
                        } else {
                            return;
                        }
                    } else if (response.equals("CANCEL")) {
                        return;
                    }

                }
                // No
                else {
                   String fileDescription = terminalUI.showNewVersionDescriptionOption();
                   float fileVersion = terminalUI.showNewVersionVersionOption();

                   Version[] version = {new Version(fileVersion, fileDescription, fileBinary)};

                   UserFile newFile = new UserFile(fileName, fileExtension, version );
                   // guardar el archivo
                   mongoDAO.saveFile(newFile);
                   terminalUI.showMessage("Fichero: " + fileName + "." + fileExtension + ", version: "+ fileVersion + ", Se ha guardado con exito");
                   return;
                }

            } else {
                terminalUI.incorrectPath();
            }
        }
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

    /*
     * METODOS AUXILIARES
     */

    //saveDocument
    private String getFileName(File file) {
        String completeFileName = file.getName();
        String FileName = "";

        int extPosition = completeFileName.lastIndexOf('.');
        if (extPosition != -1) {
            // Nombre sin extensión
            FileName = completeFileName.substring(0, extPosition);
        } else {
            // Si no tiene extensión
            FileName = completeFileName;
        }

        return FileName;
    }

    private String getFileExtension(File file) {
        String completeFileName = file.getName();
        String extension = "";

        int posicionPunto = completeFileName.lastIndexOf('.');
        if (posicionPunto != -1) {
            extension = completeFileName.substring(posicionPunto + 1);
        }

        return extension;
    }

    private Binary getBinaryFromFIle(File file) {
        try (FileInputStream fis = new FileInputStream(file)){
            byte[] fileData = new byte[(int) file.length()];

            fis.read(fileData);

            return new Binary(fileData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
