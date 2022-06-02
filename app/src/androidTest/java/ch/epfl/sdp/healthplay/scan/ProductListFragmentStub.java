package ch.epfl.sdp.healthplay.scan;
/*
import static org.mockito.Mockito.when;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import org.mockito.Answers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import ch.epfl.sdp.healthplay.R;
import ch.epfl.sdp.healthplay.database.Database;
import ch.epfl.sdp.healthplay.productlist.ProductListFragment;

public class ProductListFragmentStub extends ProductListFragment {
    private static final String TEST_CODE = "737628064502";
    @Mock
    Task<DataSnapshot> t1;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    DatabaseReference ref = Mockito.mock(DatabaseReference.class);

    @Mock
    DataSnapshot ds;

    AutoCloseable closeable;

    @Override
    protected Database getDatabase() {
        return new Database(ref);
    }

    @Override
    protected void initUser() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        closeable = MockitoAnnotations.openMocks(this);
        Map<String, Map<String, Long>> map = new HashMap<>();
        Map<String, Long> inner = new HashMap<>();
        inner.put(TEST_CODE, 1L);
        map.put("31-05-2022", inner);

        Mockito.when(ref
                .child(Database.USERS)).thenReturn(ref);
        Mockito.when(ref.child("")).thenReturn(ref);
        Mockito.when(ref.child(Database.PRODUCTS)
                .get()
        ).thenReturn(t1);
        when(t1.isSuccessful()).thenReturn(true);
        when(t1.getResult()).thenReturn(ds);
        when(ds.getValue()).thenReturn(map);
    }

    public ProductListFragmentStub() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_product_list, container, false);
    }
}*/