package EnergyGrid;

import java.text.DecimalFormat;

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
    public String toString() {
        DecimalFormat df = new DecimalFormat("#.00");

        String formatPutere = df.format(this.getPutereSpecifica());

        String detalii = "Producator " + this.getId() + " (ReactorNuclear) - PutereBaza: " + formatPutere + " - Status: ";

        if (this.getStatusOperational()) {
            detalii = detalii.concat("Operational");
        } else {
            detalii = detalii.concat("Defect");
        }

        return detalii;
    }
}
