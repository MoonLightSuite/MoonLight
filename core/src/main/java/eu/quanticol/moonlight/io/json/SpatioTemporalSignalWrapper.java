package eu.quanticol.moonlight.io.json;

import eu.quanticol.moonlight.signal.LocationService;
import eu.quanticol.moonlight.signal.SpatioTemporalSignal;

public class SpatioTemporalSignalWrapper {

    private SpatioTemporalSignal spatioTemporalSignal;
    private LocationService<Double> locationService;

    public SpatioTemporalSignalWrapper(SpatioTemporalSignal spatioTemporalSignal, LocationService<Double> locationService) {

        this.spatioTemporalSignal = spatioTemporalSignal;
        this.locationService = locationService;
    }

    public SpatioTemporalSignal getSpatioTemporalSignal() {
        return spatioTemporalSignal;
    }
}