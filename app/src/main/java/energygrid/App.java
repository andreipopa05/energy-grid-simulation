package energygrid;

import java.io.InputStream;
import java.util.Locale;
import java.util.Scanner;

public class App {
    private final Scanner scanner;

    public App(InputStream input) {
        this.scanner = new Scanner(input);
    }

    public void run() {

        GridController controller = new GridController();

        while (scanner.hasNextLine()) {
            String comanda = scanner.nextLine().trim();

            // Liniile goale se ignora
            if (comanda.isEmpty()) {
                continue;
            }

            // Tratarea fiecarei comenzi in parte si verificarea validitatii comenzii
            if (comanda.startsWith("0")) {

                if (controller.isInBlackout()) {
                    System.out.println("EROARE: Reteaua este in BLACKOUT. Simulare oprita.");
                    controller.addEvent("EROARE: Reteaua este in BLACKOUT. Simulare oprita.");
                } else {
                    String[] sectiuneComanda = comanda.split(" ");

                    if (sectiuneComanda.length < 4) {
                        System.out.println("EROARE: Format comanda invalid");
                        continue;
                    }

                    if (sectiuneComanda[1].equals("solar") || sectiuneComanda[1].equals("turbina") || sectiuneComanda[1].equals("reactor")) {
                        boolean idIsValid = checkId(controller, sectiuneComanda);

                        if (idIsValid) {

                            if (Integer.parseInt(sectiuneComanda[3]) <= 0) {
                                System.out.println("EROARE: Putere invalida");
                            } else {
                                ProducatorEnergie pr = getPr(sectiuneComanda);
                                controller.addProducer(pr);
                                System.out.println("S-a adaugat producatorul " + pr.getId() + " de tip " + sectiuneComanda[1]);
                            }

                        }

                    } else {
                        System.out.println("EROARE: Tip producator invalid");
                    }

                }
            } else if (comanda.startsWith("1")) {

                if (controller.isInBlackout()) {
                    System.out.println("EROARE: Reteaua este in BLACKOUT. Simulare oprita.");
                    controller.addEvent("EROARE: Reteaua este in BLACKOUT. Simulare oprita.");
                } else {
                    String[] sectiuneComanda = comanda.split(" ");

                    if (sectiuneComanda.length < 4) {
                        System.out.println("EROARE: Format comanda invalid");
                        continue;
                    }

                    if (sectiuneComanda[1].equals("suport_viata") || sectiuneComanda[1].equals("laborator") || sectiuneComanda[1].equals("iluminat")) {
                        boolean idIsValid = checkId(controller, sectiuneComanda);

                        if (idIsValid) {

                            if (Integer.parseInt(sectiuneComanda[3]) <= 0) {
                                System.out.println("EROARE: Cerere putere invalida");
                            } else {
                                ConsumatorEnergie cs = getCs(sectiuneComanda);
                                controller.addConsumer(cs);
                                System.out.println("S-a adaugat consumatorul " + cs.getId() + " de tip " + sectiuneComanda[1]);
                            }

                        }

                    } else {
                        System.out.println("EROARE: Tip consumator invalid");
                    }

                }

            } else if (comanda.startsWith("2")) {

                if (controller.isInBlackout()) {
                    System.out.println("EROARE: Reteaua este in BLACKOUT. Simulare oprita.");
                    controller.addEvent("EROARE: Reteaua este in BLACKOUT. Simulare oprita.");
                } else {
                    String[] sectiuneComanda = comanda.split(" ");

                    if (sectiuneComanda.length < 3) {
                        System.out.println("EROARE: Format comanda invalid");
                        continue;
                    }

                    boolean idIsValid = checkId(controller, sectiuneComanda);

                    if (idIsValid) {

                        if (Integer.parseInt(sectiuneComanda[2]) <= 0) {
                            System.out.println("EROARE: Capacitate invalida");
                        } else {
                            Baterie bt = new Baterie(sectiuneComanda[1], Double.parseDouble(sectiuneComanda[2]));
                            controller.addBattery(bt);
                            System.out.println("S-a adaugat bateria " + bt.getId() + " cu capacitatea " + bt.getCapacitateMaxima());
                        }

                    }

                }

            } else if (comanda.startsWith("3")) {
                String[] sectiuneComanda = comanda.split(" ");

                if (sectiuneComanda.length < 3) {
                    System.out.println("EROARE: Format comanda invalid");
                    continue;
                }

                if (!sectiuneComanda[1].contains(".") && !sectiuneComanda[2].contains(".")) {
                    System.out.println("EROARE: Factori invalizi");
                } else {

                    if (controller.isInBlackout()) {
                        System.out.println("EROARE: Reteaua este in BLACKOUT. Simulare oprita.");
                        controller.addEvent("EROARE: Reteaua este in BLACKOUT. Simulare oprita.");
                    } else {
                        controller.simuleazaTick(Double.parseDouble(sectiuneComanda[1]), Double.parseDouble(sectiuneComanda[2]));
                    }

                }

            } else if (comanda.startsWith("4")) {
                String[] sectiuneComanda = comanda.split(" ");

                if (sectiuneComanda.length < 3) {
                    System.out.println("EROARE: Format comanda invalid");
                    continue;
                }

                ComponentaRetea cmp = controller.findComponent(sectiuneComanda[1]);

                if (cmp != null) {

                    if (sectiuneComanda[2].equals("true")) {
                            cmp.setStatusOperational(true);
                            System.out.println("Componenta " + cmp.getId() + " este acum operationala.");

                    } else if (sectiuneComanda[2].equals("false")) {
                        cmp.setStatusOperational(false);
                        System.out.println("Componenta " + cmp.getId() + " este acum defecta.");
                    } else {
                        System.out.println("EROARE: Status invalid");
                    }

                } else {
                    System.out.println("EROARE: Nu exista componenta cu id-ul " + sectiuneComanda[1]);
                }

            } else if (comanda.startsWith("5")) {

                if (controller.getComponents().isEmpty()) {
                    System.out.println("Reteaua este goala.");
                } else {

                    if (!controller.isInBlackout()) {
                        System.out.println("Stare Retea: STABILA");
                        for (ComponentaRetea cmp : controller.getComponents()) {
                            System.out.println(cmp.toString());
                        }
                    } else {
                        System.out.println("Stare Retea: BLACKOUT");
                        for (ComponentaRetea cmp : controller.getComponents()) {
                            System.out.println(cmp.toString());
                        }
                    }

                }

            } else if (comanda.startsWith("6")) {

                if (controller.getEvents().isEmpty()) {
                    System.out.println("Istoric evenimente gol");
                } else {
                    for (String ev : controller.getEvents()) {
                        System.out.println(ev);
                    }
                }

            } else if (comanda.startsWith("7")) {

                System.out.println("Simulatorul se inchide.");
                break;
            } else {
                System.out.println("EROARE: Comanda necunoscuta.");
            }

        }

    }

    // Metoda ce construieste producatorul primit
    private static ProducatorEnergie getPr(String[] sectiuneComanda) {
        ProducatorEnergie pr;

        if (sectiuneComanda[1].equals("solar")) {
            pr = new PanouSolar(sectiuneComanda[2], Double.parseDouble(sectiuneComanda[3]));
        } else if (sectiuneComanda[1].equals("turbina")) {
            pr = new TurbinaEoliana(sectiuneComanda[2], Double.parseDouble(sectiuneComanda[3]));
        } else {
            pr = new ReactorNuclear(sectiuneComanda[2], Double.parseDouble(sectiuneComanda[3]));
        }

        return pr;
    }

    // Metoda ce construieste consumatorul primit
    private static ConsumatorEnergie getCs(String[] sectiuneComanda) {
        ConsumatorEnergie cs;

        if (sectiuneComanda[1].equals("suport_viata")) {
            cs = new SistemSuportViata(sectiuneComanda[2], Double.parseDouble(sectiuneComanda[3]));
        } else if (sectiuneComanda[1].equals("laborator")) {
            cs = new LaboratorStiintific(sectiuneComanda[2], Double.parseDouble(sectiuneComanda[3]));
        } else {
            cs = new SistemIluminat(sectiuneComanda[2], Double.parseDouble(sectiuneComanda[3]));
        }

        return cs;
    }

    // Metoda ce verifica validitatea ID-ului primit
    private static boolean checkId(GridController controller, String[] sectiuneComanda) {
        int contorComanda = 0;

        if (sectiuneComanda.length == 4) {
            contorComanda = 2;
        } else if (sectiuneComanda.length == 3) {
            contorComanda = 1;
        }

        if (controller.findComponent(sectiuneComanda[contorComanda]) != null) {
            System.out.println("EROARE: Exista deja o componenta cu id-ul " + sectiuneComanda[contorComanda]);
            return false;
        }

        return true;
    }

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);  // Acelasi separator zecimal pe orice calculator
        App app = new App(System.in);
        app.run();
    }
} 