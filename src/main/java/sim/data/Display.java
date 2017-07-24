package sim.data;

import sim.TrafficLight;

public class Display {
    public final TrafficLight.Color top;
    public final TrafficLight.Color mid;
    public final TrafficLight.Color bot;

    public Display(TrafficLight.Color top, TrafficLight.Color mid, TrafficLight.Color bot) {
        this.top = top;
        this.mid = mid;
        this.bot = bot;
    }

    @Override
    public String toString() {
        return "\tDisplay [" + top + ", " + mid + ", " + bot + ']';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Display)) return false;

        Display display = (Display) o;

        if (top != display.top) return false;
        if (mid != display.mid) return false;
        return bot == display.bot;

    }
}
