/**
 * @author Clément Trichet
 * @file Manche.java
 * @brief TODO
 */

package com.example.pluginpool;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

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

    /**
     * Ressources GUI
     */

    /**
     * @brief Méthode appelée à la création de l'activité
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manche);
    }
}