package be.twofold.json;

import nl.jqno.equalsverifier.*;
import org.junit.*;

public class JsonNullTest {

    @Test
    public void testEqualsAndHashCode() {
        EqualsVerifier
            .forClass(JsonNull.class)
            .verify();
    }

}
