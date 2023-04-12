package com.example.pluginpool;

/**
 * @class Protocole
 * @brief Définit le protocole PlugInPool
 */
public class Protocole
{
    // Trame Table -> MobilePool
    public static final int EMPOCHAGE   = 0; // !< Une trame empochage Table -> MobilePool
    /*
        Format : {Type}{Table}{Poche}{Couleur}
        | 7  | 6  | 5  | 4  | 3  | 2  | 1  | 0  |
        | 0  | X  | X  | X  | X  | X  | X  | X  |
        |Type|N°Table  |N°Poche       |Couleur  |
    */
    public static final int CHAMP_TYPE   = 128; // !< Bit n°7 dans la trame Table -> MobilePool
    public static final int CHAMP_TABLE  = 5; // !< Bit n°5
    public static final int CHAMP_POCHE  = 2; // !< Bit n°2
    public static final int CHAMP_COULEUR = 0; // !< Bit n°0
}
