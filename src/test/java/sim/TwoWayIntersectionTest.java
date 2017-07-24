package sim;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import sim.TwoWayIntersection.Designation;

import java.util.concurrent.TimeUnit;

import static com.jayway.awaitility.Awaitility.await;
import static org.testng.Assert.*;

public class TwoWayIntersectionTest {

    @DataProvider
    public Object[][] getTestGroups() {
        return new Object[][]{
                { Designation.EAST_WEST }, { Designation.NORTH_SOUTH }
        };
    }

    @Test
    public void testInitialGoGroup() {
        TwoWayIntersection intersection =  new TwoWayIntersection(1, 1);
        assertNull(intersection.getGoGroup());
    }

    @Test(dataProvider = "getTestGroups")
    public void testActivateEmptyGoGroup(Designation group) {
        TwoWayIntersection intersection =  new TwoWayIntersection(0, 0);

        intersection.activeGo(group);
        assertEquals(intersection.getGoGroup(), null);
    }

    @Test(dataProvider = "getTestGroups")
    public void testActivateGoGroup(Designation group) {
        TwoWayIntersection intersection =  new TwoWayIntersection(2, 2);

        intersection.activeGo(group);
        assertTrue(checkGoState(intersection, group));
    }

    @Test(dataProvider = "getTestGroups")
    public void testActivateStopGroup(Designation group) {
        TwoWayIntersection intersection =  new TwoWayIntersection(2, 2);

        intersection.activeGo(group);
        assertTrue(checkGoState(intersection, group));

        intersection.activeStop(group);
        assertTrue(checkWarnState(intersection, group));

        await().atMost(intersection.getStopDelayMs() + 10, TimeUnit.MILLISECONDS)
               .until(() -> {
                   return checkStopState(intersection, group);
               });
    }

    @Test
    public void testTriggerSequence() {
        TwoWayIntersection intersection =  new TwoWayIntersection(2, 2);

        intersection.activeGo(Designation.NORTH_SOUTH); // trigger default to switch from
        intersection.triggerSequence();

        assertFalse(checkGoState(intersection, Designation.EAST_WEST));
        assertTrue(checkWarnState(intersection, Designation.NORTH_SOUTH));

        await().atMost(intersection.getStopDelayMs() + 1000, TimeUnit.MILLISECONDS)
               .until(() -> {
                   return checkStopState(intersection, Designation.NORTH_SOUTH);
               });

        assertTrue(checkGoState(intersection, Designation.EAST_WEST), "should of transitioned");
    }

    private boolean checkGoState(TwoWayIntersection intersection, Designation group) {
        return intersection.groupGoActivated(intersection.getLightsByGroup(group));
    }

    private boolean checkStopState(TwoWayIntersection intersection, Designation group) {
        return intersection.groupStopActivated(intersection.getLightsByGroup(group));
    }

    private boolean checkWarnState(TwoWayIntersection intersection, Designation group) {
        return intersection.groupWarnActivated(intersection.getLightsByGroup(group));
    }
}