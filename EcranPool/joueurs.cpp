/**
 * @file Joueurs.cpp
 *
 * @brief Définition de la classe Joueurs
 * @author Benjamin GAUME
 * @version 0.1
 */

#include "joueurs.h"
#include <QDebug>

/**
 * @todo Mettre les commentaires Doxygen
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

Joueurs::~Joueurs()
{
    qDebug() << Q_FUNC_INFO;
}

QVector<QString> Joueurs::getJoueurs()
{
    /**
     * @todo Retourner les joueurs
     */
}

void Joueurs::ajouterJoueur(QString nomsJoueur)
{
    /**
     * @todo Ajouter le nomJoueur au nomsJoueurs
     */
}
