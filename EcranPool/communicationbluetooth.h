/**
 * @file communicationbluetooth.h
 *
 * @brief Déclaration de la classe CommunicationBluetooth
 * @author Benjamin GAUME
 * @version 0.2
 */

#ifndef COMMUNICATIONBLUETOOTH_H
#define COMMUNICATIONBLUETOOTH_H

#include <QObject>
#include <QtBluetooth>
#include <QBluetoothLocalDevice>


#define DELIMITEUR_DEBUT        "$"
#define DELIMITEUR_FIN          "\n"
#define DELIMITEUR_CHAMP        ";"

// Types de trame
#define TYPE_EMPOCHAGE         "E"
#define TYPE_NOMS              "N"
#define TYPE_CHANGEMENT        "C"

// Positions des champs dans la trame
#define POSITION_TYPE               1
#define POSITION_TABLE              3
#define POSITION_POCHE              5
#define POSITION_COULEUR            7
#define POSITION_JOUEUR1            7
#define POSITION_JOUEUR2            9
#define POSITION_CHANGEMENT         7

static const QString serviceUuid(
  QStringLiteral("00001101-0000-1000-8000-00805F9B34FB"));
// 00000003-0000-1000-8000-00805f9b34fb
static const QString serviceNom(QStringLiteral("EcranPool"));

/**
 * @class CommunicationBluetooth
 * @brief La classe de communication Bluetooth
 */
class CommunicationBluetooth : public QObject
{
    Q_OBJECT
  public:
    CommunicationBluetooth(QObject* parent = nullptr);
    ~CommunicationBluetooth();
    void demarrerCommunication();
    void arreterCommunication();
    void initialiserCommunication();

    QString getNomPeripheriqueLocal();
    QString getAdressePeripheriqueLocal();

  private:
    QBluetoothLocalDevice
                      peripheriqueLocal; //!< L'interface Bluetooth de la Raspberry Pi
    QBluetoothServer* serveur;           //!< Le serveur Bluetooth
    QBluetoothSocket* socket; //!< La socket de communication Bluetooth
    QBluetoothServiceInfo
      serviceInfo; //!< Les informations sur le service bluetooth

    QString         nomPeripheriqueLocal;
    QString         adressePeripheriqueLocal;
    QString         trame;
    QStringList     champsTrame;

  private slots:
    void connecterClient();
    void deconnecterClient();
    void lireDonnees();
    void decoderTrame(QStringList champsTrame);

  signals:
    void clientConnecte();
    void clientDeconnecte();
    void empochage(int numeroTable, int numeroPoche, int couleur);
    void nomsJoueurs(int numeroTable, QString nomJoueur1, QString nomJoueur2);
    void changementJoueur(int numeroTable, int changementJoueur);
};

#endif // COMMUNICATIONBLUETOOTH_H
