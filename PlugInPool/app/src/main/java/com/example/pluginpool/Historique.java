/**
 * @author Clément Trichet
 * @file Historique.java
 * @brief Activité de recherche et d'affichage des données enregistrées
 */

package com.example.pluginpool;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import java.util.Vector;

/**
 * @class Manche
 * @brief L'activité d'accès à l'historique des manches effectuées et des statistiques des joueurs
 */
public class Historique extends AppCompatActivity {

    /**
     * Constantes
     */
    private static final String TAG = "_Historique"; //!< TAG pour les logs
    private static final String[]  CATEGORIES = {"Joueurs", "Manches"};

    /**
     * Attributs
     */
    private ArrayAdapter<String> adaptateurCategoriesRecherche;  //!< @todo
    private Vector<String> noms;                //!< @todo
    private Vector<String> nomsRecherches;      //!< @todo
    private Vector<String> manches;             //!< @todo
    private Vector<String> manchesRecherchees;  //!< @todo
    private InputFilter[] filtresRecherche;     //!< @todo
    private HistoriqueJoueur fenetreJoueur;     //!< @todo
    private HistoriqueManche fenetreManche;     //!< @todo
    public BaseDeDonnees           baseDonnees;        //!< Classe d'échange avec la base de donnees

    /**
     * Ressources GUI
     */
    private ListView listeDeroulante;                       //!< @todo
    private ArrayAdapter<String> adaptateurListeDeroulante; //!< @todo
    private EditText barreRecherche;                        //!< @todo
    private Spinner categoriesRecherche;                    //!< @todo
    private ImageButton boutonMenu;                         //!< @todo
    private ImageButton boutonRechercher;                   //!< @todo
    private ImageButton boutonEffacer;                      //!< @todo

    /**
     * @brief Méthode appelée à la création de l'activité
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.historique);
        initialiserAttributs();
        initialiserRessources();
    }

    /**
     * @brief Méthode initialisant les attributs de la classe
     */
    private void initialiserAttributs()
    {
        Log.d(TAG, "initialiserAttributs");
        baseDonnees = BaseDeDonnees.getInstance(this);
        noms = baseDonnees.getNomsJoueursTries();
        nomsRecherches = new Vector<>();
        nomsRecherches.addAll(noms);
        manches = baseDonnees.getManchesTriees();
        manchesRecherchees = new Vector<>();
        manchesRecherchees.addAll(manches);

        adaptateurListeDeroulante = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, nomsRecherches);
        adaptateurCategoriesRecherche =
                new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, CATEGORIES);

        filtresRecherche = new InputFilter[] {
            new InputFilter() {
                public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend)
                {
                    if(source.length() < 1) return null;
                    for(int i = start; i < end; i++)
                    {
                        char lettre = source.charAt(i);
                        if(!(Character.isLetter(lettre) || (lettre == ' ' && i != 0) || lettre == '/'))
                        {
                            return "";
                        }
                    }
                    return null;
                }
            }
        };
        fenetreJoueur = null;
        fenetreManche = null;
    }

    /**
     * @brief Méthode initialisant les ressources graphiques de l'activité
     */
    private void initialiserRessources()
    {
        Log.d(TAG, "initialiserRessources");
        listeDeroulante = (ListView) findViewById(R.id.listView);
        adaptateurListeDeroulante = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, nomsRecherches);
        listeDeroulante.setAdapter(adaptateurListeDeroulante);
        listeDeroulante.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(categoriesRecherche.getSelectedItem().toString() == "Joueurs")
                {
                    afficherFenetreJoueur(parent.getItemAtPosition(position).toString());
                }
                else
                {
                    afficherFenetreManche(parent.getItemAtPosition(position).toString());
                }
            }
        });

        barreRecherche = (EditText) findViewById(R.id.barreRecherche);

        categoriesRecherche = (Spinner) findViewById(R.id.categorieRecherche);
        categoriesRecherche.setAdapter(adaptateurCategoriesRecherche);
        categoriesRecherche.setSelection(0);
        categoriesRecherche.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            String elementSelectionne = parent.getItemAtPosition(position).toString();
            Log.d(TAG, "clic categoriesRecherche : position = " + position + " -> " + elementSelectionne);
            if(elementSelectionne == "Joueurs")
            {
                adaptateurListeDeroulante = new ArrayAdapter<>(parent.getContext(), android.R.layout.simple_list_item_1, nomsRecherches);
                listeDeroulante.setAdapter(adaptateurListeDeroulante);
            }
            else
            {
                adaptateurListeDeroulante = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1);
                for(int manche = 0; manche < manchesRecherchees.size(); manche++)
                {
                    adaptateurListeDeroulante.add(manchesRecherchees.get(manche) + baseDonnees.getNomsJoueurs(manchesRecherchees.get(manche)));
                }
               listeDeroulante.setAdapter(adaptateurListeDeroulante);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent)
        {
            // Do nothing
        }
    });

        boutonMenu = (ImageButton) findViewById(R.id.boutonMenu);
        boutonMenu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                Log.d(TAG, "clic boutonMenu");
                finish();
            }
        });

        boutonRechercher = (ImageButton) findViewById(R.id.boutonRechercher);
        boutonRechercher.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                Log.d(TAG, "clic boutonRechercher");
                if(categoriesRecherche.getSelectedItem().toString() == "Joueurs")
                {
                    rechercherJoueurs();
                }
                else
                {
                    rechercherManches();
                }
            }
        });

        boutonEffacer = (ImageButton) findViewById(R.id.boutonEffacer);
        boutonEffacer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                Log.d(TAG, "clic boutonRechercher");
                if(categoriesRecherche.getSelectedItem().toString() == "Joueurs")
                {
                    supprimerJoueurs();
                }
                else
                {
                    supprimerManches();
                }
            }
        });
    }

    /**
     * @brief todo
     */
    private void supprimerJoueurs()
    {
        baseDonnees.supprimerJoueurs();
        noms = new Vector<>();
        adaptateurListeDeroulante = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, noms);
        listeDeroulante.setAdapter(adaptateurListeDeroulante);
    }

    /**
     * @brief todo
     */
    private void supprimerManches()
    {
        baseDonnees.supprimerManches();
        manches = new Vector<>();
        adaptateurListeDeroulante = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, manches);
        listeDeroulante.setAdapter(adaptateurListeDeroulante);
    }


    /**
     * @brief Méthode permettant de rechercher un joueur par son nom et d'afficher les résultats de la recherche dans la liste déroulante
     */
    private void rechercherJoueurs()
    {
        nomsRecherches.addAll(noms);
        boolean nomSupprime;
        String[] mots = barreRecherche.getText().toString().split("\\s+");

        int indiceJoueur = 0;
        int indiceMot = 0;
        while(indiceJoueur < nomsRecherches.size())
        {
            indiceMot = 0;
            nomSupprime = false;
            while((!nomSupprime) && indiceMot < mots.length)
            {
                if(nomsRecherches.get(indiceJoueur).contains(mots[indiceMot]))
                {
                    indiceMot++;
                }
                else
                {
                    nomSupprime = true;
                    nomsRecherches.remove(indiceJoueur);
                }
            }
            indiceJoueur += nomSupprime ? 1 : 0;
        }
        if(noms.size() != nomsRecherches.size() && nomsRecherches.size() != 0)
        {
            adaptateurListeDeroulante = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, nomsRecherches);
            listeDeroulante.setAdapter(adaptateurListeDeroulante);
        }
    }

    /**
     * @brief Méthode permettant de rechercher une manche par sa date et par le noms des joueurs participants et d'afficher les résultats de la recherche dans la liste déroulante
     */
    private void rechercherManches()
    {
        manchesRecherchees.addAll(manches);
        boolean nomSupprime;
        int indiceManche = 0;
        while(indiceManche < manchesRecherchees.size())
        {
            nomSupprime = false;
            if(! manchesRecherchees.get(indiceManche).contains(barreRecherche.getText().toString()))
            {
                manchesRecherchees.remove(indiceManche);
            }
            else
            {
                indiceManche++;
            }
        }
        if(manches.size() != manchesRecherchees.size() && manchesRecherchees.size() != 0)
        {
            adaptateurListeDeroulante = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, manchesRecherchees);
            listeDeroulante.setAdapter(adaptateurListeDeroulante);
        }
    }

    /**
     * @brief @todo
     */
    private void afficherFenetreJoueur(String joueur)
    {
        fenetreJoueur = new HistoriqueJoueur(this, joueur);
        fenetreJoueur.getWindow().setLayout(1200, 1500);
        fenetreJoueur.show();
    }

    /**
     * @brief @todo
     */
    private void afficherFenetreManche(String dateEtJoueurs)
    {
        //!< @todo oter joueurs de dateEtJoueurs
        fenetreManche = new HistoriqueManche(this, dateEtJoueursVersDate(dateEtJoueurs));
        fenetreManche.getWindow().setLayout(1200, 1500);
        fenetreManche.show();
    }

    /**
     * @brief @todo
     */
    public void actualiserNoms(String nom)
    {
        noms.remove(nom);
        nomsRecherches.remove(nom);
        adaptateurListeDeroulante.remove(nom);
        listeDeroulante.setAdapter(adaptateurListeDeroulante);
        manches = baseDonnees.getManchesTriees();
        manchesRecherchees.addAll(manches);
    }

    /**
     * @brief @todo
     */
    public void actualiserManches(String dateEtJoueur)
    {
        manches.remove(dateEtJoueur);
        manchesRecherchees.remove(dateEtJoueur);
        adaptateurListeDeroulante.remove(dateEtJoueur);
        listeDeroulante.setAdapter(adaptateurListeDeroulante);
    }

    /**
     * @brief @todo
     */
    private String dateVersDateEtJoueurs(String date)
    {
        return date + baseDonnees.getNomsJoueurs(date);
    }

    /**
     * @brief @todo
     */
    private String dateEtJoueursVersDate(String dateEtJoueurs)
    {
        return dateEtJoueurs.substring(0, 19);
    }
}