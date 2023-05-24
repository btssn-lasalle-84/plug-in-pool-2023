package com.example.pluginpool;

/**
 * @class ProtocoleEcran
 * @brief Définit le protocole Ecran-Pool
 */
public class ProtocoleEcran {

    public static char DELIMITEUR_DEBUT = "$".charAt(0);
    public static char DELIMITEUR_CHAMPS = ";".charAt(0);
    public static char DELIMITEUR_FIN = "\n".charAt(0);

    public static String TABLES = "0123";

//        ${Type};{Table};{Poche};{Couleur}\n

    public static char TYPE_EMPOCHE = "E".charAt(0);
    public static String POCHES = "012345";
    public static String COULEURS = "0123";

//        ${Type};{Table};{Joueur1};{Joueur2}\n

    public static char TYPE_NOM = "N".charAt(0);

//        ${Type};{Table};{ChangementJoueur}\n

    public static char TYPE_CHANGEMENT_JOUEUR = "C".charAt(0);
    public static String JOUEURS = "01";

}
