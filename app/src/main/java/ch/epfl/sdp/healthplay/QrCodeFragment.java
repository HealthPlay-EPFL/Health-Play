package ch.epfl.sdp.healthplay;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

import net.glxn.qrgen.android.QRCode;

import ch.epfl.sdp.healthplay.auth.ProfileFragment;
import ch.epfl.sdp.healthplay.navigation.FragmentNavigation;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QrCodeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QrCodeFragment extends Fragment {

    public QrCodeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment QrCodeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static QrCodeFragment newInstance() {
        QrCodeFragment fragment = new QrCodeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_qr_code, container, false);
        ImageView qrCode = view.findViewById(R.id.qrCode);
        Button goBackButton = view.findViewById(R.id.backToProfileButton);

        // initializing onclick listener for button.
        Bitmap myBitmap = QRCode.from(FirebaseAuth.getInstance().getCurrentUser().getUid()).bitmap();
        qrCode.setImageBitmap(myBitmap);

        goBackButton.setOnClickListener(FragmentNavigation.switchToFragmentListener(getParentFragmentManager(), new ProfileFragment()));

        return view;
    }
}