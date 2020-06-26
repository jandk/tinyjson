package be.twofold.json;

import nl.jqno.equalsverifier.*;
import org.junit.*;

public class JsonNumberTest {

    @Test
    public void testEqualsAndHashCode() {
        EqualsVerifier
            .forClass(JsonNumber.class)
            .suppress(Warning.NULL_FIELDS)
            .verify();
    }

}
