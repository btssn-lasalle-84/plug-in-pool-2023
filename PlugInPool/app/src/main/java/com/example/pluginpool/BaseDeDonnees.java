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
    public void ajouterNom(String nom)
    {
        Log.d(TAG, "ajouterNom(" + nom + ")");
        sqlite.execSQL("INSERT INTO joueurs (nom) VALUES (nom)");
    }

    /**
     * @brief Pour ajouter une manche terminée à la base de données et incrémenter le nombre de
     * parties effectuées et de victoires des joueurs concernés
     */
    public boolean ajouterManche(String                gagnant,
                                 String                perdant,
                                 boolean               premierJoueurGagnant,
                                 Vector<Vector<int[]>> manche,
                                 int                   numeroTable)
    {
        Log.d(TAG, "ajouterManche()");
        sqlite.execSQL(
          "UPDATE joueurs SET parties = parties + 1, victoires = victoires + 1 WHERE joueurs.nom = gagnant");
        sqlite.execSQL("UPDATE joueurs SET parties = parties + 1 WHERE joueurs.nom = perdant");

        ContentValues valeursManche = new ContentValues();
        valeursManche.put(
          "gagnantId",
          sqlite.rawQuery("SELECT id FROM joueurs WHERE nom = gagnant", null).getInt(0));
        valeursManche.put(
          "perdantId",
          sqlite.rawQuery("SELECT id FROM joueurs WHERE nom = perdant", null).getInt(0));
        valeursManche.put("numeroTable", numeroTable);
        long resultats = sqlite.insert("manche", null, valeursManche);

        if(resultats > 0)
        {
            int[] participantsId = { Integer.parseInt(perdant), Integer.parseInt(gagnant) };
            for(int indexTour = 0; indexTour < manche.size(); indexTour++)
            {
                ContentValues valeursTour = new ContentValues();
                valeursTour.put("mancheId",
                                sqlite.rawQuery("SELECT max(id) FROM manches", null).getInt(0));
                valeursTour.put("joueurId",
                                participantsId[(indexTour + (premierJoueurGagnant ? 1 : 0)) %
                                               BlackBall.NB_JOUEURS]);
                resultats = sqlite.insert("tours", null, valeursTour);
                if(resultats > 0)
                {
                    for(int indexEmpoche = 0; indexEmpoche < manche.get(indexTour).size();
                        indexEmpoche++)
                    {
                        ContentValues valeursEmpoche = new ContentValues();
                        valeursEmpoche.put(
                          "tourId",
                          sqlite.rawQuery("SELECT max(id) FROM tours", null).getInt(0));
                        valeursEmpoche.put("poche", manche.get(indexTour).get(indexEmpoche)[0]);
                        valeursEmpoche.put("couleur", manche.get(indexTour).get(indexEmpoche)[1]);
                    }
                }
                else
                {
                    Log.d(TAG, "ajouterManche() erreur insertion table tours");
                    return false;
                }
            }
        }
        else
        {
            Log.d(TAG, "ajouterManche() erreur insertion table manches");
            return false;
        }
        return true;
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
                "INSERT INTO joueurs(nom, parties, victoires) VALUES ('TRICHET Clément', 0, 0);");
        sqlite.execSQL(
                "INSERT INTO joueurs(nom, parties, victoires) VALUES ('GAUME Benjamin', 0, 0);");
    }
}
