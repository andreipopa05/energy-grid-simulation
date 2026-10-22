package energygrid;

public class SistemSuportViata extends ConsumatorEnergie{

    protected SistemSuportViata(String id, double cerereEnergie) {
        super(id, cerereEnergie);
        setPrioritate(1);
    }

    @Override
    public double getCerereCurenta() {
        return super.getCerereTotala();
    }

    @Override
    protected String getTypeName() {
        return "SistemSuportViata";
    }

}
