package com.example.pluginpool;

/**
 * @class BlackBall
 * @brief Définit des caractéristiques du billard Blackball
 */
public class BlackBall
{
    public static final int NOIRE   = 0; //!< @def Bille noire 0
    public static final int ROUGE   = 1; //!< @def Bille rouge 1
    public static final int JAUNE   = 2; //!< @def Bille jaune 2
    public static final int BLANCHE = 3; //!< @def Bille blanche 3
    public static final int NB_BILLES_COULEUR =
      7; // !< @def Nombre de billes d'une même couleur (rouges ou jaunes) 7
    public static final int NB_POCHES = 6; //!< @def Nombre de poches de la table 6
    public static final int NB_JOUEURS = 2; //!< @def Nombre de joueurs au blackball
    public static final int POCHE_HAUT_GAUCHE = 0;
    public static final int POCHE_HAUT_DROIT = 1;
    public static final int POCHE_MILIEU_DROIT = 2;
    public static final int POCHE_BAS_DROIT = 3;
    public static final int POCHE_BAS_GAUCHE = 4;
    public static final int POCHE_MILIEU_GAUCHE = 5; //
}
