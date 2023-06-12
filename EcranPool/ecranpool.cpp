/**
 * @file ecranpool.cpp
 *
 * @brief Définition de la classe EcranPool
 * @author Benjamin GAUME
 * @version 0.1
 */

#include "ecranpool.h"
#include "ui_ecranpool.h"
#include "joueurs.h"
#include "communicationbluetooth.h"
#include <QDateTime>
#include <QTimer>

using namespace std;

EcranPool* EcranPool::ecranPoolInstance = nullptr;

/**
 * @brief Constructeur de la classe EcranPool
 *
 * @fn EcranPool::EcranPool
 * @param parent nullptr définit la fenêtre principale de l'application
 */
EcranPool::EcranPool(QWidget* parent) :
    QWidget(parent), ui(new Ui::EcranPool), joueurs(nullptr),
    communicationBluetooth(new CommunicationBluetooth(this)), dureePartie(0),
    minuteurDecompte(nullptr), numeroTable(0), joueurActif(0),
    decompte(TEMPS_TOUR)
{
    qDebug() << Q_FUNC_INFO;
    initialiserCommunication();
    initialiserEcran();
    initialiserJoueurs();
    initialiserHeure();
    initialiserDecompteManche();
    couleurJoueur1 = -1;
    couleurJoueur2 = -1;

#ifdef TEST_EcranPool
    initialiserRaccourcisClavier();
#endif
}

/**
 * @brief Destructeur de la classe EcranPool
 *
 * @fn EcranPool::~EcranPool
 * @details Libère les ressources de l'application
 */
EcranPool::~EcranPool()
{
    if(joueurs != nullptr)
        delete joueurs;
    delete ui;
    qDebug() << Q_FUNC_INFO;
}

/**
 * @fn EcranPool::afficherEcran(EcranPool::Ecran ecran)
 * @brief Selectionne la fenêtre et l'affiche
 */
void EcranPool::afficherEcran(EcranPool::Ecran ecran)
{
    ui->ecrans->setCurrentIndex(ecran);
}

/**
 * @fn EcranPool::afficherEcranAcceuil(EcranPool::Ecran ecran)
 * @brief Selectionne la fenêtre d'accueil
 */
void EcranPool::afficherEcranAcceuil()
{
    afficherEcran(EcranPool::Ecran::Accueil);
    afficherHeure();
}

/**
 * @fn EcranPool::afficherEcranPartie(EcranPool::Ecran ecran)
 * @brief Selectionne la fenêtre de la partie
 */
void EcranPool::afficherEcranPartie()
{
    ui->labelAnnonceCoup->setText("");
    ui->labelAnnonceTour->setText("");
    ui->labelNomJoueurGauche->setText("");
    ui->labelNomJoueurDroite->setText("");
    decompte = TEMPS_TOUR;
    ui->labelDecompteManche->setText(QString::number(decompte));
    afficherEcran(EcranPool::Ecran::Partie);
    afficherHeure();
    afficherBillesRestantesJoueurs();
}

/**
 * @fn EcranPool::afficherEcranFinPartie(EcranPool::Ecran ecran)
 * @brief Selectionne la fenêtre de fin de partie
 */
void EcranPool::afficherEcranFinPartie()
{
    afficherEcran(EcranPool::Ecran::FinPartie);
    afficherHeure();
}

/**
 * @fn EcranPool::actualisationHeure
 * @brief Récupère et convertit l'heure en chaîne de caractères
 */
void EcranPool::afficherHeure()
{
    QString heureActuelle = QDateTime::currentDateTime().toString("hh:mm");
    // tous les écrans affichent l'heure
    labelsHeure[EcranPool::Ecran(ui->ecrans->currentIndex())]->setText(
      heureActuelle);
}

/**
 * @fn EcranPool::afficherDureePartie
 * @brief Affiche la durée de le partie
 */
void EcranPool::afficherDureePartie()
{
    ++dureePartie;
    QString dureeFormatee =
      QDateTime::fromTime_t(dureePartie).toUTC().toString("hh:mm:ss");
    ui->labelDureePartie->setText(dureeFormatee);
}

/**
 * @fn EcranPool::afficherDecompteManche
 * @brief Affiche le décompte pour jouer
 */
void EcranPool::afficherDecompteManche()
{
    // Vérifier si la page courante est "Ecran Partie"
    if(ui->ecrans->currentIndex() == EcranPool::Ecran::Partie)
    {
        // Afficher le décompte dans le QLabel
        ui->labelDecompteManche->setText(QString::number(decompte));

        // Décrémente le décompte
        --decompte;

        // Arrêter le décompte lorsque le temps est écoulé
        if(decompte < 0)
        {
            // Arrêter le minuteur
            minuteurDecompte->stop();

            // Réinitialiser le décompte pour le prochain tour
            decompte = TEMPS_TOUR;

            qDebug() << Q_FUNC_INFO << "decompte" << decompte;
        }
    }
}

// Méthodes privées

void EcranPool::initialiserCommunication()
{
    communicationBluetooth->initialiserCommunication();

    connect(communicationBluetooth,
            SIGNAL(clientConnecte()),
            this,
            SLOT(afficherEcranPartie()));
    connect(communicationBluetooth,
            SIGNAL(clientDeconnecte()),
            this,
            SLOT(afficherEcranAcceuil()));

    connect(communicationBluetooth,
            SIGNAL(empochage(int, int, int)),
            this,
            SLOT(afficherEmpochage(int, int, int)));

    connect(communicationBluetooth,
            SIGNAL(nomsJoueurs(int, QString, QString)),
            this,
            SLOT(afficherNomsJoueurs(int, QString, QString)));

    connect(communicationBluetooth,
            SIGNAL(changementJoueur(int, int)),
            this,
            SLOT(afficherChangementJoueur(int, int)));
    communicationBluetooth->demarrerCommunication();
}

/**
 * @fn EcranPool::initialiserEcran
 * @brief Initialise l'écran et affiche la fenêtre d'accueil
 */
void EcranPool::initialiserEcran()
{
    ui->setupUi(this);
    // tous les écrans affichent l'heure
    labelsHeure.push_back(ui->labelHeureAccueil);   // dans l'écran Accueil
    labelsHeure.push_back(ui->labelHeurePartie);    // dans l'écran Partie
    labelsHeure.push_back(ui->labelHeureFinPartie); // dans l'écran FinPartie
#ifdef PLEIN_ECRAN
    // setFixedSize(1920, 1080);
    showFullScreen();
#else
    showMaximized();
#endif
    afficherEcranAcceuil();
}

/**
 * @fn EcranPool::initialiserJoueurs
 * @brief Initialise l'affichage de l'heure
 */
void EcranPool::initialiserJoueurs()
{
    joueurs = new Joueurs();
    billesRestantes.push_back(NB_BILLES);
    billesRestantes.push_back(NB_BILLES);
}

/**table
 * @fn EcranPool::initialiserHeure
 * @brief Initialise l'affichage de l'heure
 */
void EcranPool::initialiserHeure()
{
    QTimer* horloge = new QTimer(this);
    connect(horloge, &QTimer::timeout, this, &EcranPool::afficherHeure);
    connect(horloge, &QTimer::timeout, this, &EcranPool::afficherDureePartie);
    horloge->start(INTERVALLE_SECONDE);
    afficherHeure();
    afficherDureePartie();
}

/**
 * @fn EcranPool::initialiserDecompteManche
 * @brief Initialise le décompte d'une manche
 */
void EcranPool::initialiserDecompteManche()
{
    minuteurDecompte = new QTimer(this);
    minuteurDecompte->start(INTERVALLE_SECONDE);
    connect(minuteurDecompte,
            SIGNAL(timeout()),
            this,
            SLOT(afficherDecompteManche()));
}

/**
 * @brief Affiche l'empochage
 */
void EcranPool::afficherEmpochage(int numeroTable, int numeroPoche, int couleur)
{
    qDebug() << Q_FUNC_INFO << "numeroTable" << numeroTable << "numeroPoche"
             << numeroPoche << "couleur" << couleur << "joueurActif"
             << joueurActif;

    // Afficher le numéro de table
    ui->labelNumeroTable->setText("Table n° " +
                                  QString::number(numeroTable + 1));



    /*if (couleur == Couleur::NOIRE)
    {
        if (joueurActif == 0)
        {
            // Joueur 1 a empoché la bille noire, affichage de l'écran de fin de partie
            ui->stackedWidget->setCurrentWidget(ui->FinPartie);
            ui->labelVainqueur->setText(nomJoueur2); // Afficher le nom du vainqueur (joueur 2)
        }
        else
        {
            // Joueur 2 a empoché la bille noire, affichage de l'écran de fin de partie
            ui->stackedWidget->setCurrentWidget(ui->FinPartie);
            ui->labelVainqueur->setText(nomJoueur1); // Afficher le nom du vainqueur (joueur 1)
        }
        return;
    }*/

    // Affiche les informations du coup qui vient d'être joué
    if(joueurActif == 0)
    {
        ui->labelAnnonceCoup->setText(
          "Bille " + EcranPool::recupererNomCouleur(couleur) +
          " dans poche n°" + QString::number(numeroPoche + 1) + " par " +
          nomJoueur1);
    }
    else
    {
        ui->labelAnnonceCoup->setText(
          "Bille " + EcranPool::recupererNomCouleur(couleur) +
          " dans poche n°" + QString::number(numeroPoche + 1) + " par " +
          nomJoueur2);
    }

    decompte = TEMPS_TOUR;

    if(couleur > Couleur::JAUNE)
        return;

    QVector<QString> couleurs;
    //couleurs.push_back("color: yellow;"); // couleur 0
    //couleurs.push_back("color: red;"); // couleur 1

    // Assigne la couleur de la première bille rentrée au joueur
    if (couleurJoueur1 == -1 && couleurJoueur2 == -1) // Vérifie si la couleur n'a pas déjà été attribuée
    {
        couleurJoueur1 = couleur;
        couleurJoueur2 = (couleurJoueur1 + 1) % 2;
        QString couleurJoueur1Style = "color: ";
        QString couleurJoueur2Style = "color: ";

        // Assigner la couleur du joueur 1 en fonction de la couleur de la bille
        if (couleurJoueur1 == Couleur::JAUNE)
        {
            couleurJoueur1Style += "yellow;";
            couleurJoueur2Style += "red;";
        }else if (couleurJoueur1 == Couleur::ROUGE){
            couleurJoueur1Style += "red;";
            couleurJoueur2Style += "yellow;";
        }
        // Assigner la couleur du joueur 2 en fonction de la couleur de la bille
        if (couleurJoueur2 == Couleur::JAUNE){
            couleurJoueur2Style += "yellow;";
            couleurJoueur1Style += "red;";
        }else if (couleurJoueur2 == Couleur::ROUGE){
            couleurJoueur2Style += "red;";
            couleurJoueur1Style += "yellow;";}

        ui->labelNomJoueurGauche->setStyleSheet(couleurJoueur1Style);
        ui->labelNomJoueurDroite->setStyleSheet(couleurJoueur2Style);
    }
}

/**
 * @brief Affiche les billes restantes de chaque joueur
 */
void EcranPool::afficherBillesRestantesJoueurs()
{
    // Afficher des billes billes restantes de chaque joueur dans les QLabel
    // respectifs
    ui->labelBillesRestantesJoueurGauche->setText(
      "Billes restantes : " + QString::number(billesRestantes[0]));
    ui->labelBillesRestantesJoueurDroite->setText(
      "Billes restantes : " + QString::number(billesRestantes[1]));
}

/**
 * @brief Affiche le nom de chaque joueur
 */
void EcranPool::afficherNomsJoueurs(int     numeroTable,
                                    QString nomJoueur1,
                                    QString nomJoueur2)
{
    qDebug() << Q_FUNC_INFO << "nomJoueur1" << nomJoueur1 << "nomJoueur2"
             << nomJoueur2;

    this->nomJoueur1 = nomJoueur1;
    this->nomJoueur2 = nomJoueur2;

    // Afficher le numéro de table
    ui->labelNumeroTable->setText("Table n° " +
                                  QString::number(numeroTable + 1));

    // Afficher les noms des joueurs dans les QLabel respectifs
    ui->labelNomJoueurGauche->setText(nomJoueur1);
    ui->labelNomJoueurDroite->setText(nomJoueur2);
}

/**
 * @brief Affiche le changement de joueur
 */
void EcranPool::afficherChangementJoueur(int numeroTable, int changementJoueur)
{
    qDebug() << Q_FUNC_INFO << "numeroTable" << numeroTable
             << "changementJoueur" << changementJoueur;

    this->joueurActif = changementJoueur;

    // Afficher le numéro de table
    ui->labelNumeroTable->setText("Table n° " +
                                  QString::number(numeroTable + 1));

    // Afficher le changement de joueur
    if(changementJoueur == 0)
    {
        ui->labelAnnonceTour->setText("C'est à " + nomJoueur1 + " de jouer !");
    }
    else
    {
        ui->labelAnnonceTour->setText("C'est à " + nomJoueur2 + " de jouer !");
    }

    decompte = TEMPS_TOUR;

    // Démarre le minuteur
    minuteurDecompte->start();
}

QString EcranPool::recupererNomCouleur(int couleur)
{
    QVector<QString> nomsCouleur = { "ROUGE",
                                     "JAUNE",
                                     "BLANCHE",
                                     "NOIRE",
                                     "INCONNUE" };

    return nomsCouleur[couleur];
}

#ifdef TEST_EcranPool
/**
 * @brief Méthode qui permet de créer des raccourcis clavier pour les tests des
 * écrans
 */
void EcranPool::initialiserRaccourcisClavier()
{
    // Flèche droite pour écran suivant
    QAction* actionAllerDroite = new QAction(this);
    actionAllerDroite->setShortcut(QKeySequence(Qt::Key_Right));
    addAction(actionAllerDroite);
    connect(actionAllerDroite,
            SIGNAL(triggered()),
            this,
            SLOT(afficherEcranSuivant()));

    // Flèche gauche pour écran précédent
    QAction* actionAllerGauche = new QAction(this);
    actionAllerGauche->setShortcut(QKeySequence(Qt::Key_Left));
    addAction(actionAllerGauche);
    connect(actionAllerGauche,
            SIGNAL(triggered()),
            this,
            SLOT(afficherEcranPrecedent()));
}

/**
 * @brief Méthode qui permet d'afficher l'écran suivant dans le QStackedWidget
 */
void EcranPool::afficherEcranSuivant()
{
    int ecran = EcranPool::Ecran(ui->ecrans->currentIndex());
    ++ecran;
    if(ecran >= int(EcranPool::NbEcrans))
        ecran = int(EcranPool::Accueil);
    afficherEcran(EcranPool::Ecran(ecran));
}

/**
 * @brief Méthode qui permet d'afficher l'écran précédent dans le QStackedWidget
 */
void EcranPool::afficherEcranPrecedent()
{
    int ecran = ui->ecrans->currentIndex();
    --ecran;
    if(ecran < int(EcranPool::Accueil))
        ecran = int(EcranPool::FinPartie);
    afficherEcran(EcranPool::Ecran(ecran));
}

#endif
