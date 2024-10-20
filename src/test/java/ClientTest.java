import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.lang.annotation.Target;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClientTest {

    @Test
    public void getPort_returnNumberPort() {
        int expected = 5000;
        int port = Client.getPort(new File("D:/Java/COURSE_PROJECTS", "settings.txt"));
        assertEquals(port, expected);
    }

    @Test
    public void getHost_returnHost() {
        String expected = "127.0.0.1";
        String host = Client.getHost(new File("D:/Java/COURSE_PROJECTS", "settings.txt"));
        assertEquals(host, expected);
    }


}
