package ch.epfl.sdp.healthplay.navigation;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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
    }
}
