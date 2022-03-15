package ch.epfl.sdp.healthplay.database;

import com.google.firebase.database.FirebaseDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class UserTest {
    AutoCloseable closable;

    @Mock
    FirebaseDatabase database;

    @Before
    public void setup() {
        closable = MockitoAnnotations.openMocks(this);
    }

    @Test
    public void test1() {
        //TODO: Create tests using Mockito
    }

    @After
    public void end() throws Exception {
        closable.close();
    }
}
