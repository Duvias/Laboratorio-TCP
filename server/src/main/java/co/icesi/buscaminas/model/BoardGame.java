package co.icesi.buscaminas.model;

import java.util.Random;

public class BoardGame {

        private Cell[][] board;

        private int mines;

        private boolean firstMove;

        public int getMines() {
                return mines;
        }

        public synchronized int initGame(int n,int m,int mines) {

                firstMove = true;

                this.mines = mines;

                board = new Cell[n][m];

                Random rd = new Random();

                int mi = 0;

                for (int i = 0; i < n; i++) {

                        for (int j = 0; j < m; j++) {

                                boolean isMine = false;
                                board[i][j] =new Cell(isMine,0);
                        }
                }

                while (mi < mines) {

                        int k = rd.nextInt(n);

                        int l = rd.nextInt(m);

                        Cell cell = board[k][l];

                        if (!cell.isLandMine()) {
                                cell.setLandMine(true);
                                mi++;
                        }
                }

                for (int i = 0; i < n; i++) {

                        for (int j = 0; j < m; j++) {

                                boolean isMine =board[i][j].isLandMine();

                                if (!isMine) {
                                        int minesAround =getMinesAround(i, j);
                                        board[i][j].setValue(minesAround);
                                }
                        }
                }

                return mi;
        }

    public void showAll(boolean show) {

        for (int i = 0; i < board.length; i++) {

            for (
                    int j = 0;
                    j < board[0].length;
                    j++
            ) {

                board[i][j]
                        .setShowAll(show);
            }
        }
    }

    private int getMinesAround(
            int i,
            int j
    ) {

        int mines = 0;

        mines +=
                i > 0
                        && board[i - 1][j]
                        .isLandMine()
                        ? 1
                        : 0;

        mines +=
                i < board.length - 1
                        && board[i + 1][j]
                        .isLandMine()
                        ? 1
                        : 0;

        mines +=
                j > 0
                        && board[i][j - 1]
                        .isLandMine()
                        ? 1
                        : 0;

        mines +=
                j < board[0].length - 1
                        && board[i][j + 1]
                        .isLandMine()
                        ? 1
                        : 0;

        mines +=
                i > 0
                        && j > 0
                        && board[i - 1][j - 1]
                        .isLandMine()
                        ? 1
                        : 0;

        mines +=
                i > 0
                        && j < board[0].length - 1
                        && board[i - 1][j + 1]
                        .isLandMine()
                        ? 1
                        : 0;

        mines +=
                i < board.length - 1
                        && j > 0
                        && board[i + 1][j - 1]
                        .isLandMine()
                        ? 1
                        : 0;

        mines +=
                i < board.length - 1
                        && j < board[0].length - 1
                        && board[i + 1][j + 1]
                        .isLandMine()
                        ? 1
                        : 0;

        return mines;
    }

    public void printBoard() {

        System.out.println();

        System.out.print("   ");

        for (
                int i = 0;
                i < board[0].length;
                i++
        ) {

            System.out.print(" " + i);
        }

        System.out.println();

        for (int i = 0; i < board.length; i++) {

            System.out.print(i + " [");

            for (
                    int j = 0;
                    j < board[0].length;
                    j++
            ) {

                System.out.print(
                        " " + board[i][j]
                );
            }

            System.out.println(" ]");
        }
    }

        private void recalculateValues() {

                for (int i = 0; i < board.length; i++) {

                        for (
                                int j = 0;
                                j < board[0].length;
                                j++
                        ) {

                        if (
                                !board[i][j]
                                        .isLandMine()
                        ) {

                                board[i][j]
                                        .setValue(
                                                getMinesAround(
                                                        i,
                                                        j
                                                )
                                        );
                        }
                        }
                }
        }

        private void makeFirstCellZero(int i,int j) {

                Cell selected = board[i][j];

        if (selected.isLandMine()) {

                selected.setLandMine(false);

                int k;
                int l;

                Random rd =
                        new Random();

                do {

                k = rd.nextInt(
                        board.length
                );

                l = rd.nextInt(
                        board[0].length
                );

                } while (
                        board[k][l].isLandMine()
                                || (
                                k == i
                                        && l == j
                        )
                );

                board[k][l]
                        .setLandMine(true);
        }

        recalculateValues();
        }

        public synchronized boolean selectCell(
                int i,
                int j
        ) {

        if (
                i < 0
                        || i >= board.length
                        || j < 0
                        || j >= board[0].length
        ) {

                throw new RuntimeException(
                        "Cell no valid"
                );
        }

        if (firstMove) {

                makeFirstCellZero(
                        i,
                        j
                );

                firstMove = false;
        }

        Cell cell = board[i][j];

        if (cell.isLandMine()) {

                showAll(true);

                throw new RuntimeException(
                        "Game over"
                );

        } else {

                zerosDiscovered = 0;

                if (cell.isHide()) {

                showCells(
                        i,
                        j
                );
                }

                return validWin();
        }
        }

    private boolean validWin() {

        boolean win = true;

        for (int i = 0; i < board.length; i++) {

            for (
                    int j = 0;
                    j < board[0].length;
                    j++
            ) {

                win &=
                        !board[i][j].isHide()
                                || board[i][j]
                                .isLandMine();
            }
        }

        return win;
    }

        private int zerosDiscovered;

        private static final int MAX_ZEROS =6;

        private void showCells(
                int i,
                int j
        ) {

        if (
                i < 0
                        || i >= board.length
                        || j < 0
                        || j >= board[0].length
        ) {

                return;
        }

        Cell cell = board[i][j];

        if (!cell.isHide()) {

                return;
        }

        if (cell.isMarked()) {

                return;
        }

        cell.setHide(false);

        if (cell.getValue() != 0) {

                return;
        }

        if (zerosDiscovered >= MAX_ZEROS) {

                return;
        }

        zerosDiscovered++;

        showCells(
                i,
                j - 1
        );

        showCells(
                i,
                j + 1
        );

        showCells(
                i - 1,
                j
        );

        showCells(
                i + 1,
                j
        );
        }

    public Cell[][] getBoard() {
        return board;
    }

    public synchronized void markCell(
            int i,
            int j
    ) {

        if (
                i < 0
                        || i >= board.length
                        || j < 0
                        || j >= board[0].length
        ) {

            throw new RuntimeException(
                    "Cell no valid"
            );
        }

        Cell cell = board[i][j];

        if (cell.isHide()) {

            cell.setMarked(
                    !cell.isMarked()
            );
        }
    }
}