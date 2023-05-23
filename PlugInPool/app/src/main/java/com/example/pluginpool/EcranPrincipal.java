/**
 * @file EcranPrincipal.java
 * @brief Déclaration de l'activité principale
 * @author Clément TRICHET
 */

package com.example.pluginpool;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * @class EcranPrincipal
 * @brief L'activité principale
 */
public class EcranPrincipal extends AppCompatActivity
{
    /**
     * Constantes
     */
    private static final String TAG = "_EcranPrincipal"; //!< TAG pour les logs (cf. Logcat)

    /**
     * Ressources GUI
     */
    private Button boutonStatistiques; //!< Le bouton permettant d'accèder aux statistiques des parties
    private Button boutonJouer;        //!< Le bouton permettant de jouer une partie

    /**
     * @brief Méthode appelée à la création de l'activité
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ecran_principal);
        Log.d(TAG, "onCreate()");

        initialiserRessources();
    }

    /**
     * @brief Méthode appelée au démarrage après le onCreate() ou un restart
     * après un onStop()
     */
    @Override
    protected void onStart()
    {
        super.onStart();
        Log.d(TAG, "onStart()");
    }

    /**
     * @brief Méthode appelée après onStart() ou après onPause()
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        Log.d(TAG, "onResume()");
    }

    /**
     * @brief Méthode appelée après qu'une boîte de dialogue s'est affichée (on
     * reprend sur un onResume()) ou avant onStop() (activité plus visible)
     */
    @Override
    protected void onPause()
    {
        super.onPause();
        Log.d(TAG, "onPause()");
    }

    /**
     * @brief Méthode appelée lorsque l'activité n'est plus visible
     */
    @Override
    protected void onStop()
    {
        super.onStop();
        Log.d(TAG, "onStop()");
    }

    /**
     * @brief Méthode appelée à la destruction de l'application (après onStop()
     * et détruite par le système Android)
     */
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
    }

    /**
     * @brief Initialise les ressources graphiques de l'activité
     */
    private void initialiserRessources()
    {
        boutonStatistiques = (Button)findViewById(R.id.boutonHistorique);
        boutonJouer        = (Button)findViewById(R.id.boutonJouer);

        boutonStatistiques.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Log.d(TAG, "clic boutonStatistiques");
                Intent activiteHistorique = new Intent(EcranPrincipal.this, Historique.class);
                startActivity(activiteHistorique);
            }
        });
        boutonJouer.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Log.d(TAG, "clic boutonJouer");
                Intent activiteConfigurationManche = new Intent(EcranPrincipal.this, ConfigurationManche.class);
                startActivity(activiteConfigurationManche);
            }
        });
    }
}
