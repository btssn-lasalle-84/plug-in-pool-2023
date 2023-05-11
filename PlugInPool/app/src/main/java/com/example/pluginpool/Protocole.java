package com.example.pluginpool;

/**
 * @class Protocole
 * @brief Définit le protocole PlugInPool
 */
public class Protocole
{
    // Trame MobilePool -> Table
    public static final int DEBUT = 0x9c;
    public static final int ARRET  = 0x80;
    // Trame Table -> MobilePool
    public static final int EMPOCHAGE = 0; // !< Une trame empochage Table -> MobilePool
    /*
        Format : {Type}{Table}{Poche}{Couleur}
        | 7  | 6  | 5  | 4  | 3  | 2  | 1  | 0  |
        | 0  | X  | X  | X  | X  | X  | X  | X  |
        |Type|N°Table  |N°Poche       |Couleur  |
    */
    public static final char MASQUE_TYPE   = 0b10000000;; // !< Masque du bit représentant le type de message
    public static final char MASQUE_POCHE   = 0b00001100;   // !< Masque des bits représentants le numéro de la poche
    public static final char MASQUE_COULEUR = 0b00000011;   // !< Masque des bits représentants la couleur
    public static final int CHAMP_POCHE   = 2;              // !< Bit n°2
}
