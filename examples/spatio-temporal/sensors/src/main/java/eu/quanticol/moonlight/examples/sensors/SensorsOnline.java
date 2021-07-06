package eu.quanticol.moonlight.examples.sensors;

import com.mathworks.engine.MatlabEngine;

import eu.quanticol.moonlight.domain.*;
import eu.quanticol.moonlight.formula.AtomicFormula;
import eu.quanticol.moonlight.formula.Formula;
import eu.quanticol.moonlight.formula.Parameters;
import eu.quanticol.moonlight.formula.SomewhereFormula;
import eu.quanticol.moonlight.monitoring.SpatialTemporalMonitoring;
import eu.quanticol.moonlight.monitoring.online.OnlineSpaceTimeMonitor;
import eu.quanticol.moonlight.monitoring.spatialtemporal.SpatialTemporalMonitor;
import eu.quanticol.moonlight.signal.Signal;
import eu.quanticol.moonlight.signal.SpatialTemporalSignal;
import eu.quanticol.moonlight.signal.online.*;
import eu.quanticol.moonlight.space.DistanceStructure;
import eu.quanticol.moonlight.space.LocationService;
import eu.quanticol.moonlight.space.SpatialModel;
import eu.quanticol.moonlight.util.Pair;
import eu.quanticol.moonlight.util.Stopwatch;
import eu.quanticol.moonlight.util.Utils;
import eu.quanticol.moonlight.utility.matlab.configurator.MatlabDataConverter;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.IntStream;

public class SensorsOnline {
    private static Map<String,
                       Function<Parameters,
                                Function<Pair<Integer,Integer>, Boolean>>>
                                                                 atomicFormulas;

    private static HashMap<String, Function<SpatialModel<Double>,
                               DistanceStructure<Double, ?>>> distanceFunctions;

    private static LocationService<Double, Double> locSvc;

    // Device types
    private static final String COORDINATOR = "type1";
    private static final String ROUTER = "type2";
    private static final String END_DEVICE = "type3";

    // Distance functions
    private static final String DISTANCE = "dist";

    private static double nodes;
    private static Double[] nodesType;

    public static void main(String[] args) {

        Object[] graph = Objects.requireNonNull(runSimulator());
        locSvc = Utils.createLocServiceFromSetMatrix(graph);

        setAtomicFormulas();
        setDistanceFunctions();

        Formula sWhere = formula();

        checkOffline(sWhere);
        checkOnline(sWhere);
    }

    private static Object[] runSimulator() {
        try {
            MatlabEngine eng = MatlabEngine.startMatlab();
            URL url = Objects.requireNonNull(
                    SensorsOnline.class.getResource("mobility.m"));
            String localPath = Paths.get(url.toURI())
                                    .getParent().toAbsolutePath().toString();
            eng.eval("addpath(\"" + localPath + "\")");
            return runModel(eng);
        } catch (InterruptedException |
                 URISyntaxException |
                 ExecutionException e)
        {
            e.printStackTrace();
            Thread.currentThread().interrupt();
            return new Object[0];
        }
    }

    private static Formula formula() {
        Formula isType1 = new AtomicFormula(COORDINATOR);
        return new SomewhereFormula(DISTANCE, isType1);
    }

    private static void setDistanceFunctions() {
        distanceFunctions = new HashMap<>();
        distanceFunctions.put(DISTANCE,
                m -> new DistanceStructure<>(x -> x , new DoubleDistance(),
                        0.0, 1.0, m));
    }

    private static void setAtomicFormulas() {
        atomicFormulas = new HashMap<>();
        atomicFormulas.put(COORDINATOR, p -> (x -> x.getFirst() == 1));
        atomicFormulas.put(ROUTER, p -> (x -> x.getFirst() == 2));
        atomicFormulas.put(END_DEVICE, p -> (x -> x.getFirst() == 3));
    }

    private static Object[] runModel(MatlabEngine eng)
            throws ExecutionException, InterruptedException
    {
        /// Trace generation
        eng.eval("mobility");

        // Reading results
        nodes = eng.getVariable("num_nodes");
        nodesType = MatlabDataConverter.getArray(eng
                                                  .getVariable("nodes_type"),
                                                 Double.class);

        Object[] cGraph1 = eng.getVariable("cgraph1");
        Object[] cGraph2 = eng.getVariable("cgraph2");

        return cGraph1;
    }

    private static void checkOffline(Formula f)
    {
        SpatialTemporalSignal<Pair<Integer, Integer>> stSignal =
                                  new SpatialTemporalSignal<>(nodesType.length);

        IntStream.range(0, (int) nodes - 1)
                 .forEach(i -> stSignal
                                    .add(i, (location ->
                                            new Pair<>(nodesType[location]
                                                        .intValue(), i)
                                            )
                                        )
                         );

        //TODO populate input signal

        // Actual monitoring...
        Stopwatch rec = Stopwatch.start();
        SpatialTemporalMonitor<Double, Pair<Integer, Integer>, Boolean>
                m = new SpatialTemporalMonitoring<>(atomicFormulas,
                                                    distanceFunctions,
                                                    new BooleanDomain(),
                                                    false)
                                .monitor(f, null);
        rec.stop();

        SpatialTemporalSignal<Boolean> sOut = m.monitor(locSvc, stSignal);
        List<Signal<Boolean>> signals = sOut.getSignals();
        System.out.println(signals.get(0));
    }

    private static void checkOnline(Formula f)
    {
        Stopwatch rec = Stopwatch.start();

        OnlineSpaceTimeMonitor<Double, Pair<Integer, Integer>, Boolean> m =
                onlineMonitorInit(f);

        List<TimeChain<Double, List<Pair<Integer, Integer>>>> updates =
                new ArrayList<>();
        //TODO populate updates
        updates.forEach(m::monitor);

        rec.stop();
    }

    private static
    OnlineSpaceTimeMonitor<Double, Pair<Integer, Integer>, Boolean>
    onlineMonitorInit(Formula f)
    {
        Map<String, Function<Pair<Integer,Integer>, AbstractInterval<Boolean>>>
            atoms = setOnlineAtoms();

        return new OnlineSpaceTimeMonitor<>(f, 0, new BooleanDomain(),
                                            locSvc, atoms, distanceFunctions);
    }

    private static
    Map<String, Function<Pair<Integer,Integer>, AbstractInterval<Boolean>>>
    setOnlineAtoms()
    {
        Map<String, Function<Pair<Integer,Integer>, AbstractInterval<Boolean>>>
                atoms = new HashMap<>();
        atoms.put(COORDINATOR, x -> booleanInterval(x.getFirst() == 1));
        atoms.put(ROUTER, x -> booleanInterval(x.getFirst() == 2));
        atoms.put(END_DEVICE, x -> booleanInterval(x.getFirst() == 3));
        return atoms;
    }

    private static AbstractInterval<Boolean> booleanInterval(boolean cond) {
        return cond ? new AbstractInterval<>(true, true) :
                      new AbstractInterval<>(false, false);
    }
}
