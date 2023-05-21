package com.example.pluginpool;

/**
 * @class BlackBall
 * @brief Définit des caractéristiques du billard Blackball
 */
public class BlackBall
{
    public static final int ROUGE   = 0;                //!< Bille rouge
    public static final int JAUNE   = 1;                //!< Bille jaune
    public static final int BLANCHE = 2;                //!< Bille blanche
    public static final int NOIRE   = 3;                //!< Bille noire
    public static final int NB_COULEURS = 4;            //!< Nombre de couleurs de bille différentes
    public static final int NB_GROUPES_BILLES = 2;      //!< Nombre de groupes de billes de couleur
    public static final int NB_BILLES_COULEUR = 7;      // !< Nombre de billes d'une même couleur (rouges ou jaunes) 7
    public static final int NB_POCHES = 6;              //!< Nombre de poches de la table 6
    public static final int NB_JOUEURS = 2;             //!< Nombre de joueurs au blackball
    public static final int POCHE_HAUT_GAUCHE = 0;      //!< Numero Poche en haut a gauche
    public static final int POCHE_HAUT_DROIT = 1;       //!< Numero Poche en haut a droite
    public static final int POCHE_MILIEU_DROIT = 2;     //!< Numero Poche au milieu a droite
    public static final int POCHE_BAS_DROIT = 3;        //!< Numero Poche en bas a droite
    public static final int POCHE_BAS_GAUCHE = 4;       //!< Numero Poche en bas a gauche
    public static final int POCHE_MILIEU_GAUCHE = 5;    //!< Numero Poche au milieu a gauche
    public static final int[] IMAGES_BILLES = {R.drawable.bille_rouge, R.drawable.bille_jaune}; //!< Id des images des billes rouges et jaunes en ressources
    public static final String[] NOMS_BILLES = {"Billes rouges : ", "Billes jaunes : ", "Bille blanche : ", "Bille noire : "}; //!<@todo
}
