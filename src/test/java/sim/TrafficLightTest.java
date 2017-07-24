package sim;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import sim.data.Display;

import java.util.Collections;

import static org.testng.Assert.assertEquals;
import static sim.TrafficLight.Color.*;

public class TrafficLightTest {

    private TrafficLight trafficLight;

    @BeforeMethod
    public void setup() {
        trafficLight = new TrafficLight(Collections.emptyList(), "test");
    }

    @Test
    public void testInactiveState() {
        assertEquals(trafficLight.query(), new Display(GREY, GREY, RED));
    }

    @Test
    public void testGoState() {
        trafficLight.setGo();
        assertEquals(trafficLight.query(), new Display(GREEN, GREY, GREY));
    }

    @Test
    public void testStopState() {
        trafficLight.setStop();
        assertEquals(trafficLight.query(), new Display(GREY, GREY, RED));
    }

    @Test
    public void testWarnState() {
        trafficLight.setWarn();
        assertEquals(trafficLight.query(), new Display(GREY, YELLOW, GREY));
    }
}
