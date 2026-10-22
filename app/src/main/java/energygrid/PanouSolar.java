package energygrid;

public class PanouSolar extends ProducatorEnergie{

    protected PanouSolar(String id, double putereSpecifica) {
        super(id, putereSpecifica);
    }

    public double calculeazaProductie(double factorExtern) {
        if (!this.getStatusOperational()) {
            return 0;
        }
        return factorExtern * this.getPutereSpecifica();
    }

    @Override
    protected String getTypeName() {
        return "PanouSolar";
    }

}
