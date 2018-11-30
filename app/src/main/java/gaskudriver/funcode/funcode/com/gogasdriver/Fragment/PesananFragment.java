package gaskudriver.funcode.funcode.com.gogasdriver.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import gaskudriver.funcode.funcode.com.gogasdriver.LoginActivity;
import gaskudriver.funcode.funcode.com.gogasdriver.R;
import gaskudriver.funcode.funcode.com.gogasdriver.TransaksiActivity;
import gaskudriver.funcode.funcode.com.gogasdriver.model.TransaksiModel;

/**
 * Created by funcode on 10/31/17.
 */

public class PesananFragment extends Fragment {

    private LinearLayoutManager mLayoutManager;

    NumberFormat rupiahFormat;
    FirebaseAuth mAuthDriver;
    FirebaseRecyclerAdapter adapter;
    Query TransaksiPesanan;
    FirebaseDatabase database;
    RecyclerView mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View home = inflater.inflate(R.layout.pesanan_fragment, container, false);

        mRecyclerView = (RecyclerView) home.findViewById(R.id.daftarPesanan);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        database = FirebaseDatabase.getInstance();
        mAuthDriver = FirebaseAuth.getInstance();

        final String id_driver = mAuthDriver.getCurrentUser().getUid();

        TransaksiPesanan = database.getReference("transaksi").orderByChild("IDDriver").equalTo(id_driver);

        adapter = new FirebaseRecyclerAdapter<TransaksiModel, PesanViewHolder>(
                TransaksiModel.class,
                R.layout.pesanan_fragment_list,
                PesanViewHolder.class,
                TransaksiPesanan
        ) {
            @Override
            protected void populateViewHolder(final PesanViewHolder viewHolder, final TransaksiModel model, final int position) {
                    if(model.Status == 3){
                        rupiahFormat = NumberFormat.getInstance(Locale.GERMANY);
                        String tarif = rupiahFormat.format(Double.parseDouble(String.valueOf(model.TarifAntar)));
                        String total = rupiahFormat.format(Double.parseDouble(String.valueOf(model.TotalBayar)));

                        String ResTarif = "Rp. " + tarif ;
                        String ResTotal = "Rp. " + total ;

                        final String post_key = getRef(position).getKey();
                        final String idUser = model.IDUser;
                        viewHolder.tarif.setText(ResTarif);
                        viewHolder.total.setText(ResTotal);
                        viewHolder.alamat.setText(model.Lokasi);
                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent i = new Intent(getActivity(), TransaksiActivity.class);
                                i.putExtra("idTransaksi", post_key);
                                i.putExtra("idUser", idUser);
                                startActivity(i);
                                getActivity().finish();

                            }

                        });
                    }
                    else{
                        viewHolder.top.setVisibility(View.GONE);
                        viewHolder.total.setVisibility(View.GONE);
                        viewHolder.tarif.setVisibility(View.GONE);
                        viewHolder.alamat.setVisibility(View.GONE);
                        viewHolder.mView.setVisibility(View.GONE);
                        viewHolder.card.setVisibility(View.GONE);
                        viewHolder.second.setVisibility(View.GONE);
                        viewHolder.setIsRecyclable(false);
                    }



            }

        };

        mRecyclerView.setAdapter(adapter);

        return home;

    }

    //ViewHolder for our Firebase UI
    public static class PesanViewHolder extends RecyclerView.ViewHolder {

        public TextView tarif,total,alamat;
        View mView;
        LinearLayout top,second;
        CardView card;


        public PesanViewHolder(View v) {
            super(v);
            mView = v;
            top = (LinearLayout) v.findViewById(R.id.topmen);
            second = (LinearLayout) v.findViewById(R.id.detail);
            card = (CardView) v.findViewById(R.id.card);
            tarif = (TextView) v.findViewById(R.id.tarifAntar);
            alamat = (TextView) v.findViewById(R.id.alamat);
            total = (TextView) v.findViewById(R.id.totbayar);

        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter.cleanup();
    }

}
