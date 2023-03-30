/**
 * @author Clément Trichet
 * @file GestionManche.java
 * @brief TODO
 */
package com.example.mobilepool;

import java.util.ArrayList;
import java.util.Map;

final int NOIRE = 0;                // !< @def Bille noire 0
final int ROUGE = 1;                // !< @def Bille rouge 1
final int JAUNE = 2;                // !< @def Bille jaune 2
final int BLANCHE = 3;              // !< @def Bille blanche 3
final int NB_BILLES_COULEUR = 7;    // !< @def Nombre de billes d'une même couleur (rouges ou jaunes) 7
final int NB_POCHES = 6;            // !< @def Nombre de poches de la table 6

/**
 * @class GestionManche
 * @brief GestionManche définit l'état d'une manche et regroupe l'ensemble des méthodes déclenchées par les trames issues de la table
 */
public class GestionManche {

    private String [] joueurs;
    private Map<String, Integer> couleursJoueurs;
    private Map<Integer, Integer> billes;
    private ArrayList<Integer>[] poches;
    private ArrayList<ArrayList<Integer>> manche;
    private int joueurActif;
    private Boolean couleursDefinies;
    private Boolean mancheDemarree;
    private Compteur compteur;

    public GestionManche(String joueur1, String joueur2, int premierJoueur)
    {
        joueurs = new String[2];
        joueurs[0] = joueur1;
        joueurs[1] = joueur2;
        couleursJoueurs= new Map<String, Integer>();
        billes = new Map<Integer, Integer>();
        billes.put(ROUGE, NB_BILLES_COULEUR);
        billes.put(JAUNE, NB_BILLES_COULEUR);
        poches = new ArrayList[6];
        manche = new ArrayList<ArrayList<Integer>>();
        manche.add(new ArrayList<Integer>());
        for (int poche = 0; poche < NB_POCHES; poche++) {
            this.poches.add(new ArrayList());
        }
        joueurActif = 1;
        couleursDefinies = false;
        mancheDemarree = false;
        compteur = new Compteur();
        // !< @todo affichage activitée
    }

    public void interpreterTrame(int trame)
    {
        if(trame / 128 == 0)
        {
            if(trame % 4 == 0)
            {
                if (couleursDefinies && billes[couleursJoueurs[joueurs[joueurActif]]] == 0)
                {
                    // !< @todo JoueurActif a gagné
                }
                else
                {
                    // !< @todo joueurActif a perdu
                }
            }
            else
            {
                manche.get(manche.size() - 1).add(trame % 4);
                if(!couleursDefinies && trame % 3 != 0);
                {
                    couleursDefinies = true;
                    couleursJoueurs[joueurs[joueurActif]] = trame % 4;
                    couleursJoueurs[joueurs[(joueurActif + 1) % 2]] = (trame % 4) * 2 % 3;
                }
            }
        }
        else if(! mancheDemarree)
        {
            mancheDemarree = true;
            compteur.restart();
        }
        else
        {
            manche.add(new ArrayList<Integer>());
            joueurActif = (joueurActif + 1) % 2;
            compteur.restart();
        }
    }
}