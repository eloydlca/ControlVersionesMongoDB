package org.example.ui;

import org.example.model.UserFile;
import org.example.model.Version;

import java.io.File;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class TerminalUI {

    private Scanner sc = new Scanner(System.in);

    public int showMenu() {
        String menu =
                "¡Bienvenido al controlador de versiones de ficheros!\n" +
                        "Elige la opción a la que quieras acceder:\n" +
                        "[1] Guardar un fichero\n" +
                        "[2] Recuperar un fichero\n" +
                        "[3] Eliminar un fichero\n" +
                        "[4] Salir del programa\n";

        int op = 0;
        boolean valid = false;

        while (!valid) {
            System.out.println(menu);
            try {
                op = sc.nextInt();
                if (op >= 1 && op <= 4) {
                    valid = true;
                } else {
                    System.out.println("¡Opción inválida! Introduce un número entre 1 y 4.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Entrada no válida. Por favor, introduce un número.");
                // limpia la entrada errónea
                sc.next();
            }
        }
        return op;
    }


    //vistas genericas

    //metodo para mostrar mensajes genericos
    public void showMessage(String message) {
        System.out.println(message);
    }

    public void listFiles(List<UserFile> files) {
        System.out.println("Lista de ficheros");

        int index = 1;

        for (UserFile file : files) {
            System.out.println("[" + index + "]" + file.getFileName());
            index++;
        }
    }

    public int chooseFile(int max) {
        int choice = 0;
        boolean valid = false;
        while (!valid) {
            System.out.print("Elige el número del fichero (1-" + max + "): ");
            try {
                choice = sc.nextInt();
                if (choice >= 1 && choice <= max) {
                    valid = true;
                } else {
                    System.out.println("Por favor, ingresa un número entre 1 y " + max + ".");
                }
            } catch (InputMismatchException e) {
                System.out.println("Entrada no válida. Por favor, ingresa un número.");
                sc.next(); // Limpiar la entrada errónea
            }
        }
        return choice;
    }


    public void listVersions(UserFile file) {
        System.out.println("Versiones del fichero " + file.getFileName() + ":");
        Version[] versions = file.getVersions();
        if (versions.length == 0) {
            System.out.println("No se encontraron versiones.");
        } else {
            for (Version version : versions) {
                System.out.println("Versión: " + version.getVersion());
            }
        }
    }

    public int chooseVersion(int max) {
        int choice = 0;
        boolean valid = false;
        while (!valid) {
            System.out.print("Elige el número de la versión (1-" + max + "): ");
            try {
                choice = sc.nextInt();
                if (choice >= 1 && choice <= max) {
                    valid = true;
                } else {
                    System.out.println("Por favor, ingresa un número entre 1 y " + max + ".");
                }
            } catch (InputMismatchException e) {
                System.out.println("Entrada no válida. Por favor, ingresa un número.");
                sc.next(); // Limpiar la entrada errónea
            }
        }
        return choice;
    }


    // vistas de la opcion de Guardar

    public String writeFilePath() {
        System.out.println("Introduce la ruta absoluta del fichero.");
        sc.nextLine();
        return sc.nextLine();
    }

    public void incorrectPath() {
        System.out.println("Error: ruta incorrecta, el archivo no existe.");
    }

    public String showSaveOptionsFileExist(String fileName) {
        while (true) {
            try {
                String menu = "Ya existe un fichero " +  fileName +" en la base de datos.\n" +
                        "[1] Añadr nueva version.\n" +
                        "[2] Sobreescribir el fichero guardado.\n"+
                        "[3] Cancelar.\n";

                System.out.println(menu);
                int value = sc.nextInt();

                if (value == 1) {
                    return "NEW_VERSION";
                } else if (value == 2) {
                    return "OVERWRITE";
                } else if (value == 3) {
                    return "CANCEL";
                } else {
                    System.out.println("¡Opción inválida! Introduce un número entre 1 y 3.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Entrada no válida. Por favor, introduce un número.");
                sc.next();
            }

        }
    }

    public String showNewVersionDescriptionOption() {
        while (true) {
            System.out.println("Añade una pequeña descripcion del guardado.");
            try {
                sc.next();
                return sc.nextLine();
            } catch (InputMismatchException e) {
                sc.next();
                System.out.println("Introduce una descripcion valida");
            }
        }
    }

    public float showNewVersionVersionOption() {
        while (true) {

            System.out.println("Añade la version inicial del archivo");
            try {
                return sc.nextFloat();
            } catch (InputMismatchException e) {
                sc.next();
                System.out.println("introduce un valor valido ej: 1.0");
            }
        }
    }

    public boolean confirmOverWriteFile(UserFile file) {
        System.out.println("Quieres sobreescribir el fichero? " + file.getFileName() + "." + file.getFileExtension());
        System.out.println("[1] si");
        System.out.println("[2] no");
        while (true) {
            try {
                int input =  sc.nextInt();

                if (input == 2 || input == 1) {
                    return input == 1;
                }

                System.out.println("Escribe un valor valido entre 1 o 2");
            } catch (InputMismatchException e) {
                sc.next();
                System.out.println("introduce un valor valido ej: 1.0");
            }
        }
    }

    // vistas de la opcion de Recuperar

    public String askRecoveryPath() {
        System.out.println("Introduce la ruta completa donde deseas recuperar el fichero (incluyendo nombre y extensión):");
        return sc.next();
    }

    public boolean confirmOverwrite(String filePath) {
        System.out.println("Ya existe un fichero en la ruta " + filePath + ". ¿Deseas sobreescribirlo? (SI/NO):");
        String input = sc.next();
        return input.equalsIgnoreCase("SI");
    }


    // vistas de la opcion de Borrar

    public int deleteOptions() {
        int op = 0;
        boolean valid = false;
        String menu = "¿Qué quieres hacer?\n" +
                "[1] Eliminar un fichero completo\n" +
                "[2] Eliminar una versión específica\n" +
                "[3] Volver atrás\n";

        while (!valid) {
            System.out.println(menu);
            try {
                op = sc.nextInt();
                if (op >= 1 && op <= 3) {
                    valid = true;
                } else {
                    System.out.println("¡Opción incorrecta! Por favor, introduce un número entre 1 y 3.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Entrada no válida. Por favor, introduce un número.");
                // limpia la entrada incorrecta
                sc.next();
            }
        }
        return op;
    }

    public boolean confirmDeletion(String fileName) {
        System.out.println("Para confirmar la eliminación, escribe el nombre del fichero (" + fileName + "): ");

        String input = sc.next();
        return input.equals(fileName);
    }

    public boolean confirmDeletionVersion(String fileName, float version) {
        System.out.println("Para confirmar la eliminación de la versión " + version + " del fichero " + fileName + ", escribe la versión a eliminar: ");
        String input = sc.next();
        try {
            float inputVersion = Float.parseFloat(input);
            return inputVersion == version;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    // vistas de la opcion de Salir
    public void exit() {
        System.out.println("¡Nos vemos pronto!");
    }

}
