/**
 * @author Clément Trichet
 * @file BaseDeDonnees.java
 * @brief TODO
 */

package com.example.pluginpool;

import static android.provider.MediaStore.getVersion;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Vector;

/**
 *
 *
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
    public static final int DEFAUT = -1; //!< Clef primaire d'une table par défaut (vide)
    private static final int DONNEES_JOUEUR = 3; //!< Nombre de donnees associées à un joueur
    private static final int MANCHES = 0; //!< @todo
    private static final int VICTOIRES = 1; //!< @todo
    private static final int SCORE_ELO = 2; //!< @todo
    private static final int CONSTANTE_ELO1 = 100; //!< @todo
    private static final int CONSTANTE_ELO2 = 400; //!< @todo

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
                "CREATE TABLE IF NOT EXISTS joueurs (id INTEGER PRIMARY KEY AUTOINCREMENT, nom TEXT UNIQUE NOT NULL, manches INTEGER DEFAULT 0, victoires INTEGER DEFAULT 0, scoreELO INTEGER DEFAULT 0)");
        sqlite.execSQL(
                "CREATE TABLE IF NOT EXISTS manches (id INTEGER PRIMARY KEY AUTOINCREMENT, horodatage DATETIME UNIQUE NOT NULL, gagnantId INTEGER, perdantId INTEGER, numeroTable INTEGER, FOREIGN KEY (gagnantId) REFERENCES joueurs(id) ON DELETE CASCADE, FOREIGN KEY (perdantId) REFERENCES joueurs(id) ON DELETE CASCADE)");
        sqlite.execSQL(
                "CREATE TABLE IF NOT EXISTS tours (id INTEGER PRIMARY KEY AUTOINCREMENT, joueurId INTEGER, mancheId INTEGER, FOREIGN KEY (joueurId) REFERENCES joueurs(id) ON DELETE CASCADE, FOREIGN KEY (mancheId) REFERENCES manches(id) ON DELETE CASCADE)");
        sqlite.execSQL(
                "CREATE TABLE IF NOT EXISTS empoches (id INTEGER PRIMARY KEY AUTOINCREMENT, tourId INTEGER, poche INTEGER, couleur INTEGER, FOREIGN KEY (tourId) REFERENCES tours(id) ON DELETE CASCADE)");

        initialiserBaseDeDonnees(sqlite);
    }

    /**
     * @brief Supprimer les tables existantes pour en recréer des vierges
     */
    public void effacer()
    {
        onUpgrade(sqlite, sqlite.getVersion(), sqlite.getVersion() + 1);
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
        sqlite.setVersion(newVersion);
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

    public int getNbEmpoches(int couleur, String joueur, int mancheId)
    {
        if(mancheId == -1)
        {
            Cursor curseur = sqlite.rawQuery("SELECT max(id) FROM manches", null);
            if(curseur.moveToFirst()) {
                mancheId = curseur.getInt(0);
            }
            curseur.close();
        }
        Cursor curseur = sqlite.rawQuery("SELECT COUNT(empoches.couleur) AS count FROM empoches " +
                "INNER JOIN tours ON tours.id = empoches.tourId " +
                "INNER JOIN joueurs ON joueurs.id = tours.joueurId " +
                "WHERE joueurs.nom = '" + joueur + "' " +
                "AND tours.mancheId = '" + mancheId + "' " +
                "AND empoches.couleur = '" + couleur + "'", null);
        if(curseur.moveToFirst())
        {
            int colonne = curseur.getColumnIndex("count");
            if (colonne != DEFAUT) {
                Log.d(TAG, "getNbEmpoches() NbEmpoches = " + curseur.getInt(colonne));
                return curseur.getInt(colonne);
            }
        }
        return DEFAUT;
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
     * manches effectuées et de victoires des joueurs concernés
     */
     public void ajouterManche(String[] joueurs,
                              int               indexJoueurGagnant,
                              Vector<Vector<int[]>> manche,
                              int                   numeroTable)
    {
        Log.d(BlackBall.TAG, "gagnant = " + joueurs[indexJoueurGagnant]);
        String gagnant = joueurs[indexJoueurGagnant];
        String perdant = joueurs[(indexJoueurGagnant + 1) % BlackBall.NB_JOUEURS];
        Log.d(TAG, "ajouterManche() gagnant = " + gagnant + " perdant = " + perdant);

        actualiserJoueurs(gagnant, perdant);



        int gagnantId = DEFAUT;
        int perdantId = DEFAUT;
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
            sqlite.execSQL("INSERT INTO manches (horodatage, gagnantId, perdantId, numeroTable) VALUES (strftime('%d/%m/%Y %H:%M:%S', 'now'), '" + gagnantId + "', '" + perdantId + "', '" + numeroTable + "')");
        } catch (Exception e){
            Log.d(TAG, "INSERT INTO manches " + e );
        }
        int[] participantsId = {gagnantId, perdantId};
        for(int indexTour = 0; indexTour < manche.size(); indexTour++)
        {
            int mancheId = DEFAUT;
            curseur = sqlite.rawQuery("SELECT max(id) FROM manches", null);
            if(curseur.moveToFirst()) {
                mancheId = curseur.getInt(0);
            }
            curseur.close();
            //int mancheId = sqlite.rawQuery("SELECT max(id) FROM manches", null).getInt(0);

            int joueurId = participantsId[(indexTour + indexJoueurGagnant) % BlackBall.NB_JOUEURS];
            try {
                sqlite.execSQL("INSERT INTO tours (joueurId, mancheId) VALUES ('" + joueurId + "', '" + mancheId + "')");
                Log.d(BlackBall.TAG, "nouveau tour");
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
                    Log.d(BlackBall.TAG, "empoche, couleur = " + couleur);
                } catch (Exception e) {
                    Log.d(TAG, "INSERT INTO empoches " + e );
                }
            }
        }
    }

    /**
     * @brief Actualiser le nombre de manches, de victoires et le scoreELO de chaque joueur
     */
    private void actualiserJoueurs(String gagnant, String perdant)
    {
        int[] donneesGagnant = new int[DONNEES_JOUEUR];
        int[] donneesPerdant = new int[DONNEES_JOUEUR];

        Cursor curseur = sqlite.rawQuery("SELECT manches, victoires, scoreELO FROM joueurs WHERE nom = '" + gagnant + "'", null);
        if (curseur.moveToFirst()) {
            for(int donnee = 0; donnee < DONNEES_JOUEUR; donnee++)
            {
                donneesGagnant[donnee] = curseur.getInt(donnee);
            }
        }
        curseur.close();
        curseur = sqlite.rawQuery("SELECT manches, victoires, scoreELO FROM joueurs WHERE nom = '" + perdant + "'", null);
        if (curseur.moveToFirst()) {
            for(int donnee = 0; donnee < DONNEES_JOUEUR; donnee++)
            {
                donneesPerdant[donnee] = curseur.getInt(donnee);
            }
        }
        curseur.close();

        donneesGagnant[SCORE_ELO] += CONSTANTE_ELO1 / (donneesGagnant[MANCHES] + 1 + donneesGagnant[SCORE_ELO] / (donneesGagnant[VICTOIRES] + 1)) * (1 -(1 /(1 + 10^((donneesPerdant[SCORE_ELO] - donneesGagnant[SCORE_ELO])/CONSTANTE_ELO2))));
        donneesPerdant[SCORE_ELO] += CONSTANTE_ELO1 / (donneesPerdant[MANCHES] + 1 + donneesPerdant[SCORE_ELO] / (donneesPerdant[VICTOIRES] + 1)) * (-1 /(1 + 10^((donneesGagnant[SCORE_ELO] - donneesPerdant[SCORE_ELO])/CONSTANTE_ELO2)));

        sqlite.execSQL("UPDATE joueurs SET manches = manches + 1, victoires = victoires + 1, scoreElO = '" + donneesGagnant[SCORE_ELO] + "' WHERE joueurs.nom = '" + gagnant + "'");
        sqlite.execSQL("UPDATE joueurs SET manches = manches + 1, scoreELO = '" + donneesPerdant[SCORE_ELO] + "' WHERE joueurs.nom = '" + perdant + "'");
    }
    /**
     * @brief Récupérer la liste des joueurs présents dans la base de données
     */
    public ArrayList<String> getNomsJoueurs()
    {
        ArrayList<String> nomsJoueurs = new ArrayList<String>();
        Cursor            curseur      = sqlite.rawQuery("SELECT nom FROM joueurs", null);
        if(curseur.moveToFirst())
        {
            do
            {
                String nomJoueur = curseur.getString(0);
                nomsJoueurs.add(nomJoueur);
            } while(curseur.moveToNext());
        }
        curseur.close();
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
                "INSERT INTO joueurs(nom, manches, victoires) VALUES ('TRICHET Clément', 3, 3);");
        sqlite.execSQL(
                "INSERT INTO joueurs(nom, manches, victoires) VALUES ('GAUME Benjamin', 3, 0);");
    }

    /**
     * @brief Renvoie un vecteur de string contenant le noms des joueurs enregistrés triés par scoreElo décroissant
     */
    public Vector<String> getNomsJoueursTries()
    {
        Log.d(TAG, "getNomsJoueursTries()");

        Cursor curseur = sqlite.rawQuery("SELECT nom FROM joueurs ORDER BY scoreELO DESC, id ASC", null);

        Vector<String> listeJoueurs = new Vector<>();

        while (curseur.moveToNext())
        {
            String nom = curseur.getString(curseur.getColumnIndexOrThrow("nom"));
            listeJoueurs.add(nom);
        }
        curseur.close();

        return listeJoueurs;
    }

    /**
     * @brief Renvoie un vecteur de string contenant le dates des manches enregistrées triées dans l'ordre décroissant
     */
    public Vector<String> getManchesTriees()
    {
        Log.d(TAG, "getManchesTriees()");

        Cursor curseur = sqlite.rawQuery("SELECT horodatage FROM manches ORDER BY horodatage DESC", null);

        Vector<String> listeManches = new Vector<>();

        while (curseur.moveToNext())
        {
            String date = curseur.getString(curseur.getColumnIndexOrThrow("horodatage"));
            listeManches.add(date);
        }
        curseur.close();

        return listeManches;
    }

    /**
     * @brief Renvoie le scoreELO d'un joueur dont on connaît le nom
     */
    public int getScoreElo(String nom)
    {
        Log.d(TAG, "getScoreElo( nom = " + nom + " )");

        int scoreElo = 0;
        Cursor curseur = sqlite.rawQuery("SELECT scoreELO FROM joueurs WHERE nom = '" + nom + "'", null);
        if (curseur.moveToFirst()) {
            scoreElo = curseur.getInt(0);
        }
        curseur.close();

        return scoreElo;
    }

    /**
     * @brief Renvoie le nombre de manches d'un joueur dont on connaît le nom
     */
    public int getNbManches(String nom)
    {
        Log.d(TAG, "getNbManches( nom = " + nom + " )");

        int nbManches = 0;
        Cursor curseur = sqlite.rawQuery("SELECT manches FROM joueurs WHERE nom = '" + nom + "'", null);
        if (curseur.moveToFirst()) {
            nbManches = curseur.getInt(0);
        }
        curseur.close();

        return nbManches;
    }

    /**
     * @brief Renvoie le nombre de victoires d'un joueur dont on connaît le nom
     */
    public int getNbVictoires(String nom)
    {
        Log.d(TAG, "getNbManches( nom = " + nom + " )");

        int nbVictoires = 0;
        Cursor curseur = sqlite.rawQuery("SELECT victoires FROM joueurs WHERE nom = '" + nom + "'", null);
        if (curseur.moveToFirst()) {
            nbVictoires = curseur.getInt(0);
        }
        curseur.close();

        return nbVictoires;
    }

    /**
     * @brief Supprime un joueur, dont on connaît le nom, de la base de donnees
     */
    public void supprimerJoueur(String nom)
    {
        sqlite.execSQL("DELETE FROM joueurs WHERE nom = ?", new String[] {nom});
    }

    /**
     * @brief Supprime une manche, dont on connaît le date, de la base de donnees
     */
    public void supprimerManche(String date)
    {
        sqlite.execSQL("DELETE FROM manches WHERE horodatage = ?", new String[] {date});
    }

    /**
     * @brief @todo
     */
    public String getNomJoueur(String qualificatifJoueur, String date)
    {
        Log.d(TAG, "getNomJoueur(qualificatif, date) date = " + date + " qualificatif = " + qualificatifJoueur);
        String requete = "SELECT joueurs.nom FROM joueurs  INNER JOIN manches ON joueurs.id = manches." + qualificatifJoueur + "Id WHERE manches.horodatage = \"" + date + "\"";

        Cursor curseur = sqlite.rawQuery(requete, null);
        if(curseur.moveToFirst())
        {
            return curseur.getString(curseur.getColumnIndexOrThrow("nom"));
        }
        curseur.close();
        return "Inconnu";
    }

    /**
     * @brief @todo
     */
    public void supprimerJoueurs()
    {
        sqlite.execSQL("DELETE FROM joueurs");
    }

    /**
     * @brief @todo
     */
    public void supprimerManches()
    {
        Log.d(TAG, "supprimerManches()");
        sqlite.execSQL("DELETE FROM manches");
    }

    /**
     * @brief @todo
     */
    public String getNomsJoueurs(String date)
    {
        Log.d(TAG, "getNomsJoueurs()");
        String nomsJoueurs = "";
        Cursor curseur = sqlite.rawQuery("SELECT joueurs.nom FROM joueurs " +
                "INNER JOIN manches ON joueurs.id = manches.gagnantId OR joueurs.id = manches.perdantId " +
                "WHERE manches.horodatage = ?", new String[] { date });
        while(curseur.moveToNext())
        {
            nomsJoueurs += " " + curseur.getString(curseur.getColumnIndexOrThrow("nom"));
        }
        curseur.close();
        return nomsJoueurs;
    }

    /**
     * @brief @todo
     */
    public int getMancheId(String date)
    {
        Cursor curseur = sqlite.rawQuery("SELECT id FROM manches WHERE manches.horodatage = ?", new String[] {date});
        if(curseur.moveToFirst()) {
            return curseur.getInt(0);
        }
        curseur.close();
        return BaseDeDonnees.DEFAUT;
    }
}

