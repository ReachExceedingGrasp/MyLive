package com.mylive;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class LiveViewMapActivity extends FragmentActivity implements OnMapReadyCallback {

    Button UserInfo;
    private GoogleMap mMap;
    FirebaseDatabase database;
    DatabaseReference eventRef;
    final String TAG="MYLIVE";
    MapEvent mapEvent;
    Bitmap bitmap;
    String key;
    ImageView eventImage;
    String imageURL;
    private LruCache<String, Bitmap> mMemoryCache;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_view_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        UserInfo = (Button) findViewById(R.id.button_user_info);
        database = FirebaseDatabase.getInstance();
        eventRef = database.getReference().child("events");

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        launchRingDialog();
        UserInfo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent goToUserInfo = new Intent(LiveViewMapActivity.this,UserInfoActivity.class);
                startActivity(goToUserInfo);
            }
        });
            if(Build.VERSION.SDK_INT>=23) {
                {
                    try {
                        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        Criteria criteria = new Criteria();

                        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
                        eventRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                               // String event = dataSnapshot.getValue(String.class);
                                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                                for(DataSnapshot child:children)
                                {
                                    key = child.getKey();
                                    Log.i("Children Key",child.getKey());
                                    Log.i("Children Value",child.getValue().toString());
                                    mapEvent = child.getValue(MapEvent.class);
                                    imageURL = mapEvent.image;
                                    Thread splashLoading = new Thread(){
                                        @Override
                                        public void run() {
                                            try {
                                                bitmap = getBitmapFromURL(mapEvent.image);
                                                bitmap = Bitmap.createScaledBitmap(bitmap, 120, 120, false);
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
                                    mMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(mapEvent.getLatitude(), mapEvent.getLongitude()))
                                                .title("Hello world")
                                                .icon(BitmapDescriptorFactory.fromBitmap(bitmap)))
                                                .setTag(child.getKey());
                                    }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }

                        });

                        mMap.setMyLocationEnabled(true);
                        mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                                .title("Hello world")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin)));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));
                    }
                    catch(SecurityException e)
                    {
                        Log.i("FACEBOOK","ERROR"+e);
                    }
                }
        }


            if (mMap!=null) {
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter(){
                @Override
                public View getInfoWindow(Marker marker) {
                    View view = getLayoutInflater().inflate(R.layout.custom_info_window,null);
                    eventImage = (ImageView) view.findViewById(R.id.event_icon_info_window);
                    TextView infoName = (TextView) view.findViewById(R.id.info_name);
                    TextView infoTitle = (TextView) view.findViewById(R.id.info_title);

                    String key = marker.getTag().toString();

                    LatLng location = marker.getPosition();

                    Query queryRef = eventRef.orderByKey().equalTo(key);
                    ValueEventListener valueEventListener = new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                            for(DataSnapshot child:children)
                            {
                                Log.i("Children Key",child.getKey());
                                Log.i("Children Value",child.getValue().toString());
                                mapEvent = child.getValue(MapEvent.class);
                                Thread splashLoading = new Thread(){
                                    @Override
                                    public void run() {
                                        try {
                                            bitmap = getBitmapFromURL(mapEvent.image);
                                            bitmap = Bitmap.createScaledBitmap(bitmap, 120, 120, false);
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
                                mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(mapEvent.getLatitude(), mapEvent.getLongitude()))
                                        .title("Hello world")
                                        .icon(BitmapDescriptorFactory.fromBitmap(bitmap)))
                                        .setTag(child.getKey());
                            }



                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError)
                        {

                        }
                    };
                    queryRef.addValueEventListener(valueEventListener);


                    if(mapEvent.image != null) {
                        eventImage.setImageBitmap(bitmap);
                    }
                    infoName.setText("Testing name");
                    infoTitle.setText("Testing Title");


                    return view;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    return null;
                }
            });

            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    Toast.makeText(getApplicationContext(), "Info window clicked", Toast.LENGTH_SHORT).show();
                    Intent goToEvent = new Intent(LiveViewMapActivity.this,EventInfoActivity.class);
                    goToEvent.putExtra("USER_KEY",marker.getTag().toString());
                    goToEvent.putExtra("image_url",imageURL);
                    startActivity(goToEvent);
                }
            });
        }
    }


    /**************BITMAP STUFF***********/

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

        final ProgressDialog ringProgressDialog = ProgressDialog.show(LiveViewMapActivity.this, "Please wait ...", "Loading Map ...", true);
        ringProgressDialog.setCancelable(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Here you should write your time consuming task...
                    // Let the progress ring for 10 seconds...
                    Thread.sleep(4000);
                } catch (Exception e) {

                }
                ringProgressDialog.dismiss();
            }

        }).start();

    }


}