package eu.quanticol.moonlight.monitoring.online.strategy.spacetime;

import eu.quanticol.moonlight.signal.LocationService;
import eu.quanticol.moonlight.signal.online.SignalInterface;
import eu.quanticol.moonlight.signal.online.Update;

import java.io.Serializable;
import java.util.List;

public interface OnlineSpaceTimeMonitor
<S, T extends Comparable<T> & Serializable, V, R>
{
    List<Update<T, R>> monitor(LocationService<S> locationService,
                               Update<T, V> signalUpdate);

    SignalInterface<T, R> getResult();
}