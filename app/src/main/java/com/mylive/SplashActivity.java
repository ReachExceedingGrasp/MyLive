package com.mylive;

import android.*;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.Profile;
import com.facebook.login.widget.LoginButton;

public class SplashActivity extends AppCompatActivity {


    Profile profile;
    TextView tvLoggingIn;
    boolean status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        /*************
         * Code to chceck if logged in or not
         */


                profile = Profile.getCurrentProfile();
                if (profile != null) {
                    LinearLayout view = (LinearLayout) LayoutInflater.from(getApplicationContext()).inflate(R.layout.fragment_login_facebook,null);
                    tvLoggingIn = (TextView) view.findViewById(R.id.tv_logging_in);
                    view.removeView(tvLoggingIn);
                    LinearLayout mainView = (LinearLayout) LayoutInflater.from(getApplicationContext()).inflate(R.layout.activity_main,null);
                    mainView.addView(tvLoggingIn);
                    status = true;
                }
                else {
                    LinearLayout view = (LinearLayout) LayoutInflater.from(getApplicationContext()).inflate(R.layout.fragment_login_facebook,null);
                    LoginButton loginButton = (LoginButton) view.findViewById(R.id.login_button);
                    view.removeView(loginButton);
                    LinearLayout mainView = (LinearLayout) LayoutInflater.from(getApplicationContext()).inflate(R.layout.activity_main,null);
                    mainView.addView(loginButton);
                    status=false;
                }




            /*******************/

            /*******
             * splash screen*/
        Thread splashLoading = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(3000);
                    Intent goToMain = new Intent(getApplicationContext(),MainActivity.class);
                    goToMain.putExtra("status",status);
                    startActivity(goToMain);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        splashLoading.start();
        }
}
