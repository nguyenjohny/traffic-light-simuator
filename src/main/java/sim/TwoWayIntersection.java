package sim;

import sim.data.Display;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class TwoWayIntersection {

    public static final Designation DEFAULT_STARTER = Designation.NORTH_SOUTH;

    public enum Designation { EAST_WEST, NORTH_SOUTH }

    private ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(2);
    private ScheduledFuture<?> future;
    private Map<Designation, List<TrafficLight>> lights = new HashMap<>();

    private long stopDelayMs = 100;

    public TwoWayIntersection(int numEastWest, int numNorthSouth) {
        addLightGroup(numEastWest, Designation.EAST_WEST);
        addLightGroup(numNorthSouth, Designation.NORTH_SOUTH);
    }

    private void addLightGroup(int num, Designation group) {
        final List<Consumer<Display>> listeners = Collections.singletonList(System.out::println); // default some print listeners.

        lights.put(group, new ArrayList<>());
        IntStream.rangeClosed(1, num).forEach((in) -> {
            lights.get(group).add(
                new TrafficLight(listeners, group.toString() + '(' + in + " of " + num + ')')
            );
        });
    }

    public void start(long intervalMs, long stopDelayMs) {
        this.stopDelayMs = stopDelayMs;
        activeGo(DEFAULT_STARTER);
        future = executor.scheduleAtFixedRate(this::triggerSequence, 0, intervalMs, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        future.cancel(true);
    }

    public void triggerSequence() {
        final Designation goGroup = getGoGroup();
        final Designation stoppedGroup = (goGroup == Designation.NORTH_SOUTH) ? Designation.EAST_WEST : Designation.NORTH_SOUTH;
        activeStop(goGroup).thenRun(() -> {
            activeGo(stoppedGroup);
        });
    }

    void activeGo(Designation group) {
        lights.getOrDefault(group, Collections.emptyList())
              .forEach(TrafficLight::setGo);
    }

    public long getStopDelayMs() {
        return stopDelayMs;
    }

    CompletableFuture<Void> activeStop(Designation group) {
        CompletableFuture<Void> v = new CompletableFuture<>();
        lights.getOrDefault(group, Collections.emptyList())
                .forEach(trafficLight -> {
                    trafficLight.setWarn();
                    executor.schedule(() -> {
                        trafficLight.setStop();
                        v.complete(null);
                    }, getStopDelayMs(), TimeUnit.MILLISECONDS);
                });
        return v;
    }

    Designation getGoGroup() {
         return lights.entrySet().stream().filter(e -> groupGoActivated(e.getValue()))
                    .map(Map.Entry::getKey)
                    .findFirst().orElse(null);
    }

    List<TrafficLight> getLightsByGroup(Designation group) {
        return lights.get(group);
    }

    boolean groupGoActivated(List<TrafficLight> list) {
        return !list.isEmpty() && list.stream().allMatch(TrafficLight::isGo);
    }

    boolean groupWarnActivated(List<TrafficLight> list) {
        return !list.isEmpty() && list.stream().allMatch(TrafficLight::isWarn);
    }

    boolean groupStopActivated(List<TrafficLight> list) {
        return !list.isEmpty() && list.stream().allMatch(TrafficLight::isStop);
    }

    public static void main(String[] args) throws InterruptedException {
        TwoWayIntersection intersection =  new TwoWayIntersection(1, 1);
        final long switchMs = 5 * 60 * 1000; // 5 mins
        final long stopDelayMs = 30 * 1000; // 5 mins
        intersection.start(switchMs, stopDelayMs);

        Thread.currentThread().join();
        intersection.stop();
    }

}
