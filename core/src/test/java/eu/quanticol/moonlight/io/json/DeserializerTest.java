package eu.quanticol.moonlight.io.json;

import eu.quanticol.moonlight.signal.SpatioTemporalSignal;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.fail;

class DeserializerTest {


    @Test
    void testReadTemporalSignal() {
        try (InputStream inputStream = DeserializerTest.class.getClassLoader().getResourceAsStream("eu/quanticol/moonlight/io/json/json_variable_location_service.json")) {
            String json = IOUtils.toString(inputStream);
            SpatioTemporalSignalWrapper deserialize = Deserializer.SPATIO_TEMPORAL_SIGNAL.deserialize(json);
            SpatioTemporalSignal spatioTemporalSignal = deserialize.getSpatioTemporalSignal();
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
}