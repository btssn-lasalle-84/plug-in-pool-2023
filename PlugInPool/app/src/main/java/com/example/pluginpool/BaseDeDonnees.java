/**
 * @author Clément Trichet
 * @file BaseDeDonnees.java
 * @brief TODO
 */

package com.example.pluginpool;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Vector;

/**
 * @class BaseDeDonnees
 * @brief La classe assurant la gestion de la base de données SQLITE
 */
public class BaseDeDonnees extends SQLiteOpenHelper
{
    /**
     * Constantes
     */
    private static final String TAG          = "_BaseDeDonnees"; //!< TAG pour les logs (cf. Logcat)
    private static final String POOL_DONNEES = "PoolDonnees.db";
    private static final int    VERSION_POOL_DONNEES = 1; //!< Version
    private static int ID_DEFAUT = -1; // !< Clef primaire d'une table par défaut (vide)

    /**
     * Attributs
     */
    private static BaseDeDonnees baseDeDonnees =
            null;                               //!< L'instance unique de BaseDeDonnees (singleton)
    private SQLiteDatabase sqlite = null; //<! L'accès à la base de données SQLite

    /**
     * @brief Constructeur de la classe BaseDeDonnees
     */
    private BaseDeDonnees(Context context)
    {
        super(context, POOL_DONNEES, null, VERSION_POOL_DONNEES);
        Log.d(TAG, "BaseDeDonnees()");
        if(sqlite == null)
            sqlite = this.getWritableDatabase();
    }

    /**
     * @brief Crée les différentes tables de la base de données
     */
    @Override
    public void onCreate(SQLiteDatabase sqlite)
    {
        Log.d(TAG, "onCreate()");
        sqlite.execSQL(
                "CREATE TABLE IF NOT EXISTS joueurs (id INTEGER PRIMARY KEY AUTOINCREMENT, nom TEXT UNIQUE NOT NULL, parties INTEGER DEFAULT 0, victoires INTEGER DEFAULT 0)");
        sqlite.execSQL(
                "CREATE TABLE IF NOT EXISTS manches (id INTEGER PRIMARY KEY AUTOINCREMENT, horodatage DATETIME NOT NULL, gagnantId INTEGER, perdantId INTEGER, numeroTable INTEGER, FOREIGN KEY (gagnantId) REFERENCES joueurs(id) ON DELETE CASCADE, FOREIGN KEY (perdantId) REFERENCES joueurs(id) ON DELETE CASCADE)");
        sqlite.execSQL(
                "CREATE TABLE IF NOT EXISTS tours (id INTEGER PRIMARY KEY AUTOINCREMENT, joueurId INTEGER, mancheId INTEGER, FOREIGN KEY (joueurId) REFERENCES joueurs(id) ON DELETE CASCADE, FOREIGN KEY (mancheId) REFERENCES manches(id) ON DELETE CASCADE)");
        sqlite.execSQL(
                "CREATE TABLE IF NOT EXISTS empoches (id INTEGER PRIMARY KEY AUTOINCREMENT, tourId INTEGER, poche INTEGER, couleur INTEGER, FOREIGN KEY (tourId) REFERENCES tours(id) ON DELETE CASCADE)");

        initialiserBaseDeDonnees(sqlite);
    }

    /**
     * @brief Supprimer les tables existantes pour en recréer des vierges
     * @warning le plus simple est de supprimer l'application puis de la réinstaller !
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqlite, int oldVersion, int newVersion)
    {
        Log.d(TAG, "onUpgrade()");
        sqlite.execSQL("DROP TABLE IF EXISTS empoches");
        sqlite.execSQL("DROP TABLE IF EXISTS tours");
        sqlite.execSQL("DROP TABLE IF EXISTS manches");
        sqlite.execSQL("DROP TABLE IF EXISTS joueurs");
        onCreate(sqlite);
    }

    /**
     * @fn getInstance
     * @brief Retourne l'instance BaseDeDonnees
     */
    public synchronized static BaseDeDonnees getInstance(Context context)
    {
        if(baseDeDonnees == null)
        {
            baseDeDonnees = new BaseDeDonnees(context);
        }
        return baseDeDonnees;
    }

    /**
     * @brief Ajouter un nouveau joueur à la base de données
     */
    public void ajouterNom(String nomJoueur)
    {
        try {
            Log.d(TAG, "ajouterNom(" + nomJoueur + ")");
            sqlite.execSQL("INSERT INTO joueurs (nom) VALUES ('" + nomJoueur + "')");
        } catch (SQLiteConstraintException e) {
        Log.d(TAG, "Nom déjà présent dans la base de donnée");
        }
    }

    /**
     * @brief Pour ajouter une manche terminée à la base de données et incrémenter le nombre de
     * parties effectuées et de victoires des joueurs concernés
     */
     public void ajouterManche(String[] joueurs,
                              int               indexJoueurGagnant,
                              Vector<Vector<int[]>> manche,
                              int                   numeroTable)
    {
        String gagnant = joueurs[indexJoueurGagnant];
        String perdant = joueurs[(indexJoueurGagnant + 1) % BlackBall.NB_JOUEURS];
        Log.d(TAG, "ajouterManche() gagnant = " + gagnant + " perdant = " + perdant);
        sqlite.execSQL(
                "UPDATE joueurs SET parties = parties + 1, victoires = victoires + 1 WHERE joueurs.nom = '" + gagnant + "'");
        sqlite.execSQL("UPDATE joueurs SET parties = parties + 1 WHERE joueurs.nom = '" + perdant + "'");

        int gagnantId = ID_DEFAUT;
        int perdantId = ID_DEFAUT;
         Cursor curseur = sqlite.rawQuery("SELECT id FROM joueurs WHERE nom = '" + gagnant + "'", null);
        if (curseur.moveToFirst()) {
            gagnantId = curseur.getInt(0);
        }
        curseur.close();
        curseur = sqlite.rawQuery("SELECT id FROM joueurs WHERE nom = '" + perdant + "'", null);
        if (curseur.moveToFirst()) {
            perdantId = curseur.getInt(0);
        }
        curseur.close();

        //int gagnantId = sqlite.rawQuery("SELECT id FROM joueurs WHERE nom = '" + gagnant + "'", null).getInt(0);
        //int perdantId = sqlite.rawQuery("SELECT id FROM joueurs WHERE nom = '" + perdant + "'", null).getInt(0);
        try {
            sqlite.execSQL("INSERT INTO manches (horodatage, gagnantId, perdantId, numeroTable) VALUES (datetime('now'), '" + gagnantId + "', '" + perdantId + "', '" + numeroTable + "')");
        } catch (Exception e){
            Log.d(TAG, "INSERT INTO manches " + e );
        }
        int[] participantsId = {perdantId, gagnantId};
        for(int indexTour = 0; indexTour < manche.size(); indexTour++)
        {
            int mancheId = ID_DEFAUT;
            curseur = sqlite.rawQuery("SELECT max(id) FROM manches", null);
            if(curseur.moveToFirst()) {
                mancheId = curseur.getInt(0);
            }
            curseur.close();
            //int mancheId = sqlite.rawQuery("SELECT max(id) FROM manches", null).getInt(0);

            int joueurId = participantsId[(indexTour + indexJoueurGagnant) % BlackBall.NB_JOUEURS];
            try {
                sqlite.execSQL("INSERT INTO tours (joueurId, mancheId) VALUES ('" + joueurId + "', '" + mancheId + "')");
            } catch (Exception e) {
                Log.d(TAG, "INSERT INTO tours " + e );
            }

            for(int indexEmpoche = 0; indexEmpoche < manche.get(indexTour).size();
                indexEmpoche++)
            {
                int poche = manche.get(indexTour).get(indexEmpoche)[0];
                int couleur = manche.get(indexTour).get(indexEmpoche)[1];
                try {
                    sqlite.execSQL("INSERT INTO empoches (tourId, poche, couleur) VALUES ((SELECT max(id) FROM tours), '" + poche + "', '" + couleur + "')");
                } catch (Exception e) {
                    Log.d(TAG, "INSERT INTO empoches " + e );
                }
            }
        }
    }

    /**
     * @brief Récupérer la liste des joueurs présents dans la base de données
     */
    public ArrayList<String> getNomsJoueurs()
    {
        ArrayList<String> nomsJoueurs = new ArrayList<String>();
        Cursor            cursor      = sqlite.rawQuery("SELECT nom FROM joueurs", null);
        if(cursor.moveToFirst())
        {
            do
            {
                String nomJoueur = cursor.getString(0);
                nomsJoueurs.add(nomJoueur);
            } while(cursor.moveToNext());
        }
        cursor.close();
        Log.d(TAG, "getNomsJoueurs() " + nomsJoueurs);
        return nomsJoueurs;
    }

    /**
     * @brief Ajouter des données initiales dans la base de données
     */
    private void initialiserBaseDeDonnees(SQLiteDatabase sqlite)
    {
        Log.d(TAG, "initialiserBaseDeDonnees()");
        // Pour les tests
        sqlite.execSQL(
                "INSERT INTO joueurs(nom, parties, victoires) VALUES ('TRICHET Clément', 3, 3);");
        sqlite.execSQL(
                "INSERT INTO joueurs(nom, parties, victoires) VALUES ('GAUME Benjamin', 3, 0);");
    }
}