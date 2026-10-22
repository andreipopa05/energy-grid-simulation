package energygrid;

import java.text.DecimalFormat;
import java.util.*;

public class GridController {
    private final List<ConsumatorEnergie> consumatori = new ArrayList<>();
    private final List<ProducatorEnergie> producatori = new ArrayList<>();
    private final List<Baterie> baterii = new ArrayList<>();
    private final List<ComponentaRetea> componente = new ArrayList<>();
    private final List<ConsumatorEnergie> decuplati = new ArrayList<>();
    private final List<String> evenimente = new ArrayList<>();
    private boolean esteInBlackout = false;
    private int tickContor = 0;

    public boolean isInBlackout() {
        return esteInBlackout;
    }

    public void addEvent(String message) {
        evenimente.add(message);
    }

    public List<String> getEvents() {
        return Collections.unmodifiableList(evenimente);
    }

    public List<ComponentaRetea> getComponents() {
        return Collections.unmodifiableList(componente);
    }

    public void addProducer(ProducatorEnergie producer) {
        producatori.add(producer);
        componente.add(producer);
    }

    public void addConsumer(ConsumatorEnergie consumer) {
        consumatori.add(consumer);
        componente.add(consumer);
    }

    public void addBattery(Baterie battery) {
        baterii.add(battery);
        componente.add(battery);
    }

    // Intoarce componenta cu id-ul dat sau null
    public ComponentaRetea findComponent(String id) {
        for (ComponentaRetea cmp : componente) {
            if (cmp.getId().equals(id)) {
                return cmp;
            }
        }
        return null;
    }

    public void simuleazaTick(double factorSoare, double factorVant) {
        tickContor++;
        decuplati.clear();

        // Daca reteaua e deja in blackout, simularea nu mai continua
        if (esteInBlackout) {
            return;
        }

        // Cuplez consumatorii
        for (ConsumatorEnergie cons : consumatori) {
            cons.cupleazaLaRetea();
        }

        // Calculez productia totala
        double productieTotala = 0;
        for (ProducatorEnergie prod : producatori) {
            productieTotala += prod.calculeazaProductie(factorSoare, factorVant);
        }

        // Calculez cererea totala
        double cerereTotala = 0;
        for (ConsumatorEnergie cons : consumatori) {
            cerereTotala += cons.getCerereCurenta();
        }

        double delta = productieTotala - cerereTotala;

        // Verific in ce caz ma aflu
        if (delta >= 0) {
            // Surplus
            incarcaBaterii(delta);
            System.out.println(getOutput(cerereTotala, productieTotala));
            return;
        }

        // Deficit
        double deficit = descarcaBaterii(-delta);
        if (deficit <= 0) {
            System.out.println(getOutput(cerereTotala, productieTotala));
            return;
        }

        // Triage
        deficit = decupleazaConsumatori(deficit);
        if (deficit <= 0) {
            evenimente.add("Tick " + tickContor + ": Deficit - Decuplat " + String.join(" ", getIdDecuplati()));
            System.out.println(getOutput(cerereTotala, productieTotala));
            return;
        }

        // Blackout
        evenimente.add("Tick " + tickContor + ": BLACKOUT! SIMULARE OPRITA.");
        System.out.println("BLACKOUT! SIMULARE OPRITA.");
        esteInBlackout = true;
    }

    // Ofer surplusul bateriilor, pe rand; ce nu incape se pierde
    private void incarcaBaterii(double surplus) {
        double ramas = surplus;

        for (Baterie bat : baterii) {
            ramas = bat.incarca(ramas);
        }
    }

    // Iau energie din baterii; intorc deficitul care ramane neacoperit
    private double descarcaBaterii(double deficit) {
        double ramas = deficit;

        for (Baterie bat : baterii) {
            ramas -= bat.descarca(ramas);
        }

        return ramas;
    }

    // Decuplez consumatorii (intai prioritatea 3, apoi 2, niciodata 1) pana se
    // acopera deficitul; intorc deficitul care ramane neacoperit
    private double decupleazaConsumatori(double deficit) {
        double ramas = deficit;

        // Sortez o copie a listei dupa prioritate (cea mai mare intai); sortarea
        // e stabila, deci la prioritate egala ramane ordinea in care au fost adaugati
        List<ConsumatorEnergie> sortati = new ArrayList<>(consumatori);
        sortati.sort((a, b) -> Integer.compare(b.getPrioritate(), a.getPrioritate()));

        for (ConsumatorEnergie cons : sortati) {
            if (cons.getPrioritate() > 1 && ramas > 0) {
                decuplati.add(cons);
                ramas -= cons.getCerereCurenta();  // Citesc cererea inainte de decuplare
                cons.decupleazaDeLaRetea();
            }
        }

        return ramas;
    }

    private List<String> getIdDecuplati() {
        List<String> id = new ArrayList<>();

        for (ConsumatorEnergie cons : decuplati) {
            id.add(cons.getId());
        }

        return id;
    }

    // Metoda pentru a genera feedback-ul unui output (lista decuplatilor e goala daca nu s-a decuplat nimeni)
    private String getOutput(double cerereTotala, double productieTotala) {
        double statusBaterii = 0;

        for (Baterie bat : baterii) {
            statusBaterii += bat.getEnergieStocata();
        }

        DecimalFormat df = new DecimalFormat("0.00");

        return "TICK: Productie " + df.format(productieTotala) + ", Cerere " + df.format(cerereTotala)
                + ". Baterii: " + df.format(statusBaterii) + " MW. Decuplati: ["
                + String.join(", ", getIdDecuplati()) + "]";
    }
}