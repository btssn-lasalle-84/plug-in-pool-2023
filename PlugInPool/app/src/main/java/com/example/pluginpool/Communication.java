/**
 * @file Communication.java
 * @brief Déclaration de la classe Communication
 * @author Clément TRICHET
 */

package com.example.pluginpool;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

/**
 * @class Communication
 * @brief La classe Communicaion Bluetooth
 */
public class Communication
{
    /**
     * Constantes
     */
    private static final String TAG = "_Communication"; //!< TAG pour les logs
    private static final UUID   identifiantUUID =
      UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public final static int CONNEXION_BLUETOOTH   = 0;
    public final static int RECEPTION_BLUETOOTH   = 1;
    public final static int DECONNEXION_BLUETOOTH = 2;
    public final static int TABLE = 0;
    public final static int ECRAN = 1;

    /**
     * Attributs
     */
    private static Communication[] communications =
            {null, null}; //!< les deux uniques instances de Communication (multiton)
    private BluetoothAdapter adaptateurBluetooth = null;
    private BluetoothDevice  peripherique        = null;
    private BluetoothSocket  canalBluetooth      = null;
    private InputStream      inputStream         = null;
    private OutputStream     outputStream        = null;
    private boolean          connecte            = false;
    private Thread           filExecutionReception;
    private Handler          handler = null;

    /**
     * @fn getInstance
     * @brief Retourne l'instance Communication
     */
    public synchronized static Communication getInstance(int peripherique)
    {
        if(communications[peripherique] == null)
            communications[peripherique] = new Communication();
        return communications[peripherique];
    }

    /**
     * @fn getInstance
     * @brief Retourne l'instance Communication
     */
    public synchronized static Communication getInstance(Handler handler, int peripherique)
    {
        if(communications[peripherique] == null)
            communications[peripherique] = new Communication(handler);
        else
            communications[peripherique].setHandler(handler);
        return communications[peripherique];
    }

    /**
     * @brief Constructeur par défaut
     */
    private Communication()
    {
        activer();
    }

    /**
     * @brief Constructeur d'initialisation
     */
    private Communication(Handler handler)
    {
        this.handler = handler;
        activer();
    }

    /**
     * @fn setHandler
     * @brief Fixe le gestionnaire de messages du thread UI
     * @param handler Handler le gestionnaire de messages du thread UI
     */
    public void setHandler(Handler handler)
    {
        this.handler = handler;
    }

    /**
     * @brief Pour activer le bluetooth
     */
    @SuppressLint("MissingPermission")
    public void activer()
    {
        this.adaptateurBluetooth = BluetoothAdapter.getDefaultAdapter();
        if(this.adaptateurBluetooth == null)
        {
            Log.e(TAG, "Bluetooth non supporte par l'appareil.");
        }
        else if(!adaptateurBluetooth.isEnabled())
        {
            Log.d(TAG, "Bluetooth désactive, activation");
            adaptateurBluetooth.enable();
        }
        else
        {
            Log.d(TAG, "Bluetooth active");
        }
    }

    /**
     * @brief Pour rechercher des tables
     */
    @SuppressLint("MissingPermission")
    public void rechercherTables()
    {
        /**
         * @todo Rechercher les tables
         */
    }

    /**
     * @brief Pour se connecter à une table
     */
    @SuppressLint("MissingPermission")
    public boolean seConnecter(String nomPeripherique)
    {
        // Activer le Bluetooth
        activer();

        // Rechercher les appareils déjà appairés
        Set<BluetoothDevice> peripheriquesAppaires = adaptateurBluetooth.getBondedDevices();
        if(peripheriquesAppaires.size() > 0)
        {
            Log.e(TAG, "Nb appareils appairés : " + peripheriquesAppaires.size());
            for(BluetoothDevice appareil: peripheriquesAppaires)
            {
                if(appareil.getName().contains(nomPeripherique))
                {
                    this.peripherique = appareil;
                    break;
                }
            }
        }
        if(peripherique == null)
        {
            Log.e(TAG, "Appareil Bluetooth non trouve");
            return false;
        }
        else
        {
            Log.d(TAG,
                  "Appareil Bluetooth sélectionné : " + peripherique.getName() + " " +
                    peripherique.getAddress());
            return creerSocket();
        }
    }

    /**
     * @brief Pour se déconnecter du périphérique Bluetooth
     */
    @SuppressLint("MissingPermission")
    public void seDeconnecter() {
        try {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            if (canalBluetooth != null) {
                canalBluetooth.close();
            }
            connecte = false;
            if (handler != null) {
                Message messageHandler = new Message();
                messageHandler.what = DECONNEXION_BLUETOOTH;
                messageHandler.obj = peripherique.getName();
                handler.sendMessage(messageHandler);
            }
        } catch (Exception e) {
            Log.d(TAG, "Erreur lors de la fermeture des connexions.");
        }
    }

    /**
     * @brief Pour créer un socket Bluetooth
     */
    @SuppressLint("MissingPermission")
    public boolean creerSocket()
    {
        // Créer le canal Bluetooth
        try
        {
            canalBluetooth = peripherique.createRfcommSocketToServiceRecord(identifiantUUID);
        }
        catch(IOException e)
        {
            Log.e(TAG, "Erreur lors de la creation du canal");
            return false;
        }
        // Connecter le canal
        try
        {
            canalBluetooth.connect();
            inputStream  = canalBluetooth.getInputStream();
            outputStream = canalBluetooth.getOutputStream();
            connecte     = true;
            if(handler != null)
            {
                Log.d(TAG, "Message handler");
                Message messageHandler = new Message();
                messageHandler.what    = CONNEXION_BLUETOOTH;
                messageHandler.obj     = peripherique.getName();
                handler.sendMessage(messageHandler);
            }
            // Démarrer la reception
            recevoir();
            Log.d(TAG, "Canal Bluetooth connecté");
            return true;
        }
        catch(IOException e)
        {
            Log.e(TAG, "Erreur lors de la connexion du canal");
            try
            {
                canalBluetooth.close();
            }
            catch(IOException closeException)
            {
                Log.e(TAG, "Erreur lors de la fermeture du socket");
            }
            connecte = false;
            return false;
        }
    }

    /**
     * @brief Pour se déconnecter d'une table
     */
    @SuppressLint("MissingPermission")
    public void deconnecter()
    {
        // Fermer les connexions et le socket
        try
        {
            if(inputStream != null)
            {
                inputStream.close();
            }
            if(outputStream != null)
            {
                outputStream.close();
            }
            if(canalBluetooth != null)
            {
                canalBluetooth.close();
            }
            connecte = false;
            if(handler != null)
            {
                Message messageHandler = new Message();
                messageHandler.what    = DECONNEXION_BLUETOOTH;
                messageHandler.obj     = peripherique.getName();
                handler.sendMessage(messageHandler);
            }
        }
        catch(IOException e)
        {
            Log.e(TAG, "Erreur lors de la fermeture des connexions.");
        }
    }

    /**
     * @brief Pour envoyer un message via le Bluetooth
     */
    public void envoyer(int message)
    {
        if(!connecte)
            return;
        if(canalBluetooth == null)
            return;

        new Thread() {
            @Override
            public void run()
            {
                try
                {
                    if(!canalBluetooth.isConnected())
                    {
                        Log.d(TAG, "envoyer() socket non connecté !");
                    }
                    else
                    {
                        Log.d(TAG, "envoyer() 0x" + Integer.toHexString(message));
                        outputStream.write(message);
                        outputStream.flush();
                    }
                }
                catch(IOException e)
                {
                    Log.e(TAG, "Erreur lors de l'envoi de données");
                }
            }
        }.start();
    }

    /**
     * @brief Pour envoyer un message via le Bluetooth
     */
    public void envoyer(String message)
    {
        if(!connecte)
            return;
        if(canalBluetooth == null)
            return;

        new Thread() {
            @Override
            public void run()
            {
                try
                {
                    if(!canalBluetooth.isConnected())
                    {
                        Log.d(TAG, "envoyer() socket non connecté !");
                    }
                    else
                    {
                        Log.d(TAG, "envoyer( " + message + " )");
                        outputStream.write(message.getBytes());
                        outputStream.flush();
                    }
                }
                catch(IOException e)
                {
                    Log.e(TAG, "Erreur lors de l'envoi de données");
                }
            }
        }.start();
    }

    /**
     * @brief Pour recevoir des messages via le Bluetooth
     */
    public void recevoir()
    {
        Log.d(TAG, "recevoir()");
        filExecutionReception = new Thread(new Runnable() {
            @Override
            public void run()
            {
                Log.d(TAG, "recevoir() thread démarré");
                while(connecte)
                {
                    try
                    {
                        int message = (char)inputStream.read();
                        if(message != -1)
                        {
                            if(handler != null)
                            {
                                Message messageHandler = new Message();
                                messageHandler.what    = RECEPTION_BLUETOOTH;
                                messageHandler.obj     = message;
                                handler.sendMessage(messageHandler);
                            }
                        }
                    }
                    catch(IOException e)
                    {
                        Log.e(TAG, "Erreur lors de la réception de données");
                        deconnecter();
                    }
                }
                Log.d(TAG, "recevoir() thread arrêté");
            }
        });
        filExecutionReception.start();
    }
}