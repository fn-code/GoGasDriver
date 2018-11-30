package gaskudriver.funcode.funcode.com.gogasdriver;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;

import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class TransaksiActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    ArrayList<LatLng> MarkerPoints;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;

    private GoogleApiClient client;

    private String post_key, id_driver;
    FirebaseDatabase database;
    DatabaseReference mDatabaseReference, mDatabaseReference2;
    DatabaseReference mUser, mDbase, gasStok;
    FirebaseAuth mAuth;
    private BroadcastReceiver broadcastReceiver;
    FloatingActionButton fab2, fab3, fab4, fab1;
    String idgas,url,nama_pemesan, idUser;
    Integer transaksi,jmlTransaksi, totbayarUser;
    Query getTransaksi;
    int g1,g2;
    static final Integer CALL = 0x2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaksi);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) findViewById(R.id.fab3);

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        id_driver = mAuth.getCurrentUser().getUid();

        post_key = getIntent().getExtras().getString("idTransaksi");
        idUser = getIntent().getExtras().getString("idUser");
        DatabaseReference user = database.getReference("users").child(idUser);

        gasStok = database.getReference("Jenis");
        mDatabaseReference = database.getReference("transaksi");
        mUser = database.getReference("driver").child(id_driver);
        mDbase = database.getReference("users").child(idUser);


        getTransaksi = database.getReference("detail_transaksi").orderByChild("IDTransaksi").equalTo(post_key);

        mUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                transaksi = dataSnapshot.child("Transaksi").getValue(Integer.class);
                jmlTransaksi = dataSnapshot.child("jmlTransaksi").getValue(Integer.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                url = dataSnapshot.child("Photo").getValue(String.class);
                nama_pemesan = dataSnapshot.child("Nama").getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabaseReference.child(post_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer tarAnt = dataSnapshot.child("TarifAntar").getValue(Integer.class);
                Integer totAnt = dataSnapshot.child("TotalBayar").getValue(Integer.class);
                totbayarUser = tarAnt + totAnt;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(TransaksiActivity.this, DetailTransaksiActivity.class);
                i.putExtra("idTransaksi", post_key);
                i.putExtra("idUser", idUser);
                startActivity(i);
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDbase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String telp = dataSnapshot.child("NoTelp").getValue(String.class);
                        if(ContextCompat.checkSelfPermission(TransaksiActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                                ActivityCompat.requestPermissions(TransaksiActivity.this,new String[] {Manifest.permission.CALL_PHONE}, CALL);
                            }
                        }else{
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:"+telp));
                            startActivity(callIntent);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(TransaksiActivity.this);
                alertDialog.setTitle("Perhatian.");
                alertDialog.setMessage("Anda yakin ingin menyelesaikan pesanan ini ?");
                alertDialog.setCancelable(false);

                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, int which) {
                        getTransaksi.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                final String GasID = dataSnapshot.child("IDGas").getValue(String.class);
                                final Integer jml = dataSnapshot.child("JumlahPesanan").getValue(Integer.class);

                                if(GasID.equals("1")){
                                    g1 += jml;
                                    countStokGas(GasID, g1);
                                }else if(GasID.equals("2")){
                                    g2 += jml;
                                    countStokGas(GasID, g2);

                                }
                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {

                            }

                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        int trans = transaksi + totbayarUser;
                        int jmlT = jmlTransaksi + 1;
                        mUser.child("Transaksi").setValue(trans);
                        mUser.child("jmlTransaksi").setValue(jmlT);
                        mUser.child("Status").setValue(1);
                        mDatabaseReference.child(post_key).child("Status").setValue(4);
                        Intent i = new Intent(TransaksiActivity.this, HomeActivity.class);
                        startActivity(i);
                        finish();
                        dialog.cancel();
                    }
                });
                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                alertDialog.show();
            }
        });


        MarkerPoints = new ArrayList<>();
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

        mDatabaseReference.child(post_key).addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                Double LokasiLat =  dataSnapshot.child("TujuanLatitude").getValue(Double.class);
                Double LokasiLng=  dataSnapshot.child("TujuanLongitude").getValue(Double.class);
                String Tujuan=  dataSnapshot.child("Lokasi").getValue(String.class);

                    LatLng dest = new LatLng(LokasiLat, LokasiLng);
                    Marker marker2 = mMap.addMarker(new MarkerOptions().position(dest).title(Tujuan));
                    //Marker marker2 = mMap.addMarker(new MarkerOptions().position(dest).title(Tujuan).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker)));
                    mMap.setPadding(10, 10, 10, 10);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(dest));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle_retro));

    }




    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("DriverMaps Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }


    @Override
    public void onStart() {
        super.onStart();

        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());

        Intent i =new Intent(getApplicationContext(),GPS_Service.class);
        startService(i);

        if(broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {

                    Double latitude = (Double) intent.getExtras().get("Latitude");
                    Double longitude = (Double) intent.getExtras().get("Longitude");

                    mUser.child("Latitude").setValue(latitude);
                    mUser.child("Longitude").setValue(longitude);



                }
            };
        }
        registerReceiver(broadcastReceiver,new IntentFilter("location_update"));


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
        if(broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver);
        }

    }


    @Override
    public void onStop() {
        super.onStop();


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }




    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(final Location location) {

        mLastLocation = location;

        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();

        }

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);


        }

    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}
                                    , 99);

                            mMap.setMyLocationEnabled(true);
                        }
                        return;

                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "Lokasi gagal didapatkan", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

    public void countStokGas(final String GasIdJenis, final int jmlGasPesanan){
        gasStok.child(GasIdJenis).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer jmlStkNow = dataSnapshot.child("Stok").getValue(Integer.class);
                int j = jmlStkNow - jmlGasPesanan;
                gasStok.child(GasIdJenis).child("Stok").setValue(j);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Selesaikan Pesanan Anda Terlebih Dahulu", Toast.LENGTH_SHORT).show();
        return;


    }

    @Override
    public void setExitSharedElementCallback(SharedElementCallback listener) {
        super.setExitSharedElementCallback(listener);
    }




    @Override
    protected void onResume() {
        super.onResume();
    }

}

