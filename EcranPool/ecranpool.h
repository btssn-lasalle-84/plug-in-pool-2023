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
        ROUGE,
        JAUNE,
        BLANCHE,
        NOIRE,
        NB_COULEURS
    };

  public:
    EcranPool(QWidget* parent = nullptr);
    ~EcranPool();

  private:
    Ui::EcranPool* ui;      //<! la fenêtre
    Joueurs*       joueurs; //<! les joueurs
    CommunicationBluetooth*
                      communicationBluetooth; //<! la communication Bluetooth
    static EcranPool* ecranPoolInstance;
    QLabel* labelNumeroTable; // Déclaration du QLabel qui contient le numéro de
                              // la table
    QVector<QLabel*> labelsHeure; //<! les labels pour l'affichage de l'heure
    qint64 dureePartie; //<! pour l'affichage de la durée d'une partie

    void initialiserCommunication();
    void initialiserEcran();
    void initialiserJoueurs();
    void initialiserHeure();
    void initialiserDecompteManche();
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
    void afficherNumeroTable(int table);
    void afficherNomsJoueurs(const QString& nomJoueur1,
                             const QString& nomJoueur2);

#ifdef TEST_EcranPool
    void afficherEcranSuivant();
    void afficherEcranPrecedent();
#endif
};

#endif // ECRANPOOL_H
