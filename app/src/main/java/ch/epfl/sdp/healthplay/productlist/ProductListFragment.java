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
 * Use the {@link ProductListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ProgressBar bar;

    private Database db = new Database();
    private List<String> mProducts;
    private List<String> mDates;
    private FirebaseUser user;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProductListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProductListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProductListFragment newInstance(String param1, String param2) {
        ProductListFragment fragment = new ProductListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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

    private void initRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.productListRecyclerView);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(getContext(), mProducts, mDates);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        bar.setVisibility(View.GONE);
    }
}