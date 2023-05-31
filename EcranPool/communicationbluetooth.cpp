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

    // Configuration de l'adresse MAC dans la clé USB Bluetooth
    peripheriqueLocal.ad(QBluetoothAddress(adresseMAC));
    peripheriqueLocal.powerOn();
    nomPeripheriqueLocal      = peripheriqueLocal.name();
    QBluetoothAddress adresse = peripheriqueLocal.address();
    peripheriqueLocal.setHostMode(QBluetoothLocalDevice::HostDiscoverable);

    qDebug() << "Communication Bluetooth initialisée avec l'adresse MAC :"
             << adresseMAC;
}

/**
 * @brief Méthode pour vérifier si la communication est fonctionnelle
 */
bool CommunicationBluetooth::testerCommunication()
{
    // Vérifie si le serveur est initialisé
    if(serveur == nullptr)
    {
        qDebug() << "La communication Bluetooth n'est pas démarrée.";
        return false;
    }

    // Vérifie si le service est enregistré
    if(!serviceInfo.isRegistered())
    {
        qDebug() << "Le service Bluetooth n'est pas enregistré.";
        return false;
    }

    qDebug() << "La communication Bluetooth est fonctionnelle.";
    return true;
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
    return peripheriqueLocal.address().toString();
}

/**
 * @brief Méthode pour définir l'adresse MAC
 */
void CommunicationBluetooth::definirAdresseMAC()
{
    QBluetoothAddress adresse(adresseMAC);
    if(adresse.isNull())
    {
        qDebug() << "Adresse MAC invalide : " << adresseMAC;
        return;
    }

    if(!peripheriqueLocal.setHost(adresse))
    {
        qDebug() << "Impossible de définir l'adresse MAC : " << adresseMAC;
        return;
    }

    qDebug() << "Adresse MAC définie : " << adresseMAC;
}
