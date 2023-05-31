/**
 * @file communicationbluetooth.cpp
 *
 * @brief Définition de la classe CommunicationBluetooth
 * @author Benjamin GAUME
 * @version 0.2
 */

#include "communicationbluetooth.h"
#include <QDebug>
#include <QBluetoothLocalDevice>

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
        qDebug() << Q_FUNC_INFO;
        serveur =
          new QBluetoothServer(QBluetoothServiceInfo::RfcommProtocol, this);
        connect(serveur,
                SIGNAL(newConnection()),
                this,
                SLOT(connecterClient()));

        QBluetoothUuid uuid(QBluetoothUuid::Rfcomm);
        // QBluetoothUuid uuid = QBluetoothUuid(serviceUuid);
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
    // Vérifie la présence du Bluetooth en local
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
    qDebug() << Q_FUNC_INFO;
}

/**
 * @brief Initialise la communication Bluetooth (SERVEUR)
 *
 * @fn CommunicationBluetooth::initialiserCommunication()
 */
void CommunicationBluetooth::initialiserCommunication()
{
    // Vérifie la présence du Bluetooth en local
    if(!peripheriqueLocal.isValid())
        return;

    peripheriqueLocal.powerOn();
    nomPeripheriqueLocal     = peripheriqueLocal.name();
    adressePeripheriqueLocal = peripheriqueLocal.address().toString();
    qDebug() << Q_FUNC_INFO << "nomPeripheriqueLocal" << nomPeripheriqueLocal
             << "adressePeripheriqueLocal" << adressePeripheriqueLocal;
    // peripheriqueLocal.setHostMode(QBluetoothLocalDevice::HostDiscoverable);
    peripheriqueLocal.setHostMode(QBluetoothLocalDevice::HostConnectable);
}

/**
 * @brief Récupère le nom du périphérique local
 */
QString CommunicationBluetooth::getNomPeripheriqueLocal()
{
    return peripheriqueLocal.name();
}

/**
 * @brief Récupère l'adresse du périphérique local
 */
QString CommunicationBluetooth::getAdressePeripheriqueLocal()
{
    return adressePeripheriqueLocal;
}

void CommunicationBluetooth::connecterClient()
{
    qDebug() << Q_FUNC_INFO;
    socket = serveur->nextPendingConnection();
    if(!socket)
        return;

    connect(socket, SIGNAL(disconnected()), this, SLOT(deconnecterClient()));
    connect(socket, SIGNAL(readyRead()), this, SLOT(lireDonnees()));

    emit clientConnecte();
}

void CommunicationBluetooth::deconnecterClient()
{
    qDebug() << Q_FUNC_INFO;
    emit clientDeconnecte();
}

void CommunicationBluetooth::lireDonnees()
{
    QByteArray donnees;

    donnees += socket->readAll();
    qDebug() << Q_FUNC_INFO << donnees;
}
