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

EcranPool* EcranPool::ecranPoolInstance = nullptr;

/**
 * @brief Constructeur de la classe EcranPool
 *
 * @fn EcranPool::EcranPool
 * @param parent nullptr définit la fenêtre principale de l'application
 */
EcranPool::EcranPool(QWidget* parent) :
    QWidget(parent), ui(new Ui::EcranPool), joueurs(nullptr),
    communicationBluetooth(new CommunicationBluetooth(this)), dureePartie(0)
{
    qDebug() << Q_FUNC_INFO;
    initialiserCommunication();
    initialiserEcran();
    initialiserJoueurs();
    initialiserHeure();
    initialiserDecompteManche();
    afficherNomsJoueurs(numeroTable, nomJoueur1, nomJoueur2);
    afficherChangementJoueur(numeroTable, changementJoueur, nomJoueur1, nomJoueur2);

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
    int numeroTable = 0;
    afficherEcran(EcranPool::Ecran::Partie);
    afficherHeure();
    // ecranPoolInstance->afficherNumeroTable(numeroTable);
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
        static int decompte      = 45; // décompte initial de 45 secondes
        QString    texteDecompte = QString::number(
          decompte); // Convertir le décompte en chaîne de caractères

        // Afficher le décompte dans le QLabel
        ui->labelDecompteManche->setText(texteDecompte);

        // Décrémente le décompte
        --decompte;

        // Arrêter le décompte lorsque le temps est écoulé
        if(decompte < 0)
        {
            // Arrêter le minuteur associé à la méthode
            QTimer* minuteur = qobject_cast<QTimer*>(sender());
            minuteur->stop();

            // Réinitialiser le décompte pour la prochaine manche
            decompte = 45;
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
            SLOT(afficherChangementJoueur(int, int, QString, QString)));
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
    QTimer* decompte = new QTimer(this);
    decompte->start(INTERVALLE_SECONDE);
    connect(decompte, SIGNAL(timeout()), this, SLOT(afficherDecompteManche()));
}

/**
 * @brief Affiche l'empochage
 */
void EcranPool::afficherEmpochage(int numeroTable, int numeroPoche, int couleur)
{
    qDebug() << Q_FUNC_INFO << "numeroTable" << numeroTable;
    QString texte = "Table n° " + QString::number(numeroTable);
    ui->labelNumeroTable->setText("Table n° " + QString::number(numeroTable));
    ui->labelNumeroTable->setText(texte);
    qDebug() << Q_FUNC_INFO << "numeroTable" << texte;
    /**
     * @todo Afficher la couleur et le numéro de poche
     */
}

/**
 * @brief Affiche le nom de chaque joueur
 */
void EcranPool::afficherNomsJoueurs(int numeroTable, QString nomJoueur1, QString nomJoueur2)
{
    qDebug() << Q_FUNC_INFO << "nomJoueur1" << nomJoueur1 << "nomJoueur2"
             << nomJoueur2;

    // Afficher le numéro de table
    QString texte = "Table n° " + QString::number(numeroTable);
    ui->labelNumeroTable->setText("Table n° " + QString::number(numeroTable));
    ui->labelNumeroTable->setText(texte);

    // Afficher les noms des joueurs dans les QLabel respectifs
    ui->labelNomJoueurGauche->setText(nomJoueur1);
    ui->labelNomJoueurDroite->setText(nomJoueur2);
}

/**
 * @brief Affiche le changement de joueur
 */
void EcranPool::afficherChangementJoueur(int numeroTable, int changementJoueur, QString nomJoueur1, QString nomJoueur2)
{
    // Afficher le numéro de table
    QString texte = "Table n° " + QString::number(numeroTable);
    ui->labelNumeroTable->setText("Table n° " + QString::number(numeroTable));
    ui->labelNumeroTable->setText(texte);

    changementJoueur = 0;
    // Afficher le changement de joueur
    if(changementJoueur == 0)
    {
        ui->labelAnnonceTour->setText(nomJoueur1);
    }
    else
    {
        ui->labelAnnonceTour->setText(nomJoueur2);
    }
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
