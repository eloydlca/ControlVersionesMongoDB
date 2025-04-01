package org.example.dao;

import java.util.InputMismatchException;
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


    public void exit() {
        System.out.println("¡Nos vemos pronto!");
    }

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

}
