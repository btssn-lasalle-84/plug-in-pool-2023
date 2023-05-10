#ifndef JOUEURS_H
#define JOUEURS_H

#include <vector>
#include <string>

/**
 * @class s
 * @brief Déclare les joueurs de la partie
 */
class Joueurs
{
  public:
    Joueurs(std::vector<std::string> nomsJoueurs);
    ~Joueurs();

  private:
    std::vector<std::string> nomsJoueurs;
};

#endif // JOUEURS_H
