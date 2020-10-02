package be.twofold.json.parse;

import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

import static org.assertj.core.api.Assertions.*;

@RunWith(Parameterized.class)
public class JsonParserTest {

    private static final Path Root = Paths.get("src/test/resources/JSONTestSuite/parsing");

    private final String filename;

    public JsonParserTest(String filename) {
        this.filename = filename;
    }

    @Parameterized.Parameters(name = "{0}")
    public static List<String> getFileNames() throws Exception {
        return Files.list(Root)
            .filter(path -> path.getFileName().toString().endsWith(".json"))
            .map(path -> path.getFileName().toString())
            .collect(Collectors.toList());
    }

    @Test
    public void test() {
        String type = filename.substring(0, 2);
        switch (type) {
            case "i_":
                testUndetermined(filename);
                break;
            case "n_":
                testForFailure(filename);
                break;
            case "y_":
                testForSuccess(filename);
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }

    private void testForFailure(String filename) {
        assertThatExceptionOfType(JsonParseException.class)
            .isThrownBy(() -> parseFile(filename));
    }

    private void testForSuccess(String filename) {
        assertThatCode(() -> parseFile(filename))
            .doesNotThrowAnyException();
    }

    private void testUndetermined(String filename) {
        try {
            parseFile(filename);
        } catch (JsonParseException e) {
            System.err.println("F -- " + filename + " -- " + getRootCause(e).getClass().getSimpleName());
        }
        System.out.println("P -- " + filename);
    }

    private static void parseFile(String filename) {
        try (Reader reader = Files.newBufferedReader(Root.resolve(filename))) {
            new JsonParser(reader).parse();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static Throwable getRootCause(Throwable throwable) {
        List<Throwable> list = new ArrayList<>();
        while (throwable != null && !list.contains(throwable)) {
            list.add(throwable);
            throwable = throwable.getCause();
        }
        return list.isEmpty() ? null : list.get(list.size() - 1);
    }

}
