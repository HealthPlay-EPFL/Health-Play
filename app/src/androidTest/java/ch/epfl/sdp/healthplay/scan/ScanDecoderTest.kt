package ch.epfl.sdp.healthplay.scan;

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.launchFragment
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.epfl.sdp.healthplay.*
import okhttp3.internal.wait
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

@RunWith(AndroidJUnit4::class)
class ScanDecoderTest {
    @Test
    fun testScanNothing() {
        val scenario = launchFragmentInContainer<BarcodeScanFragmentTest>(
                initialState = Lifecycle.State.INITIALIZED
        )
        // EventFragment has gone through onAttach(), but not onCreate().
        // Verify the initial state.
        scenario.moveToState(Lifecycle.State.RESUMED)
        TimeUnit.SECONDS.sleep(2)
        scenario.onFragment { f ->
            try {
                val uri: Uri = Uri.parse("android.resource://ch.epfl.sdp.healthplay/drawable/barcode_example");
                ScanDecoder.scan(uri, f.context, { println("") }, f.view)
            } catch (e: Exception) {
            }
        }
    }


    @Test
    fun testScanNothing2() {
        val scenario = launchFragmentInContainer<BarcodeScanFragmentTest>(
                initialState = Lifecycle.State.INITIALIZED
        )
        // EventFragment has gone through onAttach(), but not onCreate().
        // Verify the initial state.
        scenario.moveToState(Lifecycle.State.RESUMED)
        TimeUnit.SECONDS.sleep(2)
        scenario.onFragment { f ->
            try {
                f.view!!.findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.shadowFrameScanFriend).visibility = View.VISIBLE
                val uri = Uri.parse("android.resource://ch.epfl.sdp.healthplay/drawable/qr_example")
                ScanDecoder.scan(uri, f.context, Consumer { println("") }, f.view).wait()

                onView(withId(R.id.addFriendScanButton)).perform(click())
            } catch (e: Exception) {
            }
        }


    }

    @Test
    fun testScanNothing3() {
        val scenario = launchFragmentInContainer<BarcodeScanFragmentTest>(
                initialState = Lifecycle.State.INITIALIZED
        )
        // EventFragment has gone through onAttach(), but not onCreate().
        // Verify the initial state.
        scenario.moveToState(Lifecycle.State.RESUMED)
        TimeUnit.SECONDS.sleep(2)
        scenario.onFragment { f ->
            try {
                val uri = Uri.parse("android.resource://ch.epfl.sdp.healthplay/drawable/logo")
                ScanDecoder.scan(uri, f.context, Consumer { println("") }, f.view)
            } catch (e: Exception) {
            }
        }


    }

    @Test
    fun clickOnScan() {
        val scenario = launchFragmentInContainer<BarcodeScanFragmentTest>(
                initialState = Lifecycle.State.INITIALIZED
        )
        // EventFragment has gone through onAttach(), but not onCreate().
        // Verify the initial state.
        scenario.moveToState(Lifecycle.State.RESUMED)

        onView(withId(R.id.get_information_from_barcode)).perform(click())
        TimeUnit.SECONDS.sleep(2)
    }

    @Test
    fun clickOnManual() {
        val scenario = launchFragmentInContainer<BarcodeScanFragmentTest>(
                initialState = Lifecycle.State.INITIALIZED
        )
        // EventFragment has gone through onAttach(), but not onCreate().
        // Verify the initial state.
        scenario.moveToState(Lifecycle.State.RESUMED)
        TimeUnit.SECONDS.sleep(2)

        scenario.onFragment { f ->
            f.enterManually()
        }
    }

    /*@Test
    public void testScanNothing3() {

        Intent intente = new Intent(ApplicationProvider.getApplicationContext(), BarcodeInformationActivity.class);
        intente.putExtra(null, "a");

        try (ActivityScenario<BarcodeInformationActivity> testRule = ActivityScenario.launch(intente)) {

            testRule.onActivity(a -> {
                try {
                    Uri uri = Uri.parse("android.resource://" + a.getApplicationContext().getPackageName() + "/drawable/logo");
                    ScanDecoder.scan(uri, a.getApplicationContext(),
                            productJson -> {
                                System.out.println("hi");
                            }, a.findViewById(R.id.fragmentScan).getRootView());
                    TimeUnit.SECONDS.sleep(2);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }*/
}
