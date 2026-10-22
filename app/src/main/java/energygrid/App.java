package energygrid;

import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class App {
    private static final String EROARE_BLACKOUT = "EROARE: Reteaua este in BLACKOUT. Simulare oprita.";
    private static final String EROARE_FORMAT = "EROARE: Format comanda invalid";
    private static final List<String> TIPURI_PRODUCATOR = List.of("solar", "turbina", "reactor");
    private static final List<String> TIPURI_CONSUMATOR = List.of("suport_viata", "laborator", "iluminat");

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
            String[] sectiuneComanda = comanda.split(" ");

            switch (sectiuneComanda[0]) {
                case "0" -> adaugaProducator(controller, sectiuneComanda);
                case "1" -> adaugaConsumator(controller, sectiuneComanda);
                case "2" -> adaugaBaterie(controller, sectiuneComanda);
                case "3" -> ruleazaTick(controller, sectiuneComanda);
                case "4" -> seteazaStatus(controller, sectiuneComanda);
                case "5" -> afiseazaStatus(controller);
                case "6" -> afiseazaIstoric(controller);
                case "7" -> {
                    System.out.println("Simulatorul se inchide.");
                    return;
                }
                default -> System.out.println("EROARE: Comanda necunoscuta.");
            }
        }

    }

    // Comanda 0: adauga un producator
    private static void adaugaProducator(GridController controller, String[] sectiuneComanda) {
        if (refuzaInBlackout(controller)) {
            return;
        }

        if (sectiuneComanda.length < 4) {
            System.out.println(EROARE_FORMAT);
            return;
        }

        if (!TIPURI_PRODUCATOR.contains(sectiuneComanda[1])) {
            System.out.println("EROARE: Tip producator invalid");
            return;
        }

        if (!checkId(controller, sectiuneComanda[2])) {
            return;
        }

        Double putere = parseNumar(sectiuneComanda[3]);
        if (putere == null || putere <= 0) {
            System.out.println("EROARE: Putere invalida");
            return;
        }

        ProducatorEnergie pr = getPr(sectiuneComanda);
        controller.addProducer(pr);
        System.out.println("S-a adaugat producatorul " + pr.getId() + " de tip " + sectiuneComanda[1]);
    }

    // Comanda 1: adauga un consumator
    private static void adaugaConsumator(GridController controller, String[] sectiuneComanda) {
        if (refuzaInBlackout(controller)) {
            return;
        }

        if (sectiuneComanda.length < 4) {
            System.out.println(EROARE_FORMAT);
            return;
        }

        if (!TIPURI_CONSUMATOR.contains(sectiuneComanda[1])) {
            System.out.println("EROARE: Tip consumator invalid");
            return;
        }

        if (!checkId(controller, sectiuneComanda[2])) {
            return;
        }

        Double cerere = parseNumar(sectiuneComanda[3]);
        if (cerere == null || cerere <= 0) {
            System.out.println("EROARE: Cerere putere invalida");
            return;
        }

        ConsumatorEnergie cs = getCs(sectiuneComanda);
        controller.addConsumer(cs);
        System.out.println("S-a adaugat consumatorul " + cs.getId() + " de tip " + sectiuneComanda[1]);
    }

    // Comanda 2: adauga o baterie
    private static void adaugaBaterie(GridController controller, String[] sectiuneComanda) {
        if (refuzaInBlackout(controller)) {
            return;
        }

        if (sectiuneComanda.length < 3) {
            System.out.println(EROARE_FORMAT);
            return;
        }

        if (!checkId(controller, sectiuneComanda[1])) {
            return;
        }

        Double capacitate = parseNumar(sectiuneComanda[2]);
        if (capacitate == null || capacitate <= 0) {
            System.out.println("EROARE: Capacitate invalida");
            return;
        }

        Baterie bt = new Baterie(sectiuneComanda[1], capacitate);
        controller.addBattery(bt);
        System.out.println("S-a adaugat bateria " + bt.getId() + " cu capacitatea " + bt.getCapacitateMaxima());
    }

    // Comanda 3: ruleaza un tick de simulare
    private static void ruleazaTick(GridController controller, String[] sectiuneComanda) {
        if (sectiuneComanda.length < 3) {
            System.out.println(EROARE_FORMAT);
            return;
        }

        Double factorSoare = parseNumar(sectiuneComanda[1]);
        Double factorVant = parseNumar(sectiuneComanda[2]);

        // Factorii trebuie sa fie numere, iar cel putin unul sa contina punct zecimal
        if (factorSoare == null || factorVant == null
                || (!sectiuneComanda[1].contains(".") && !sectiuneComanda[2].contains("."))) {
            System.out.println("EROARE: Factori invalizi");
            return;
        }

        if (refuzaInBlackout(controller)) {
            return;
        }

        controller.simuleazaTick(factorSoare, factorVant);
    }

    // Comanda 4: seteaza statusul operational al unei componente
    private static void seteazaStatus(GridController controller, String[] sectiuneComanda) {
        if (sectiuneComanda.length < 3) {
            System.out.println(EROARE_FORMAT);
            return;
        }

        ComponentaRetea cmp = controller.findComponent(sectiuneComanda[1]);

        if (cmp == null) {
            System.out.println("EROARE: Nu exista componenta cu id-ul " + sectiuneComanda[1]);
            return;
        }

        if (sectiuneComanda[2].equals("true")) {
            cmp.setStatusOperational(true);
            System.out.println("Componenta " + cmp.getId() + " este acum operationala.");
        } else if (sectiuneComanda[2].equals("false")) {
            cmp.setStatusOperational(false);
            System.out.println("Componenta " + cmp.getId() + " este acum defecta.");
        } else {
            System.out.println("EROARE: Status invalid");
        }
    }

    // Comanda 5: afiseaza starea retelei si a fiecarei componente
    private static void afiseazaStatus(GridController controller) {
        if (controller.getComponents().isEmpty()) {
            System.out.println("Reteaua este goala.");
            return;
        }

        if (controller.isInBlackout()) {
            System.out.println("Stare Retea: BLACKOUT");
        } else {
            System.out.println("Stare Retea: STABILA");
        }

        for (ComponentaRetea cmp : controller.getComponents()) {
            System.out.println(cmp);
        }
    }

    // Comanda 6: afiseaza istoricul de evenimente
    private static void afiseazaIstoric(GridController controller) {
        if (controller.getEvents().isEmpty()) {
            System.out.println("Istoric evenimente gol");
            return;
        }

        for (String ev : controller.getEvents()) {
            System.out.println(ev);
        }
    }

    // Daca reteaua e in blackout, afisez eroarea, o salvez in istoric si intorc true
    private static boolean refuzaInBlackout(GridController controller) {
        if (!controller.isInBlackout()) {
            return false;
        }

        System.out.println(EROARE_BLACKOUT);
        controller.addEvent(EROARE_BLACKOUT);
        return true;
    }

    // Intoarce numarul din text sau null daca textul nu e un numar valid
    private static Double parseNumar(String text) {
        try {
            double numar = Double.parseDouble(text);
            return Double.isFinite(numar) ? numar : null;
        } catch (NumberFormatException e) {
            return null;
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
    private static boolean checkId(GridController controller, String id) {
        if (controller.findComponent(id) != null) {
            System.out.println("EROARE: Exista deja o componenta cu id-ul " + id);
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