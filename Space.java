package br.com.dio.model;

public class Space {

    private final Integer expected;
    private final boolean fixed;
    private Integer actual;

    public Space(Integer expected, boolean fixed) {
        this.expected = expected;
        this.fixed = fixed;
        this.actual = fixed ? expected : null;
    }

    public Integer getExpected() {
        return expected;
    }

    public boolean isFixed() {
        return fixed;
    }

    public Integer getActual() {
        return actual;
    }

    public void setActual(Integer actual) {
        this.actual = actual;
    }

    public void clearSpace() {
        if (!fixed) {
            this.actual = null;
        }
    }
}