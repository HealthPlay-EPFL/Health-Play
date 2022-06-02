package ch.epfl.sdp.healthplay;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import ch.epfl.sdp.healthplay.kneetag.MainActivity;
import ch.epfl.sdp.healthplay.planthunt.PlanthuntDescriptionFragment;
import ch.epfl.sdp.healthplay.planthunt.PlanthuntMainActivity;

public class GameMenuFragment extends Fragment {

    public GameMenuFragment() {
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
        View view = inflater.inflate(R.layout.fragment_game_menu, container, false);

        //Display Planthunt description when Planthunt button image is clicked
        final ImageButton planthuntDescriptionButton = view.findViewById(R.id.planthuntThumbnail);
        planthuntDescriptionButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (getFragmentManager() != null){
                            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                            fragmentTransaction.setReorderingAllowed(true);
                            fragmentTransaction.replace(R.id.fragmentContainerView, new PlanthuntDescriptionFragment());
                            fragmentTransaction.commit();
                        }
                        else{
                            System.out.println("Fragment is null!");
                        }
                    }
                }
        );

        //Display Kneetag description when Kneetag button image is clicked
        final ImageButton kneetagButton = view.findViewById(R.id.kneetagThumbnail);
        kneetagButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (getFragmentManager() != null){
                            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                            fragmentTransaction.setReorderingAllowed(true);
                            fragmentTransaction.replace(R.id.fragmentContainerView, new KneetagDescriptionFragment());
                            fragmentTransaction.commit();}
                        else{
                            System.out.println("Fragment is null!");
                        }
                    }
                }
        );
        //It's the button to launch the planthunt Game
        final Button planthuntLaunchButton = view.findViewById(R.id.planthuntPlay);
        planthuntLaunchButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), PlanthuntMainActivity.class);
                        startActivity(intent);
                    }
                }
        );
        //It's the button to launch the kneetag Game
        final Button kneetagLaunchButton = view.findViewById(R.id.kneetagPlay);
        kneetagLaunchButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                    }
                }
        );
        Button leader = view.findViewById(R.id.button3);
        leader.setOnClickListener(v -> onClickLeaderBoard());
        return view;
    }

    public void onClickLeaderBoard() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.replace(R.id.fragmentContainerView, new MonthlyLeaderBoardFragment());
        fragmentTransaction.commit();
    }
}