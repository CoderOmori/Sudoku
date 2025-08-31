package br.com.dio.model;

import java.util.List;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class Board {

    private final List<List<Space>> spaces;

    public Board(List<List<Space>> spaces) {
        this.spaces = spaces;
    }

    public List<List<Space>> getSpaces() {
        return spaces;
    }

    public boolean changeValue(int row, int col, int value) {
        Space space = spaces.get(row).get(col);
        if (space.isFixed()) {
            return false;
        }
        if (isValidMove(row, col, value)) {
            space.setActual(value);
            return true;
        }
        return false;
    }

    public boolean clearValue(int row, int col) {
        Space space = spaces.get(row).get(col);
        if (space.isFixed()) {
            return false;
        }
        space.setActual(null);
        return true;
    }

    public boolean hasErrors() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                Integer value = spaces.get(row).get(col).getActual();
                if (nonNull(value)) {
                    for (int i = 0; i < 9; i++) {
                        if (i != col && nonNull(spaces.get(row).get(i).getActual()) && spaces.get(row).get(i).getActual().equals(value)) {
                            return true;
                        }
                    }
                    for (int i = 0; i < 9; i++) {
                        if (i != row && nonNull(spaces.get(i).get(col).getActual()) && spaces.get(i).get(col).getActual().equals(value)) {
                            return true;
                        }
                    }
                    int startRow = row - row % 3;
                    int startCol = col - col % 3;
                    for (int i = 0; i < 3; i++) {
                        for (int j = 0; j < 3; j++) {
                            if (startRow + i != row || startCol + j != col) {
                                if (nonNull(spaces.get(startRow + i).get(startCol + j).getActual()) && spaces.get(startRow + i).get(startCol + j).getActual().equals(value)) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean gameIsFinished() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (isNull(spaces.get(row).get(col).getActual())) {
                    return false;
                }
            }
        }
        return !hasErrors();
    }
    
    public void reset() {
        for (List<Space> row : spaces) {
            for (Space space : row) {
                if (!space.isFixed()) {
                    space.setActual(null);
                }
            }
        }
    }

    private boolean isValidMove(int row, int col, int value) {
        for (int i = 0; i < 9; i++) {
            if (nonNull(spaces.get(row).get(i).getActual()) && spaces.get(row).get(i).getActual().equals(value)) {
                return false;
            }
        }
        for (int i = 0; i < 9; i++) {
            if (nonNull(spaces.get(i).get(col).getActual()) && spaces.get(i).get(col).getActual().equals(value)) {
                return false;
            }
        }
        
        int startRow = row - row % 3;
        int startCol = col - col % 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (nonNull(spaces.get(startRow + i).get(startCol + j).getActual()) && spaces.get(startRow + i).get(startCol + j).getActual().equals(value)) {
                    return false;
                }
            }
        }
        return true;
    }

    public GameStatus getStatus() {
        if (gameIsFinished()) {
            return GameStatus.FINISHED;
        } else if (hasErrors()) {
            return GameStatus.ERROR;
        } else {
            return GameStatus.IN_PROGRESS;
        }
    }
}