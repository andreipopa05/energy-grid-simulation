package energygrid;


public class SistemIluminat extends ConsumatorEnergie{

    protected SistemIluminat(String id, double cerereEnergie) {
        super(id, cerereEnergie);
        setPrioritate(3);
    }


    @Override
    protected String getTypeName() {
        return "SistemIluminat";
    }

}
