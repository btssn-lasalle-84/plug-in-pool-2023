#ifndef ECRANPOOL_H
#define ECRANPOOL_H

/**
 * @file ecranpool.h
 *
 * @brief Déclaration de la classe EcranPool
 * @author Benjamin GAUME
 * @version 0.1
 */

#include <QtWidgets>

//#define PLEIN_ECRAN

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
    Ui::EcranPool* ui; //<! la fenêtre
    QLabel*        logo;
    void           initialiserEcran();
    QBoxLayout*    mainLayout;

  public slots:
    void afficherEcran(EcranPool::Ecran ecran);
    void afficherEcranAcceuil();
    void afficherEcranPartie();
    void afficherEcranFinPartie();
};
#endif // ECRANPOOL_H
