package sim;

import sim.data.Display;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class TrafficLight {

    public enum Color { GREEN, YELLOW, RED, GREY }
    public enum Position { TOP, MID, BOT}

    private final List<Consumer<Display>> subscribers;
    private final ConcurrentHashMap<Position, Boolean> indicators;

    private final Supplier<Color> greenLight, yellowLight, redLight;

    private final String designation;

    public TrafficLight(List<Consumer<Display>> subscribers,  String group) {
        this.subscribers = subscribers;
        this.designation = group;

        indicators = new ConcurrentHashMap<>();
        indicators.put(Position.TOP, false);
        indicators.put(Position.MID, false);
        indicators.put(Position.BOT, true);

        greenLight = () -> indicators.get(Position.TOP)? Color.GREEN : Color.GREY;
        yellowLight = () -> indicators.get(Position.MID)? Color.YELLOW : Color.GREY;
        redLight = () -> indicators.get(Position.BOT)? Color.RED : Color.GREY;
    }

    Display query() {
        return new Display(greenLight.get(), yellowLight.get(), redLight.get());
    }

    public void setGo() {
        update(true, false, false);
    }

    public void setWarn() {
        update(false, true, false);
    }

    public void setStop() {
        update(false, false, true);
    }

    public boolean isGo() {
        return indicators.get(Position.TOP) && !indicators.get(Position.MID) && !indicators.get(Position.BOT);
    }

    public boolean isWarn() {
        return !indicators.get(Position.TOP) && indicators.get(Position.MID) && !indicators.get(Position.BOT);
    }

    public boolean isStop() {
        return !indicators.get(Position.TOP) && !indicators.get(Position.MID) && indicators.get(Position.BOT);
    }

    private void update(boolean top, boolean mid, boolean bot) {
        indicators.put(Position.TOP, top);
        indicators.put(Position.MID, mid);
        indicators.put(Position.BOT, bot);
        emit();
    }

    private void emit() {
        final Display display = query();
        System.out.println(LocalDateTime.now() + " ## " + designation + " ## Traffic light changed:");
        subscribers.forEach(sub -> sub.accept(display));
    }

}
