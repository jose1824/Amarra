package com.cookiecatguzman.amarra;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.cookiecatguzman.amarra.fragments.InicioFragment;
import com.cookiecatguzman.amarra.fragments.MapaFragment;
import com.cookiecatguzman.amarra.fragments.PerfilFragment;
import com.cookiecatguzman.amarra.fragments.UnidadesFragment;
import com.cookiecatguzman.amarra.fragments.dummy.UnidadesContent;

public class PantallaInicialActivity extends AppCompatActivity implements UnidadesFragment.OnListFragmentInteractionListener {

    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            FragmentManager manager = getSupportFragmentManager();

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    manager.beginTransaction().replace(R.id.content, new InicioFragment()).commit();
                    return true;
                case R.id.navigation_mapa:
                    manager.beginTransaction().replace(R.id.content, new MapaFragment()).commit();
                    return true;
                case R.id.navigation_unidades:
                    manager.beginTransaction().replace(R.id.content, new UnidadesFragment()).commit();
                    return true;
                case R.id.navigation_perfil:
                    manager.beginTransaction().replace(R.id.content, new PerfilFragment()).commit();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_inicial);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.content, new InicioFragment()).commit();
    }

    @Override
    public void onListFragmentInteraction(UnidadesContent.DummyItem item) {

    }
}
