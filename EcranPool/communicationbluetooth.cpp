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

/**
 * @brief Connecte le client
 */
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

/**
 * @brief Déconnecte le client
 */
void CommunicationBluetooth::deconnecterClient()
{
    qDebug() << Q_FUNC_INFO;
    emit clientDeconnecte();
}

/**
 * @brief Lit les données
 */
void CommunicationBluetooth::lireDonnees()
{
    QByteArray donnees;
    bool       decodage = false;

    donnees = socket->readAll();

    donneesRecues += QString(donnees.data());
    qDebug() << Q_FUNC_INFO << "donneesRecues" << donneesRecues;

    if(donneesRecues.contains(DELIMITEUR_DEBUT) &&
       donneesRecues.endsWith(DELIMITEUR_FIN))
    {
        QStringList trames =
          donneesRecues.split(DELIMITEUR_FIN, QString::SkipEmptyParts);
        qDebug() << Q_FUNC_INFO << "trames" << trames;

        QStringList champsTrame;
        for(int i = 0; i < trames.count(); ++i)
        {
            if(trames[i].startsWith(DELIMITEUR_DEBUT))
            {
                champsTrame = trames[i].split(DELIMITEUR_CHAMP);
                decodage    = decoderTrame(champsTrame);
                qDebug() << Q_FUNC_INFO << "decodage" << decodage;
                if(decodage)
                    champsTrame.clear();
            }
        }
        donneesRecues.clear();
    }
}

/**
 * @brief Décode la trame reçue
 */
bool CommunicationBluetooth::decoderTrame(const QStringList& champsTrame)
{
    if(champsTrame.isEmpty())
        return false;

    if(champsTrame.count() < NB_CHAMPS_MIN)
        return false;

    qDebug() << Q_FUNC_INFO << "champsTrame" << champsTrame;

    QString type = champsTrame.value(POSITION_TYPE);

    if(type.isEmpty())
        return false;

    switch(type.at(POSITION_TYPE + 1).toLatin1())
    {
        case TYPE_EMPOCHAGE:
        {
            int table   = champsTrame.value(POSITION_TABLE).toInt();
            int poche   = champsTrame.value(POSITION_POCHE).toInt();
            int couleur = champsTrame.value(POSITION_COULEUR).toInt();
            qDebug() << Q_FUNC_INFO << "type"
                     << type.at(POSITION_TYPE + 1).toLatin1() << "table"
                     << table << "poche" << poche << "couleur" << couleur;
            emit empochage(table, poche, couleur);
            break;
        }
        case TYPE_NOMS:
        {
            int     table   = champsTrame.value(POSITION_TABLE).toInt();
            QString joueur1 = champsTrame.value(POSITION_JOUEUR1);
            QString joueur2 = champsTrame.value(POSITION_JOUEUR2);
            qDebug() << Q_FUNC_INFO << "type"
                     << type.at(POSITION_TYPE + 1).toLatin1() << "table"
                     << table << "joueur1" << joueur1 << "joueur2" << joueur2;
            emit nomsJoueurs(table, joueur1, joueur2);
            break;
        }
        case TYPE_CHANGEMENT:
        {
            int  table      = champsTrame.value(POSITION_TABLE).toInt();
            int  changement = champsTrame.value(POSITION_CHANGEMENT).toInt();
            emit changementJoueur(table, changement);
            qDebug() << Q_FUNC_INFO << "type"
                     << type.at(POSITION_TYPE + 1).toLatin1() << "table"
                     << table << "changement" << changement;
            break;
        }
        default:
            qDebug() << Q_FUNC_INFO << "type inconnu";
            return false;
            break;
    }
    return true;
}
