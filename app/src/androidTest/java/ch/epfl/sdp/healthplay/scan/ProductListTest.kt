package ch.epfl.sdp.healthplay.scan

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.epfl.sdp.healthplay.productlist.ProductListFragment
import com.google.firebase.auth.FirebaseAuth
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class ProductListTest {
    companion object {
        private const val TEST_CODE = "737628064502"
    }

    @Before
    fun before() {
        FirebaseAuth.getInstance().signInWithEmailAndPassword("health-play@admin.ch", "123456")
    }

    @Test
    fun test1() {

        val scenario = launchFragmentInContainer<ProductListFragment>(
                initialState = Lifecycle.State.INITIALIZED
        )
        // EventFragment has gone through onAttach(), but not onCreate().
        // Verify the initial state.
        scenario.moveToState(Lifecycle.State.RESUMED)
        TimeUnit.SECONDS.sleep(2)
        scenario.onFragment {
            TimeUnit.SECONDS.sleep(2)
        }

    }
}
