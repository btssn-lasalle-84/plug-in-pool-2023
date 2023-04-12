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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

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
    public static final int PREMIER_JOUEUR  = 0; //!< @def Numéro ou indice associé au premier joueur
    public static final int SECOND_JOUEUR   = 1; //!< @def Numéro ou indice associé au premier joueur

    /**
     * Attributs
     */
    private String[]                joueurs;            //!< Attribut contenant le nom des joueurs
    private Map<String, Integer>    couleursJoueurs;    //!< Table ayant pour clef le nom d'un joueur et pour valeur la couleur des billes de son groupe
    private Integer[]               billes;             //!< Table ayant pour clef la couleur d'un groupe de billes et pour valeur, le nombre de billes de ce groupe encore en jeu
    private Integer[][]             poches;             //!< Liste des poches, sous la forme de deux-listes dont les valeurs correspondent au nombre de billes empochées ayant pour couleur l'indice de ces valeurs
    private Vector<Vector<Integer>> manche;             //!< Liste des tours de la manche, chaque tour étant représenté par une liste de billes empochées au cours de ce dernier
    private int                     joueurActif;        //!< Booléen indiquant le joueur dont le tour est en cours
    private Boolean                 couleursDefinies;   //!< Booléen indiquant si la couleur du groupe des billes attribué aux joueurs est définie ou non
    private Boolean                 mancheDemarree;     //!< Booléen indiquant si la manche a ou non démarré
    private Compteur                compteur;           //!< Compteur indiquant le temps restant du coup en cours

    /**
     * Ressources GUI
     */
    private TextView nomJoueur1;                  //!< Affichage du nom du premier joueur
    private TextView nomJoueur2;                  //!< Affichage du nom du second joueur
    private TextView[][] nbBillesEmpochees;       //!< Affichage du nombre de billes de chaque couleur empochées dans chacune des poches
    private ImageView[][] fondBillesEmpochees;    //!< Images de fond du nombre de billes de chaque couleur empochées dans chacune des poches

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
        joueurs         = new String[BlackBall.NB_JOUEURS];
        joueurs[PREMIER_JOUEUR] = activiteManche.getStringExtra("joueur1");
        joueurs[SECOND_JOUEUR]  = activiteManche.getStringExtra("joueur2");
        Log.d(TAG, "onCreate() " + joueurs[PREMIER_JOUEUR] + " vs " + joueurs[SECOND_JOUEUR]);
        couleursJoueurs = new HashMap<>();
        billes = new Integer[2];
        billes[BlackBall.ROUGE] = BlackBall.NB_BILLES_COULEUR;
        billes[BlackBall.JAUNE] = BlackBall.NB_BILLES_COULEUR;

        poches = new Integer[BlackBall.NB_POCHES][BlackBall.NB_JOUEURS];
        for(int poche = 0; poche < BlackBall.NB_POCHES; poche++)
        {
            //!< @fixme tableau à deux dimensions ?
            // this.poches.add(new Vector<>());
        }

        manche = new Vector<Vector<Integer>>();
        manche.add(new Vector<Integer>());

        joueurActif      = PREMIER_JOUEUR;
        couleursDefinies = false;
        mancheDemarree   = false;
        compteur         = new Compteur();
    }

    private void initialiserRessources()
    {
        nomJoueur1 = (TextView)findViewById(R.id.Joueur1);
        nomJoueur2 = (TextView)findViewById(R.id.Joueur2);

        nomJoueur1.setText(joueurs[PREMIER_JOUEUR]);
        nomJoueur2.setText(joueurs[SECOND_JOUEUR]);

        nbBillesEmpochees[BlackBall.POCHE_HAUT_GAUCHE][BlackBall.JAUNE]     = ((TextView) findViewById(R.id.poche0BilleJauneNombre));
        nbBillesEmpochees[BlackBall.POCHE_HAUT_GAUCHE][BlackBall.ROUGE]     = ((TextView) findViewById(R.id.poche0BilleRougeNombre));
        nbBillesEmpochees[BlackBall.POCHE_HAUT_DROIT][BlackBall.JAUNE]      = ((TextView) findViewById(R.id.poche1BilleJauneNombre));
        nbBillesEmpochees[BlackBall.POCHE_HAUT_DROIT][BlackBall.ROUGE]      = ((TextView) findViewById(R.id.poche1BilleRougeNombre));
        nbBillesEmpochees[BlackBall.POCHE_MILIEU_DROIT][BlackBall.JAUNE]    = ((TextView) findViewById(R.id.poche2BilleJauneNombre));
        nbBillesEmpochees[BlackBall.POCHE_MILIEU_DROIT][BlackBall.ROUGE]    = ((TextView) findViewById(R.id.poche2BilleRougeNombre));
        nbBillesEmpochees[BlackBall.POCHE_BAS_DROIT][BlackBall.JAUNE]       = ((TextView) findViewById(R.id.poche3BilleJauneNombre));
        nbBillesEmpochees[BlackBall.POCHE_BAS_DROIT][BlackBall.ROUGE]       = ((TextView) findViewById(R.id.poche3BilleRougeNombre));
        nbBillesEmpochees[BlackBall.POCHE_BAS_GAUCHE][BlackBall.JAUNE]      = ((TextView) findViewById(R.id.poche4BilleJauneNombre));
        nbBillesEmpochees[BlackBall.POCHE_BAS_GAUCHE][BlackBall.ROUGE]      = ((TextView) findViewById(R.id.poche4BilleRougeNombre));
        nbBillesEmpochees[BlackBall.POCHE_MILIEU_GAUCHE][BlackBall.JAUNE]   = ((TextView) findViewById(R.id.poche5BilleJauneNombre));
        nbBillesEmpochees[BlackBall.POCHE_MILIEU_GAUCHE][BlackBall.ROUGE]   = ((TextView) findViewById(R.id.poche5BilleRougeNombre));

        fondBillesEmpochees[BlackBall.POCHE_HAUT_GAUCHE][BlackBall.JAUNE]   = ((ImageView) findViewById(R.id.poche0BilleJauneView));
        fondBillesEmpochees[BlackBall.POCHE_HAUT_GAUCHE][BlackBall.ROUGE]   = ((ImageView) findViewById(R.id.poche0BilleRougeView));
        fondBillesEmpochees[BlackBall.POCHE_HAUT_DROIT][BlackBall.JAUNE]      = ((ImageView) findViewById(R.id.poche1BilleJauneView));
        fondBillesEmpochees[BlackBall.POCHE_HAUT_DROIT][BlackBall.ROUGE]      = ((ImageView) findViewById(R.id.poche1BilleRougeView));
        fondBillesEmpochees[BlackBall.POCHE_MILIEU_DROIT][BlackBall.JAUNE]    = ((ImageView) findViewById(R.id.poche2BilleJauneView));
        fondBillesEmpochees[BlackBall.POCHE_MILIEU_DROIT][BlackBall.ROUGE]    = ((ImageView) findViewById(R.id.poche2BilleRougeView));
        fondBillesEmpochees[BlackBall.POCHE_BAS_DROIT][BlackBall.JAUNE]       = ((ImageView) findViewById(R.id.poche3BilleJauneView));
        fondBillesEmpochees[BlackBall.POCHE_BAS_DROIT][BlackBall.ROUGE]       = ((ImageView) findViewById(R.id.poche3BilleRougeView));
        fondBillesEmpochees[BlackBall.POCHE_BAS_GAUCHE][BlackBall.JAUNE]      = ((ImageView) findViewById(R.id.poche4BilleJauneView));
        fondBillesEmpochees[BlackBall.POCHE_BAS_GAUCHE][BlackBall.ROUGE]      = ((ImageView) findViewById(R.id.poche4BilleRougeView));
        fondBillesEmpochees[BlackBall.POCHE_MILIEU_GAUCHE][BlackBall.JAUNE]   = ((ImageView) findViewById(R.id.poche5BilleJauneView));
        fondBillesEmpochees[BlackBall.POCHE_MILIEU_GAUCHE][BlackBall.ROUGE]   = ((ImageView) findViewById(R.id.poche5BilleRougeView));
    }

    /**
     * @brief TODO
     */
    private void empocherBille(int numero, int couleur)
    {
        poches[numero][couleur]++;
        nbBillesEmpochees[numero][couleur].setText(poches[numero][couleur]);
        if(poches[numero][couleur] == 1)
        {
            fondBillesEmpochees[numero][couleur].setVisibility(View.VISIBLE);
            nbBillesEmpochees[numero][couleur].setVisibility(View.VISIBLE);
        }
        billes[couleur]--;
        //!< @todo rendre invisible
        manche.get(manche.size() - 1).add(couleur);
    }

    /**
     * @brief Méthode gérant les actions à effectuer suite à la réception d'une trame d'empochage
     */
    public void traiterTrame(int trame)
    {
        Log.d(TAG, "traiterTrame() trame = " + trame);
        /**
         * @fixme Il faut remanier ce code de plus il contient des erreurs
         */
        if(trame / Protocole.CHAMP_TYPE == Protocole.EMPOCHAGE)
        {
            if(trame % 4 == 0)
            {
                //!< @fixme joueurs est un tableau de String et la Map
                if(couleursDefinies && billes[couleursJoueurs.get(joueurs[joueurActif])] == 0)
                {
                    //!< @todo JoueurActif a gagné
                }
                else
                {
                    //!< @todo joueurActif a perdu
                }
            }
            else
            {
                manche.get(manche.size() - 1).add(trame % 4);
                if(!couleursDefinies && trame % 3 != 0)
                    ;
                {
                    couleursDefinies = true;
                    couleursJoueurs.put(joueurs[joueurActif], trame % 4);
                    couleursJoueurs.put(joueurs[(joueurActif + 1) % BlackBall.NB_JOUEURS], (trame % 4) * 2 % 3);
                }
            }
        }
        else if(!mancheDemarree)
        {
            mancheDemarree = true;
            //!< @todo redémarrer compteur
        }
        else
        {
            manche.add(new Vector<Integer>());
            joueurActif = (joueurActif + 1) % 2;
            //!< @todo redémarrer compteur
        }
    }
}