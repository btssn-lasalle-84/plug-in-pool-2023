/**
 * @author Clément Trichet
 * @file Manche.java
 * @brief TODO
 */

package com.example.pluginpool;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import android.os.Handler;

/**
 * @class Manche
 * @brief L'activité de suivi de manche
 */
public class Manche extends AppCompatActivity
{
    /**
     * Constantes
     */
    private static final String TAG = "_Manche"; //!< TAG pour les logs (cf. Logcat)
    public static final int PREMIER_JOUEUR  = 0; //!< Numéro ou indice associé au premier joueur
    public static final int SECOND_JOUEUR   = 1; //!< Numéro ou indice associé au second joueur
    private static final int NUMERO_TABLE_DEFAUT = - 1; //!< Numéro par défaut de la table
    /**
     * Attributs
     */
    private FinDeManche             fenetreFinDeManche;
    private boolean                 connexionTable;
    private BaseDeDonnees           baseDonnees;        //!< Classe d'échange avec la base de donnees
    private int                     numeroTable;        //!< Numero de la table
    private String[]                joueurs;            //!< Attribut contenant le nom des joueurs
    private Map<String, Integer>    couleursJoueurs;    //!< Table ayant pour clef le nom d'un joueur et pour valeur la couleur des billes de son groupe
    private Integer[]               billes;             //!< Table ayant pour clef la couleur d'un groupe de billes et pour valeur, le nombre de billes de ce groupe encore en jeu
    private Integer[][]             poches;             //!< Liste des poches, sous la forme de deux-listes dont les valeurs correspondent au nombre de billes empochées ayant pour couleur l'indice de ces valeurs
    private Vector<Vector<int[]>>   manche;             //!< Liste des tours de la manche, chaque tour étant représenté par une liste de billes empochées au cours de ce dernier
    private int                     joueurActif;        //!< Booléen indiquant le joueur dont le tour est en cours
    private Boolean                 couleursDefinies;   //!< Booléen indiquant si la couleur du groupe des billes attribué aux joueurs est définie ou non
    private Boolean                 mancheDemarree;     //!< Booléen indiquant si la manche a ou non démarré
    private Compteur                compteur;           //!< Compteur indiquant le temps restant du coup en cours
    Communication   communication  = null;              //!< Classe de communication Bluetooth
    private Handler handler        = null;              //!< Handler permettant la communication entre le thread de réception bluetooth et celui de l'interface graphique

    /**
     * Ressources GUI
     */

    private TextView[] nomJoueurs;                  //!< Affichage du nom des joueurs
    private TextView[][] nbBillesEmpochees;       //!< Affichage du nombre de billes de chaque couleur empochées dans chacune des poches
    private ImageView[][] fondBillesEmpochees;    //!< Images de fond du nombre de billes de chaque couleur empochées dans chacune des poches
    private TextView decompte;                    //!< Nombre de seconde restantes
    private  View fondCompteur;                   //!< Image de fond du compeur
    private ProgressBar barreProgression;         //!< Barre de progression du compteur
    private ImageView[][] billesRestantes;           //!< Images des billes restantes de chaque joueur

    /**
     * @brief Méthode appelée à la création de l'activité
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate()");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.manche);

        initialiserHandler();
        initialiserAttributs();
        initialiserRessources();
        communication.envoyer(Protocole.DEBUT);
    }

    /**
     * @brief Initialise les attributs lors de l'instanciation
     */
    private void initialiserAttributs()
    {
        Log.d(TAG, "initialiserAttributs() ");

        fenetreFinDeManche = new FinDeManche(this);
        baseDonnees = BaseDeDonnees.getInstance(this);
        numeroTable = NUMERO_TABLE_DEFAUT;
        communication = Communication.getInstance(handler);
        Intent activiteManche = getIntent();
        joueurs         = new String[BlackBall.NB_JOUEURS];
        joueurs[PREMIER_JOUEUR] = activiteManche.getStringExtra("joueur1");
        joueurs[SECOND_JOUEUR]  = activiteManche.getStringExtra("joueur2");
        connexionTable = activiteManche.getBooleanExtra("connexionTable", false);
        compteur         = new Compteur(this);

        initialiserAttributsDeDebutDeManche();
    }

    /**
     * @brief Initialise les Ressources lors de l'instanciation
     */
    private void initialiserRessources()
    {
        Log.d(TAG, "initialiserRessources() ");

        nomJoueurs = new TextView[BlackBall.NB_JOUEURS];
        nomJoueurs[PREMIER_JOUEUR] = (TextView)findViewById(R.id.Joueur1);
        nomJoueurs[SECOND_JOUEUR] = (TextView)findViewById(R.id.Joueur2);

        nbBillesEmpochees = new TextView[BlackBall.NB_POCHES][BlackBall.NB_GROUPES_BILLES];
        fondBillesEmpochees = new ImageView[BlackBall.NB_POCHES][BlackBall.NB_GROUPES_BILLES];

        nbBillesEmpochees[BlackBall.POCHE_HAUT_GAUCHE][BlackBall.JAUNE]     = (TextView) findViewById(R.id.poche0BilleJauneNombre);
        nbBillesEmpochees[BlackBall.POCHE_HAUT_GAUCHE][BlackBall.ROUGE]     = (TextView) findViewById(R.id.poche0BilleRougeNombre);
        nbBillesEmpochees[BlackBall.POCHE_HAUT_DROIT][BlackBall.JAUNE]      = (TextView) findViewById(R.id.poche1BilleJauneNombre);
        nbBillesEmpochees[BlackBall.POCHE_HAUT_DROIT][BlackBall.ROUGE]      = (TextView) findViewById(R.id.poche1BilleRougeNombre);
        nbBillesEmpochees[BlackBall.POCHE_MILIEU_DROIT][BlackBall.JAUNE]    = (TextView) findViewById(R.id.poche2BilleJauneNombre);
        nbBillesEmpochees[BlackBall.POCHE_MILIEU_DROIT][BlackBall.ROUGE]    = (TextView) findViewById(R.id.poche2BilleRougeNombre);
        nbBillesEmpochees[BlackBall.POCHE_BAS_DROIT][BlackBall.JAUNE]       = (TextView) findViewById(R.id.poche3BilleJauneNombre);
        nbBillesEmpochees[BlackBall.POCHE_BAS_DROIT][BlackBall.ROUGE]       = (TextView) findViewById(R.id.poche3BilleRougeNombre);
        nbBillesEmpochees[BlackBall.POCHE_BAS_GAUCHE][BlackBall.JAUNE]      = (TextView) findViewById(R.id.poche4BilleJauneNombre);
        nbBillesEmpochees[BlackBall.POCHE_BAS_GAUCHE][BlackBall.ROUGE]      = (TextView) findViewById(R.id.poche4BilleRougeNombre);
        nbBillesEmpochees[BlackBall.POCHE_MILIEU_GAUCHE][BlackBall.JAUNE]   = (TextView) findViewById(R.id.poche5BilleJauneNombre);
        nbBillesEmpochees[BlackBall.POCHE_MILIEU_GAUCHE][BlackBall.ROUGE]   = (TextView) findViewById(R.id.poche5BilleRougeNombre);

        fondBillesEmpochees[BlackBall.POCHE_HAUT_GAUCHE][BlackBall.JAUNE]   = (ImageView) findViewById(R.id.poche0BilleJauneView);
        fondBillesEmpochees[BlackBall.POCHE_HAUT_GAUCHE][BlackBall.ROUGE]   = (ImageView) findViewById(R.id.poche0BilleRougeView);
        fondBillesEmpochees[BlackBall.POCHE_HAUT_DROIT][BlackBall.JAUNE]    = (ImageView) findViewById(R.id.poche1BilleJauneView);
        fondBillesEmpochees[BlackBall.POCHE_HAUT_DROIT][BlackBall.ROUGE]    = (ImageView) findViewById(R.id.poche1BilleRougeView);
        fondBillesEmpochees[BlackBall.POCHE_MILIEU_DROIT][BlackBall.JAUNE]  = (ImageView) findViewById(R.id.poche2BilleJauneView);
        fondBillesEmpochees[BlackBall.POCHE_MILIEU_DROIT][BlackBall.ROUGE]  = (ImageView) findViewById(R.id.poche2BilleRougeView);
        fondBillesEmpochees[BlackBall.POCHE_BAS_DROIT][BlackBall.JAUNE]     = (ImageView) findViewById(R.id.poche3BilleJauneView);
        fondBillesEmpochees[BlackBall.POCHE_BAS_DROIT][BlackBall.ROUGE]     = (ImageView) findViewById(R.id.poche3BilleRougeView);
        fondBillesEmpochees[BlackBall.POCHE_BAS_GAUCHE][BlackBall.JAUNE]    = (ImageView) findViewById(R.id.poche4BilleJauneView);
        fondBillesEmpochees[BlackBall.POCHE_BAS_GAUCHE][BlackBall.ROUGE]    = (ImageView) findViewById(R.id.poche4BilleRougeView);
        fondBillesEmpochees[BlackBall.POCHE_MILIEU_GAUCHE][BlackBall.JAUNE] = (ImageView) findViewById(R.id.poche5BilleJauneView);
        fondBillesEmpochees[BlackBall.POCHE_MILIEU_GAUCHE][BlackBall.ROUGE] = (ImageView) findViewById(R.id.poche5BilleRougeView);

        billesRestantes = new ImageView[BlackBall.NB_JOUEURS][BlackBall.NB_BILLES_COULEUR];

        billesRestantes[PREMIER_JOUEUR][0] = (ImageView) findViewById(R.id.bille0Joueur1);
        billesRestantes[PREMIER_JOUEUR][1] = (ImageView) findViewById(R.id.bille1Joueur1);
        billesRestantes[PREMIER_JOUEUR][2] = (ImageView) findViewById(R.id.bille2Joueur1);
        billesRestantes[PREMIER_JOUEUR][3] = (ImageView) findViewById(R.id.bille3Joueur1);
        billesRestantes[PREMIER_JOUEUR][4] = (ImageView) findViewById(R.id.bille4Joueur1);
        billesRestantes[PREMIER_JOUEUR][5] = (ImageView) findViewById(R.id.bille5Joueur1);
        billesRestantes[PREMIER_JOUEUR][6] = (ImageView) findViewById(R.id.bille6Joueur1);

        billesRestantes[SECOND_JOUEUR][0] = (ImageView) findViewById(R.id.bille0Joueur2);
        billesRestantes[SECOND_JOUEUR][1] = (ImageView) findViewById(R.id.bille1Joueur2);
        billesRestantes[SECOND_JOUEUR][2] = (ImageView) findViewById(R.id.bille2Joueur2);
        billesRestantes[SECOND_JOUEUR][3] = (ImageView) findViewById(R.id.bille3Joueur2);
        billesRestantes[SECOND_JOUEUR][4] = (ImageView) findViewById(R.id.bille4Joueur2);
        billesRestantes[SECOND_JOUEUR][5] = (ImageView) findViewById(R.id.bille5Joueur2);
        billesRestantes[SECOND_JOUEUR][6] = (ImageView) findViewById(R.id.bille6Joueur2);

        barreProgression = (ProgressBar) findViewById(R.id.barreProgression);
        decompte = (TextView) findViewById(R.id.decompte);
        fondCompteur = (View) findViewById(R.id.fondCompteur);
        initialiserRessourcesDeDebutdeManche();
    }

    /**
     * @brief Méthode regroupant l'ensembles des actions déclenchées par l'empochage d'une bille de couleur
     */
    private void empocherBilleCouleur(int numero, int couleur)
    {
        Log.d(TAG, "empocherBilleCouleur( numero" + numero + " couleur = " + couleur + " )");
        if(! couleursDefinies)
        {
            couleursDefinies = true;
            couleursJoueurs.put(joueurs[joueurActif], couleur);
            couleursJoueurs.put(joueurs[(joueurActif + 1) % BlackBall.NB_JOUEURS], (couleur + 1) % BlackBall.NB_GROUPES_BILLES);
            int couleurFond = couleursJoueurs.get(joueurs[joueurActif]) == BlackBall.JAUNE ? getResources().getColor(R.color.jaune) : getResources().getColor(R.color.rouge);
            fondCompteur.setBackgroundTintList(ColorStateList.valueOf(couleurFond));
            afficherBillesRestantes(couleur);
        }
        billes[couleur]--;

        //!< @todo rendre invisible
        int joueurBille = (couleursJoueurs.get(joueurs[PREMIER_JOUEUR]) == couleur) ? PREMIER_JOUEUR : SECOND_JOUEUR;
        billesRestantes[joueurBille][billes[couleur]].setVisibility(View.INVISIBLE);

        poches[numero][couleur]++;
        nbBillesEmpochees[numero][couleur].setText("" + poches[numero][couleur]);
        if(poches[numero][couleur] == 1)
        {
            fondBillesEmpochees[numero][couleur].setVisibility(View.VISIBLE);
            nbBillesEmpochees[numero][couleur].setVisibility(View.VISIBLE);
        }
    }

    private void afficherBillesRestantes(int couleurBille)
    {
        int couleur;
        for(int joueur = PREMIER_JOUEUR; joueur < BlackBall.NB_JOUEURS; joueur++)
        {
            couleur = (couleursJoueurs.get(joueurs[joueur]) == couleurBille) ? BlackBall.IMAGES_BILLES[joueur]: BlackBall.IMAGES_BILLES[(joueur + 1) % BlackBall.NB_JOUEURS];
            nomJoueurs[joueur].setTextColor(couleur);  //!<@fixme doesn't work
            for(int bille = 0; bille < billes[couleursJoueurs.get(joueurs[joueur])]; bille++)
            {
                billesRestantes[joueur][bille].setImageResource(couleur);
                billesRestantes[joueur][bille].setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * @brief Méthode regroupant l'ensembles des actions déclenchées par l'empochage d'une bille blanche
     */
    private void empocherBilleBlanche()
    {
        Log.d(TAG, "empocherBilleBlanche()");
        //!< @todo Ask client! changerTourOrNot? That's the question
    }

    /**
     * @brief Méthode regroupant l'ensembles des actions déclenchées par l'empochage d'une bille noire
     */
    private void empocherBilleNoire()
    {
        Log.d(TAG, "empocherBilleNoire()");

        int indexJoueurGagnant;
        if(couleursDefinies) {
            indexJoueurGagnant = ((manche.size() % BlackBall.NB_JOUEURS != 0 && billes[couleursJoueurs.get(joueurs[PREMIER_JOUEUR])] == 0) || (manche.size() % BlackBall.NB_JOUEURS == 0 && !(billes[couleursJoueurs.get(joueurs[PREMIER_JOUEUR])] == 0))) ? PREMIER_JOUEUR : SECOND_JOUEUR;
        }
        else {
            indexJoueurGagnant = (joueurActif + 1) % BlackBall.NB_JOUEURS;
        }

        baseDonnees.ajouterManche(joueurs, indexJoueurGagnant, manche, numeroTable);
        fenetreFinDeManche.setTitle("Partie terminée");
        fenetreFinDeManche.setMessage("Bravo " + joueurs[indexJoueurGagnant] + " !"); //@todo Mes plus froides félicitations
        fenetreFinDeManche.show();
        communication.envoyer(Protocole.ARRET);
    }

    /**
     * @brief Méthode regroupant l'ensembles des actions déclenchées par un message spécial
     */
    private void traiterTrameService(byte trame)
    {
        Log.d(TAG, "traiterTrameService( 0b" + Protocole.byteToBinaryString(trame) + ")");
        if(mancheDemarree)
        {
            joueurActif = joueurActif == PREMIER_JOUEUR ? SECOND_JOUEUR : PREMIER_JOUEUR;
            manche.add(new Vector<int[]>());
            if(couleursDefinies)
            {
                int couleurFond = (couleursJoueurs.get(joueurs[joueurActif]) == BlackBall.JAUNE) ? getResources().getColor(R.color.jaune) : getResources().getColor(R.color.rouge);
                fondCompteur.setBackgroundTintList(ColorStateList.valueOf(couleurFond));
            }
        }
        else
        {
            mancheDemarree = true;
        }
    }

    /**
     * @brief Initialise la gestion des messages en provenance des threads
     */
    private void initialiserHandler()
    {
        this.handler = new Handler(this.getMainLooper())
        {
            @Override
            public void handleMessage(@NonNull Message message)
            {
                // Log.d(TAG, "[Handler] id message = " + message.what);
                // Log.d(TAG, "[Handler] message = " + message.obj.toString());

                switch(message.what)
                {
                    case Communication.CONNEXION_BLUETOOTH:
                        Log.d(TAG, "[Handler] CONNEXION_BLUETOOTH");
                        connexionTable = true;
                        break;
                    case Communication.RECEPTION_BLUETOOTH:
                        Log.d(TAG, "[Handler] RECEPTION_BLUETOOTH");
                        Log.d(TAG, "message = 0x" + Integer.toHexString((int)message.obj));
                        compteur.redemarrer();
                        byte trame = ((Integer)message.obj).byteValue();
                        Log.d(TAG, "trame = " + trame);
                        if((trame & Protocole.MASQUE_TYPE) != 0)
                        {
                            traiterTrameService(trame);
                        }
                        else
                        {
                            int couleur = (int)(trame & Protocole.MASQUE_COULEUR);
                            int poche = (int)((trame & Protocole.MASQUE_POCHE) >> Protocole.CHAMP_POCHE);
                            int[] empoche = {poche, couleur};
                            manche.get(manche.size() - 1).add(empoche);
                            if(couleur == BlackBall.NOIRE)
                            {
                                empocherBilleNoire();
                            }
                            else if(couleur == BlackBall.BLANCHE)
                            {
                                empocherBilleBlanche();
                            }
                            else
                            {
                                empocherBilleCouleur(poche, couleur);
                            }
                        }
                        break;
                    case Communication.DECONNEXION_BLUETOOTH:
                        Log.d(TAG, "[Handler] DECONNEXION_BLUETOOTH");
                        connexionTable = false;
                        break;
                }
            }
        };
    }

    /**
     * @brief Reinitialise les attributs  et ressources au début d'une nouvelle manche
     */
    public void recommencer()
    {
        reinitialiserAttributs();
        initialiserRessourcesDeDebutdeManche();
        //!<@todo réinit compteur à faire ou déjà fait dans une des fonctions?
    }

    /**
     * @brief Reinitialise les attributs au début d'une nouvelle manche
     */
    private void reinitialiserAttributs() {
        String premierJoueur = joueurs[SECOND_JOUEUR];
        joueurs[SECOND_JOUEUR] = joueurs[PREMIER_JOUEUR];
        joueurs[PREMIER_JOUEUR] = premierJoueur;
        initialiserAttributsDeDebutDeManche();
    }

    private  void initialiserAttributsDeDebutDeManche()
    {
        Log.d(TAG, "initialiserAttributsDeDebutDeManche()");
        billes = new Integer[BlackBall.NB_JOUEURS];
        billes[BlackBall.ROUGE] = BlackBall.NB_BILLES_COULEUR;
        billes[BlackBall.JAUNE] = BlackBall.NB_BILLES_COULEUR;

        poches = new Integer[BlackBall.NB_POCHES][BlackBall.NB_JOUEURS];
        for(int numero = 0; numero < BlackBall.NB_POCHES; numero++)
        {
            poches[numero] = new Integer[2];
            Arrays.fill(poches[numero], 0);
        }

        manche = new Vector<Vector<int[]>>();
        manche.add(new Vector<int[]>());

        couleursJoueurs = new HashMap<>();
        joueurActif = PREMIER_JOUEUR;
        couleursDefinies = false;
        mancheDemarree = false;
    }

    /**
     * @brief Reinitialise les ressources au début d'une nouvelle manche
     */
    private void initialiserRessourcesDeDebutdeManche()
    {
        for (int numero = 0; numero < nbBillesEmpochees.length; numero++) {
            for (int couleur = 0; couleur < nbBillesEmpochees[numero].length; couleur++) {
                fondBillesEmpochees[numero][couleur].setVisibility(View.INVISIBLE);
                nbBillesEmpochees[numero][couleur].setVisibility(View.INVISIBLE);
                Log.d(TAG, "initialiserRessourcesdeDebutdeManche(), numero " + numero + ", couleur " + couleur);
                nbBillesEmpochees[numero][couleur].setText("0");
            }
        }
        fondCompteur.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.cyan)));
        nomJoueurs[PREMIER_JOUEUR].setText("" + joueurs[PREMIER_JOUEUR]);
        nomJoueurs[SECOND_JOUEUR].setText("" + joueurs[SECOND_JOUEUR]);
        nomJoueurs[PREMIER_JOUEUR].setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.cyan)));
        nomJoueurs[SECOND_JOUEUR].setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.cyan)));
        for(int joueur = PREMIER_JOUEUR; joueur < BlackBall.NB_JOUEURS; joueur++)
        {
            for(int bille = 0; bille < BlackBall.NB_BILLES_COULEUR; bille++)
            {
                billesRestantes[joueur][bille].setVisibility(View.INVISIBLE);
            }
        }
    }

    public void actualiserCompteur(int tempsRestant)
    {
        Drawable fond = new ColorDrawable(getResources().getColor(R.color.bleu));
        ClipDrawable clipDrawable = new ClipDrawable(fond, Gravity.LEFT, ClipDrawable.HORIZONTAL);
        clipDrawable.setLevel(tempsRestant);
        barreProgression.setProgressDrawable(clipDrawable);
        decompte.setText("" + tempsRestant);
    }
}

