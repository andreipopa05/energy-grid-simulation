package Tema1;

public abstract class ProducatorEnergie extends ComponentaRetea{
    private final double putereSpecifica;

    protected ProducatorEnergie(String id, double putereSpecifica) {
        super(id);
        this.putereSpecifica = putereSpecifica;
    }

    public double getPutereSpecifica() {
        return this.putereSpecifica;
    }

    public abstract double calculeazaProductie(double factorExtern);

    public String toString() {
        return super.toString();
    }

}
