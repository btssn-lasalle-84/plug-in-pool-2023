/**
 * @author Clément Trichet
 * @file Manche.java
 * @brief TODO
 */

package com.example.pluginpool;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

/**
 * @class Manche
 * @brief L'activité de suivi de manche
 */
public class Manche extends AppCompatActivity
{
    /**
     * Constantes
     */
    private static final String TAG = "_Manche"; //!< TAG pour les logs (cf. Logcat)

    /**
     * Attributs
     */
    private String[] joueurs;           //!< les joueurs
    private GestionManche gestionManche = null; //!< pour gérer la manche

    /**
     * Ressources GUI
     */
    private TextView nomJoueur1;
    private TextView nomJoueur2;

    /**
     * @brief Méthode appelée à la création de l'activité
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manche);

        initialiserAttributs();
        initialiserRessources();
    }

    private void initialiserAttributs()
    {
        Intent activiteManche = getIntent();
        joueurs[0]            = activiteManche.getStringExtra("joueur1");
        joueurs[1]            = activiteManche.getStringExtra("joueur2");
        Log.d(TAG, "onCreate() " + joueurs[0] + " vs " + joueurs[1]);

        gestionManche = new GestionManche(joueurs[0], joueurs[1], 0);
    }

    private void initialiserRessources()
    {
        nomJoueur1 = (TextView)findViewById(R.id.Joueur1);
        nomJoueur2 = (TextView)findViewById(R.id.Joueur2);

        nomJoueur1.setText(joueurs[0]);
        nomJoueur2.setText(joueurs[1]);
    }
}