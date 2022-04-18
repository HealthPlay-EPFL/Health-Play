package ch.epfl.sdp.healthplay;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class PlanthuntDescriptionFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_planthunt_description, container, false);
        final ImageButton planthuntDescriptionButton = view.findViewById(R.id.plantDescButtonBack);
        planthuntDescriptionButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.setReorderingAllowed(true);
                        fragmentTransaction.replace(R.id.fragmentContainerView, new GameMenuFragment());
                        fragmentTransaction.commit();
                    }
                }
        );
        return view;
    }
}