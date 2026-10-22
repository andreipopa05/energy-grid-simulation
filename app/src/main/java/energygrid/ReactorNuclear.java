package energygrid;


public class ReactorNuclear extends ProducatorEnergie{

    protected ReactorNuclear(String id, double putereSpecifica) {
        super(id, putereSpecifica);
    }

    public double calculeazaProductie(double factorExtern) {
        if (!this.getStatusOperational()) {
            return 0;
        }

        return this.getPutereSpecifica();
    }

    @Override
    protected String getTypeName() {
        return "ReactorNuclear";
    }

}
