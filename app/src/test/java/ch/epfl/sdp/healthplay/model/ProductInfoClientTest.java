package ch.epfl.sdp.healthplay.model;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mock.*;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.exceptions.base.MockitoAssertionError;
import org.mockito.junit.MockitoJUnit;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ProductInfoClientTest {
    private final static String TEST = "Test string";

    AutoCloseable closeable;

    ProductInfoClient client;

    @Mock
    HttpURLConnection connection;

    @Before
    public void setup() throws IOException {
        closeable = MockitoAnnotations.openMocks(this);
        when(connection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
        when(connection.getInputStream()).thenReturn(
                new ByteArrayInputStream(TEST.getBytes(StandardCharsets.UTF_8)
                )
        );
        doCallRealMethod().when(connection).setRequestMethod(anyString());
        doCallRealMethod().when(connection).setRequestProperty(anyString(), anyString());
    }

    @Test
    public void testCorrectStringIsReturned() {
        client = new ProductInfoClient(connection);
        assertEquals("Test string", client.getInfo());
    }

    @Test
    public void whenExceptionReturnErrorString() throws IOException {
        when(connection.getInputStream()).thenThrow(IOException.class);

        client = new ProductInfoClient(connection);
        assertEquals("ERROR", client.getInfo());
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullConnectionThrowsException() {
        client = new ProductInfoClient((HttpURLConnection) null);
    }


    @Test(expected = IllegalArgumentException.class)
    public void nullBarcodeThrowsException() throws IOException {
        client = new ProductInfoClient((String) null);
    }

    @After
    public void finish() throws Exception {
        closeable.close();
    }
}
