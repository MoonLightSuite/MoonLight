/*
 * MoonLight: a light-weight framework for runtime monitoring
 * Copyright (C) 2018-2021
 *
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.quanticol.moonlight.examples.temporal.afc;

import eu.quanticol.moonlight.domain.AbstractInterval;
import eu.quanticol.moonlight.io.DataReader;
import eu.quanticol.moonlight.io.FileType;
import eu.quanticol.moonlight.io.parsing.ParsingStrategy;
import eu.quanticol.moonlight.io.parsing.RawTrajectoryExtractor;
import eu.quanticol.moonlight.monitoring.online.OnlineTimeMonitor;
import eu.quanticol.moonlight.signal.online.SegmentInterface;
import eu.quanticol.moonlight.signal.online.TimeChain;
import eu.quanticol.moonlight.signal.online.TimeSegment;
import eu.quanticol.moonlight.signal.online.Update;
import eu.quanticol.moonlight.util.Plotter;
import eu.quanticol.moonlight.util.Stopwatch;

import java.util.*;
import java.util.stream.Collectors;

import static eu.quanticol.moonlight.examples.temporal.afc.AFCHelpers.*;
import static eu.quanticol.moonlight.examples.temporal.afc.AFCSettings.*;

public class AFCMoonlightRunner {

    private static final List<String> output = new ArrayList<>();
    private static final List<Stopwatch> stopwatches = new ArrayList<>();

    private static final int RND_SEED = 7;

    private static final List<List<SegmentInterface<
            Double, AbstractInterval<Double>>>> results = new ArrayList<>();

    private static final boolean PLOTTING = false;

    private static final Plotter plt = new Plotter();

    public static void main(String[] args) {
        String id = String.valueOf((int) LAST_TIME);

        repeatedRunner("In-Order " + id,
                       s -> moonlight(false, id, stopwatches),
                       stopwatches, output);
        repeatedRunner("OO-Order " + id, s -> moonlight(true, id, stopwatches),
                       stopwatches, output);

        LOG.info("------> Experiment results (sec):");
        plt.waitActivePlots(10);

        output.forEach(LOG::info);

        if(results.get(0).equals(results.get(1)))
            LOG.info("Results match");
        else
            LOG.severe("Results don't match");
    }



    static void moonlight(boolean shuffle, String id, List<Stopwatch> s)
    {
            List<List<SegmentInterface<Double, AbstractInterval<Double>>>>
                    moonlightColl = execMoonlight(shuffle, id, s);
            List<SegmentInterface<Double, AbstractInterval<Double>>>
                    moonlight = moonlightColl.get(moonlightColl.size() - 1);

            results.add(moonlight);

            List<List<Double>> mRes = handleData(moonlightColl);

            List<Double> mStart = mRes.get(0);
            List<Double> mEnd = mRes.get(1);

            if (PLOTTING)
                plt.plot(mStart, mEnd, "Moonlight");
    }

    static void moonlightChain(boolean shuffle, String id, List<Stopwatch> s)
    {
        List<List<SegmentInterface<Double, AbstractInterval<Double>>>>
                moonlightColl = execChainMoonlight(shuffle, id, s);
        List<SegmentInterface<Double, AbstractInterval<Double>>>
                moonlight = moonlightColl.get(moonlightColl.size() - 1);

        results.add(moonlight);

        List<List<Double>> mRes = handleData(moonlightColl);

        List<Double> mStart = mRes.get(0);
        List<Double> mEnd = mRes.get(1);

        if (PLOTTING)
            plt.plot(mStart, mEnd, "Moonlight");
    }

    static List<List<SegmentInterface<Double, AbstractInterval<Double>>>>
    execMoonlight(boolean shuffle, String id, List<Stopwatch> stopwatches)
    {
        ParsingStrategy<double[][]> st = new RawTrajectoryExtractor(1);
        FileType type = FileType.CSV;
        double[] input = new DataReader<>(dataPath(id), type, st).read()[0];

        List<Update<Double, Double>> updates = genUpdates(input, shuffle,
                                                          SCALE, RND_SEED);
        OnlineTimeMonitor<Double, Double> m = instrument();

        List<List<SegmentInterface<Double, AbstractInterval<Double>>>>
                result = new ArrayList<>();

        // Moonlight execution recording...
        Stopwatch rec = Stopwatch.start();
        for (Update<Double, Double> u : updates) {
            result.add(m.monitor(u).getSegments().copy().toList());
        }
        rec.stop();
        stopwatches.add(rec);

        return result;
    }

    static List<List<SegmentInterface<Double, AbstractInterval<Double>>>>
    execChainMoonlight(boolean shuffle, String id, List<Stopwatch> stopwatches)
    {
        ParsingStrategy<double[][]> st = new RawTrajectoryExtractor(1);
        FileType type = FileType.CSV;
        double[] input = new DataReader<>(dataPath(id), type, st).read()[0];

        List<Update<Double, Double>> updates = genUpdates(input, shuffle,
                                                          SCALE, RND_SEED);
        OnlineTimeMonitor<Double, Double> m = instrument();

        List<List<SegmentInterface<Double, AbstractInterval<Double>>>>
                result = new ArrayList<>();

        TimeChain<Double, Double> chain = updatesToTimeChain(updates);

        // Moonlight execution recording...
        Stopwatch rec = Stopwatch.start();
        result.add(m.monitor(chain).getSegments().toList());
        rec.stop();
        stopwatches.add(rec);

        return result;
    }

    // WARNING: we are assuming sequential updates
    private static TimeChain<Double, Double> updatesToTimeChain(
            List<Update<Double, Double>> updates)
    {
        List<SegmentInterface<Double, Double>> ups =
                updates.stream()
                        .map(u -> new TimeSegment<>(u.getStart(), u.getValue()))
                        .collect(Collectors.toList());

        return new TimeChain<>(ups, updates.get(updates.size() - 1).getEnd());
    }


}
