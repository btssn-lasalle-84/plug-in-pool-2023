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

static const QString serviceUuid(
  QStringLiteral("00001101-0000-1000-8000-00805F9B34FB"));
static const QString serviceNom(QStringLiteral("EcranPool"));
static const QString adresseMAC("00:1A:7D:DA:71:0A");

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
    bool testerCommunication();
    void definirAdresseMAC();

    QString getNomPeripheriqueLocal();
    QString getAdressePeripheriqueLocal();

  private:
    QBluetoothLocalDevice
                      peripheriqueLocal; //!< L'interface Bluetooth de la Raspberry Pi
    QBluetoothServer* serveur;           //!< Le serveur Bluetooth
    QBluetoothSocket* socket; //!< La socket de communication Bluetooth
    QBluetoothServiceInfo
      serviceInfo; //!< Les informations sur le service bluetooth

    QString nomPeripheriqueLocal;
    QString adressePeripheriqueLocal;

  private slots:

  signals:
};

#endif // COMMUNICATIONBLUETOOTH_H
