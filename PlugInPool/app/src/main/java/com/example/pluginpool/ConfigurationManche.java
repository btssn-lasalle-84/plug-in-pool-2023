/**
 * @file ConfigurationManche.java
 * @brief Déclaration de l'activité de configuration d'une manche
 * @author Clément TRICHET
 */

package com.example.pluginpool;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
    private ArrayList<String>    nomsJoueurs;           //!< Tableau contenant le nom des joueurs déjà enregistrés
    private ArrayAdapter<String> adaptateurNomsJoueurs; //!< Adaptateur pour l'affichage du nom des joueurs déjà enregistrés
    private InputFilter[] filtresNom;                   //!< Filtre les caractères non admis dans le nom d'un joueur
    private String  choixNomTable  = "Aucune";
    Communication   communication  = null;              //!< Classe de communication Bluetooth
    private Boolean connexionTable = false;
    private Handler handler        = null;              //!< Handler permettant la communication entre le thread de réception bluetooth et celui de l'interface graphique

    /**
     * Ressources GUI
     */
    private Spinner choixNomsJoueur1; //!< Spinner permettant de choisir le premier joueur parmi la
                                      //!< liste des joueurs enregistrés
    private Spinner choixNomsJoueur2; //!< Spinner permettant de choisir le second joueur parmi la
                                      //!< liste des joueurs enregistrés
    private EditText editionNomJoueur1; //!< Zone permettant de saisir le nom du premier joueur
    private EditText editionNomJoueur2; //!< Zone permettant de saisir le nom du premier joueur
    private RadioButton
      boutonTable1; //!< Bouton permettant de sélectionner la table 1 pour s'y connecter
    private RadioButton
      boutonTable2; //!< Bouton permettant de sélectionner la table 2 pour s'y connecter
    private RadioButton
      boutonTable3; //!< Bouton permettant de sélectionner la table 3 pour s'y connecter
    private RadioButton
      boutonTable4; //!< Bouton permettant de sélectionner la table 4 pour s'y connecter
    private ImageButton
      boutonActualiser; //!< Bouton permettant de rechercher les tables disponibles
    private Button
      boutonSuivant; //!< Bouton permettant de passer à l'activité de suivi de partie "Manche"

    /**
     * @brief Méthode appelée à la création de l'activité
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configuration_manche);
        Log.d(TAG, "onCreate()");
        initialiserHandler();
        initialiserAttributs();
        initialiserRessources();
    }

    /**
     * @brief Initialise les attributs de l'activité
     */
    private void initialiserAttributs()
    {
        nomsJoueurs   = new ArrayList<>();
        communication = Communication.getInstance(handler);
        //!< @todo initialiser nomsJoueur à partir de la base de donnees
        nomsJoueurs.add("Robert"); // Provisoire
        nomsJoueurs.add("Lulu");   // Provisoire
        nomsJoueurs.add("Roger");  // Provisoire
        adaptateurNomsJoueurs =
          new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, nomsJoueurs);
        filtresNom = new InputFilter[] { new InputFilter() {
          public CharSequence
            filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend){
                if(source.length() < 1) return null;
                for(int i = start; i < end; i++)
                {
                    char lettre = source.charAt(i);
                    if(!(Character.isLetter(lettre) || lettre == ' '))
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
     * @brief Initialise les ressources graphiques de l'activité
     */
    private void initialiserRessources()
    {
        choixNomsJoueur1      = (Spinner)findViewById(R.id.joueur1Spinner);
        choixNomsJoueur2      = (Spinner)findViewById(R.id.joueur2Spinner);
        editionNomJoueur1     = (EditText)findViewById(R.id.joueur1Edit);
        editionNomJoueur2     = (EditText)findViewById(R.id.joueur2Edit);
        RadioGroup choixTable = findViewById(R.id.groupeBoutonsTables);
        boutonTable1          = (RadioButton)findViewById(R.id.boutonTable1);
        boutonTable2          = (RadioButton)findViewById(R.id.boutonTable2);
        boutonTable3          = (RadioButton)findViewById(R.id.boutonTable3);
        boutonTable4          = (RadioButton)findViewById(R.id.boutonTable4);
        boutonActualiser      = (ImageButton)findViewById(R.id.boutonActualiser);
        boutonSuivant         = (Button)findViewById(R.id.boutonSuivant);

        choixNomsJoueur1.setAdapter(adaptateurNomsJoueurs);
        choixNomsJoueur2.setAdapter(adaptateurNomsJoueurs);
        choixNomsJoueur1.setSelection(0);
        choixNomsJoueur2.setSelection(1);

        choixNomsJoueur1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String nomSelectionne = parent.getItemAtPosition(position).toString();
                Log.d(TAG, "clic choixNomJoueur1 : position = " + position + " -> " + nomSelectionne);
                editionNomJoueur1.setText(nomSelectionne);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                // Do nothing
            }
        });

        choixNomsJoueur2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String nomSelectionne = parent.getItemAtPosition(position).toString();
                Log.d(TAG, "clic choixNomJoueur2 : position = " + position + " -> " + nomSelectionne);
                editionNomJoueur2.setText(nomSelectionne);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                // Do nothing
            }
        });

        editionNomJoueur1.setHint("Saisir le nom du premier joueur");
        editionNomJoueur2.setHint("Saisir le nom du second joueur");

        editionNomJoueur1.setFilters(filtresNom);
        editionNomJoueur2.setFilters(filtresNom);

        boutonActualiser.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                Log.d(TAG, "clic boutonActualiser");
                //!< @todo rafraichir tables en attente de connexion
            }
        });

        choixTable.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup groupe, int checkedId)
            {
                RadioButton boutonTable = (RadioButton)findViewById(groupe.getCheckedRadioButtonId());
                choixNomTable           = boutonTable.getText().toString();
                Log.d(TAG, "clic choixTable : " + choixNomTable);
                //!< @todo seConnecter() à choixNomTable
                communication.seConnecter("pool-1");
                // connexionTable = true; // Provisoire pour les tests
            }
        });

        boutonSuivant.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                Log.d(TAG, "clic boutonSuivant");
                if(estConfiguree())
                {
                    Log.d(TAG,
                          "Manche : " + editionNomJoueur1.getText().toString() + " vs " +
                            editionNomJoueur2.getText().toString());
                    Log.d(TAG, "Table : " + choixNomTable);
                    //!< @todo enregistrer les noms, instancier GestionManche?
                    Intent activiteManche = new Intent(ConfigurationManche.this, Manche.class);
                    activiteManche.putExtra("joueur1", editionNomJoueur1.getText().toString());
                    activiteManche.putExtra("joueur2", editionNomJoueur2.getText().toString());
                    startActivity(activiteManche);
                }
                else
                {
                    //!< @todo afficher un message d'erreur ?
                }
            }
        });
    }

    private Boolean estConfiguree()
    {
        Log.d(TAG, "estConfiguree() connexionTable = " + connexionTable);
        if(!connexionTable)
            return false;
        Log.d(TAG,
              "estConfiguree() joueur1 = " + !editionNomJoueur1.getText().toString().isEmpty() +
                " - joueur2 = " + !editionNomJoueur2.getText().toString().isEmpty());
        if(editionNomJoueur1.getText().toString().isEmpty() ||
           editionNomJoueur2.getText().toString().isEmpty())
            return false;
        Log.d(TAG,
              "estConfiguree() joueur1 != joueur2 = " +
                !editionNomJoueur1.getText().toString().equals(editionNomJoueur2.getText().toString()));
        if(editionNomJoueur1.getText().toString().equals(editionNomJoueur2.getText().toString()))
            return false;
        return true;
    }

    /**
     * @brief Initialise la gestion des messages en provenance des threads
     */
    private void initialiserHandler()
    {
        this.handler = new Handler(this.getMainLooper()) {
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
                        // Test
                        // communication.envoyer(Protocole.TRAME_START);
                        break;
                    case Communication.RECEPTION_BLUETOOTH:
                        Log.d(TAG, "[Handler] RECEPTION_BLUETOOTH");
                        Log.d(TAG, "message = 0x" + Integer.toHexString((int)message.obj));
                        break;
                    case Communication.DECONNEXION_BLUETOOTH:
                        Log.d(TAG, "[Handler] DECONNEXION_BLUETOOTH");
                        connexionTable = false;
                        break;
                }
            }
        };
    }
}