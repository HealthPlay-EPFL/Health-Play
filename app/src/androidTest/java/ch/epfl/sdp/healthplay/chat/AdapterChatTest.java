package ch.epfl.sdp.healthplay.chat;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sdp.healthplay.AuthUiActivityTest;
import ch.epfl.sdp.healthplay.HomeScreenActivity;
import ch.epfl.sdp.healthplay.R;

@RunWith(AndroidJUnit4.class)
public class AdapterChatTest {

    @Rule
    public ActivityScenarioRule<HomeScreenActivity> testRule = new ActivityScenarioRule<>(HomeScreenActivity.class);

    @Before
    public void before() throws InterruptedException{
        AuthUiActivityTest.signIn("health.play@gmail.com", "123456");
        onView(withId(R.id.FriendList_button)).perform(click());
    }

    @Test
    public void getItemCount() {
        List<ModelChat> list = new ArrayList<>();
        list.add(new ModelChat());
        AdapterChat adapterChat = new AdapterChat(null, list, "");
        assertEquals(adapterChat.getItemCount(), 1);
    }

    @Test
    public void getItemViewType() {
        List<ModelChat> list = new ArrayList<>();
        ModelChat modelChat = new ModelChat();
        modelChat.setSender("z0WPZPfFGMTatM2pX30TT71TWyo2");
        list.add(modelChat);
        AdapterChat adapterChat = new AdapterChat(null, list, "");
        //Assert that it is equal to MSG_TYPE_LEFT
        assertEquals(0, adapterChat.getItemViewType(0));
    }
}