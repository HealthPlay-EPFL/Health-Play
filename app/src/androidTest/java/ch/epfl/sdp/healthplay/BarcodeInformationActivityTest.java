package ch.epfl.sdp.healthplay;

import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class BarcodeInformationActivityTest {
    private static final String TEST_CODE = "737628064502";

    @Test
    public void test() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), BarcodeInformationActivity.class);
        intent.putExtra(ProductInfoActivity.EXTRA_MESSAGE, TEST_CODE);

        try (ActivityScenario<GreetingActivity> ignored = ActivityScenario.launch(intent)) {
            Espresso.onView(withId(R.id.pName)).check(
                    ViewAssertions.matches(
                            ViewMatchers.withText("Thai peanut noodle kit includes stir-fry rice noodles & thai peanut seasoning")
                    )
            );
            Espresso.onView(withId(R.id.pEnergy)).check(
                    ViewAssertions.matches(
                            ViewMatchers.withText("385")
                    )
            );
        }
    }
}
