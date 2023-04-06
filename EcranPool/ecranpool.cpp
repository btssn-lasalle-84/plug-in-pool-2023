/**
 * @file ecranpool.cpp
 *
 * @brief Définition de la classe EcranPool
 * @author Benjamin GAUME
 * @version 0.1
 */

#include "ecranpool.h"
#include "ui_ecranpool.h"

/**
 * @brief Constructeur de la classe EcranPool
 *
 * @fn EcranPool::EcranPool
 * @param parent nullptr définit la fenêtre principale de l'application
 */
EcranPool::EcranPool(QWidget* parent) : QWidget(parent), ui(new Ui::EcranPool)
{
    qDebug() << Q_FUNC_INFO;
    initialiserEcran();
}

/**
 * @brief Destructeur de la classe EcranPool
 *
 * @fn EcranPool::~EcranPool
 * @details Libère les ressources de l'application
 */
EcranPool::~EcranPool()
{
    delete ui;
    qDebug() << Q_FUNC_INFO;
}

/**
 * @fn EcranPool::afficherEcran(Basketgame::Ecran ecran)
 * @brief Selectionne la fenêtre et l'affiche
 */
void EcranPool::afficherEcran(EcranPool::Ecran ecran)
{
    ui->ecrans->setCurrentIndex(ecran);
}

void EcranPool::afficherEcranAcceuil()
{
    afficherEcran(EcranPool::Ecran::Accueil);
}

void EcranPool::afficherEcranPartie()
{
    afficherEcran(EcranPool::Ecran::Partie);
}

void EcranPool::afficherEcranFinPartie()
{
    afficherEcran(EcranPool::Ecran::FinPartie);
}

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
