package ch.epfl.sdp.healthplay;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;

import androidx.camera.core.ImageCapture;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.common.Barcode;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import ch.epfl.sdp.healthplay.database.Database;

@RunWith(AndroidJUnit4.class)
public class BarcodeScanFragmentTest {

    @Before
    public void init(){
        FragmentScenario frag = FragmentScenario.launchInContainer(MockBarcodeScanFragment.class);
    }


    @Test
    public void testEnterManually() {
        onView(withId(R.id.enter_manually_button)).check(matches(isDisplayed()));
        onView(withId(R.id.enter_manually_button)).perform(click());
        onView(withId(R.id.findProductInfos)).check(matches(isDisplayed()));
        onView(withId(R.id.barcodeText)).check(matches(isDisplayed()));
    }

    @Test
    public void testScan() throws InterruptedException {
        TimeUnit.SECONDS.sleep(1);
        onView(withId(R.id.get_information_from_barcode)).check(matches(isDisplayed()));
        onView(withId(R.id.get_information_from_barcode)).perform(
                new ViewAction() {
                    @Override
                    public Matcher<View> getConstraints() {
                        return ViewMatchers.isEnabled(); // no constraints, they are checked above
                    }

                    @Override
                    public String getDescription() {
                        return "click plus button";
                    }

                    @Override
                    public void perform(UiController uiController, View view) {
                        view.performClick();
                    }
                }
        );
        //onView(withId(R.id.progressBar)).check(matches(isDisplayed()));
    }

    public static class MockBarcodeScanFragment extends BarcodeScanFragment {

        private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
        private PreviewView previewView;
        private final Database database = new Database();

        // This field is only used in tests
        protected final static CountingIdlingResource idlingResource =
                new CountingIdlingResource("LOOKUP_BARCODE");

        // The barcode formats used
        private final static BarcodeScannerOptions options =
                new BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(
                                // Allow scanning of product barcodes
                                Barcode.FORMAT_EAN_13,
                                Barcode.FORMAT_EAN_8,
                                // Allow scanning of QR codes
                                Barcode.FORMAT_QR_CODE)
                        .build();
        private ImageCapture imageCapture;

        private View view;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            view = inflater.inflate(R.layout.fragment_barcode_scan, container, false);
            previewView = view.findViewById(R.id.previewView2);


            view.findViewById(R.id.get_information_from_barcode).setOnClickListener(v -> {
                try {
                    onClick();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            view.findViewById(R.id.enter_manually_button).setOnClickListener(v -> enterManually());
            return view;
        }

        @Override
        public void onClick() throws IOException {
            idlingResource.increment();
            // Set the progress bar to visible and disable user interaction
            ProgressBar bar = view.findViewById(R.id.progressBar);
            bar.setVisibility(View.VISIBLE);
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            Uri uri = Uri.parse("android.resource://" + getActivity().getPackageName() + "/drawable/barcode_example.png");
            try {
                scan(uri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Take out progress bar and clear not touchable flags
            bar.setVisibility(View.GONE);
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }
}
