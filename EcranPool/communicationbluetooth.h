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

#define DELIMITEUR_DEBUT "$"
#define DELIMITEUR_FIN   "\n"
#define DELIMITEUR_CHAMP ";"

// Types de trame
#define TYPE_EMPOCHAGE  'E' // $E;0;1;1\n
#define TYPE_NOMS       'N' // $N;0;Sylvie;Robert\n
#define TYPE_CHANGEMENT 'C' // $C;0;0\n

#define NB_CHAMPS_MIN 3

// Positions des champs dans la trame
#define POSITION_TYPE       0
#define POSITION_TABLE      1
#define POSITION_POCHE      2
#define POSITION_COULEUR    3
#define POSITION_JOUEUR1    2
#define POSITION_JOUEUR2    3
#define POSITION_CHANGEMENT 2

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
      peripheriqueLocal;       //!< L'interface Bluetooth de la Raspberry Pi
    QBluetoothServer* serveur; //!< Le serveur Bluetooth
    QBluetoothSocket* socket;  //!< La socket de communication Bluetooth
    QBluetoothServiceInfo
      serviceInfo; //!< Les informations sur le service bluetooth

    QString nomPeripheriqueLocal;
    QString adressePeripheriqueLocal;
    QString donneesRecues;

  private slots:
    void connecterClient();
    void deconnecterClient();
    void lireDonnees();
    bool decoderTrame(const QStringList& champsTrame);

  signals:
    void clientConnecte();
    void clientDeconnecte();
    void empochage(int numeroTable, int numeroPoche, int couleur);
    void nomsJoueurs(int numeroTable, QString nomJoueur1, QString nomJoueur2);
    void changementJoueur(int numeroTable, int changementJoueur);
};

#endif // COMMUNICATIONBLUETOOTH_H
