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

  public:
    EcranPool(QWidget* parent = nullptr);
    ~EcranPool();

  private:
    QElapsedTimer    dureePartie;
    Ui::EcranPool*   ui;          //<! la fenêtre
    QVector<QLabel*> labelsHeure; //<! les labels pour l'affichage de l''heure
    void             initialiserEcran();
    void             initialiserHeure();
    void             afficherDureePartie();
#ifdef TEST_EcranPool
    void initialiserRaccourcisClavier();
#endif

  public slots:
    void afficherEcran(EcranPool::Ecran ecran);
    void afficherEcranAcceuil();
    void afficherEcranPartie();
    void afficherEcranFinPartie();
    void actualiserHeure();
#ifdef TEST_EcranPool
    void afficherEcranSuivant();
    void afficherEcranPrecedent();
#endif
};

#endif // ECRANPOOL_H
