package gaskudriver.funcode.funcode.com.gogasdriver.Fragment;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import gaskudriver.funcode.funcode.com.gogasdriver.HomeActivity;
import gaskudriver.funcode.funcode.com.gogasdriver.LoginActivity;
import gaskudriver.funcode.funcode.com.gogasdriver.R;


public class ProfilFragment extends Fragment {
    ImageView userPF;
    EditText hp, pass;
    Button btnPass, btnHP, btnKeluar;
    TextView nama;

    FirebaseAuth userAuth;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View setting = inflater.inflate(R.layout.profil_fragment, container, false);
        userAuth = FirebaseAuth.getInstance();

        userPF = (ImageView) setting.findViewById(R.id.users_profil);
        nama = (TextView) setting.findViewById(R.id.nm_user);
        hp = (EditText) setting.findViewById(R.id.hpBaru);
        pass = (EditText) setting.findViewById(R.id.passBaru);
        btnHP = (Button) setting.findViewById(R.id.btnhp);
        btnPass = (Button) setting.findViewById(R.id.btnPass);
        btnKeluar = (Button) setting.findViewById(R.id.btnKeluar);

        String idUser = userAuth.getCurrentUser().getUid();
        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("driver").child(idUser);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String namaDB = dataSnapshot.child("Nama").getValue(String.class);
                String utl = dataSnapshot.child("Photo").getValue(String.class);

                Picasso.with(getActivity().getApplicationContext()).load(utl)
                        .placeholder(R.drawable.gas)
                        .error(R.drawable.gas)
                        .resize(100,100).centerCrop().into(userPF);
                nama.setText(namaDB);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser userInfo = userAuth.getCurrentUser();
                String newP = pass.getText().toString().trim();

                if(TextUtils.getTrimmedLength(newP) >= 7  ){

                    userInfo.updatePassword(newP)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getActivity(), "Password berhasil di ubah", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getActivity(), "Password gagal di ubah", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                }else{
                    Toast.makeText(getActivity(), "Password minimal 7 digit ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnKeluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userAuth.signOut();
                Intent i = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                startActivity(i);
                getActivity().finish();
            }
        });



        btnHP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newHP = hp.getText().toString().trim();

                if(TextUtils.getTrimmedLength(newHP) >= 11  ){
                    userRef.child("NoTelp").setValue(newHP);
                    Toast.makeText(getActivity(), "No Handphone Berhasil Diubah", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getActivity(), HomeActivity.class);
                    startActivity(i);

                }else{
                    Toast.makeText(getActivity(), "Periksa kembali no yang anda masukan ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return setting;
    }
}
