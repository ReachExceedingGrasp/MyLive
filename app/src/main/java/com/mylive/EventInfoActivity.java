package com.mylive;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Sunny on 3/25/2017.
 */

public class EventInfoActivity extends AppCompatActivity {
    MapEventInfo mapEventInfo;
    FirebaseDatabase database;
    DatabaseReference eventRef;
    TextView details;
    Bitmap bitmap;
    String imageURL;
    ImageView event_image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info);
        database = FirebaseDatabase.getInstance();
        eventRef = database.getReference().child("event_social_info");
        Bundle data = getIntent().getExtras();
        String key= data.getString("USER_KEY");
        imageURL = data.getString("image_url");
        details= (TextView) findViewById(R.id.textView_details);
        TextView clickableLinks = (TextView) findViewById(R.id.textView_clickable_links);
        event_image = (ImageView) findViewById(R.id.event_image);
        launchRingDialog();

        eventRef.child(key).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("MAP EVENT", "MAP EVENT CHECK");
                mapEventInfo = dataSnapshot.getValue(MapEventInfo.class);
                Thread splashLoading = new Thread(){
                    @Override
                    public void run() {
                        try {
                            bitmap = getBitmapFromURL(imageURL);
                            bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, false);
                            bitmap = getCroppedBitmap(bitmap);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                splashLoading.start();
                try {
                    splashLoading.join();
                }
                catch(InterruptedException e)
                {
                }
                event_image.setImageBitmap(bitmap);

                Log.i("EVENT INFO",mapEventInfo.toString());
                details.setText("Description: "+mapEventInfo.description+"\n\n"+
                        "Email: "+mapEventInfo.email+"\n\n"+
                        "Insta: "+mapEventInfo.insta+"\n\n"+
                        "Phone: "+mapEventInfo.phone+"\n\n"+
                        "Purchase Page: "+mapEventInfo.purchasePage+"\n\n"+
                        "Website: "+mapEventInfo.website+"\n\n");
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
        Button checkIn = (Button)findViewById(R.id.button_check_in);
        checkIn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Checked In", Toast.LENGTH_SHORT).show();

            }
        });

        }


    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int extraHeight = 0;
        int extraWidth = 0;
        int centerX = (width+extraWidth)/2;
        int centerY = (height+extraHeight)/2;
        int radius = (width+extraWidth)/2;

        Bitmap output = Bitmap.createBitmap(width+extraWidth, height+extraHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, width+extraWidth, height+extraHeight);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(centerX, centerY, radius-5, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP));
        paint.setColor(Color.RED);
        canvas.drawCircle(centerX, centerY, radius , paint);


        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }

    public void launchRingDialog() {

        final ProgressDialog ringProgressDialog = ProgressDialog.show(EventInfoActivity.this, "Please wait ...", "Loading Event Information ...", true);
        ringProgressDialog.setCancelable(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Here you should write your time consuming task...
                    // Let the progress ring for 10 seconds...
                    Thread.sleep(1000);
                } catch (Exception e) {

                }
                ringProgressDialog.dismiss();
            }

        }).start();
    }

}
