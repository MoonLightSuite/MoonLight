package eu.quanticol.moonlight.io.json;

import eu.quanticol.moonlight.signal.SpatioTemporalSignal;
import eu.quanticol.moonlight.signal.VariableArraySignal;

public class Deserializer {

    public static final DeserializerFunction<VariableArraySignal> VARIABLE_ARRAY_SIGNAL = getVariableArraySignalDeserializer();
    public static final DeserializerFunction<SpatioTemporalSignalWrapper> SPATIO_TEMPORAL_SIGNAL = getSpatioTemporalSignalDeserializer();


    private Deserializer() {
        //Utlity class
    }

    private static DeserializerFunction<VariableArraySignal> getVariableArraySignalDeserializer() {
        JSONReader<VariableArraySignal> variableArraySignalJSONReader = new JSONReader<>(new VariableSignalDeserializer(), VariableArraySignal.class);
        return variableArraySignalJSONReader.getDeserializer();
    }

    private static DeserializerFunction<SpatioTemporalSignalWrapper> getSpatioTemporalSignalDeserializer() {
        JSONReader<SpatioTemporalSignalWrapper> spatioTemporalSignalWrapperJSONReader = new JSONReader<>(new SpatioTemporalSignalDeserializer(), SpatioTemporalSignalWrapper.class);
        return spatioTemporalSignalWrapperJSONReader.getDeserializer();
    }
}