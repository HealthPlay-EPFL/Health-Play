package ch.epfl.sdp.healthplay.navigation;

import android.util.Log;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import ch.epfl.sdp.healthplay.QrCodeFragment;
import ch.epfl.sdp.healthplay.R;

public class FragmentNavigation {

    /**
     * Switch to the given fragment
     * @param fragment
     */
    public static void switchToFragment(FragmentManager fragmentManager, Fragment fragment){
        if(fragmentManager != null && fragment != null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, fragment, null)
                    .commit();
        }
        else{
            Log.e("FRAGMEN SWITCH", "Couldn't switch fragment because either the fragment manager or the fragement is null");
        }
    }

    public static View.OnClickListener switchToFragmentListener(FragmentManager fragmentManager, Fragment fragment){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToFragment(fragmentManager, fragment);
            }
        };
    }
}
