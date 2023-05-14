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
#include <QDateTime>
#include <QTimer>

/**
 * @brief Constructeur de la classe EcranPool
 *
 * @fn EcranPool::EcranPool
 * @param parent nullptr définit la fenêtre principale de l'application
 */
EcranPool::EcranPool(QWidget* parent) :
    QWidget(parent), ui(new Ui::EcranPool), joueurs(nullptr)
{
    qDebug() << Q_FUNC_INFO;
    initialiserEcran();
    initialiserHeure();
    initialiserJoueurs();
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
}

/**
 * @fn EcranPool::afficherEcranPartie(EcranPool::Ecran ecran)
 * @brief Selectionne la fenêtre de la partie
 */
void EcranPool::afficherEcranPartie()
{
    afficherEcran(EcranPool::Ecran::Partie);
}

/**
 * @fn EcranPool::afficherEcranFinPartie(EcranPool::Ecran ecran)
 * @brief Selectionne la fenêtre de fin de partie
 */
void EcranPool::afficherEcranFinPartie()
{
    afficherEcran(EcranPool::Ecran::FinPartie);
}

/**
 * @fn EcranPool::actualisationHeure
 * @brief Récupère et convertit l'heure en chaîne de caractères
 */
void EcranPool::actualiserHeure()
{
    QDateTime heureActuelle = QDateTime::currentDateTime();
    QString   timeString    = heureActuelle.toString("hh:mm");
    ui->labelHeure->setText(timeString);
}

// Méthodes privées

/**
 * @fn EcranPool::initialiserEcran
 * @brief Initialise l'écran et affiche la fenêtre d'accueil
 */
void EcranPool::initialiserEcran()
{
    ui->setupUi(this);
#ifdef PLEIN_ECRAN
    showFullScreen();
#else
    showMaximized();
#endif
    afficherEcranAcceuil();
}

/**
 * @fn EcranPool::initialiserHeure
 * @brief Initialise l'affichage de l'heure
 */
void EcranPool::initialiserHeure()
{
    QTimer* horloge = new QTimer(this);
    connect(horloge, &QTimer::timeout, this, &EcranPool::actualiserHeure);
    horloge->start(INTERVALLE_SECONDE);
    actualiserHeure();
}

/**
 * @fn EcranPool::initialiserJoueurs
 * @brief Initialise l'affichage de l'heure
 */
void EcranPool::initialiserJoueurs()
{
    joueurs = new Joueurs();
}
