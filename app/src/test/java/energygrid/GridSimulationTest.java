package energygrid;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GridSimulationTest {
    private final PrintStream originalOut = System.out;
    private final ByteArrayOutputStream captured = new ByteArrayOutputStream();

    @BeforeAll
    static void setLocale() {
        Locale.setDefault(Locale.US);
    }

    @BeforeEach
    void captureOutput() {
        System.setOut(new PrintStream(captured, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void restoreOutput() {
        System.setOut(originalOut);
    }

    // Ruleaza aplicatia pe comenzile date si intoarce tot ce a afisat
    private String run(String... comenzi) {
        String input = String.join("\n", comenzi) + "\n";
        new App(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8))).run();
        return captured.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
    }

    private static String lines(String... linii) {
        return String.join("\n", linii) + "\n";
    }

    @Test
    void batteryIsChargedOnSurplusAndUsedOnDeficit() {
        String output = run(
                "0 solar s1 100",
                "1 laborator lab1 30",
                "2 b1 100",
                "3 1.0 0.5",
                "3 0.5 0.5",
                "3 0.0 0.0",
                "7");

        assertEquals(lines(
                "S-a adaugat producatorul s1 de tip solar",
                "S-a adaugat consumatorul lab1 de tip laborator",
                "S-a adaugat bateria b1 cu capacitatea 100.0",
                "TICK: Productie 100.00, Cerere 30.00. Baterii: 70.00 MW. Decuplati: []",
                "TICK: Productie 50.00, Cerere 30.00. Baterii: 90.00 MW. Decuplati: []",
                "TICK: Productie 0.00, Cerere 30.00. Baterii: 60.00 MW. Decuplati: []",
                "Simulatorul se inchide."), output);
    }

    @Test
    void triageDisconnectsLowestPriorityFirstAndThenBlackoutStopsTheGrid() {
        String output = run(
                "0 solar s1 100",
                "1 laborator lab1 60",
                "1 laborator lab2 50",
                "1 iluminat lum1 40",
                "1 suport_viata sv1 30",
                "2 b1 100",
                "3 1.0 0.5",
                "3 0.1 0.5",
                "3 0.0 0.0",
                "6",
                "7");

        assertEquals(lines(
                "S-a adaugat producatorul s1 de tip solar",
                "S-a adaugat consumatorul lab1 de tip laborator",
                "S-a adaugat consumatorul lab2 de tip laborator",
                "S-a adaugat consumatorul lum1 de tip iluminat",
                "S-a adaugat consumatorul sv1 de tip suport_viata",
                "S-a adaugat bateria b1 cu capacitatea 100.0",
                "TICK: Productie 100.00, Cerere 180.00. Baterii: 0.00 MW. Decuplati: [lum1, lab1]",
                "BLACKOUT! SIMULARE OPRITA.",
                "EROARE: Reteaua este in BLACKOUT. Simulare oprita.",
                "Tick 1: Deficit - Decuplat lum1 lab1",
                "Tick 2: BLACKOUT! SIMULARE OPRITA.",
                "EROARE: Reteaua este in BLACKOUT. Simulare oprita.",
                "Simulatorul se inchide."), output);
    }

    @Test
    void disconnectedListIsResetAtTheStartOfEveryTick() {
        // Regresie: lista consumatorilor decuplati nu trebuie sa pastreze ce s-a
        // decuplat in tick-urile anterioare
        String output = run(
                "0 solar s1 100",
                "1 laborator lab1 60",
                "1 iluminat lum1 50",
                "1 suport_viata sv1 30",
                "3 1.0 0.5",
                "3 0.5 0.5",
                "6",
                "7");

        assertEquals(lines(
                "S-a adaugat producatorul s1 de tip solar",
                "S-a adaugat consumatorul lab1 de tip laborator",
                "S-a adaugat consumatorul lum1 de tip iluminat",
                "S-a adaugat consumatorul sv1 de tip suport_viata",
                "TICK: Productie 100.00, Cerere 140.00. Baterii: 0.00 MW. Decuplati: [lum1]",
                "TICK: Productie 50.00, Cerere 140.00. Baterii: 0.00 MW. Decuplati: [lum1, lab1]",
                "Tick 1: Deficit - Decuplat lum1",
                "Tick 2: Deficit - Decuplat lum1 lab1",
                "Simulatorul se inchide."), output);
    }

    @Test
    void eachProducerTypeUsesItsOwnFactorAndAFaultyOneStopsProducing() {
        String output = run(
                "0 solar s1 100",
                "0 turbina t1 200",
                "0 reactor r1 50",
                "1 laborator lab1 100",
                "3 0.5 0.25",
                "4 r1 false",
                "3 0.5 0.25",
                "7");

        assertEquals(lines(
                "S-a adaugat producatorul s1 de tip solar",
                "S-a adaugat producatorul t1 de tip turbina",
                "S-a adaugat producatorul r1 de tip reactor",
                "S-a adaugat consumatorul lab1 de tip laborator",
                "TICK: Productie 150.00, Cerere 100.00. Baterii: 0.00 MW. Decuplati: []",
                "Componenta r1 este acum defecta.",
                "TICK: Productie 100.00, Cerere 100.00. Baterii: 0.00 MW. Decuplati: []",
                "Simulatorul se inchide."), output);
    }

    @Test
    void invalidInputPrintsErrorsAndTheProgramKeepsRunning() {
        String output = run(
                "0 solar s1 abc",
                "0 solar s1 100",
                "0 solar s1 50",
                "0solar s2 10",
                "3 abc 0.5",
                "3 1 1",
                "4 s1 maybe",
                "4 zzz true",
                "5",
                "7");

        assertEquals(lines(
                "EROARE: Putere invalida",
                "S-a adaugat producatorul s1 de tip solar",
                "EROARE: Exista deja o componenta cu id-ul s1",
                "EROARE: Comanda necunoscuta.",
                "EROARE: Factori invalizi",
                "EROARE: Factori invalizi",
                "EROARE: Status invalid",
                "EROARE: Nu exista componenta cu id-ul zzz",
                "Stare Retea: STABILA",
                "Producator s1 (PanouSolar) - PutereBaza: 100.00 - Status: Operational",
                "Simulatorul se inchide."), output);
    }
}