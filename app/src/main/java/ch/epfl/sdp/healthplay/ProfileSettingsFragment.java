package ch.epfl.sdp.healthplay;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;

import ch.epfl.sdp.healthplay.auth.ProfileFragment;
import ch.epfl.sdp.healthplay.auth.SignedInFragment;
import ch.epfl.sdp.healthplay.database.Database;

public class ProfileSettingsFragment extends Fragment {

    public final static String MESSAGE = "ch.epfl.sdp.healthplay.FIRST_USER";

    private static final String PROFILE_HAS_BEEN_CREATED = "Your profile has been created !";
    private static final String CHANGES_APPLIED = "Changes have been applied.";
    private static final String ERROR_INCORRECT_DATE = "Error, please enter a date that is before today.";
    private static final String ERROR_INVALID_DATE = "Error, please enter a correct date format.";

    FirebaseUser user;

    private String hintName = "";
    private String hintSurname = "";
    private String hintUsername = "";
    private String hintBirthday = "01/01/2000"; // In any case, defaults to this value
    private String hintWeight = "";
    private boolean firstTime = false;  // This boolean is to check whether the user first created their profile
    private View view;


    private void modifyText(FirebaseUser user, int layoutId, String field) {
        Database db = new Database();
        db.readField(user.getUid(), field, task -> {
            if (!task.isSuccessful()) {
                Log.e("ERROR", "ERRRORORORO");
            }
            String answer;
            if (field.equals(Database.LAST_CURRENT_WEIGHT)) {
                answer = String.valueOf(task.getResult().getValue());
            } else {
                answer = task.getResult().getValue(String.class);
            }
            EditText name = view.findViewById(layoutId);
            if (field.equals(Database.BIRTHDAY) && answer != null) {
                // Receives the date as format like 2022-03-17
                // Must reverse the order
                String[] date = answer.split("-");
                answer = date[2] + "/" + date[1] + "/" + date[0];
            }
            name.setHint(answer);

            // Wish we had Java 17 :(
            switch (field) {
                case Database.NAME:
                    hintName = answer;
                    break;
                case Database.SURNAME:
                    hintSurname = answer;
                    break;
                case Database.USERNAME:
                    hintUsername = answer;
                    break;
                case Database.BIRTHDAY:
                    hintBirthday = answer;
                    break;
                case Database.LAST_CURRENT_WEIGHT:
                    hintWeight = answer;
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile_settings, container, false);
        SignedInFragment.SetMode(getContext());
        Intent intent = getActivity().getIntent();
        firstTime = intent.getBooleanExtra(MESSAGE, false);

        Button button = view.findViewById(R.id.button2);
        if (firstTime) {
            button.setText("Create profile");
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });

        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // Set all the hints
            modifyText(user, R.id.modifyNameEditText, Database.NAME);
            modifyText(user, R.id.modifySurnameEditText, Database.SURNAME);
            modifyText(user, R.id.modifyUsernameEditText, Database.USERNAME);
            modifyText(user, R.id.modifyBirthDateEditText, Database.BIRTHDAY);
            modifyText(user, R.id.modifyWeightEditText, Database.LAST_CURRENT_WEIGHT);
        }
        return view;
    }

    public void saveChanges() {
        if (user != null) {
            Database db = new Database();
            String uid = user.getUid();
            EditText text = view.findViewById(R.id.modifyNameEditText);
            String textString = getOrHint(text, Database.NAME);
            db.writeName(uid, textString);

            text = view.findViewById(R.id.modifySurnameEditText);
            textString = getOrHint(text, Database.SURNAME);
            db.writeSurname(uid, textString);

            text = view.findViewById(R.id.modifyUsernameEditText);
            textString = getOrHint(text, Database.USERNAME);
            db.writeUsername(uid, textString);

            text = view.findViewById(R.id.modifyBirthDateEditText);
            String birthday = getOrHint(text, Database.BIRTHDAY);
            try {
                // Try parsing the date
                String[] date = birthday.split("/");

                int year = Integer.parseInt(date[2]);
                int month = Integer.parseInt(date[1]) - 1;  // Months are 0 indexed
                int day = Integer.parseInt(date[0]);


                // Compare if the date is less than today
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day);
                // Check if date makes sense
                calendar.setLenient(false);
                System.out.println(calendar.get(Calendar.YEAR));
                if (!calendar.before(Calendar.getInstance())) {
                    text.setError(ERROR_INCORRECT_DATE);
                    return;
                }
                // Write to database
                db.writeBirthday(uid, date[2] + "-" + date[1] + "-" + date[0]);
            } catch (Exception e) {
                text.setError(ERROR_INVALID_DATE);
                return;
            }

            text = view.findViewById(R.id.modifyWeightEditText);
            textString = getOrHint(text, Database.LAST_CURRENT_WEIGHT);
            db.writeWeight(uid, Double.parseDouble(textString));

            Context context = getContext();
            int duration = Toast.LENGTH_SHORT;
            CharSequence toastText = "Hello toast!";

            // If the user logs in for the first time, go to either the home screen
            // or goto ProfileActivity
            if (firstTime) {
                toastText = PROFILE_HAS_BEEN_CREATED;
            } else {
                toastText = CHANGES_APPLIED;
            }

            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.setReorderingAllowed(true);
            fragmentTransaction.replace(R.id.fragmentContainerView, new ProfileFragment());
            fragmentTransaction.commit();

            Toast toast = Toast.makeText(context, toastText, duration);
            toast.show();
        }
    }

    /**
     * Method used to check whether the user entered something in the field.
     * If it is not the case, return the hint value in the TextView
     */
    private String getOrHint(EditText text, String field) {
        String returnText = text.getText().toString();
        if (returnText.equals("")) {
            // Wish we had Java 17 :(
            switch (field) {
                case Database.NAME:
                    returnText = hintName;
                    break;
                case Database.SURNAME:
                    returnText = hintSurname;
                    break;
                case Database.USERNAME:
                    returnText = hintUsername;
                    break;
                case Database.BIRTHDAY:
                    returnText = hintBirthday;
                    break;
                case Database.LAST_CURRENT_WEIGHT:
                    returnText = hintWeight;
            }
        }
        return returnText;
    }
}