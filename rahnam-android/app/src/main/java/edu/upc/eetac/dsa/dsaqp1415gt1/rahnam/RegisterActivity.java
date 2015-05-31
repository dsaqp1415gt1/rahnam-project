package edu.upc.eetac.dsa.dsaqp1415gt1.rahnam;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import edu.upc.eetac.dsa.dsaqp1415gt1.rahnam.api.User;

/**
 * Created by Cristina on 31/05/2015.
 */
public class RegisterActivity extends Activity {

    private final static String TAG = LoginActivity.class.getName();
    String username;
    User user = new User();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        SharedPreferences prefs = getSharedPreferences("rahnam-profile",
                Context.MODE_PRIVATE);
      //  String username = prefs.getString("username", null);
       // String userpass = prefs.getString("userpass", null);


        setContentView(R.layout.register_layout);
    }

}
