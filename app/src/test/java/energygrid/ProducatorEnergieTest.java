package energygrid;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProducatorEnergieTest {

    @Test
    void solarPanelUsesTheSunFactor() {
        PanouSolar panou = new PanouSolar("s1", 100);

        assertEquals(50, panou.calculeazaProductie(0.5, 0.25), 1e-9);
    }

    @Test
    void windTurbineUsesTheWindFactor() {
        TurbinaEoliana turbina = new TurbinaEoliana("t1", 200);

        assertEquals(50, turbina.calculeazaProductie(0.5, 0.25), 1e-9);
    }

    @Test
    void nuclearReactorIgnoresBothFactors() {
        ReactorNuclear reactor = new ReactorNuclear("r1", 80);

        assertEquals(80, reactor.calculeazaProductie(0.5, 0.25), 1e-9);
        assertEquals(80, reactor.calculeazaProductie(0, 0), 1e-9);
    }

    @Test
    void faultyProducersProduceNothing() {
        PanouSolar panou = new PanouSolar("s1", 100);
        TurbinaEoliana turbina = new TurbinaEoliana("t1", 100);
        ReactorNuclear reactor = new ReactorNuclear("r1", 100);

        panou.setStatusOperational(false);
        turbina.setStatusOperational(false);
        reactor.setStatusOperational(false);

        assertEquals(0, panou.calculeazaProductie(1.0, 1.0), 1e-9);
        assertEquals(0, turbina.calculeazaProductie(1.0, 1.0), 1e-9);
        assertEquals(0, reactor.calculeazaProductie(1.0, 1.0), 1e-9);
    }
}