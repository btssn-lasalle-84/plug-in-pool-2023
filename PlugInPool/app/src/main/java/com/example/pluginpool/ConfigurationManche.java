/**
 * @file ConfigurationManche.java
 * @brief Déclaration de l'activité de configuration d'une manche
 * @author Clément TRICHET
 */

package com.example.pluginpool;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Spinner;

/**
 * @class ConfigurationManche
 * @brief L'activité de configuration d'une manche
 */
public class ConfigurationManche extends AppCompatActivity
{
    /**
     * Constantes
     */
    private static final String TAG = "_ConfigurationManche"; //!< TAG pour les logs

    /**
     * Attributs
     */

    /**
     * Ressources GUI
     */

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration_manche);
        Log.d(TAG, "onCreate()");

        initialiserRessources();
    }

    private void initialiserRessources()
    {
        /**
         * @brief Initialise les ressources graphiques de l'activité
         */
    }
}