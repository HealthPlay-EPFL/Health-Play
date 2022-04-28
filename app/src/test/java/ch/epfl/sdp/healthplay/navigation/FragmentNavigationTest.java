package ch.epfl.sdp.healthplay.navigation;

import static org.junit.Assert.*;

import androidx.fragment.app.FragmentContainer;
import androidx.fragment.app.FragmentContainerView;

import org.junit.Test;

public class FragmentNavigationTest {

    @Test
    public void switchToFragmentTest() {
        FragmentNavigation.switchToFragment(null, null);
    }
}