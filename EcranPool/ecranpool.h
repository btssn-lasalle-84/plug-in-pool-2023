/**
 * @file ecranpool.h
 *
 * @brief Déclaration de la classe EcranPool
 * @author Benjamin GAUME
 * @version 0.1
 */

#ifndef ECRANPOOL_H
#define ECRANPOOL_H

#include <QtWidgets>
#include <stdlib.h>
#include <vector>
#include <string>

/**
 * @def TEST_EcranPool
 * @brief Pour le mode avec les raccourcis clavier
 */
#define TEST_EcranPool

/**
 * @def PLEIN_ECRAN
 * @brief Pour activer le mode plein écran sur la Raspberry Pi
 */
#define PLEIN_ECRAN

/**
 * @def TEMPS_TOUR
 * @brief Le temps pour jouer en secondes
 */
#define TEMPS_TOUR 45

/**
 * @def NB_BILLES
 * @brief Le nombre de billes par joueur
 */
#define NB_BILLES 7

namespace Ui
{
class EcranPool;
}

class Joueurs;
class CommunicationBluetooth;

/**
 * @class EcranPool
 * @brief L'IHM de l'application
 */
class EcranPool : public QWidget
{
    Q_OBJECT

  public:
    static const int INTERVALLE_SECONDE = 1000;
    /**
     * @enum Ecran
     * @brief Les différents écrans
     */
    enum Ecran
    {
        Accueil,
        Partie,
        FinPartie,
        NbEcrans
    };
    /**
     * @enum Couleur
     * @brief Les différentes couleurs
     */
    enum Couleur
    {
        INCONNUE = -1,
        ROUGE,
        JAUNE,
        BLANCHE,
        NOIRE,
        NB_COULEURS
    };

  public:
    EcranPool(QWidget* parent = nullptr);
    ~EcranPool();

    static QString recupererNomCouleur(int couleur);

  private:
    Ui::EcranPool* ui;      //<! la fenêtre
    Joueurs*       joueurs; //<! les joueurs
    CommunicationBluetooth*
                     communicationBluetooth; //<! la communication Bluetooth
    QVector<QLabel*> labelsHeure; //<! les labels pour l'affichage de l'heure
    qint64       dureePartie; //<! pour l'affichage de la durée d'une partie
    QTimer*      minuteurDecompte; //<! pour gérer le temps d'un tour
    int          numeroTable;      //<! Correspond au numéro de la table
    int          joueurActif;      //<! 0 pour nomJoueur1 et 1 pour nomJoueur2
    QString      nomJoueur1;       //<! Correspond au joueur de gauche sur l'IHM
    QString      nomJoueur2;       //<! Correspond au joueur de droite sur l'IHM
    int          decompte;         //<! le temps d'un tour
    int          couleurJoueur1;   //<! couleur du joueur de gauche sur l'IHM
    int          couleurJoueur2;   //<! couleur du joueur de droite sur l'IHM
    QVector<int> billesRestantes;  //<! billes des joueurs

    void initialiserCommunication();
    void initialiserEcran();
    void initialiserJoueurs();
    void initialiserHeure();
    void initialiserDecompteManche();
    void initialiserPartie();
#ifdef TEST_EcranPool
    void initialiserRaccourcisClavier();
#endif

  public slots:
    void afficherEcran(EcranPool::Ecran ecran);
    void afficherEcranAcceuil();
    void afficherEcranPartie();
    void afficherEcranFinPartie();
    void afficherHeure();
    void afficherDureePartie();
    void afficherDecompteManche();
    void afficherEmpochage(int numeroTable, int numeroPoche, int couleur);
    void afficherBillesRestantesJoueurs();
    void afficherNomsJoueurs(int     numeroTable,
                             QString nomJoueur1,
                             QString nomJoueur2);
    void afficherChangementJoueur(int numeroTable, int changementJoueur);

#ifdef TEST_EcranPool
    void afficherEcranSuivant();
    void afficherEcranPrecedent();
#endif
};

#endif // ECRANPOOL_H
