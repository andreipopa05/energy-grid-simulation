package energygrid;


public class LaboratorStiintific extends ConsumatorEnergie{

    protected LaboratorStiintific(String id, double cerereEnergie) {
        super(id,cerereEnergie);
        setPrioritate(2);
    }


    @Override
    protected String getTypeName() {
        return "LaboratorStiintific";
    }

}
