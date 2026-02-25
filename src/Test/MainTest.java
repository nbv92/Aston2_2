package Test;

import org.example.Main;
import org.junit.jupiter.api.Test;
import java.io.*;

import static org.junit.Assert.assertTrue;

class MainTest {
    @Test
    void testMainCreateAndExit() throws Exception {
        String input = "1\nJohn\njohn@mail.com\n28\n0\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        Main.main(new String[]{});

        String output = outContent.toString();
        assertTrue(output.contains("Введите имя:"));
        assertTrue(output.contains("Выберите операцию:"));
    }
}