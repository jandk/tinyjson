package be.twofold.json;

import nl.jqno.equalsverifier.*;
import org.junit.*;

public class JsonStringTest {

    @Test
    public void testEqualsAndHashCode() {
        EqualsVerifier
            .forClass(JsonString.class)
            .suppress(Warning.NULL_FIELDS)
            .verify();
    }

}
