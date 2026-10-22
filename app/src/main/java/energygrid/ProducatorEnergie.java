package energygrid;
import java.text.DecimalFormat;

public abstract class ProducatorEnergie extends ComponentaRetea{
    private final double putereSpecifica;

    protected ProducatorEnergie(String id, double putereSpecifica) {
        super(id);
        this.putereSpecifica = putereSpecifica;
    }

    public double getPutereSpecifica() {
        return this.putereSpecifica;
    }

    // Fiecare producator alege factorul extern care il intereseaza
    public abstract double calculeazaProductie(double factorSoare, double factorVant);

    // Numele producatorului
    protected abstract String getTypeName();

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("#.00");
        String status = this.getStatusOperational() ? "Operational" : "Defect";

        return "Producator " + this.getId() + " (" + this.getTypeName() + ") - PutereBaza: "
                + df.format(this.getPutereSpecifica()) + " - Status: " + status;
    }

}
