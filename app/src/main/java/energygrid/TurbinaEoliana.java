package energygrid;

public class TurbinaEoliana extends ProducatorEnergie{

    protected TurbinaEoliana(String id, double putereSpecifica) {
        super(id, putereSpecifica);
    }

    @Override
    public double calculeazaProductie(double factorSoare, double factorVant) {
        if (!this.getStatusOperational()) {
            return 0;
        }

        return factorVant * this.getPutereSpecifica();
    }

    @Override
    protected String getTypeName() {
        return "TurbinaEoliana";
    }

}
