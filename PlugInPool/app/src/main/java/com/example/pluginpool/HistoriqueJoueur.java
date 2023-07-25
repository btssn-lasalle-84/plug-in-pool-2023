/**
 * @file HistoriqueJoueur.java
 * @brief Déclaration de la classe définissant la fenêtre apparaissant lors de la sélection d'un joueur
 * @author Clément TRICHET
 */

package com.example.pluginpool;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * @class HistoriqueJoueur
 * @brief La fenêtre s'affichant lors de la sélection lors de la selection d'un joueur, sur laquelle figurent les informations relatives à ce dernier
 */
public class HistoriqueJoueur extends AlertDialog
{
    /**
     * Constantes
     */
    private static final String TAG = "_HistoriqueJoueur"; //!< TAG pour les logs

    /**
     * Attributs
     */
    private Historique activiteHistorique;

    /**
     * Ressources GUI
     */
    private TextView nomJoueur;
    private TextView scoreElo;
    private TextView nbManches;
    private TextView nbVictoires;
    private ImageButton boutonSuppression;
    private View fenetre;

    /**
     * @brief Constructeur de la classe HistoriqueJoueur
     */
    public HistoriqueJoueur(Historique historique, String nom)
    {
        super(historique);
        setContentView(R.layout.fenetre_historique_joueur);
        activiteHistorique = historique;
        initialiserRessources(nom);
    }

    /**
     * @brief Initialise les ressources graphiques de la classe HistoriqueJoueur
     */
    private void initialiserRessources(String nom)
    {
        fenetre = LayoutInflater.from(getContext()).inflate(R.layout.fenetre_historique_joueur, null);
        nomJoueur = (TextView) fenetre.findViewById(R.id.nomJoueur);
        scoreElo = (TextView) fenetre.findViewById(R.id.scoreElo);
        nbManches = (TextView) fenetre.findViewById(R.id.nbManches);
        nbVictoires = (TextView) fenetre.findViewById(R.id.nbVictoires);
        boutonSuppression = (ImageButton) fenetre.findViewById(R.id.boutonSupprimer);

        nomJoueur.setText(nom);
        scoreElo.setText("Score : " + activiteHistorique.baseDonnees.getScoreElo(nom));
        nbManches.setText("Nombre de manches : " + activiteHistorique.baseDonnees.getNbManches(nom));
        nbVictoires.setText("Nombre de victoires : " + activiteHistorique.baseDonnees.getNbVictoires(nom));

        boutonSuppression.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                activiteHistorique.baseDonnees.supprimerJoueur(nom);
                activiteHistorique.actualiserNoms(nom);
                HistoriqueJoueur.this.dismiss();
            }
        });

        setView(fenetre);
    }
}
