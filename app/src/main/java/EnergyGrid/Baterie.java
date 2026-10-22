package EnergyGrid;

import java.text.DecimalFormat;

public class Baterie extends ComponentaRetea{
    private final double capacitateMaxima;
    private double energieStocata = 0;

    protected Baterie(String id, double capacitateMaxima) {
        super(id);
        this.capacitateMaxima = capacitateMaxima;
    }

    public double getEnergieStocata() {
        return this.energieStocata;
    }

    public double getCapacitateMaxima() {
        return this.capacitateMaxima;
    }

    public double incarca(double energieDisponibila) {
        if (getStatusOperational()) {
            double energieCurenta = this.energieStocata + energieDisponibila;

            if (energieCurenta > this.capacitateMaxima) {
                this.energieStocata = this.capacitateMaxima;
                return energieCurenta - this.capacitateMaxima;
            }

            this.energieStocata = energieCurenta;

            return 0;
        }

        return energieDisponibila;
    }

    public double descarca(double energieCeruta) {
        if (getStatusOperational()) {
            double energieCurenta = this.energieStocata - energieCeruta;

            if (energieCurenta < 0) {
                double energieFurnizata = this.energieStocata;

                this.energieStocata = 0;

                return energieFurnizata;
            }

            this.energieStocata = energieCurenta;

            return energieCeruta;
        }

        return 0;
    }

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("0.00");

        String formatenergie = df.format(this.getEnergieStocata());
        String formatCapacitate = df.format(this.capacitateMaxima);

        String detalii = "Baterie " + this.getId() + " - Stocare: " + formatenergie + "/" + formatCapacitate + " - Status: ";

        if (this.getStatusOperational()) {
            detalii = detalii.concat("Operational");
        } else {
            detalii = detalii.concat("Defect");
        }

        return detalii;
    }

}
