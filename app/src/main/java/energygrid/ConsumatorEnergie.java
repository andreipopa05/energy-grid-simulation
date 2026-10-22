package energygrid;

import java.text.DecimalFormat;

public abstract class ConsumatorEnergie extends ComponentaRetea{
    private final double cerereEnergie;
    private int prioritate;
    private boolean esteAlimentat;

    protected ConsumatorEnergie(String id, double cerereEnergie) {
        super(id);
        this.cerereEnergie = cerereEnergie;
        this.esteAlimentat = true;
    }

    public double getCerereCurenta() {
        if (esteAlimentat && getStatusOperational()) {
            return this.cerereEnergie;
        } else {
            return 0;
        }
    }

    public boolean getEsteAlimentat() {
        return this.esteAlimentat;
    }

    public double getCerereTotala() {
        return this.cerereEnergie;
    }

    public int getPrioritate() {
        return this.prioritate;
    }

    public void setPrioritate(int prioritate) {
        this.prioritate = prioritate;
    }

    public void cupleazaLaRetea() {
        this.esteAlimentat = true;
    }

    public void decupleazaDeLaRetea() {
        this.esteAlimentat = false;
    }

    // Numele consumatorului
    protected abstract String getTypeName();

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("0.00");
        String status = this.getEsteAlimentat() ? "Alimentat" : "Decuplat";

        return "Consumator " + this.getId() + " (" + this.getTypeName() + ") - Cerere: "
                + df.format(this.getCerereTotala()) + " - Prioritate: " + this.getPrioritate()
                + " - Status: " + status;
    }

}
