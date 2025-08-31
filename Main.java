package br.com.dio;

import br.com.dio.model.Board;
import br.com.dio.model.Space;
import static br.com.dio.util.BoardTemplate.BOARD_TEMPLATE;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import java.util.Scanner;
import static java.util.stream.Collectors.toMap;
import java.util.stream.Stream;

public class Main {

    private final static Scanner scanner = new Scanner(System.in);

    private static Board board;

    private final static int BOARD_LIMIT = 9;

    public static void main(String[] args) {
        final Map<String, String> positions = (args != null && args.length > 0)
                ? Stream.of(args)
                .collect(toMap(
                        k -> k.split(";")[0],
                        v -> v.split(";")[1]
                ))
                : getExamplePositions();

        int option = -1;
        while (true) {
            System.out.println("\nSelecione uma das opções a seguir");
            System.out.println("1 - Iniciar um novo Jogo");
            System.out.println("2 - Colocar um novo número");
            System.out.println("3 - Remover um número");
            System.out.println("4 - Visualizar jogo atual");
            System.out.println("5 - Verificar status do jogo");
            System.out.println("6 - Limpar jogo");
            System.out.println("7 - Finalizar jogo");
            System.out.println("8 - Sair");

            option = scanner.nextInt();

            switch (option) {
                case 1:
                    startGame(positions);
                    break;
                case 2:
                    inputNumber();
                    break;
                case 3:
                    removeNumber();
                    break;
                case 4:
                    showCurrentGame();
                    break;
                case 5:
                    showGameStatus();
                    break;
                case 6:
                    clearGame();
                    break;
                case 7:
                    finishGame();
                    break;
                case 8:
                    System.exit(0);
                    break;
                default:
                    System.out.println("Opção inválida, selecione uma das opções do menu");
            }
        }
    }

    private static void startGame(final Map<String, String> positions) {
        if (nonNull(board)) {
            System.out.println("O jogo já foi iniciado. Por favor, limpe o jogo atual para começar um novo.");
            return;
        }

        List<List<Space>> spaces = new ArrayList<List<Space>>();
        for (int i = 0; i < BOARD_LIMIT; i++) {
            spaces.add(new ArrayList<Space>());
            for (int j = 0; j < BOARD_LIMIT; j++) {
                String positionConfig = positions.get(String.format("%s,%s", i, j));
                Integer expected = Integer.parseInt(positionConfig.split(",")[0]);
                boolean fixed = Boolean.parseBoolean(positionConfig.split(",")[1]);
                Space currentSpace = new Space(expected, fixed);
                spaces.get(i).add(currentSpace);
            }
        }

        board = new Board(spaces);
        System.out.println("O jogo está pronto para começar!");
    }

    private static void inputNumber() {
        if (isNull(board)) {
            System.out.println("O jogo ainda não foi iniciado.");
            return;
        }

        System.out.println("Informe a linha em que o número será inserido (0-8)");
        int row = runUntilGetValidNumber(0, 8);
        System.out.println("Informe a coluna em que o número será inserido (0-8)");
        int col = runUntilGetValidNumber(0, 8);
        System.out.printf("Informe o número que vai entrar na posição [%s,%s] (1-9)\n", row, col);
        int value = runUntilGetValidNumber(1, 9);
        
        if (!board.changeValue(row, col, value)) {
            if (board.getSpaces().get(row).get(col).isFixed()) {
                System.out.printf("A posição [%s,%s] tem um valor fixo e não pode ser alterada.\n", row, col);
            } else {
                System.out.printf("O valor %d é um movimento inválido na posição [%s,%s]. Verifique se ele já existe na linha, coluna ou bloco 3x3.\n", value, row, col);
            }
        } else {
            System.out.println("Valor inserido com sucesso!");
        }
    }

    private static void removeNumber() {
        if (isNull(board)) {
            System.out.println("O jogo ainda não foi iniciado");
            return;
        }

        System.out.println("Informe a linha do número a ser removido (0-8)");
        int row = runUntilGetValidNumber(0, 8);
        System.out.println("Informe a coluna do número a ser removido (0-8)");
        int col = runUntilGetValidNumber(0, 8);
        
        if (!board.clearValue(row, col)) {
            System.out.printf("A posição [%s,%s] tem um valor fixo e não pode ser removido.\n", row, col);
        } else {
            System.out.println("Valor removido com sucesso!");
        }
    }

    private static void showCurrentGame() {
        if (isNull(board)) {
            System.out.println("O jogo ainda não foi iniciado");
            return;
        }

        Object[] args = new Object[81];
        int argPos = 0;
        for (int row = 0; row < BOARD_LIMIT; row++) {
            for (int col = 0; col < BOARD_LIMIT; col++) {
                Space space = board.getSpaces().get(row).get(col);
                Object value = isNull(space.getActual()) ? " " : space.getActual();
                args[argPos++] = " " + value;
            }
        }
        System.out.println("Seu jogo se encontra da seguinte forma");
        System.out.printf((BOARD_TEMPLATE) + "\n", args);
    }

    private static void showGameStatus() {
        if (isNull(board)) {
            System.out.println("O jogo ainda não foi iniciado");
            return;
        }

        System.out.printf("O jogo atualmente se encontra no status %s\n", board.getStatus().getLabel());
        if (board.hasErrors()) {
            System.out.println("O jogo contém erros");
        } else {
            System.out.println("O jogo não contém erros");
        }
    }

    private static void clearGame() {
        if (isNull(board)) {
            System.out.println("O jogo ainda não foi iniciado");
            return;
        }

        System.out.println("Tem certeza que deseja limpar seu jogo e perder todo seu progresso?");
        String confirm = scanner.next();
        while (!confirm.equalsIgnoreCase("sim") && !confirm.equalsIgnoreCase("não")) {
            System.out.println("Informe 'sim' ou 'não'");
            confirm = scanner.next();
        }

        if (confirm.equalsIgnoreCase("sim")) {
            board.reset();
        }
    }

    private static void finishGame() {
        if (isNull(board)) {
            System.out.println("O jogo ainda não foi iniciado");
            return;
        }

        if (board.gameIsFinished()) {
            System.out.println("Parabéns você concluiu o jogo");
            showCurrentGame();
            board = null;
        } else if (board.hasErrors()) {
            System.out.println("Seu jogo contém erros, verifique seu tabuleiro e ajuste-o");
        } else {
            System.out.println("Você ainda precisa preencher algum espaço");
        }
    }

    private static int runUntilGetValidNumber(final int min, final int max) {
        int current = scanner.nextInt();
        while (current < min || current > max) {
            System.out.printf("Informe um número entre %s e %s\n", min, max);
            current = scanner.nextInt();
        }
        return current;
    }
    
    private static Map<String, String> getExamplePositions() {
        Map<String, String> positions = new java.util.HashMap<>();
        positions.put("0,0", "5,true"); positions.put("0,1", "3,true"); positions.put("0,2", "4,false"); positions.put("0,3", "6,false"); positions.put("0,4", "7,true"); positions.put("0,5", "8,false"); positions.put("0,6", "9,false"); positions.put("0,7", "1,false"); positions.put("0,8", "2,false");
        positions.put("1,0", "6,false"); positions.put("1,1", "7,true"); positions.put("1,2", "2,true"); positions.put("1,3", "1,false"); positions.put("1,4", "9,false"); positions.put("1,5", "5,false"); positions.put("1,6", "3,true"); positions.put("1,7", "4,true"); positions.put("1,8", "8,false");
        positions.put("2,0", "1,true"); positions.put("2,1", "9,false"); positions.put("2,2", "8,false"); positions.put("2,3", "3,true"); positions.put("2,4", "4,true"); positions.put("2,5", "2,true"); positions.put("2,6", "5,false"); positions.put("2,7", "6,false"); positions.put("2,8", "7,false");
        positions.put("3,0", "8,false"); positions.put("3,1", "5,false"); positions.put("3,2", "9,false"); positions.put("3,3", "7,false"); positions.put("3,4", "6,true"); positions.put("3,5", "1,true"); positions.put("3,6", "4,true"); positions.put("3,7", "2,true"); positions.put("3,8", "3,false");
        positions.put("4,0", "4,true"); positions.put("4,1", "2,false"); positions.put("4,2", "6,false"); positions.put("4,3", "8,false"); positions.put("4,4", "5,false"); positions.put("4,5", "3,false"); positions.put("4,6", "7,false"); positions.put("4,7", "9,false"); positions.put("4,8", "1,false");
        positions.put("5,0", "7,false"); positions.put("5,1", "1,false"); positions.put("5,2", "3,true"); positions.put("5,3", "9,false"); positions.put("5,4", "2,false"); positions.put("5,5", "6,false"); positions.put("5,6", "8,false"); positions.put("5,7", "5,false"); positions.put("5,8", "4,true");
        positions.put("6,0", "9,false"); positions.put("6,1", "6,false"); positions.put("6,2", "1,true"); positions.put("6,3", "5,true"); positions.put("6,4", "3,true"); positions.put("6,5", "7,true"); positions.put("6,6", "2,false"); positions.put("6,7", "8,false"); positions.put("6,8", "9,false");
        positions.put("7,0", "2,false"); positions.put("7,1", "8,true"); positions.put("7,2", "4,false"); positions.put("7,3", "7,false"); positions.put("7,4", "6,true"); positions.put("7,5", "9,false"); positions.put("7,6", "1,false"); positions.put("7,7", "7,false"); positions.put("7,8", "4,false");
        positions.put("8,0", "3,false"); positions.put("8,1", "4,false"); positions.put("8,2", "5,false"); positions.put("8,3", "2,false"); positions.put("8,4", "8,false"); positions.put("8,5", "1,true"); positions.put("8,6", "3,false"); positions.put("8,7", "7,false"); positions.put("8,8", "6,false");
        return positions;
    }
}