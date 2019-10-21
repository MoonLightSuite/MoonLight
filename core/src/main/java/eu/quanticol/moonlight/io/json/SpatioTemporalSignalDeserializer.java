package eu.quanticol.moonlight.io.json;

import com.google.gson.*;
import eu.quanticol.moonlight.signal.Assignment;
import eu.quanticol.moonlight.signal.SpatioTemporalSignal;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SpatioTemporalSignalDeserializer implements JsonDeserializer<SpatioTemporalSignalWrapper> {
    @Override
    public SpatioTemporalSignalWrapper deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        SpatioTemporalSignal spatioTemporalSignal = getSignal(json);
        return new SpatioTemporalSignalWrapper(spatioTemporalSignal, null);
    }

    private SpatioTemporalSignal getSignal(JsonElement json) {
        JsonObject jso = json.getAsJsonObject();
        JsonObject signalType = getAsJsonObject(jso, "signal_type");
        Function<Object[], Assignment> toAssignment = getAssignment(getSignalsType(signalType));
        JsonObject edgesType = getAsJsonObject(jso, "edge_type");
        JsonArray nodes = jso.get("nodes").getAsJsonArray();
        List<String> locations = IntStream.range(0, nodes.size()).mapToObj(nodes::get).map(JsonElement::getAsString).collect(Collectors.toList());

        if (isLocationServiceStatic(jso)) {

        }

        JsonArray trajectory = jso.get("trajectory").getAsJsonArray();
        SpatioTemporalSignal<Assignment> spatioTemporalSignal = new SpatioTemporalSignal<>(nodes.size());
        ArrayList<String> variables = new ArrayList<>(signalType.keySet());
        for (int i = 0; i < trajectory.size(); i++) {
            JsonObject jsonElement = trajectory.get(i).getAsJsonObject();
            double time = jsonElement.get("time").getAsDouble();
            JsonObject signals = jsonElement.get("signals").getAsJsonObject();
            spatioTemporalSignal.add(time, l -> toAssignment.apply(getValues(signals.get(nodes.get(l).getAsString()).getAsJsonObject(), variables)));
        }
        return spatioTemporalSignal;
    }


    private Object[] getValues(JsonObject object, List<String> keys) {
        return keys.stream().map(object::get).toArray();
    }

    private Function<Object[], Assignment> getAssignment(Class<?>[] varTypes) {
        return object -> new Assignment(varTypes, object);
    }

    private Class<?>[] getSignalsType(JsonObject jso) {
        return jso.keySet().stream().map(jso::get).map(s -> JSONUtils.getVariableType(s.getAsString())).toArray(Class<?>[]::new);
    }

    private JsonObject getAsJsonObject(JsonObject jsonObject, String key) {
        return jsonObject.get(key).getAsJsonObject();
    }

    private boolean isLocationServiceStatic(JsonObject jsonObject) {
        return jsonObject.keySet().contains("edges");
    }


}