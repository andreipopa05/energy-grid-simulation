package energygrid;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BaterieTest {

    @Test
    void chargeWithinCapacityStoresEverything() {
        Baterie baterie = new Baterie("b1", 100);

        assertEquals(0, baterie.incarca(40), 1e-9);
        assertEquals(40, baterie.getEnergieStocata(), 1e-9);
    }

    @Test
    void chargeBeyondCapacityReturnsTheSurplus() {
        Baterie baterie = new Baterie("b1", 100);
        baterie.incarca(80);

        assertEquals(30, baterie.incarca(50), 1e-9);
        assertEquals(100, baterie.getEnergieStocata(), 1e-9);
    }

    @Test
    void dischargeBeyondStoredReturnsOnlyWhatWasAvailable() {
        Baterie baterie = new Baterie("b1", 100);
        baterie.incarca(30);

        assertEquals(30, baterie.descarca(50), 1e-9);
        assertEquals(0, baterie.getEnergieStocata(), 1e-9);
    }

    @Test
    void faultyBatteryNeitherChargesNorDischarges() {
        Baterie baterie = new Baterie("b1", 100);
        baterie.incarca(50);
        baterie.setStatusOperational(false);

        // Bateria defecta intoarce tot ce i se ofera si nu da nimic
        assertEquals(20, baterie.incarca(20), 1e-9);
        assertEquals(0, baterie.descarca(20), 1e-9);
        assertEquals(50, baterie.getEnergieStocata(), 1e-9);
    }
}