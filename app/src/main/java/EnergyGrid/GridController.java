package EnergyGrid;

import java.text.DecimalFormat;
import java.util.*;

public class GridController {
    final List<ConsumatorEnergie> consumatori = new ArrayList<>();
    final List<ProducatorEnergie> producatori = new ArrayList<>();
    private final List<Baterie> baterii = new ArrayList<>();
    final List<ComponentaRetea> componente = new ArrayList<>();
    private final List<ConsumatorEnergie> decuplati = new ArrayList<>();
    final List<String> evenimente = new ArrayList<>();
    boolean esteInBlackout = false;
    private int TickContor = 0;

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
        TickContor++;
        decuplati.clear();

        // Tick-ul ruleaza pana la blackout sau caz satisfacator
        while (!esteInBlackout) {
            double productieTotala = 0;
            double cerereTotala = 0;
            double delta;

            // Cuplez consumatorii
            for (ConsumatorEnergie cons : consumatori) {
                cons.cupleazaLaRetea();
            }

            // Calculez productia totala
            for (ProducatorEnergie prod : producatori) {
                    if (prod instanceof PanouSolar) {
                        productieTotala += prod.calculeazaProductie(factorSoare);
                    } else if (prod instanceof TurbinaEoliana) {
                        productieTotala += prod.calculeazaProductie(factorVant);
                    } else {
                        productieTotala += prod.calculeazaProductie(0);
                    }
            }

            // Calculez cererea totala
            for (ConsumatorEnergie cons : consumatori) {
                cerereTotala += cons.getCerereCurenta();
            }

            delta = productieTotala - cerereTotala;

            // Verific in ce caz ma aflu
            if (delta >= 0) {
                // Surplus

                for (Baterie bat : baterii) {
                    delta = bat.incarca(delta);
                }

                getOutputSuccess(productieTotala, cerereTotala, baterii);

                break;

            } else if (delta < 0){
                // Deficit

                double deficit = -delta;

                for(Baterie bat : baterii) {
                    double energieDeLaBaterie = bat.descarca(deficit);
                    deficit -= energieDeLaBaterie;
                }

                if (deficit <= 0) {
                    getOutputSuccess(productieTotala, cerereTotala, baterii);
                    break;
                }

                if (deficit > 0) {
                    // Triage

                    for (int i = 0; i < consumatori.size() - 1; i++) {
                        for (int j = i + 1; j < consumatori.size(); j++) {
                            if (consumatori.get(i).getPrioritate() < consumatori.get(j).getPrioritate()) {
                                Collections.swap(consumatori, i, j);
                            }
                        }
                    }

                    for (ConsumatorEnergie cons : consumatori) {
                        if (cons.getPrioritate() > 1 && deficit > 0) {
                            decuplati.add(cons);
                            deficit -= cons.getCerereCurenta();
                            cons.decupleazaDeLaRetea();
                        }
                    }

                    if (deficit <= 0) {
                        String eveniment = "Tick " + TickContor + ": Deficit - Decuplat ";

                        for (int i = 0; i < decuplati.size(); i++) {
                            eveniment = eveniment.concat(decuplati.get(i).getId());
                            if (i < decuplati.size() - 1) {
                                eveniment = eveniment.concat(" ");
                            }
                        }

                        evenimente.add(eveniment);

                        String output = getOutput(cerereTotala, productieTotala);
                        System.out.println(output);

                        break;
                    }

                    if (deficit > 0) {
                        // Blackout

                        String eveniment = "Tick " + TickContor + ": BLACKOUT! SIMULARE OPRITA.";

                        evenimente.add(eveniment);
                        System.out.println("BLACKOUT! SIMULARE OPRITA.");

                        esteInBlackout = true;
                    }
                }
            }
        }

    }

    // Metoda pentru a genera feedback-ul unui  output cu succes
    private static void getOutputSuccess(double productieTotala, double cerereTotala, List<Baterie> baterii) {
        double statusBaterii = 0;

        for (Baterie bat : baterii) {
            statusBaterii += bat.getEnergieStocata();
        }

        DecimalFormat df = new DecimalFormat("0.00");

        String formatStatus = df.format(statusBaterii);
        String formatCerere = df.format(cerereTotala);
        String formatProductie = df.format(productieTotala);

        System.out.println("TICK: Productie " + formatProductie + ", Cerere " + formatCerere + ". Baterii: " + formatStatus + " MW. Decuplati: []");
    }

    // Metoda pentru a genera feedback pentru un output fara succes
    private String getOutput(double cerereTotala, double productieTotala) {
        double statusBaterii = 0;

        for (Baterie bat : baterii) {
            statusBaterii += bat.getEnergieStocata();
        }

        DecimalFormat df = new DecimalFormat("0.00");

        String formatStatus = df.format(statusBaterii);
        String formatCerere = df.format(cerereTotala);
        String formatProductie = df.format(productieTotala);

        String output = "TICK: Productie " + formatProductie + ", Cerere " + formatCerere + ". Baterii: " + formatStatus + " MW. Decuplati: [";

        for (int i = 0; i < decuplati.size(); i++) {
            output = output.concat(decuplati.get(i).getId());

            if (i < decuplati.size() - 1) {
                output = output.concat(", ");
            }
        }

        output = output.concat("]");

        return output;
    }
}
