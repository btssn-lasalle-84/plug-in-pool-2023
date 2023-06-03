/**
 * @file HistoriqueManche.java
 * @brief Déclaration de la classe définissant la fenêtre apparaissant lors de la selection d'une manche
 * @author Clément TRICHET
 */

package com.example.pluginpool;

import android.app.AlertDialog;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * @class HistoriqueManche
 * @brief La fenêtre s'affichant lors de la sélection lors de la selection d'une manche, sur laquelle figurent les informations relatives à cette dernière
 */
public class HistoriqueManche extends AlertDialog
{
    /**
     * Constantes
     */
    private static final String TAG = "_HistoriqueManche"; //!< TAG pour les logs

    /**
     * Attributs
     */
    private Historique activiteHistorique;
    private String[] joueurs;

    /**
     * Ressources GUI
     */
    private ImageButton boutonSuppression;
    private TextView[] nomsJoueurs;
    private TextView[][] billesEmpochees;

    /**
     * @brief Constructeur de la classe HistoriqueManche
     */
    public HistoriqueManche(Historique historique, String date)
    {
        super(historique);
        setContentView(R.layout.fenetre_historique_manche);
        activiteHistorique = historique;
        initialiserRessources(date);
    }

    /**
     * @brief Initialise les ressources graphiques de la classe HistoriqueManche
     */
    private void initialiserRessources(String date)
    {
        Log.d(TAG, "initialiserRessources( date =  " + date + " )");

        View fenetre = LayoutInflater.from(getContext()).inflate(R.layout.fenetre_historique_manche, null);
        boutonSuppression = (ImageButton) fenetre.findViewById(R.id.boutonSupprimer);
        billesEmpochees = new TextView[BlackBall.NB_JOUEURS][BlackBall.NB_COULEURS];
        billesEmpochees[Manche.PREMIER_JOUEUR][BlackBall.ROUGE] = (TextView) fenetre.findViewById(R.id.nbRougesJoueur1);
        billesEmpochees[Manche.SECOND_JOUEUR][BlackBall.ROUGE] = (TextView) fenetre.findViewById(R.id.nbRougesJoueur2);
        billesEmpochees[Manche.PREMIER_JOUEUR][BlackBall.JAUNE] = (TextView) fenetre.findViewById(R.id.nbJaunesJoueur1);
        billesEmpochees[Manche.SECOND_JOUEUR][BlackBall.JAUNE] = (TextView) fenetre.findViewById(R.id.nbJaunesJoueur2);
        billesEmpochees[Manche.PREMIER_JOUEUR][BlackBall.BLANCHE] = (TextView) fenetre.findViewById(R.id.nbBlanchesJoueur1);
        billesEmpochees[Manche.SECOND_JOUEUR][BlackBall.BLANCHE] = (TextView) fenetre.findViewById(R.id.nbBlanchesJoueur2);
        billesEmpochees[Manche.PREMIER_JOUEUR][BlackBall.NOIRE] = (TextView) fenetre.findViewById(R.id.nbNoiresJoueur1);
        billesEmpochees[Manche.SECOND_JOUEUR][BlackBall.NOIRE] = (TextView) fenetre.findViewById(R.id.nbNoiresJoueur2);
        joueurs = new String[BlackBall.NB_JOUEURS];
        joueurs[Manche.PREMIER_JOUEUR] = activiteHistorique.baseDonnees.getNomJoueur("gagnant", date);
        joueurs[Manche.SECOND_JOUEUR] = activiteHistorique.baseDonnees.getNomJoueur("perdant", date);
        nomsJoueurs = new TextView[BlackBall.NB_JOUEURS];
        nomsJoueurs[Manche.PREMIER_JOUEUR] = (TextView) fenetre.findViewById(R.id.joueur1);
        nomsJoueurs[Manche.PREMIER_JOUEUR].setText("Gagnant : " + joueurs[Manche.PREMIER_JOUEUR]);
        nomsJoueurs[Manche.SECOND_JOUEUR] = (TextView) fenetre.findViewById(R.id.joueur2);
        nomsJoueurs[Manche.SECOND_JOUEUR].setText("Perdant : " + joueurs[Manche.SECOND_JOUEUR]);
        boutonSuppression.setOnClickListener(new View.OnClickListener() {

             @Override
             public void onClick(View view) {
                 activiteHistorique.baseDonnees.supprimerManche(date);
                 HistoriqueManche.this.dismiss();
             }
         });
        setResultats();
    }

    /**
     * @brief Définit le contenu des Textview, affichant le nombre de billes de chaque couleur empochées par le joueur au cours de la manche
     */
    private void setResultats()
    {
        for(int joueur = 0; joueur < BlackBall.NB_JOUEURS; joueur++)
        {
            for(int couleur = BlackBall.ROUGE; couleur < BlackBall.NB_COULEURS; couleur++)
            {
                billesEmpochees[joueur][couleur].setText(BlackBall.NOMS_BILLES + String.valueOf( activiteHistorique.baseDonnees.getNbEmpoches(couleur, joueurs[joueur], BaseDeDonnees.DEFAUT)));
            }
        }
    }
}
