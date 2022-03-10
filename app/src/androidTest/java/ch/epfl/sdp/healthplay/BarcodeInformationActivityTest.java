package ch.epfl.sdp.healthplay;

import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;

import org.junit.Test;

public class BarcodeInformationActivityTest {
    private static final String TEST_CODE = "737628064502";

    @Test
    public void test() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), BarcodeInformationActivity.class);
        intent.putExtra(ProductInfoActivity.EXTRA_MESSAGE, TEST_CODE);

        try (ActivityScenario<GreetingActivity> scenario = ActivityScenario.launch(intent)) {
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
