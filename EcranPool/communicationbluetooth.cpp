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
 * @brief Démarre la communication Bluetooth (SERVEUR)
 *
 * @fn CommunicationBluetooth::demarrerCommunication
 */
void CommunicationBluetooth::demarrerCommunication()
{
    // vérifie la présence du Bluetooth en local
    if(!peripheriqueLocal.isValid())
        return;

    if(serveur == nullptr)
    {
        serveur =
          new QBluetoothServer(QBluetoothServiceInfo::RfcommProtocol, this);
        connect(serveur, SIGNAL(newConnection()), this, SLOT(nouveauClient()));

        QBluetoothUuid uuid = QBluetoothUuid(serviceUuid);
        serviceInfo         = serveur->listen(uuid, serviceNom);
    }
}

/**
 * @brief Arrête la communication Bluetooth (SERVEUR)
 *
 * @fn CommunicationBluetooth::arreterCommunication()
 */
void CommunicationBluetooth::arreterCommunication()
{
    // vérifie la présence du Bluetooth en local
    if(!peripheriqueLocal.isValid())
        return;

    if(serveur == nullptr)
        return;

    serviceInfo.unregisterService();

    if(socket)
    {
        if(socket->isOpen())
            socket->close();
        delete socket;
        socket = nullptr;
    }

    delete serveur;
    serveur = nullptr;
}

/**
 * @brief Initialise la communication Bluetooth (SERVEUR)
 *
 * @fn CommunicationBluetooth::initialiserCommunication()
 */
void CommunicationBluetooth::initialiserCommunication()
{
    // vérifie la présence du Bluetooth en local
    if(!peripheriqueLocal.isValid())
        return;
    peripheriqueLocal.powerOn();
    nomPeripheriqueLocal     = peripheriqueLocal.name();
    adressePeripheriqueLocal = peripheriqueLocal.address().toString();
    peripheriqueLocal.setHostMode(QBluetoothLocalDevice::HostDiscoverable);
}
