/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ch.epfl.sdp.healthplay.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;


import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.AuthUI.IdpConfig;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.firebase.ui.auth.util.ExtraConstants;

import ch.epfl.sdp.healthplay.HomeScreenActivity;
import ch.epfl.sdp.healthplay.ProfileSettingsActivity;
import ch.epfl.sdp.healthplay.ProfileSettingsFragment;
import ch.epfl.sdp.healthplay.R;


import com.google.android.gms.common.Scopes;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.ActionCodeSettings;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;

import java.util.ArrayList;
import java.util.List;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import ch.epfl.sdp.healthplay.database.Database;
import ch.epfl.sdp.healthplay.databinding.AuthUiLayoutBinding;

public class AuthUiActivity extends AppCompatActivity
        implements ActivityResultCallback<FirebaseAuthUIAuthenticationResult> {
    private static final String TAG = "AuthUiActivity";



    private AuthUiLayoutBinding mBinding;

    private final ActivityResultLauncher<Intent> signIn =
            registerForActivityResult(new FirebaseAuthUIActivityResultContract(), this);

    @NonNull
    public static Intent createIntent(@NonNull Context context) {
        return new Intent(context, AuthUiActivity.class);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = AuthUiLayoutBinding.inflate(getLayoutInflater());


        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            Database db = new Database();
            String id = currentUser.getUid();
            db.initStatToDay(id);
            startActivity(new Intent(this,HomeScreenActivity.class));
            finish();
            return;
        }
        signIn.launch(getSignInIntent());


    }


    @NonNull
    public AuthUI getAuthUI() {
        AuthUI authUI = AuthUI.getInstance();
        if (mBinding.useAuthEmulator.isChecked()) {
            authUI.useEmulator("10.0.2.2", 9099);
        }

        return authUI;
    }

    private Intent getSignInIntent() {
        AuthUI.SignInIntentBuilder builder = getAuthUI().createSignInIntentBuilder()
                .setTheme(getSelectedTheme())
                .setLogo(getSelectedLogo())
                .setAvailableProviders(getSelectedProviders())
                .setIsSmartLockEnabled(mBinding.credentialSelectorEnabled.isChecked(),
                        mBinding.hintSelectorEnabled.isChecked())
                .setAlwaysShowSignInMethodScreen(true).setIsSmartLockEnabled(false);


        return builder.build();
    }




    @Override
    protected void onResume() {
        super.onResume();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null && getIntent().getExtras() == null) {
            startSignedInActivity(null);
            finish();
        }
    }

    private void handleSignInResponse(int resultCode, @Nullable IdpResponse response) {
        // Successfully signed in
        if (resultCode == RESULT_OK) {
            Intent intent = new Intent(this, HomeScreenActivity.class);
            FirebaseUserMetadata metadata = FirebaseAuth.getInstance().getCurrentUser().getMetadata();
            if (metadata != null && metadata.getCreationTimestamp() == metadata.getLastSignInTimestamp()) {
                new Database().writeNewUser(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                        "", 0, 0);
                // Tell intent that the user has been created for the first time
                intent.putExtra(ProfileSettingsFragment.MESSAGE, true);
                startActivity(intent);
            } else {
                startActivity(new Intent(this, HomeScreenActivity.class));
            }
            finish();
        } else {
            // Sign in failed
            if (response == null) {
                // User pressed back button
                showSnackbar(R.string.sign_in_cancelled);
                return;
            }

            if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                showSnackbar(R.string.no_internet_connection);
                return;
            }



            if (response.getError().getErrorCode() == ErrorCodes.ERROR_USER_DISABLED) {
                showSnackbar(R.string.account_disabled);
                return;
            }

            showSnackbar(R.string.unknown_error);
            Log.e(TAG, "Sign-in error: ", response.getError());
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(this,HomeScreenActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void startSignedInActivity(@Nullable IdpResponse response) {
        startActivity(new Intent(this,AuthUiActivity.class));
    }

    @StyleRes
    private int getSelectedTheme() {

            return com.firebase.ui.auth.R.style.FirebaseUI_DefaultMaterialTheme;
    }

    @DrawableRes
    private int getSelectedLogo() {

            return R.drawable.logo;

    }

    private List<IdpConfig> getSelectedProviders() {
        List<IdpConfig> selectedProviders = new ArrayList<>();

            selectedProviders.add(new IdpConfig.EmailBuilder()
                    .setRequireName(mBinding.requireName.isChecked())
                    .setAllowNewAccounts(mBinding.allowNewEmailAccounts.isChecked())
                    //.setDefaultEmail("health.play@gmail.com")
                    .build());



        return selectedProviders;
    }






    private void showSnackbar(@StringRes int errorMessageRes) {
        Snackbar.make(mBinding.getRoot(), errorMessageRes, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onActivityResult(@NonNull FirebaseAuthUIAuthenticationResult result) {
        // Successfully signed in
        IdpResponse response = result.getIdpResponse();
        handleSignInResponse(result.getResultCode(), response);
    }
}
