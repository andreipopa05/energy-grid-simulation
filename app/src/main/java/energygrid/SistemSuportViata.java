package energygrid;

import java.text.DecimalFormat;

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
    public String toString() {
        DecimalFormat df = new DecimalFormat("0.00");

        String formatCerere = df.format(this.getCerereCurenta());

        String detalii = "Consumator " + this.getId() + " (SistemSuportViata) - Cerere: " + formatCerere + " - Prioritate: " + this.getPrioritate() + " - Status: ";

        if (this.getEsteAlimentat()) {
            detalii = detalii.concat("Alimentat");
        } else {
            detalii = detalii.concat("Decuplat");
        }

        return detalii;
    }
}
