package ch.epfl.sdp.healthplay.scan;

import android.view.View;

import androidx.navigation.Navigation;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import ch.epfl.sdp.healthplay.HomeScreenActivity;
import ch.epfl.sdp.healthplay.ProfileSettingsActivity;
import ch.epfl.sdp.healthplay.R;

@RunWith(AndroidJUnit4.class)
public class ProductListTest {

    @Rule
    public ActivityScenarioRule<HomeScreenActivity> testRule =
            new ActivityScenarioRule<>(HomeScreenActivity.class);

    private static final String TEST_CODE = "737628064502";

    @Before
    public void before() throws ExecutionException, InterruptedException {
        FirebaseAuth.getInstance().signInWithEmailAndPassword("health-play@admin.ch", "123456");
    }

    @Test
    public void test1() {
        testRule.getScenario().onActivity(a -> {

            View v = a.findViewById(R.id.bottomNavigationView).findViewById(R.id.profileActivity);
            v.performClick();
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            v.findViewById(R.id.goToProductList).callOnClick();
        });
    }
}