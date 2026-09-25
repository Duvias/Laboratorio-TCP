package co.icesi.buscaminas.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import co.icesi.buscaminas.client.dtos.Request;
import co.icesi.buscaminas.client.dtos.Response;
import co.icesi.buscaminas.client.model.Cell;

public class MainClient {

    private static final String YELLOW =
            "\u001B[33m";

    private static final String RED =
            "\u001B[31m";

    private static final String RESET =
            "\u001B[0m";

    private static final String DEFAULT_HOST =
            "localhost";

    private static final int DEFAULT_PORT =
            12345;

    public static void main(String[] args) {

        String host =
                args.length > 0
                        ? args[0]
                        : DEFAULT_HOST;

        int port =
                args.length > 1
                        ? Integer.parseInt(args[1])
                        : DEFAULT_PORT;

        BuscaminasTCPClient client =
                new BuscaminasTCPClient();

        Scanner scanner =
                new Scanner(System.in);

        boolean running = true;

        System.out.println(
                "============================================="
        );

        System.out.println(
                "BUSCAMINAS DISTRIBUIDO - CLIENTE TCP"
        );

        System.out.println(
                "============================================="
        );

        System.out.println(
                "Servidor: "
                        + host
                        + ":"
                        + port
        );

        while (running) {

            printMenu();

            int option =
                    readInt(
                            scanner,
                            "Seleccione una opcion: "
                    );

            try {

                switch (option) {

                    case 1:
                        initGame(
                                client,
                                scanner,
                                host,
                                port
                        );
                        break;

                    case 2:
                        selectCell(
                                client,
                                scanner,
                                host,
                                port
                        );
                        break;

                    case 3:
                        markCell(
                                client,
                                scanner,
                                host,
                                port
                        );
                        break;

                    case 4:
                        getBoard(
                                client,
                                host,
                                port
                        );
                        break;

                    case 5:
                        surrender(
                                client,
                                host,
                                port
                        );
                        break;

                    case 6:
                        running = false;
                        break;

                    default:
                        System.out.println(
                                "Opcion no valida."
                        );
                }

            } catch (Exception e) {

                System.out.println(
                        "Error: "
                                + e.getMessage()
                );
            }
        }

        scanner.close();

        System.out.println(
                "Cliente finalizado."
        );
    }

    private static void printMenu() {

        System.out.println();

        System.out.println(
                "[1] Iniciar nueva partida (Filas, Columnas, Minas)"
        );

        System.out.println(
                "[2] Destapar celda (Fila, Columna)"
        );

        System.out.println(
                "[3] Marcar / Desmarcar bandera (Fila, Columna)"
        );

        System.out.println(
                "[4] Consultar estado actual del tablero"
        );

        System.out.println(
                "[5] Rendirse y revelar tablero completo"
        );

        System.out.println(
                "[6] Salir"
        );
    }

    private static void initGame(
            BuscaminasTCPClient client,
            Scanner scanner,
            String host,
            int port
    ) throws Exception {

        int n =
                readInt(
                        scanner,
                        "Filas: "
                );

        int m =
                readInt(
                        scanner,
                        "Columnas: "
                );

        int mines =
                readInt(
                        scanner,
                        "Minas: "
                );

        Map<String, String> data =
                new HashMap<>();

        data.put(
                "n",
                String.valueOf(n)
        );

        data.put(
                "m",
                String.valueOf(m)
        );

        data.put(
                "minas",
                String.valueOf(mines)
        );

        Request request =
                new Request(
                        "INIT_GAME",
                        data
                );

        Response response =
                client.sendRequest(
                        host,
                        port,
                        request
                );

        showResponse(response);
    }

    private static void selectCell(
            BuscaminasTCPClient client,
            Scanner scanner,
            String host,
            int port
    ) throws Exception {

        int i =
                readInt(
                        scanner,
                        "Fila: "
                );

        int j =
                readInt(
                        scanner,
                        "Columna: "
                );

        Response response =
                client.sendRequest(
                        host,
                        port,
                        coordinatesRequest(
                                "SELECT_CELL",
                                i,
                                j
                        )
                );

        showResponse(response);

        if (
                response.getBoolean(
                        "gameEnd",
                        false
                )
        ) {

            if (
                    response.getBoolean(
                            "win",
                            false
                    )
            ) {

                System.out.println();
                System.out.println(
                        "*** GANASTE LA PARTIDA ***"
                );

            } else {

                System.out.println();
                System.out.println(
                        "*** GAME OVER ***"
                );

                System.out.println(
                        "Se piso una mina."
                );
            }
        }
    }

    private static void markCell(
            BuscaminasTCPClient client,
            Scanner scanner,
            String host,
            int port
    ) throws Exception {

        int i =
                readInt(
                        scanner,
                        "Fila: "
                );

        int j =
                readInt(
                        scanner,
                        "Columna: "
                );

        Response response =
                client.sendRequest(
                        host,
                        port,
                        coordinatesRequest(
                                "MARK_CELL",
                                i,
                                j
                        )
                );

        showResponse(response);
    }

    private static void getBoard(
            BuscaminasTCPClient client,
            String host,
            int port
    ) throws Exception {

        Response response =
                client.sendRequest(
                        host,
                        port,
                        new Request(
                                "GET_BOARD",
                                new HashMap<>()
                        )
                );

        showResponse(response);
    }

    private static void surrender(
            BuscaminasTCPClient client,
            String host,
            int port
    ) throws Exception {

        Response response =
                client.sendRequest(
                        host,
                        port,
                        new Request(
                                "SOW_ALL",
                                new HashMap<>()
                        )
                );

        showResponse(response);

        System.out.println();

        System.out.println(
                "*** TABLERO REVELADO ***"
        );
    }

    private static Request coordinatesRequest(
            String action,
            int i,
            int j
    ) {

        Map<String, String> data =
                new HashMap<>();

        data.put(
                "i",
                String.valueOf(i)
        );

        data.put(
                "j",
                String.valueOf(j)
        );

        return new Request(
                action,
                data
        );
    }

    private static void showResponse(
            Response response
    ) {

        if (response == null) {

            System.out.println(
                    "No se recibio respuesta."
            );

            return;
        }

        if (
                !"OK".equals(
                        response.status
                )
        ) {

            System.out.println(
                    "Servidor: "
                            + response.status
            );

            String message =
                    response.getMessage();

            if (message != null) {

                System.out.println(
                        "Mensaje: "
                                + message
                );
            }
        }

        Cell[][] board =
                response.getBoard();

        if (board != null) {

            printBoard(board);
        }
    }

    private static void printBoard(Cell[][] board) {

        if (board == null || board.length == 0) {
            return;
        }

        System.out.println();

        System.out.print("     ");

        for (
                int j = 0;
                j < board[0].length;
                j++
        ) {

            System.out.printf(
                    "%5d",
                    j
            );
        }

        System.out.println();

        for (
                int i = 0;
                i < board.length;
                i++
        ) {

            System.out.printf(
                    "%3d  ",
                    i
            );

            for (
                    int j = 0;
                    j < board[i].length;
                    j++
            ) {

                System.out.print(
                        formatCell(
                                board[i][j]
                        )
                );
            }

            System.out.println();
        }
    }

    private static String formatCell(Cell cell) {

        String content;

        if (cell.isMarked && cell.hide) {

            content =
                    YELLOW
                            + "M"
                            + RESET;

        } else if (cell.hide && !cell.showAll) {

            content = ".";

        } else if (cell.isLandMine) {

            content =
                    RED
                            + "*"
                            + RESET;

        } else if (cell.value == 0) {

            content = " ";

        } else {

            content =
                    String.valueOf(
                            cell.value
                    );
        }

        return String.format(
                "[ %s ]",
                content
        );
    }

    private static int readInt(
            Scanner scanner,
            String message
    ) {

        System.out.print(message);

        while (!scanner.hasNextInt()) {

            System.out.print(
                    "Ingrese un numero: "
            );

            scanner.next();
        }

        return scanner.nextInt();
    }
}