/**
 * @author Clément Trichet
 * @file Historique.java
 * @brief TODO
 */

package com.example.pluginpool;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
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
    private BaseDeDonnees           baseDonnees;        //!< Classe d'échange avec la base de donnees

    /**
     * Ressources GUI
     */
    private ListView listeDeroulante;                       //!< @todo
    private ArrayAdapter<String> adaptateurListeDeroulante; //!< @todo
    private EditText barreRecherche;                        //!< @todo
    private Spinner categoriesRecherche;                    //!< @todo
    private Button boutonMenu;                              //!< @todo
    private ImageButton boutonRechercher;                   //!< @todo
    /**
     * @brief Méthode appelée à la création de l'activité
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        filtresRecherche = new InputFilter[] { new InputFilter() {
            public CharSequence
            filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend){
                if(source.length() < 1) return null;
                for(int i = start; i < end; i++)
                {
                    char lettre = source.charAt(i);
                    if(!(Character.isLetter(lettre) || lettre == ' ' || lettre == '/'))
                    {
                        return "";
                    }
                }
                return null;
            }
        }
                , new InputFilter() {
            public CharSequence filter(CharSequence source,
                                       int          start,
                                       int          end,
                                       Spanned      dest,
                                       int          dstart,
                                       int          dend)
            {
                if(source.length() < 1)
                    return null;
                char premiereLettre = source.charAt(0);
                if(premiereLettre == ' ')
                {
                    return "";
                }
                return null;
            }
        }
        };
    }

    /**
     * @brief Méthode initialisant les ressources graphiques de l'activité
     */
    private void initialiserRessources()
    {
        listeDeroulante = (ListView) findViewById(R.id.listView);
        adaptateurListeDeroulante = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, nomsRecherches);
        listeDeroulante.setAdapter(adaptateurListeDeroulante);

        barreRecherche = (EditText) findViewById(R.id.barreRecherche);
        categoriesRecherche = (Spinner) findViewById(R.id.categorieRecherche);

        boutonMenu = (Button) findViewById(R.id.boutonMenu);
        boutonMenu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                Log.d(TAG, "clic boutonMenu");
                Intent activiteEcranPrincipal = new Intent(Historique.this, EcranPrincipal.class);
                startActivity(activiteEcranPrincipal);
            }
        });

        boutonRechercher = (ImageButton) findViewById(R.id.boutonRechercher);
        boutonMenu.setOnClickListener(new View.OnClickListener() {
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
    }

    /**
     * @brief Méthode permettant de rechercher un joueur par son nom et d'afficher les résultats de la recherche dans la liste déroulante
     */
    private void rechercherJoueurs()
    {
        nomsRecherches.addAll(noms);
        int logueurInitiale = nomsRecherches.size();
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
                if(nomsRecherches.contains(mots[indiceMot]))
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
        if(logueurInitiale != nomsRecherches.size())
        {
            adaptateurListeDeroulante = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, nomsRecherches);
            listeDeroulante.setAdapter(adaptateurListeDeroulante);
        }
    }

    /**
     * @brief Méthode permettant de rechercher une manche par sa date et d'afficher les résultats de la recherche dans la liste déroulante
     */
    private void rechercherManches()
    {
        //!<@todo
    }
}