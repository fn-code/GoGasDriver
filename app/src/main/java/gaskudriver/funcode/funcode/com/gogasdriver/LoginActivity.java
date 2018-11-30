package gaskudriver.funcode.funcode.com.gogasdriver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {

    Button btnMasul;
    EditText username;
    EditText pass;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    ProgressDialog mProgres;
    FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnMasul = (Button) findViewById(R.id.masuk_pangkalan);
        username = (EditText) findViewById(R.id.email);
        pass = (EditText) findViewById(R.id.pass);
        mProgres = new ProgressDialog(this);
        btnMasul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checklogin();
            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser() != null){
                    finish();
                    Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(i);
                }

            }
        };
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("driver");
    }


    private void checklogin() {

        String Username = username.getText().toString().trim();
        String Pass = pass.getText().toString().trim();

        if(!TextUtils.isEmpty(Username) && !TextUtils.isEmpty(Pass)) {

            mProgres.setMessage("Cek Login ...");
            mProgres.show();

            mAuth.signInWithEmailAndPassword(Username, Pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        mProgres.dismiss();
                        finish();
                        CheckUserExit();
                    } else {
                        mProgres.dismiss();
                        Toast.makeText(LoginActivity.this, "Username Dan Password Anda Tidak Sesuai", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }


    }

    private void CheckUserExit() {

        final String id_user = mAuth.getCurrentUser().getUid();
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(id_user)){

                    String token = FirebaseInstanceId.getInstance().getToken();
                    mDatabase.child(id_user).child("Token").setValue(token);

                    Intent LoginInten = new Intent(LoginActivity.this, HomeActivity.class);
                    LoginInten.putExtra("idDriver", id_user);
                    LoginInten.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(LoginInten);
                    finish();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);



    }



}
