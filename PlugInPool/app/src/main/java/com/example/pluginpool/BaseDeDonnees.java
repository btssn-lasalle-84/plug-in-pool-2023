/**
 * @author Clément Trichet
 * @file BaseDeDonnees.java
 * @brief TODO
 */

package com.example.pluginpool;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Vector;

/**
 * @class BaseDeDonnees
 * @brief La classe s'occupant de la gestion de la base de données
 */
public class BaseDeDonnees extends SQLiteOpenHelper
{
    /**
     * Constantes
     */
    private static final String  TAG = "_BaseDeDonnees"; //!< TAG pour les logs (cf. Logcat)
    private static final String  POOL_DONNEES         = "PoolDonnees.db";
    private static final int     VERSION_POOL_DONNEES = 1;     //!< Version
    private static final int     INDEX_TABLE_VIDE = - 1; //!< Index d'une table ne contenant aucun element

    /**
     * Attributs
     */
    private static BaseDeDonnees baseDonnees =
            null;                                    //!< L'instance unique de BaseDeDonnees (singleton)
    private SQLiteDatabase baseDonnee = null; //<! L'accès à la base de données

    /**
     * @brief Constructeur de la classe BaseDeDonnees
     */
    private BaseDeDonnees(Context context)
    {
        super(context, POOL_DONNEES, null, VERSION_POOL_DONNEES);
        Log.d(TAG, "BaseDeDonnees()");
        if(baseDonnee == null)
            baseDonnee = this.getWritableDatabase();
    }

    /**
     * @brief Crée les différentes tables de la base de données
     */
    @Override
    public void onCreate(SQLiteDatabase baseDonnee) {
        Log.d(TAG, "onCreate()");
        baseDonnee.execSQL(
                "CREATE TABLE IF NOT EXISTS joueurs (id INTEGER PRIMARY KEY AUTOINCREMENT, nom TEXT UNIQUE NOT NULL, parties INTEGER DEFAULT 0, victoires INTEGER DEFAULT 0)");
        baseDonnee.execSQL(
                "CREATE TABLE IF NOT EXISTS manches (id INTEGER PRIMARY KEY AUTOINCREMENT, horodatage DATETIME NOT NULL, gagnantId INTEGER, perdantId INTEGER, numeroTable INTEGER, FOREIGN KEY (gagnantId) REFERENCES joueurs(id) ON DELETE CASCADE, FOREIGN KEY (perdantId) REFERENCES joueurs(id) ON DELETE CASCADE)");
        baseDonnee.execSQL(
                "CREATE TABLE IF NOT EXISTS tours (id INTEGER PRIMARY KEY AUTOINCREMENT, joueurId INTEGER, mancheId INTEGER, FOREIGN KEY (joueurId) REFERENCES joueurs(id) ON DELETE CASCADE, FOREIGN KEY (mancheId) REFERENCES manches(id) ON DELETE CASCADE)");
        baseDonnee.execSQL(
                "CREATE TABLE IF NOT EXISTS empoches (id INTEGER PRIMARY KEY AUTOINCREMENT, tourId INTEGER, poche INTEGER, couleur INTEGER, FOREIGN KEY (tourId) REFERENCES tours(id) ON DELETE CASCADE)");

        // Pour les tests
        baseDonnee.execSQL("INSERT INTO joueurs(nom, parties, victoires) VALUES ('TRICHET Clément', 0, 0);");
        baseDonnee.execSQL("INSERT INTO joueurs(nom, parties, victoires) VALUES ('GAUME Benjamin', 0, 0);");
    }


    /**
     * @brief Supprimer les tables existantes pour en recréer des vierges
     * @warning le plus simple est de supprimer l'application puis de la réinstaller !
     */
    @Override
    public void onUpgrade(SQLiteDatabase baseDonnee,int oldVersion, int newVersion)
    {
        baseDonnee.execSQL("DROP TABLE IF EXISTS empoches");
        baseDonnee.execSQL("DROP TABLE IF EXISTS tours");
        baseDonnee.execSQL("DROP TABLE IF EXISTS manches");
        baseDonnee.execSQL("DROP TABLE IF EXISTS joueurs");
        onCreate(baseDonnee);
    }

    /**
     * @fn getInstance
     * @brief Retourne l'instance BaseDeDonnees
     */
    public synchronized static BaseDeDonnees getInstance(Context context)
    {
        if(baseDonnees == null)
        {
            baseDonnees = new BaseDeDonnees(context);
        }
        return baseDonnees;
    }

    /**
     * @brief Ajouter un nouveau joueur à la base de données
     */
    public void ajouterNom(String nom)
    {
        Log.d(TAG, "ajouterNom(" + nom + ")");
        baseDonnee.execSQL("INSERT INTO joueurs (nom) VALUES (nom)");
    }

    /**
     * @brief Pour ajouter une nouvelle manche à la base de données et incrémenter le nombre de
     * parties effectuées et de victoires des joueurs concernés
     */
    public void ajouterManche(String                gagnant,
                              String                perdant,
                              boolean               premierJoueurGagnant,
                              Vector<Vector<int[]>> manche,
                              int                   numeroTable)
    {
        baseDonnee.execSQL("UPDATE joueurs SET parties = parties + 1, victoires = victoires + 1 WHERE joueurs.nom = gagnant");
        baseDonnee.execSQL("UPDATE joueurs SET parties = parties + 1, victoires = victoires + 1 WHERE joueurs.nom = perdant");

        ContentValues valeursManche = new ContentValues();
        valeursManche.put("gagnantId", baseDonnee.rawQuery("SELECT id FROM joueurs WHERE nom = gagnant", null).getInt(0));
        valeursManche.put("perdantId", baseDonnee.rawQuery("SELECT id FROM joueurs WHERE nom = perdant", null).getInt(0));
        valeursManche.put("numeroTable", numeroTable);
        baseDonnee.insert("manche", null, valeursManche);

        int[] participantsId = { Integer.parseInt(perdant), Integer.parseInt(gagnant) };
        for(int indexTour = 0; indexTour < manche.size(); indexTour++)
        {
            ContentValues valeursTour = new ContentValues();
            valeursTour.put("mancheId", baseDonnee.rawQuery("SELECT max(id) FROM manches", null).getInt(0));
            valeursTour.put(
              "joueurId",
              participantsId[(indexTour + (premierJoueurGagnant ? 1 : 0)) % BlackBall.NB_JOUEURS]);
            baseDonnee.insert("tours", null, valeursTour);

            for(int indexEmpoche = 0; indexEmpoche < manche.get(indexTour).size(); indexEmpoche++)
            {
                ContentValues valeursEmpoche = new ContentValues();
                valeursEmpoche.put("tourId", baseDonnee.rawQuery("SELECT max(id) FROM tours", null).getInt(0));
                valeursEmpoche.put("poche", manche.get(indexTour).get(indexEmpoche)[0]);
                valeursEmpoche.put("couleur", manche.get(indexTour).get(indexEmpoche)[1]);
            }
        }
    }

    public ArrayList<String> getNomsJoueurs()
    {
        ArrayList<String> nomsJoueurs = new ArrayList<String>();
        Cursor            cursor      = baseDonnee.rawQuery("SELECT nom FROM joueurs", null);
        if(cursor.moveToFirst())
        {
            do
            {
                String nomJoueur = cursor.getString(0);
                nomsJoueurs.add(nomJoueur);
            } while(cursor.moveToNext());
        }
        cursor.close();
        return nomsJoueurs;
    }
}
