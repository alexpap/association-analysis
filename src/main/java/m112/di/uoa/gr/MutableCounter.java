package m112.di.uoa.gr;

import java.util.Comparator;

/**
 * @author alexpap
 */
public class MutableCounter implements Comparator<MutableCounter>, Comparable<MutableCounter> {

    private int counter = 0;

    public void incr() { ++counter; }
    public int get() { return counter; }
    public void set(int value) { counter = value; }

    public int compare(MutableCounter mc1, MutableCounter mc2) {
        return Integer.compare(mc1.get(),mc1.get());
    }

    public int compareTo(MutableCounter mc) {
        return Integer.compare(this.get(), mc.get());
    }
}
