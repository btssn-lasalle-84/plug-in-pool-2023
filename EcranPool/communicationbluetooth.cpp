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

    if (serveur == nullptr)
    {
        serveur = new QBluetoothServer(QBluetoothServiceInfo::RfcommProtocol, this);
        connect(serveur, SIGNAL(newConnection()), this, SLOT(nouveauClient()));

        QBluetoothUuid uuid = QBluetoothUuid(serviceUuid);
        serviceInfo = serveur->listen(uuid, serviceNom);
    }
}

/**
* @brief Arrête la communication Bluetooth (SERVEUR)
*
* @fn CommunicationBluetooth::arreterCommunication()
*/
void CommunicationBluetooth::arreterCommunication()
{
    {
        if (serveur == nullptr)
        return;

        serviceInfo.unregisterService();

        if (socket)
        {
            if (socket->isOpen())
               socket->close();
            delete socket;
            socket = NULL;
        }

        delete serveur;
        serveur = NULL;
    }
}

/**
* @brief Initialise la communication Bluetooth (SERVEUR)
*
* @fn CommunicationBluetooth::initialiserCommunication()
*/
void CommunicationBluetooth::initialiserCommunication()
{
    peripheriqueLocal.powerOn();
    nomPeripheriqueLocal = peripheriqueLocal.name();
    adressePeripheriqueLocal = peripheriqueLocal.address().toString();
    peripheriqueLocal.setHostMode(QBluetoothLocalDevice::HostDiscoverable);
}



/**
 * @brief Vérifie si le Bluetooth est actif
 *
 * @fn CommunicationBluetooth::verifierActivationBluetooth()
 */
/*
void CommunicationBluetooth::verifierActivationBluetooth(
  QString& nomPeripheriqueLocal)
{
    if(peripheriqueLocal.isValid())
    {

    }
}
*/
