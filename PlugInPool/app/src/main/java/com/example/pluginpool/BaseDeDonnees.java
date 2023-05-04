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
    private static final String POOL_DONNEES = "PoolDonnees.db";
    private static final int VERSION_POOL_DONNEES = 1;  //!< Version
    private static final boolean VICTOIRE = true;          //!< Victoire
    private static final boolean DEFAITE = false;           //!< Defaite
    private static BaseDeDonnees baseDonnees = null; //!< L'instance unique de BaseDeDonnees (singleton)

    /**
     * @brief Constructeur de la classe BaseDeDonnees
     */
    public BaseDeDonnees(Context context)
    {
        super(context, POOL_DONNEES, null, VERSION_POOL_DONNEES);
    }

    @Override
    /**
     * @brief Crée les différentes tables de la base de données
     */
    private void onCreate(SQLiteDatabase baseDonnees)
    {
        baseDonnees.execSQL("CREATE TABLE IF NOT EXISTS joueurs (id INTEGER PRIMARY KEY, nom TEXT, parties INTEGER, victoires INTEGER)");
        baseDonnees.execSQL("CREATE TABLE IF NOT EXISTS manches (id INTEGER PRIMARY KEY, gagnantId INTEGER, perdantId INTEGER, numeroTable INTEGER, FOREIGN KEY (gagnantId) REFERENCES joueurs(id), FOREIGN KEY (perdantId) REFERENCES joueurs(id))");
        baseDonnees.execSQL("CREATE TABLE IF NOT EXISTS tours (id INTEGER PRIMARY KEY, joueurId INTEGER, mancheId INTEGER, FOREIGN KEY (joueurId) REFERENCES joueurs(id), FOREIGN KEY (mancheId) REFERENCES manches(id))");
        baseDonnees.execSQL("CREATE TABLE IF NOT EXISTS empoches (id INTEGER PRIMARY KEY, tourId INTEGER, poche INTEGER, couleur INTEGER, FOREIGN KEY (tourId) REFERENCES tours(id))");
    }

    @Override
    /**
     * @brief Supprimer les tables existantes pour en recréer des vierges
     */
    public void onUpgrade(SQLiteDatabase baseDonnee, int oldVersion, int newVersion) {
        baseDonnees.execSQL("DROP TABLE IF EXISTS joueurs");
        baseDonnees.execSQL("DROP TABLE IF EXISTS manches");
        baseDonnees.execSQL("DROP TABLE IF EXISTS tours");
        baseDonnees.execSQL("DROP TABLE IF EXISTS empoches");
        onCreate(baseDonnees);
    }

    /**
     * @fn getInstance
     * @brief Retourne l'instance BaseDeDonnees
     */
    public synchronized static BaseDeDonnees getInstance()
    {
        if(baseDonnees == null)
        {
            baseDonnees = new BaseDeDonnees();
        }
        return baseDonnees;
    }

    /**
     * @brief Ajouter un nouveau joueur à la base de données
     */
    public void ajouterNom(String nom)
    {
        SQLiteDatabase baseDonnees = this.getWritableDatabase();
        Cursor curseur = baseDonnees.rawQuery("SELECT * FROM joueurs WHERE nom=?", new String[]{nom});
        if (curseur.getCount() == 0)
        {
            ContentValues valeursJoueur = new ContentValues();
            valeursJoueur.put("nom", nom);
            baseDonnees.insert("joueurs", null, valeursJoueur);
        }
        curseur.close();
    }

    /**
     * @brief Pour ajouter une nouvelle manche à la base de données et incrémenter le nombre de parties effectuées et de victoires des joueurs concernés
     */
    public void ajouterManche(String gagnant, String perdant, boolean premierJoueurGagnant, Vector<Vector<int[]>>   manche, int numeroTable)
    {
        int[] participantsId = {perdant, gagnant};
        actualiserTableJoueurs(gagnant, VICTOIRE);
        actualiserTableJoueurs(perdant, DEFAITE);

        SQLiteDatabase baseDonnees = this.getWritableDatabase();
        Cursor curseur = baseDonnee.rawQuery("SELECT * FROM joueurs WHERE nom=?", new String[]{gagnant});
        if (curseur.moveToFirst())
        {
            participantsId[VICTOIRE] = curseur.getInt(curseur.getColumnIndex("id"));
        }
        else
        {
            throw new Error("Joueur Inconnu");
        }
        curseur.close();

        Cursor curseur = baseDonnee.rawQuery("SELECT * FROM joueurs WHERE nom=?", new String[]{perdant});
        if (curseur.moveToFirst())
        {
            participantsId[DEFAITE] = curseur.getInt(curseur.getColumnIndex("id"));
        }
        else
        {
            throw new Error("Joueur Inconnu");
        }
        curseur.close();

        ContentValues valeursManche = new ContentValues();
        valeursManche.put("gagnantId", participantsId[(int)VICTOIRE]);
        valeursManche.put("perdantId", participantsId[DEFAITE]);
        valeursManche.put("numeroTable", numeroTable);
        baseDonnees.insert("manche", null, valeursJoueur);

        String selectQuery = "SELECT id FROM manches ORDER BY id DESC LIMIT 1";
        Cursor curseur = baseDonnees.rawQuery(selectQuery, null);
        mancheId = curseur.getInt(curseur.getColumnIndex("id"));
        curseur.close();
        for(int indexTour = 0; indexTour < manche.size(); indexTour++)
        {
            ContentValues valeursTour = new ContentValues();
            valeursTour.put("mancheId", mancheId);
            valeursTour.put("joueurId", participants[(indexTour + premierJoueurGagnant) % Blackball.NB_JOUEURS]);
            baseDonnees.insert("tours", null, valeursTour);

            selectQuery = "SELECT id FROM tours ORDER BY id DESC LIMIT 1";
            Cursor curseur = baseDonnees.rawQuery(selectQuery, null);
            tourId = curseur.getInt(cursor.getColumnIndex("id"));
            curseur.close();
            for(int indexEmpoche = 0; indexEmpoche < manche[indexTour].size(); indexEmpoche++)
            {
                ContentValues valeursEmpoche = new ContentValues();
                valeursEmpoche.put("tourId", tourId);
                valeursEmpoche.put("poche", manche[indexTour][indexEmpoche][0]);
                valeursEmpoche.put("couleur", manche[indexTour][indexEmpoche][1]);
            }
        }
    }

    /**
     * @brief Pour incrémenter le nombre de parties effectuées et de victoires du joueur concerné
     */
    public void actualiserTableJoueurs(String joueur, boolean aGagne)
    {
        SQLiteDatabase baseDonnees = this.getWritableDatabase();
        Cursor curseur = baseDonnees.rawQuery("SELECT * FROM joueurs WHERE nom=?", new String[]{nom});
        if (curseur.moveToFirst())
        {
            int joueurId = curseur.getInt(curseur.getColumnIndex("id"));
            int parties = curseur.getInt(curseur.getColumnIndex("parties"));
            ContentValues valeursJoueur = new ContentValues();
            valeursJoueur.put("parties", parties + 1);
            if (aGagne)
            {
                int victoires = curseur.getInt(curseur.getColumnIndex("victoires"));
                valeursJoueur.put("victoires", victoires + 1);
            }
            baseDonnees.update("joueurs", valeursJoueur, "id=?", new String[]{String.valueOf(joueurId)});
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
        Cursor cursor = baseDonnees.rawQuery("SELECT nom FROM joueurs", null);
        if (cursor.moveToFirst())
        {
            do {
                String nomJoueur = cursor.getString(0);
                nomsJoueurs.add(nomJoueur);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return nomsJoueurs;
    }
}
