/**
 * @author Clément Trichet
 * @file Manche.java
 * @brief TODO
 */

package com.example.pluginpool;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

import java.util.ArrayList;
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

    private TextView nomJoueur1;                  //!< Affichage du nom du premier joueur
    private TextView nomJoueur2;                  //!< Affichage du nom du second joueur
    private TextView[][] nbBillesEmpochees;       //!< Affichage du nombre de billes de chaque couleur empochées dans chacune des poches
    private ImageView[][] fondBillesEmpochees;    //!< Images de fond du nombre de billes de chaque couleur empochées dans chacune des poches
    private TextView decompte;                    //!< Nombre de seconde restantes
    private  View fondCompteur;                   //!< Image de fond du compeur
    private ProgressBar barreProgression;         //!< Barre de progression du compteur

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
        nomJoueur1 = (TextView)findViewById(R.id.Joueur1);
        nomJoueur2 = (TextView)findViewById(R.id.Joueur2);

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

        barreProgression = (ProgressBar) findViewById(R.id.barreProgression);
        decompte = (TextView) findViewById(R.id.decompte);
        fondCompteur = (View) findViewById(R.id.fondCompteur);
        initialiserRessourcesdeDebutdeManche();
    }

    /**
     * @brief Méthode regroupant l'ensembles des actions déclenchées par l'empochage d'une bille de couleur
     */
    private void empocherBilleCouleur(int numero, int couleur)
    {
        if(! couleursDefinies)
        {
            couleursDefinies = true;
            couleursJoueurs.put(joueurs[joueurActif], couleur);
            couleursJoueurs.put(joueurs[(joueurActif + 1) % BlackBall.NB_JOUEURS], (couleur + 1) % BlackBall.NB_GROUPES_BILLES);
            if(couleur == BlackBall.ROUGE)
            {
                fondCompteur.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.rouge)));
            }
            else
            {
                int couleurFond = couleursJoueurs.get(joueurs[joueurActif]) == BlackBall.JAUNE ? getResources().getColor(R.color.jaune) : getResources().getColor(R.color.rouge);
                fondCompteur.setBackgroundTintList(ColorStateList.valueOf(couleurFond));
            }
        }
        poches[numero][couleur]++;
        nbBillesEmpochees[numero][couleur].setText(poches[numero][couleur]);
        if(poches[numero][couleur] == 1)
        {
            fondBillesEmpochees[numero][couleur].setVisibility(View.VISIBLE);
            nbBillesEmpochees[numero][couleur].setVisibility(View.VISIBLE);
        }
        billes[couleur]--;
        //!< @todo rendre invisible
    }

    /**
     * @brief Méthode regroupant l'ensembles des actions déclenchées par l'empochage d'une bille blanche
     */
    private void empocherBilleBlanche()
    {
        //!< @todo Ask client! changerTourOrNot? That's the question
    }

    /**
     * @brief Méthode regroupant l'ensembles des actions déclenchées par l'empochage d'une bille noire
     */
    private void empocherBilleNoire()
    {
        int indexJoueurGagnant = ((manche.size() % BlackBall.NB_JOUEURS != 0 && billes[couleursJoueurs.get(joueurs[PREMIER_JOUEUR])] == 0) || (manche.size() % BlackBall.NB_JOUEURS == 0 && !(billes[couleursJoueurs.get(joueurs[PREMIER_JOUEUR])] == 0))) ? PREMIER_JOUEUR : SECOND_JOUEUR;
        baseDonnees.ajouterManche(joueurs, indexJoueurGagnant, manche, numeroTable);
        fenetreFinDeManche.setTitle("Partie terminée");
        fenetreFinDeManche.setMessage("Bravo"); //@todo Mes plus froides félicitations
        fenetreFinDeManche.show();
        communication.envoyer(Protocole.ARRET);
    }

    /**
     * @brief Méthode regroupant l'ensembles des actions déclenchées par un message spécial
     */
    private void gererMessageSpecial(char message)
    {
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
        compteur.redemarrer();
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
                        char donneesMessage = (char)message.obj;
                        if((donneesMessage & Protocole.MASQUE_TYPE) != 0)
                        {
                            gererMessageSpecial(donneesMessage);
                        }
                        else
                        {
                            int couleur = (int)(donneesMessage & Protocole.MASQUE_COULEUR);
                            int poche = (int)((donneesMessage & Protocole.MASQUE_POCHE) >> Protocole.CHAMP_POCHE);
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
        initialiserRessourcesdeDebutdeManche();
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
    private void initialiserRessourcesdeDebutdeManche()
    {
        for (int numero = 0; numero < nbBillesEmpochees.length; numero++) {
            for (int couleur = 0; couleur < nbBillesEmpochees[numero].length; couleur++) {
                fondBillesEmpochees[numero][couleur].setVisibility(View.INVISIBLE);
                nbBillesEmpochees[numero][couleur].setVisibility(View.INVISIBLE);
                Log.d(TAG, "initialiserRessourcesdeDebutdeManche(), numero " + numero + ", couleur " + couleur);
                nbBillesEmpochees[numero][couleur].setText(0);                                                          //!<@fixme
            }
        }
        fondCompteur.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.cyan)));
        nomJoueur1.setText(joueurs[PREMIER_JOUEUR]);
        nomJoueur2.setText(joueurs[SECOND_JOUEUR]);
        nomJoueur1.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.cyan)));
        nomJoueur2.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.cyan)));
    }

    public void actualiserCompteur(int tempsRestant)
    {
        Drawable fond = new ColorDrawable(getResources().getColor(R.color.bleu));
        ClipDrawable clipDrawable = new ClipDrawable(fond, Gravity.LEFT, ClipDrawable.HORIZONTAL);
        clipDrawable.setLevel(tempsRestant);
        barreProgression.setProgressDrawable(clipDrawable);
        decompte.setText(tempsRestant);
    }
}

