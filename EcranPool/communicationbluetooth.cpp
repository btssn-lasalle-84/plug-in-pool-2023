/**
 * @file communicationbluetooth.cpp
 *
 * @brief Définition de la classe CommunicationBluetooth
 * @author Benjamin GAUME
 * @version 0.2
 */

#include "communicationbluetooth.h"
#include <QDebug>

/**
 * @brief Constructeur de la classe CommunicationBluetooth
 *
 * @fn CommunicationBluetooth::CommunicationBluetooth
 * @param parent L'adresse de l'objet parent
 */
CommunicationBluetooth::CommunicationBluetooth(QObject* parent) :
    QObject(parent), serveur(nullptr), socket(nullptr)
{
    qDebug() << Q_FUNC_INFO;
}

/**
 * @brief Destruteur de la classe CommunicationBluetooth
 *
 * @fn CommunicationBluetooth::~CommunicationBluetooth
 */
CommunicationBluetooth::~CommunicationBluetooth()
{
    qDebug() << Q_FUNC_INFO;
}

/**
 * @brief Vérifie si le Bluetooth est actif
 *
 * @fn CommunicationBluetooth::verifierActivationBluetooth()
 */
void CommunicationBluetooth::verifierActivationBluetooth(
  QString& nomPeripheriqueLocal)
{
    if(peripheriqueLocal.isValid())
    {
        // Activer le Bluetooth
        peripheriqueLocal.powerOn();

        // Lire le nom du périphérique local
        nomPeripheriqueLocal = peripheriqueLocal.name();

        // Rendre le périphérique visible aux autres
        peripheriqueLocal.setHostMode(QBluetoothLocalDevice::HostDiscoverable);
    }
}
