package gaskudriver.funcode.funcode.com.gogasdriver.Fragment;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.NumberFormat;
import java.util.Locale;

import gaskudriver.funcode.funcode.com.gogasdriver.R;
import gaskudriver.funcode.funcode.com.gogasdriver.model.TransaksiModel;


public class HistoryFragment extends Fragment {

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
        View history = inflater.inflate(R.layout.history_fragment, container, false);

        mRecyclerView = (RecyclerView) history.findViewById(R.id.historyPesanan);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        database = FirebaseDatabase.getInstance();
        mAuthDriver = FirebaseAuth.getInstance();

        final String id_driver = mAuthDriver.getCurrentUser().getUid();

        TransaksiPesanan = database.getReference("transaksi").orderByChild("IDDriver").equalTo(id_driver);

        adapter = new FirebaseRecyclerAdapter<TransaksiModel, HistoryFragment.historyViewHolder>(
                TransaksiModel.class,
                R.layout.pesanan_fragment_list,
                HistoryFragment.historyViewHolder.class,
                TransaksiPesanan
        ) {
            @Override
            protected void populateViewHolder(final HistoryFragment.historyViewHolder viewHolder, final TransaksiModel model, final int position) {
                if(model.Status == 4 || model.Status == 5){
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
                            /*Intent i = new Intent(getActivity(), TransaksiActivity.class);
                            i.putExtra("idTransaksi", post_key);
                            i.putExtra("idUser", idUser);
                            startActivity(i);
                            getActivity().finish();*/

                            if(model.Status == 4){
                                Toast.makeText(getActivity(), "Pesanan Berhasil", Toast.LENGTH_SHORT).show();
                            }else if(model.Status == 5) {
                                Toast.makeText(getActivity(), "Pesanan Dibatalkan", Toast.LENGTH_SHORT).show();
                            }

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

        return history;
    }

    //ViewHolder for our Firebase UI
    public static class historyViewHolder extends RecyclerView.ViewHolder {

        public TextView tarif,total,alamat;
        View mView;
        LinearLayout top,second;
        CardView card;


        public historyViewHolder(View v) {
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
