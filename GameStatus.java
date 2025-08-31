package br.com.dio.model;

public enum GameStatus {
    IN_PROGRESS("Em Andamento"),
    ERROR("Com Erros"),
    FINISHED("Concluído");

    private final String label;

    GameStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}