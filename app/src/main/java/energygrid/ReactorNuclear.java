package energygrid;

public class ReactorNuclear extends ProducatorEnergie{

    protected ReactorNuclear(String id, double putereSpecifica) {
        super(id, putereSpecifica);
    }

    @Override
    public double calculeazaProductie(double factorSoare, double factorVant) {
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
