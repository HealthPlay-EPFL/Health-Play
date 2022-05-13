package ch.epfl.sdp.healthplay.productlist;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import ch.epfl.sdp.healthplay.R;
import ch.epfl.sdp.healthplay.database.Database;
import ch.epfl.sdp.healthplay.model.Product;
import ch.epfl.sdp.healthplay.model.ProductInfoClient;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductListFragment extends Fragment {

    ProgressBar bar;

    private Database db = new Database();
    private List<String> mProducts;
    private List<String> mDates;
    private FirebaseUser user;

    public ProductListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product_list, container, false);

        bar = view.findViewById(R.id.progressBarProductList);
        bar.setVisibility(View.VISIBLE);

        // Get the authenticated user if any
        user = FirebaseAuth.getInstance().getCurrentUser();
        // Check if user is not null
        // The user should not be null as this page is accessed through the profile page
        Objects.requireNonNull(user);

        initProductsAndDates(view);

        //TODO: Might be useful ?
        //Window window = getActivity().getWindow();

        //window.setStatusBarColor(getResources().getColor(R.color.light_blue_600, getActivity().getTheme()));


        return view;
    }

    /**
     * Init the arrays of products and dates
     */
    private void initProductsAndDates(View view) {
        mProducts = new ArrayList<>();
        mDates = new ArrayList<>();

        db.mDatabase
                .child(Database.USERS)
                .child(user.getUid())
                .child(Database.PRODUCTS)
                .get()
                .addOnSuccessListener(dataSnapshot -> {
                    @SuppressWarnings("unchecked")
                    Map<String, Map<String, Long>> productsUnordered = (Map<String, Map<String, Long>>) dataSnapshot.getValue();
                    if (productsUnordered != null) {
                        TreeMap<String, Map<String, Long>> products = new TreeMap<>(Collections.reverseOrder());
                        products.putAll(productsUnordered);
                        products.forEach((date, pMap) -> pMap.forEach((p, quantity) -> {
                            mProducts.add(p);
                            mDates.add(date);
                        }));
                    }
                    initRecyclerView(view);
                });
    }

    /**
     * Init the recycler view with the two arrays
     */
    private void initRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.productListRecyclerView);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(getContext(), mProducts, mDates);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        bar.setVisibility(View.GONE);
    }
}