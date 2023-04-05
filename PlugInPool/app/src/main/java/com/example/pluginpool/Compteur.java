package com.example.pluginpool;

import java.util.Timer;
import java.util.TimerTask;

public class Compteur extends Timer
{
    public static final int DUREE_TIR = 45;

    private int tempsRestant;
    private GestionManche gestionManche;

    private TimerTask actualiser;
    public void Compteur(manche)
    {
        tempsRestant = DUREE_TIR;
        gestionManche = manche();

        actualiser = new TimerTask()
        {
            public void run()
            {
                if (tempsRestant > 0)
                {
                    tempsRestant -= 1;
                    manche.activity_manche_affichage.actualiserCompteur(tempsRestant);
                }
                else
                {
                    this.cancel();
                }
            }
        };
    }

    public void redemarrer()
    {
        this.cancel();
        this.purge();
        this.tempsRestant = DUREE_TIR;
        this.scheduleAtFixedRate(actualiser, 0, 1000);
    }
}
