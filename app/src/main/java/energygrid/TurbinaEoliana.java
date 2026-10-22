package energygrid;


public class TurbinaEoliana extends ProducatorEnergie{

    protected TurbinaEoliana(String id, double putereSpecifica) {
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
        return "TurbinaEoliana";
    }

}
