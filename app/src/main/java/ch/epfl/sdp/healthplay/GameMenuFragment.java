package ch.epfl.sdp.healthplay;

import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import ch.epfl.sdp.healthplay.kneetag.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GameMenuFragment} factory method to
 * create an instance of this fragment.
 */
public class GameMenuFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GameMenuFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment GameMenuFragment.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_game_menu, container, false);
        //setTitle("Games");

        //Display Planthunt description when Planthunt button image is clicked
        final ImageButton planthuntDescriptionButton = view.findViewById(R.id.planthuntThumbnail);
        planthuntDescriptionButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), PlanthuntDescriptionActivity.class);
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), planthuntDescriptionButton, ViewCompat.getTransitionName(planthuntDescriptionButton));
                        startActivity(intent, options.toBundle());
                    }
                }
        );

        //Display Kneetag description when Kneetag button image is clicked
        final ImageButton kneetagButton = view.findViewById(R.id.kneetagThumbnail);
        kneetagButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), KneetagDescriptionActivity.class);
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), kneetagButton, ViewCompat.getTransitionName(kneetagButton));
                        startActivity(intent, options.toBundle());
                    }
                }
        );
        //It's the button to launch the plantHunt Game
        final Button planthuntLaunchButton = view.findViewById(R.id.planthuntPlay);
        planthuntLaunchButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), PlanthuntCameraActivity.class);
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
        return view;
    }
}