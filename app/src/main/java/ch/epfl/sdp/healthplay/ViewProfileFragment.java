package ch.epfl.sdp.healthplay;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ch.epfl.sdp.healthplay.auth.ProfileFragment;
import ch.epfl.sdp.healthplay.auth.SignedInFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ViewProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewProfileFragment extends ProfileFragment {

    private View view;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String mParam1;


    public ViewProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment viewProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ViewProfileFragment newInstance(String param1) {
        ViewProfileFragment fragment = new ViewProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_view_profile, container, false);
        SignedInFragment.SetMode(getContext());
        initString();
        String userId = mParam1;
        ImageView imageView = view.findViewById(R.id.profile_picture);
        getImage(userId, imageView);
        TextView TextViewUsername = view.findViewById(R.id.profileUsername);
        initUsername(userId, TextViewUsername);
        TextView TextViewBirthday = view.findViewById(R.id.profileBirthday);
        initBirthday(userId, TextViewBirthday);
        TextView TextViewStatsButton = view.findViewById(R.id.statsButton);
        TextView TextViewWeight = view.findViewById(R.id.profileWeight);
        TextView TextViewHealthPoint = view.findViewById(R.id.profileHealthPoint);
        initStats(userId, TextViewStatsButton, TextViewWeight, TextViewHealthPoint);
        TextView TextViewName = view.findViewById(R.id.profileName);
        initName(userId, TextViewName);

        return view;
    }


}