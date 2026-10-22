package energygrid;

public class PanouSolar extends ProducatorEnergie{

    protected PanouSolar(String id, double putereSpecifica) {
        super(id, putereSpecifica);
    }

    @Override
    public double calculeazaProductie(double factorSoare, double factorVant) {
        if (!this.getStatusOperational()) {
            return 0;
        }
        return factorSoare * this.getPutereSpecifica();
    }

    @Override
    protected String getTypeName() {
        return "PanouSolar";
    }

}
