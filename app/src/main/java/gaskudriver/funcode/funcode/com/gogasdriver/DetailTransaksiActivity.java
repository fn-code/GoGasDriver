package gaskudriver.funcode.funcode.com.gogasdriver;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.Locale;

import gaskudriver.funcode.funcode.com.gogasdriver.model.DetailTransaksiModel;

public class DetailTransaksiActivity extends AppCompatActivity {

    TextView alamat,total, tarifAntar, palamat, nmuser;
    ImageView imgProfil;
    FirebaseDatabase database;
    Query transaksi;
    String idTransaksi, idUser, id_driver;
    CardView batal;
    Double jmlTransaksi,TransaksiDriver;
    DatabaseReference mUser, transSts;
    private LinearLayoutManager mLayoutManager;

    FirebaseAuth mAuth;
    NumberFormat rupiahFormat;
    FirebaseRecyclerAdapter<DetailTransaksiModel, DetailTransaksiViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_transaksi);

        alamat = (TextView) findViewById(R.id.alamat);
        imgProfil = (ImageView) findViewById(R.id.users_profil);
        nmuser = (TextView) findViewById(R.id.nm_user);
        palamat = (TextView) findViewById(R.id.palamat);
        total = (TextView) findViewById(R.id.totbayar);
        tarifAntar = (TextView) findViewById(R.id.tarifAntar);
        batal = (CardView) findViewById(R.id.batal);

        mAuth = FirebaseAuth.getInstance();
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        idTransaksi = getIntent().getExtras().getString("idTransaksi");
        idUser = getIntent().getExtras().getString("idUser");

        id_driver = mAuth.getCurrentUser().getUid();

        database = FirebaseDatabase.getInstance();
        transaksi = database.getReference("transaksi").child(idTransaksi);
        transSts = database.getReference("transaksi");
        mUser = database.getReference("driver").child(id_driver);
        DatabaseReference user = database.getReference("users").child(idUser);


        transaksi.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String al = dataSnapshot.child("Lokasi").getValue(String.class);
                Integer tarifAn = dataSnapshot.child("TarifAntar").getValue(Integer.class);
                Integer tot = dataSnapshot.child("TotalBayar").getValue(Integer.class);

                alamat.setText(al);
                total.setText(String.valueOf(tot));
                tarifAntar.setText(String.valueOf(tarifAn));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TransaksiDriver = dataSnapshot.child("Transaksi").getValue(Double.class);
                jmlTransaksi = dataSnapshot.child("jmlTransaksi").getValue(Double.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String url = dataSnapshot.child("Photo").getValue(String.class);
                String nama_pemesan = dataSnapshot.child("Nama").getValue(String.class);
                Glide.with(DetailTransaksiActivity.this).load(url).into(imgProfil);
                nmuser.setText(nama_pemesan);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.daftarpesan);
        mLayoutManager = new LinearLayoutManager(DetailTransaksiActivity.this, LinearLayoutManager.VERTICAL, false);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        Query mDatabaseReference = database.getReference("detail_transaksi").orderByChild("IDTransaksi").equalTo(idTransaksi);

        adapter = new FirebaseRecyclerAdapter<DetailTransaksiModel, DetailTransaksiViewHolder>(
                DetailTransaksiModel.class,
                R.layout.activity_detail_transaksi_list,
                DetailTransaksiViewHolder.class,
                mDatabaseReference
        ) {
            @Override
            protected void populateViewHolder(final DetailTransaksiViewHolder viewHolder, DetailTransaksiModel model, final int position) {
                viewHolder.nama.setText(String.valueOf(model.Gas+" - "+model.JumlahPesanan+" Buah"));
                if(model.Status == 1){
                    viewHolder.jenis.setText("Beli Baru");
                }else {
                    viewHolder.jenis.setText("Isi Ulang");
                }

                String totl;
                try {
                    NumberFormat rupiahFormat = NumberFormat.getInstance(Locale.GERMANY);
                    totl = rupiahFormat.format(Double.parseDouble(String.valueOf(model.Total)));
                } catch (NumberFormatException e) {
                    totl = String.valueOf(0);
                }
                String Result = "Rp. " + totl;
                viewHolder.tot.setText(Result);

            }
        };
        mRecyclerView.setAdapter(adapter);

        batal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(DetailTransaksiActivity.this);
                alertDialog.setTitle("Perhatian.");
                alertDialog.setMessage("Anda yakin ingin membatalkan pesanan ini ?");
                alertDialog.setCancelable(false);
                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, int which) {
                        mUser.child("Status").setValue(1);
                        transSts.child(idTransaksi).child("Status").setValue(5);
                        Intent i = new Intent(DetailTransaksiActivity.this, HomeActivity.class);
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





    }

    //ViewHolder for our Firebase UI
    public static class DetailTransaksiViewHolder extends RecyclerView.ViewHolder {
        public TextView nama, jenis,tot;
        View mView;


        public DetailTransaksiViewHolder(View v) {
            super(v);
            mView = v;
            nama = (TextView) v.findViewById(R.id.nama);
            jenis = (TextView) v.findViewById(R.id.jenis);
            tot = (TextView) v.findViewById(R.id.tot);

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(DetailTransaksiActivity.this, TransaksiActivity.class);
        i.putExtra("idTransaksi", idTransaksi);
        i.putExtra("idUser", idUser);
        startActivity(i);
    }
}
