package EnergyGrid;

import java.text.DecimalFormat;

public class SistemIluminat extends ConsumatorEnergie{

    protected SistemIluminat(String id, double cerereEnergie) {
        super(id, cerereEnergie);
        setPrioritate(3);
    }

    @Override
    public double getCerereCurenta() {
        return super.getCerereCurenta();
    }

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("0.00");

        String formatCerere = df.format(this.getCerereTotala());

        String detalii = "Consumator " + this.getId() + " (SistemIluminat) - Cerere: " + formatCerere + " - Prioritate: " + this.getPrioritate() + " - Status: ";

        if (this.getEsteAlimentat()) {
            detalii = detalii.concat("Alimentat");
        } else {
            detalii = detalii.concat("Decuplat");
        }

        return detalii;
    }

}
