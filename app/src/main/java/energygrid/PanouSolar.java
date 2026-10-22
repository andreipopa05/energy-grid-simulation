package energygrid;

import java.text.DecimalFormat;

public class PanouSolar extends ProducatorEnergie{

    protected PanouSolar(String id, double putereSpecifica) {
        super(id, putereSpecifica);
    }

    public double calculeazaProductie(double factorExtern) {
        if (!this.getStatusOperational()) {
            return 0;
        }
        return factorExtern * this.getPutereSpecifica();
    }

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("#.00");

        String formatPutere = df.format(this.getPutereSpecifica());

        String detalii = "Producator " + this.getId() + " (PanouSolar) - PutereBaza: " + formatPutere + " - Status: ";

        if (this.getStatusOperational()) {
            detalii = detalii.concat("Operational");
        } else {
            detalii = detalii.concat("Defect");
        }

        return detalii;
    }
}
