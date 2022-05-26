package ch.epfl.sdp.healthplay.scan;

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.launchFragment
import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.epfl.sdp.healthplay.*
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

@RunWith(AndroidJUnit4::class)
class ScanDecoderTest {

    /*@Test
    public void testScanNothing() {

        Intent intente = new Intent(ApplicationProvider.getApplicationContext(), BarcodeInformationActivity.class);
        intente.putExtra(null, "a");

        try (ActivityScenario<BarcodeInformationActivity> testRule = ActivityScenario.launch(intente)) {

            testRule.onActivity(a -> {
                try {
                    Uri uri = Uri.parse("android.resource://" + a.getApplicationContext().getPackageName() + "/drawable/barcode_example");
                    ScanDecoder.scan(uri, a.getApplicationContext(),
                            productJson -> {
                                System.out.println("hi");
                            }, a.findViewById(R.id.fragmentScan));
                    TimeUnit.SECONDS.sleep(2);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }*/

    /*
    try {
                    Uri uri = Uri.parse("android.resource://" + a.getApplicationContext().getPackageName() + "/drawable/qr_example");
                    ScanDecoder.scan(uri, a.getApplicationContext(),
                            productJson -> {
                                System.out.println("hi");
                            }, a.findViewById(R.id.fragmentScan).getRootView());
                    TimeUnit.SECONDS.sleep(2);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
     */

    @Test
    fun testScanNothing2() {
        val scenario = launchFragment<BarcodeScanFragmentTest>()
        scenario.onFragment { f ->
            try {
                val uri = Uri.parse("android.resource://" + "ch.epfl.sdp.healthplay" + "/drawable/qr_example")
                ScanDecoder.scan(uri, f.context, Consumer { println("") }, f.view)
                TimeUnit.SECONDS.sleep(5)
            } catch (e: Exception) {}
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
