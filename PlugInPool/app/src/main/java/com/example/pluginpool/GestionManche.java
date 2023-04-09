/**
 * @author Clément Trichet
 * @file GestionManche.java
 * @brief TODO
 */

package com.example.pluginpool;

import android.util.Log;

import com.example.pluginpool.BlackBall;
import com.example.pluginpool.Protocole;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @class GestionManche
 * @brief GestionManche définit l'état d'une manche et regroupe l'ensemble des méthodes déclenchées
 * par les trames issues de la table
 */
public class GestionManche
{
    /**
     * Constantes
     */
    private static final String TAG     = "_GestionManche"; //!< TAG pour les logs
    public static final int     NOIRE   = 0;                //!< Bille noire 0
    public static final int     ROUGE   = 1;                //!< Bille rouge 1
    public static final int     JAUNE   = 2;                //!< Bille jaune 2
    public static final int     BLANCHE = 3;                //!< Bille blanche 3
    public static final int     NB_BILLES_COULEUR =
      7; //!< Nombre de billes d'une même couleur (rouges ou jaunes) 7
    public static final int NB_POCHES = 6; //!< Nombre de poches de la table 6

    /**
     * Attributs
     */
    private String[] joueurs;                      //!< les joueurs
    private Map<String, Integer>  couleursJoueurs; //!< ...
    private Map<Integer, Integer> billes;
    private ArrayList<Integer>[] poches;
    private ArrayList<ArrayList<Integer>> manche;
    private int                           joueurActif;
    private Boolean                       couleursDefinies;
    private Boolean                       mancheDemarree;

    public GestionManche(String joueur1, String joueur2, int premierJoueur)
    {
        Log.d(TAG, "GestionManche(" + joueur1 + ", " + joueur2 + ", " + premierJoueur + ")");
        joueurs         = new String[2];
        joueurs[0]      = joueur1;
        joueurs[1]      = joueur2;
        couleursJoueurs = new HashMap<>();

        billes = new HashMap<>();
        billes.put(BlackBall.ROUGE, BlackBall.NB_BILLES_COULEUR);
        billes.put(BlackBall.JAUNE, BlackBall.NB_BILLES_COULEUR);

        poches = new ArrayList[BlackBall.NB_POCHES];
        for(int poche = 0; poche < BlackBall.NB_POCHES; poche++)
        {
            //!< @fixme tableau à deux dimensions ?
            // this.poches.add(new ArrayList());
        }

        manche = new ArrayList<ArrayList<Integer>>();
        manche.add(new ArrayList<Integer>());

        joueurActif      = premierJoueur;
        couleursDefinies = false;
        mancheDemarree   = false;

        // Test
        /*
        couleursJoueurs.put(joueurs[0], BlackBall.ROUGE);
        couleursJoueurs.put(joueurs[1], BlackBall.JAUNE);
        couleursDefinies = true;
        mancheDemarree   = true;
        */
    }

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
                if(couleursDefinies && billes.get(couleursJoueurs.get(joueurs[joueurActif])) == 0)
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
                    couleursJoueurs.put(joueurs[(joueurActif + 1) % 2], (trame % 4) * 2 % 3);
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
            manche.add(new ArrayList<Integer>());
            joueurActif = (joueurActif + 1) % 2;
            //!< @todo redémarrer compteur
        }
    }
}
