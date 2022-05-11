package ch.epfl.sdp.healthplay.chat;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

import android.view.Display;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sdp.healthplay.R;

@RunWith(AndroidJUnit4.class)
public class AdapterChatTest {

    @Before
    public void before() throws InterruptedException{
        FirebaseAuth.getInstance().signInWithEmailAndPassword("health.play@gmail.com", "123456");
        onView(withId(R.id.FriendList_button)).perform(click());
        onView(withId(R.id.addFriendBouton)).perform(click());
    }

    @Test
    public void getItemCount() {
        List<ModelChat> list = new ArrayList<>();
        list.add(new ModelChat());
        AdapterChat adapterChat = new AdapterChat(null, list, "");
        assertEquals(adapterChat.getItemCount(), 1);
    }
}