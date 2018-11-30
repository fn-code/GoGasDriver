package gaskudriver.funcode.funcode.com.gogasdriver;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import gaskudriver.funcode.funcode.com.gogasdriver.Fragment.HistoryFragment;
import gaskudriver.funcode.funcode.com.gogasdriver.Fragment.PesananFragment;
import gaskudriver.funcode.funcode.com.gogasdriver.Fragment.ProfilFragment;

public class HomeActivity extends AppCompatActivity {


    FirebaseAuth mAuthDriver;
    String idUsers;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:

                    getSupportActionBar().setTitle(R.string.title_home);
                    Fragment homeFrag = new PesananFragment();
                    FragmentTransaction transac = getSupportFragmentManager().beginTransaction();
                    transac.replace(R.id.content, homeFrag);
                    transac.commit();

                    return true;
                case R.id.navigation_dashboard:

                    getSupportActionBar().setTitle(R.string.title_dashboard);
                    Fragment historyFrag = new HistoryFragment();
                    FragmentTransaction transacHistory = getSupportFragmentManager().beginTransaction();
                    transacHistory.replace(R.id.content, historyFrag);
                    transacHistory.commit();

                    return true;
                case R.id.navigation_notifications:
                    getSupportActionBar().setTitle(R.string.title_notifications);

                    Fragment settingFrag = new ProfilFragment();
                    FragmentTransaction transacSet = getSupportFragmentManager().beginTransaction();
                    transacSet.replace(R.id.content, settingFrag);
                    transacSet.commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getSupportActionBar().setTitle(R.string.title_home);

        mAuthDriver = FirebaseAuth.getInstance();


        Fragment homeFrag = new PesananFragment();
        FragmentTransaction transac = getSupportFragmentManager().beginTransaction();
        transac.replace(R.id.content, homeFrag);
        transac.commit();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        /*idUsers = mAuthDriver.getCurrentUser().getUid();
        Toast.makeText(this, idUsers, Toast.LENGTH_SHORT).show();*/
    }

}
