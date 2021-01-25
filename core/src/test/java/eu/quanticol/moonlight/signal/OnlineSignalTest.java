package eu.quanticol.moonlight.signal;

import eu.quanticol.moonlight.domain.AbstractInterval;
import eu.quanticol.moonlight.domain.DoubleDomain;
import eu.quanticol.moonlight.domain.Interval;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OnlineSignalTest {

    @Test
    void refine1() {
        DoubleDomain domain = new DoubleDomain();
        AbstractInterval<Double> data = new AbstractInterval<>(30.0,10.0);
        AbstractInterval<Double> any = new AbstractInterval<>(
                Double.NEGATIVE_INFINITY,
                Double.POSITIVE_INFINITY);
        OnlineSignal<Double> signal  = new OnlineSignal<>(domain.min(), domain.max());

        signal.refine(0, 10, data);

        assertEquals(data, signal.getValueAt(0));
        assertEquals(data, signal.getValueAt(5));
        assertEquals(any, signal.getValueAt(10));
        assertEquals(any, signal.getValueAt(600));

        System.out.println(signal);
    }

    @Test
    void refine2() {
        DoubleDomain domain = new DoubleDomain();
        AbstractInterval<Double> data = new AbstractInterval<>(30.0,10.0);
        AbstractInterval<Double> any = new AbstractInterval<>(
                Double.NEGATIVE_INFINITY,
                Double.POSITIVE_INFINITY);
        OnlineSignal<Double> signal  = new OnlineSignal<>(domain.min(), domain.max());

        signal.refine(0, 10, data);

        AbstractInterval<Double> data1 = new AbstractInterval<>(10.0, 10.0);
        signal.refine(5, 10, data1);

        assertEquals(data, signal.getValueAt(0));
        assertEquals(data1, signal.getValueAt(5));
        assertEquals(data1, signal.getValueAt(9));
        assertEquals(any, signal.getValueAt(10));
        assertEquals(any, signal.getValueAt(600));

        System.out.println(signal);
    }

    @Test
    void refine3() {
        DoubleDomain domain = new DoubleDomain();
        AbstractInterval<Double> data = new AbstractInterval<>(30.0,10.0);
        AbstractInterval<Double> any = new AbstractInterval<>(
                Double.NEGATIVE_INFINITY,
                Double.POSITIVE_INFINITY);
        OnlineSignal<Double> signal  = new OnlineSignal<>(domain.min(), domain.max());

        signal.refine(0, 10, data);

        AbstractInterval<Double> data1 = new AbstractInterval<>(20.0, 10.0);
        signal.refine(5, 10, data1);

        AbstractInterval<Double> data2 = new AbstractInterval<>(10.0, 10.0);
        signal.refine(3, 8, data2);

        assertEquals(data, signal.getValueAt(0));
        assertEquals(data2, signal.getValueAt(5));
        assertEquals(data1, signal.getValueAt(9));
        assertEquals(any, signal.getValueAt(10));
        assertEquals(any, signal.getValueAt(600));

        System.out.println(signal);
    }
}