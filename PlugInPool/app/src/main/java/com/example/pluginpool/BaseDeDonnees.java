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
    private static final String  POOL_DONNEES         = "PoolDonnees.db";
    private static final int     VERSION_POOL_DONNEES = 1;     //!< Version
    private static final boolean VICTOIRE             = true;  //!< Victoire
    private static final boolean DEFAITE              = false; //!< Defaite
    private static BaseDeDonnees baseDonnees =
      null;                                    //!< L'instance unique de BaseDeDonnees (singleton)
    private SQLiteDatabase accesSQLite = null; //<! L'accès à la base de données

    /**
     * @brief Constructeur de la classe BaseDeDonnees
     */
    public BaseDeDonnees(Context context)
    {
        super(context, POOL_DONNEES, null, VERSION_POOL_DONNEES);
        if(accesSQLite == null)
            accesSQLite = this.getWritableDatabase();
    }

    /**
     * @brief Crée les différentes tables de la base de données
     */
    @Override
    public void onCreate(SQLiteDatabase bdd)
    {
        accesSQLite.execSQL(
          "CREATE TABLE IF NOT EXISTS joueurs (id INTEGER PRIMARY KEY AUTOINCREMENT, nom TEXT, parties INTEGER DEFAULT 0, victoires INTEGER DEFAULT 0)");
        accesSQLite.execSQL(
          "CREATE TABLE IF NOT EXISTS manches (id INTEGER PRIMARY KEY AUTOINCREMENT, horodatage DATETIME NOT NULL, gagnantId INTEGER, perdantId INTEGER, numeroTable INTEGER, FOREIGN KEY (gagnantId) REFERENCES joueurs(id) ON DELETE CASCADE, FOREIGN KEY (perdantId) REFERENCES joueurs(id) ON DELETE CASCADE)");
        accesSQLite.execSQL(
          "CREATE TABLE IF NOT EXISTS tours (id INTEGER PRIMARY KEY AUTOINCREMENT, joueurId INTEGER, mancheId INTEGER, FOREIGN KEY (joueurId) REFERENCES joueurs(id) ON DELETE CASCADE, FOREIGN KEY (mancheId) REFERENCES manches(id) ON DELETE CASCADE)");
        accesSQLite.execSQL(
          "CREATE TABLE IF NOT EXISTS empoches (id INTEGER PRIMARY KEY AUTOINCREMENT, tourId INTEGER, poche INTEGER, couleur INTEGER, FOREIGN KEY (tourId) REFERENCES tours(id) ON DELETE CASCADE)");
    }

    /**
     * @brief Supprimer les tables existantes pour en recréer des vierges
     */
    @Override
    public void onUpgrade(SQLiteDatabase bdd, int oldVersion, int newVersion)
    {
        accesSQLite.execSQL("DROP TABLE IF EXISTS empoches");
        accesSQLite.execSQL("DROP TABLE IF EXISTS tours");
        accesSQLite.execSQL("DROP TABLE IF EXISTS manches");
        accesSQLite.execSQL("DROP TABLE IF EXISTS joueurs");
        onCreate(bdd);
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
        Cursor curseur =
          accesSQLite.rawQuery("SELECT * FROM joueurs WHERE nom=?", new String[] { nom });
        if(curseur.getCount() == 0)
        {
            ContentValues valeursJoueur = new ContentValues();
            valeursJoueur.put("nom", nom);
            accesSQLite.insert("joueurs", null, valeursJoueur);
        }
        curseur.close();
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
        int[] participantsId = { Integer.parseInt(perdant), Integer.parseInt(gagnant) };
        actualiserTableJoueurs(gagnant, VICTOIRE);
        actualiserTableJoueurs(perdant, DEFAITE);

        Cursor curseur =
          accesSQLite.rawQuery("SELECT * FROM joueurs WHERE nom=?", new String[] { gagnant });
        if(curseur.moveToFirst())
        {
            participantsId[(VICTOIRE) ? 1 : 0] = curseur.getInt(curseur.getColumnIndex("id"));
        }
        else
        {
            throw new Error("Joueur Inconnu");
        }
        curseur.close();

        curseur =
          accesSQLite.rawQuery("SELECT * FROM joueurs WHERE nom=?", new String[] { perdant });
        if(curseur.moveToFirst())
        {
            participantsId[(DEFAITE) ? 1 : 0] = curseur.getInt(curseur.getColumnIndex("id"));
        }
        else
        {
            throw new Error("Joueur Inconnu");
        }
        curseur.close();

        /**
         * @fixme trop d'erreurs !
         */
        /*
        ContentValues valeursManche = new ContentValues();
        valeursManche.put("gagnantId", participantsId[(VICTOIRE) ? 1 : 0]);
        valeursManche.put("perdantId", participantsId[(DEFAITE) ? 1 : 0]);
        valeursManche.put("numeroTable", numeroTable);
        accesSQLite.insert("manche", null, valeursJoueur);

        String selectQuery = "SELECT id FROM manches ORDER BY id DESC LIMIT 1";
        curseur            = accesSQLite.rawQuery(selectQuery, null);
        mancheId           = curseur.getInt(curseur.getColumnIndex("id"));
        curseur.close();
        for(int indexTour = 0; indexTour < manche.size(); indexTour++)
        {
            ContentValues valeursTour = new ContentValues();
            valeursTour.put("mancheId", mancheId);
            valeursTour.put(
              "joueurId",
              participants[(indexTour + premierJoueurGagnant) % Blackball.NB_JOUEURS]);
            accesSQLite.insert("tours", null, valeursTour);

            selectQuery = "SELECT id FROM tours ORDER BY id DESC LIMIT 1";
            curseur     = baseDonnees.rawQuery(selectQuery, null);
            tourId      = curseur.getInt(cursor.getColumnIndex("id"));
            curseur.close();
            for(int indexEmpoche = 0; indexEmpoche < manche[indexTour].size(); indexEmpoche++)
            {
                ContentValues valeursEmpoche = new ContentValues();
                valeursEmpoche.put("tourId", tourId);
                valeursEmpoche.put("poche", manche[indexTour][indexEmpoche][0]);
                valeursEmpoche.put("couleur", manche[indexTour][indexEmpoche][1]);
            }
        }
        */
    }

    /**
     * @brief Pour incrémenter le nombre de parties effectuées et de victoires du joueur concerné
     */
    public void actualiserTableJoueurs(String joueur, boolean aGagne)
    {
        Cursor curseur =
          accesSQLite.rawQuery("SELECT * FROM joueurs WHERE nom=?", new String[] { joueur });
        if(curseur.moveToFirst())
        {
            int           joueurId      = curseur.getInt(curseur.getColumnIndex("id"));
            int           parties       = curseur.getInt(curseur.getColumnIndex("parties"));
            ContentValues valeursJoueur = new ContentValues();
            valeursJoueur.put("parties", parties + 1);
            if(aGagne)
            {
                int victoires = curseur.getInt(curseur.getColumnIndex("victoires"));
                valeursJoueur.put("victoires", victoires + 1);
            }
            accesSQLite.update("joueurs",
                               valeursJoueur,
                               "id=?",
                               new String[] { String.valueOf(joueurId) });
        }
        else
        {
            throw new Error("Joueur Inconnu");
        }
        curseur.close();
    }

    public ArrayList<String> getNomsJoueurs()
    {
        ArrayList<String> nomsJoueurs = new ArrayList<String>();
        Cursor            cursor      = accesSQLite.rawQuery("SELECT nom FROM joueurs", null);
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
