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
    public static final byte MASQUE_TYPE   = (byte)0b10000000;; // !< Masque du bit représentant le type de message
    public static final byte MASQUE_POCHE   = (byte)0b00001100;   // !< Masque des bits représentants le numéro de la poche
    public static final byte MASQUE_COULEUR = (byte)0b00000011;   // !< Masque des bits représentants la couleur
    public static final int CHAMP_POCHE   = 2;              // !< Bit n°2

    public static String byteToBinaryString(byte trame) {
        StringBuilder bitString = new StringBuilder();
        for (int indexBit = 7; indexBit >= 0; indexBit--) {
            bitString.append((trame & (1 << indexBit)) == 0 ? '0' : '1');
        }
        return bitString.toString();
    }
}
