/**
 * @file FinDeManche.java
 * @brief Déclaration de la classe définissant la fenêtre apparaissant au terme d'une manche
 * @author Clément TRICHET
 */

package com.example.pluginpool;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;


/**
 * @class FinDeManche
 * @brief La fenêtre s'affichant au terme d'une manche
 */
public class FinDeManche extends AlertDialog
{
    /**
     * Constantes
     */
    private static final String TAG = "_FinDeManche"; //!< TAG pour les logs

    /**
     * Attributs
     */
    private Manche activiteManche;          //!< @todo

    /**
     * Ressources GUI
     */
    private ImageButton boutonMenu;         //!< @todo
    private ImageButton boutonRejouer;      //!< @todo
    private TextView entete;                //!< @todo
    private TextView[][] billesEmpochees;   //!< @todo
    private TextView[] joueurs;             //!< @todo
    private View fenetre;                   //!< @todo

    /**
     * @brief Constructeur de la classe FinDeManche
     */
    protected FinDeManche(Manche manche, String joueur1, String joueur2) {
        super(manche);
        activiteManche = manche;
        initialiserRessources(joueur1, joueur2);
    }

    /**
     * @brief Initialise les ressources graphiques de la classe FinDeManche
     */
    private void initialiserRessources(String joueur1, String joueur2)
    {
        Log.d(TAG, "initialiserRessources(joueur1 =  " + joueur1 + ", joueur2 = " + joueur2 + " )");

        fenetre = LayoutInflater.from(getContext()).inflate(R.layout.fenetre_fin_de_manche, null);

        boutonMenu = (ImageButton) fenetre.findViewById(R.id.boutonMenu);
        boutonRejouer =  (ImageButton) fenetre.findViewById(R.id.boutonRejouer);

        entete = (TextView) fenetre.findViewById(R.id.entete);

        billesEmpochees = new TextView[BlackBall.NB_JOUEURS][BlackBall.NB_COULEURS];
        billesEmpochees[Manche.PREMIER_JOUEUR][BlackBall.ROUGE] = (TextView) fenetre.findViewById(R.id.nbRougesJoueur1);
        billesEmpochees[Manche.SECOND_JOUEUR][BlackBall.ROUGE] = (TextView) fenetre.findViewById(R.id.nbRougesJoueur2);
        billesEmpochees[Manche.PREMIER_JOUEUR][BlackBall.JAUNE] = (TextView) fenetre.findViewById(R.id.nbJaunesJoueur1);
        billesEmpochees[Manche.SECOND_JOUEUR][BlackBall.JAUNE] = (TextView) fenetre.findViewById(R.id.nbJaunesJoueur2);
        billesEmpochees[Manche.PREMIER_JOUEUR][BlackBall.BLANCHE] = (TextView) fenetre.findViewById(R.id.nbBlanchesJoueur1);
        billesEmpochees[Manche.SECOND_JOUEUR][BlackBall.BLANCHE] = (TextView) fenetre.findViewById(R.id.nbBlanchesJoueur2);
        billesEmpochees[Manche.PREMIER_JOUEUR][BlackBall.NOIRE] = (TextView) fenetre.findViewById(R.id.nbNoiresJoueur1);
        billesEmpochees[Manche.SECOND_JOUEUR][BlackBall.NOIRE] = (TextView) fenetre.findViewById(R.id.nbNoiresJoueur2);
        joueurs = new TextView[BlackBall.NB_JOUEURS];
        joueurs[Manche.PREMIER_JOUEUR] = (TextView) fenetre.findViewById(R.id.joueur1);
        joueurs[Manche.PREMIER_JOUEUR].setText(joueur1);
        joueurs[Manche.SECOND_JOUEUR] = (TextView) fenetre.findViewById(R.id.joueur2);
        joueurs[Manche.SECOND_JOUEUR].setText(joueur2);
        setResultats();

        fenetre.setMinimumWidth(200); //!< @todo CONSTANTES
        fenetre.setMinimumHeight(300);

        setView(fenetre);

        boutonMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activiteManche.communications[Communication.TABLE].seDeconnecter();
                Communication.supprimerInstance();
                activiteManche.setResult(Activity.RESULT_OK, new Intent());
                FinDeManche.this.dismiss();
                activiteManche.finish();
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

    /**
     * @brief Définit le contenu des Textview, affichant le nombre de billes de chaque couleur empochées par le joueur au cours de la manche
     */
    private void setResultats()
    {
        for(int joueur = 0; joueur < BlackBall.NB_JOUEURS; joueur++)
        {
            for(int couleur = BlackBall.ROUGE; couleur < BlackBall.NB_COULEURS; couleur++)
            {
                billesEmpochees[joueur][couleur].setText(BlackBall.NOMS_BILLES[couleur] + String.valueOf( activiteManche.baseDonnees.getNbEmpoches(couleur, (String)joueurs[joueur].getText(), BaseDeDonnees.DEFAUT)));
            }
        }
    }

    public void setEntete(String joueur)
    {
        entete.setText(joueur + " remporte la manche !");
    }
}

