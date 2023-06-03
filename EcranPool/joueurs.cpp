/**
 * @file joueurs.cpp
 *
 * @brief Définition de la classe Joueurs
 * @author Benjamin GAUME
 * @version 1.0
 */

#include "joueurs.h"
#include <QDebug>

/**
 * @brief Constructeur de la classe Joueurs
 *
 * @fn Joueurs::Joueurs
 */
Joueurs::Joueurs()
{
    qDebug() << Q_FUNC_INFO;
}

Joueurs::Joueurs(QVector<QString> nomsJoueurs)
{
    qDebug() << Q_FUNC_INFO << "nomsJoueurs" << nomsJoueurs;
    this->nomsJoueurs = nomsJoueurs;
}

/**
 * @brief Destructeur de la classe Joueurs
 *
 * @fn Joueurs::~Joueurs
 * @details Libère les ressources de l'application
 */
Joueurs::~Joueurs()
{
    qDebug() << Q_FUNC_INFO;
}

/**
 * @fn Joueurs::getJoueurs
 * @brief Récupére les joueurs
 */
QVector<QString> Joueurs::getJoueurs()
{
    return nomsJoueurs;
}

/**
 * @fn Joueurs::ajouterJoueur
 * @brief Ajoute un joueur
 */
void Joueurs::ajouterJoueur(QString nomJoueur)
{
    nomsJoueurs.append(nomJoueur);
}
