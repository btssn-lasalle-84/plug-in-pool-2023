/**
 * @file FinDeManche.java
 * @brief Déclaration de la classe définissant la fenêtre apparaissant à la fin d'une manche
 * @author Clément TRICHET
 */

package com.example.pluginpool;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.pluginpool.EcranPrincipal;

/**
 * @class FinDeManche
 * @brief La fenêtre s'affichant au terme d'une manche
 */
public class FinDeManche extends AlertDialog {
    private Button boutonMenu, boutonRejouer;
    private TextView resultats;
    private Manche activiteManche;

    /**
     * @brief Constructeur de la classe FinDeManche
     */
    protected FinDeManche(Manche manche) {
        super(manche);
        activiteManche = manche;
        initialiserRessources();
    }

    /**
     * @brief Initialise les ressources graphiques de la classe FinDeManche
     */
    private void initialiserRessources()
    {
        View fenetre = LayoutInflater.from(getContext()).inflate(R.layout.fenetre_fin_de_manche, null);
        boutonMenu = fenetre.findViewById(R.id.boutonMenu);
        boutonRejouer = fenetre.findViewById(R.id.boutonRejouer);
        resultats = fenetre.findViewById(R.id.resultats);
        setView(fenetre);

        boutonMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activiteManche.communication.seDeconnecter();
                Intent intent = new Intent(getContext(), EcranPrincipal.class);
                getContext().startActivity(intent);
            }
        });
        boutonRejouer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activiteManche.recommencer();
                FinDeManche.this.dismiss();
            }
        });
    }
}

