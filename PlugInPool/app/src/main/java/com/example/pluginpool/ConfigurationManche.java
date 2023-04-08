/**
 * @file ConfigurationManche.java
 * @brief Déclaration de l'activité de configuration d'une manche
 * @author Clément TRICHET
 */

package com.example.pluginpool;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * @class ConfigurationManche
 * @brief L'activité de configuration d'une manche
 */
public class ConfigurationManche extends AppCompatActivity
{
    /**
     * Constantes
     */
    private static final String TAG = "_ConfigurationManche"; //!< TAG pour les logs

    /**
     * Attributs
     */
    ArrayList<String> nomsJoueurs;
    ArrayAdapter<String> adaptateur;
    InputFilter[] filtresNom;

    /**
     * Ressources GUI
     */
    private Spinner joueur1Spinner;         //!< Spinner permettant de choisir le premier joueur parmi la liste des joueurs enregistrés
    private Spinner joueur2Spinner;         //!< Spinner permettant de choisir le second joueur parmi la liste des joueurs enregistrés
    private EditText joueur1Edit;           //!< Zone permettant de saisir le nom du premier joueur
    private EditText joueur2Edit;           //!< Zone permettant de saisir le nom du premier joueur
    private ImageButton boutonActualiser;   //!< Bouton permettant de rechercher les tables disponibles
    private RadioButton boutonTable1;       //!< Bouton permettant de sélectionner la table 1 pour s'y connecter
    private RadioButton boutonTable2;       //!< Bouton permettant de sélectionner la table 2 pour s'y connecter
    private RadioButton boutonTable3;       //!< Bouton permettant de sélectionner la table 3 pour s'y connecter
    private RadioButton boutonTable4;       //!< Bouton permettant de sélectionner la table 4 pour s'y connecter
    private Button boutonSuivant;           //!< Bouton permettant de passer à l'activité de suivi de partie "Manche"

    /**
     * @brief Méthode appelée à la création de l'activité
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration_manche);
        Log.d(TAG, "onCreate()");
        initialiserAttributs();
        initialiserRessources();
    }

    /**
     * @brief Initialise les attributs de l'activité
     */
    private void initialiserAttributs()
    {
        nomsJoueurs = new ArrayList<>();
        //!< @todo initialiser nomsJoueur à partir de la base de donnees
        nomsJoueurs.add("Robert");  // Provisoire
        nomsJoueurs.add("Lulu");    // Provisoire
        adaptateur = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, nomsJoueurs);
        filtresNom = new InputFilter[]
        {
            new InputFilter()
            {
                public CharSequence filter(CharSequence source, int start, int end,
                                           Spanned dest, int dstart, int dend)
                {
                    for (int i = start; i < end; i++)
                    {
                        char lettre = source.charAt(i);
                        if (! (Character.isLetter(lettre) || lettre == ' ' ))
                        {
                            return "";
                        }
                    }
                    return null;
                }
            },
            new InputFilter()
            {
                public CharSequence filter(CharSequence source, int start, int end,
                                           Spanned dest, int dstart, int dend)
                {
                    char premiereLettre = source.charAt(0);
                    if (premiereLettre == ' ')
                    {
                        return "";
                    }
                    return null;
                }
            }
        };
    }

    /**
     * @brief Initialise les ressources graphiques de l'activité
     */
    private void initialiserRessources()
    {
        joueur1Spinner = (Spinner)findViewById(R.id.joueur1Spinner);
        joueur2Spinner = (Spinner)findViewById(R.id.joueur2Spinner);
        joueur1Edit = (EditText)findViewById(R.id.joueur1Edit);
        joueur2Edit = (EditText)findViewById(R.id.joueur2Edit);
        boutonActualiser = (ImageButton)findViewById(R.id.boutonActualiser);
        RadioGroup groupeBoutonsRadios = findViewById(R.id.groupeBoutonsRadio);
        boutonTable1 = (RadioButton)findViewById(R.id.boutonTable1);
        boutonTable2 = (RadioButton)findViewById(R.id.boutonTable2);
        boutonTable3 = (RadioButton)findViewById(R.id.boutonTable3);
        boutonTable4 = (RadioButton)findViewById(R.id.boutonTable4);
        boutonSuivant = (Button)findViewById(R.id.boutonSuivant);

        joueur1Spinner.setAdapter(adaptateur);
        joueur2Spinner.setAdapter(adaptateur);
        joueur1Spinner.setSelection(-1);
        joueur2Spinner.setSelection(-1);

        joueur1Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String nomSelectionne = parent.getItemAtPosition(position).toString();
                joueur1Edit.setText(nomSelectionne);
                joueur1Spinner.performClick();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                // Do nothing
            }
        });


        joueur2Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String nomSelectionne = parent.getItemAtPosition(position).toString();
                joueur2Edit.setText(nomSelectionne);
                joueur2Spinner.performClick();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                // Do nothing
            }
        });


        joueur1Edit.setHint("Saisir le nom du premier joueur");
        joueur2Edit.setHint("Saisir le nom du second joueur");

        joueur1Edit.setFilters(filtresNom);
        joueur2Edit.setFilters(filtresNom);

        boutonActualiser.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Log.d(TAG, "clic boutonActualiser");
                //!< @todo rafraichir tables en attente de connexion
            }
        });

        groupeBoutonsRadios.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup groupe, int checkedId)
            {
                //!< @todo seConnecter(checkedId);
            }
        });

        boutonSuivant.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Log.d(TAG, "clic boutonSuivant");
                if(groupeBoutonsRadios.getCheckedRadioButtonId() != -1 && joueur1Edit.getText().toString() != "" && joueur2Edit.getText().toString() != "" && joueur1Edit.getText().toString() != joueur2Edit.getText().toString())
                {
                    //!< @todo enregistrer les noms, instancier GestionManche?
                    Intent activiteManche = new Intent(ConfigurationManche.this, Manche.class);
                    startActivity(activiteManche);
                }
                else
                {
                    //!< @todo afficher un message d'erreur ?
                }
            }
        });
    }
}