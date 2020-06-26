package be.twofold.json;

import nl.jqno.equalsverifier.*;
import org.junit.*;

public class JsonArrayTest {

    @Test
    public void testEqualsAndHashCode() {
        EqualsVerifier
            .forClass(JsonArray.class)
            .suppress(Warning.NULL_FIELDS)
            .verify();
    }

}
