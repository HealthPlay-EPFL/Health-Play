package ch.epfl.sdp.healthplay.auth;

import static com.firebase.ui.auth.AuthUI.EMAIL_LINK_PROVIDER;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.google.firebase.auth.UserInfo;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sdp.healthplay.HomeScreenActivity;
import ch.epfl.sdp.healthplay.LeaderBoardActivity;
import ch.epfl.sdp.healthplay.R;
import ch.epfl.sdp.healthplay.database.Database;
import ch.epfl.sdp.healthplay.databinding.FragmentSignedInBinding;

public class SignedInFragment extends Fragment {
    private static final String TAG = "SignedInActivity";
    public static final String IDP_RESPONSE = "extra_idp_response";
    private FragmentSignedInBinding mBinding;
    private View view;
    private final int NUMBER_OF_LANGUAGE = 4;
    private Button[] button_language = new Button[NUMBER_OF_LANGUAGE];
    private final static int ENGLISH = 0, FRENCH = 1, ITALIAN = 2, GERMAN =3;

    public SignedInFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_signed_in, container, false);
        IdpResponse response = getActivity().getIntent().getParcelableExtra(IDP_RESPONSE);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            startActivity(AuthUiActivity.createIntent(getActivity()));
            return view;
        }
        mBinding = FragmentSignedInBinding.inflate(inflater);
        view = mBinding.getRoot();
        initButton();

        mBinding.deleteAccount.setOnClickListener(view -> deleteAccountClicked());
        mBinding.signOut.setOnClickListener(view -> signOut());

        return view;
    }

    public void onClickNight() {
        setMode(AppCompatDelegate.MODE_NIGHT_YES);
        getActivity().setTheme(R.style.darkTheme);
    }

    public void onClickLight() {
        setMode(AppCompatDelegate.MODE_NIGHT_NO);
        getActivity().setTheme(R.style.AppTheme);
    }

    public void clickOnLanguage(int language){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getContext().getString(R.string.saved_language_mode), language);
        editor.apply();
        SetMode(getContext());
        reloadFrag();
    }

    private void reloadFrag(){
        Intent intent = new Intent(getContext(), HomeScreenActivity.class);
        getActivity().finish();
        startActivity(intent);
    }

    /**
     * Set the appTheme Light/Dark for one context
     * @param activity reference of the context
     */
    public static void SetMode(Context activity) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
        int mode = sharedPref.getInt(activity.getString(R.string.saved_night_mode), AppCompatDelegate.MODE_NIGHT_NO);
        int language_mode = sharedPref.getInt(activity.getString(R.string.saved_language_mode), ENGLISH);
        AppCompatDelegate.setDefaultNightMode(mode);
        if(language_mode == FRENCH && mode == AppCompatDelegate.MODE_NIGHT_YES){
            activity.setTheme(R.style.darkThemeFrench);
        }else if(language_mode == FRENCH && mode == AppCompatDelegate.MODE_NIGHT_NO){
            activity.setTheme(R.style.AppThemeFrench);
        }else if(language_mode == ITALIAN && mode == AppCompatDelegate.MODE_NIGHT_YES){
            activity.setTheme(R.style.darkThemeItalian);
        }else if(language_mode == ITALIAN && mode == AppCompatDelegate.MODE_NIGHT_NO){
            activity.setTheme(R.style.AppThemeItalian);
        }else if(language_mode == GERMAN && mode == AppCompatDelegate.MODE_NIGHT_YES){
            activity.setTheme(R.style.darkThemeGerman);
        }else if(language_mode == GERMAN && mode == AppCompatDelegate.MODE_NIGHT_NO){
            activity.setTheme(R.style.AppThemeGerman);
        }else
        if(mode == AppCompatDelegate.MODE_NIGHT_YES) {
            activity.setTheme(R.style.darkTheme);
        }
        else {
            activity.setTheme(R.style.AppTheme);
        }
    }


    private void setMode(int mode) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.saved_night_mode), mode);
        editor.apply();
        AppCompatDelegate.setDefaultNightMode(mode);
    }

    public void signOut() {
        AuthUI.getInstance()
                .signOut(getContext())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(getContext(), HomeScreenActivity.class));
                        return;
                    } else {
                        Log.w(TAG, "signOut:failure", task.getException());
                        showSnackbar(R.string.sign_out_failed);
                    }
                });
    }


    public void deleteAccountClicked() {
        String messageDelete = "Are you sure you want to delete this account?";
        String yes = "Yes, nuke it!";
        String no = "No";
        new MaterialAlertDialogBuilder(getContext())
                .setMessage(messageDelete)
                .setPositiveButton(yes, (dialogInterface, i) -> deleteAccount())
                .setNegativeButton(no, null)
                .show();
    }

    private void deleteAccount() {
        new Database().deleteUser(FirebaseAuth.getInstance().getCurrentUser().getUid());
        AuthUI.getInstance()
                .delete(getActivity())
                .addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(getActivity(),HomeScreenActivity.class));
                    } else {
                        showSnackbar(R.string.delete_account_failed);
                    }
                });
    }

    private void showSnackbar(@StringRes int errorMessageRes) {
        Snackbar.make(mBinding.getRoot(), errorMessageRes, Snackbar.LENGTH_LONG).show();
    }

    private void initButton(){
        Button[] button_language = new Button[4];
        Button night = view.findViewById(R.id.night);
        night.setOnClickListener(v -> onClickNight());
        Button light = view.findViewById(R.id.light);
        light.setOnClickListener(v -> onClickLight());
        Button french = view.findViewById(R.id.french);
        french.setOnClickListener(v -> clickOnLanguage(FRENCH));
        Button english = view.findViewById(R.id.english);
        english.setOnClickListener(v -> clickOnLanguage(ENGLISH));
        Button italian = view.findViewById(R.id.italian);
        italian.setOnClickListener(v -> clickOnLanguage(ITALIAN));
        Button german = view.findViewById(R.id.german);
        german.setOnClickListener(v -> clickOnLanguage(GERMAN));
        button_language[0] = english;
        button_language[1] = french;
        button_language[2] = italian;
        button_language[3] = german;
        setEnabled(button_language);
    }

    private void setEnabled(Button[] buttons){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        int language_mode = sharedPref.getInt(getContext().getString(R.string.saved_language_mode), 0);
        buttons[language_mode].setEnabled(false);
        buttons[language_mode].setVisibility(View.INVISIBLE);
    }
}