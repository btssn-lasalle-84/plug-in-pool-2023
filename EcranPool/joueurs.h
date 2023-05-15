#ifndef JOUEURS_H
#define JOUEURS_H

#include <QVector>
#include <QString>

/**
 * @class Joueurs
 * @brief Déclare les joueurs de la partie
 */
class Joueurs
{
  public:
    Joueurs();
    Joueurs(QVector<QString> nomsJoueurs);
    ~Joueurs();

    QVector<QString> getJoueurs();
    void             ajouterJoueur(QString nomsJoueur);

  private:
    QVector<QString> nomsJoueurs;
};

#endif // JOUEURS_H
