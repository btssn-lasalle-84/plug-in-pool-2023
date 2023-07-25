# Simulateur PLUG-IN-POOL 2023

## Présentation du protocole implanté dans le simulateur ESP'ACE

Ce document présente rapidement le fonctionnement du simulateur ainsi que le protocole implémenté. Le protocole complet est disponible dans Google Drive. Actuellement, la version du protocole est la **0.2** (7 juin 2023).

## Configuration du simulateur

```cpp
#define NB_POCHES       6
#define NB_BILLES_ROUGE 7
#define NB_BILLES_JAUNE 7
```

Un tir prend entre 1 et 4 secondes. Le joueur a 1 chance sur 2 d'empocher une bille sinon il loupe ou il fait une faute (empochage d'une mauvaise bille).

## Format général

Le protocole distingue deux types de trame (le bit n°7) :

- trame de données (empochage) émise par la table
- trame de service émise par la tablette

### Trame de données (empochage)

Format : `{Type}{Table}{Poche}{Couleur}`

|7|6|5|4|3|2|1|0|
|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|
|**0**|X|X|X|X|X|X|X|

- Type : bit 7
- N°Table : bits 6 et 5
- N°Poche : bits 4, 3 et 2
- Couleur bille : bits 1 et 0


### Trame de service

Format : `{Type}{Table}{Requête/Réponse}`

|7|6|5|4|3|2|1|0|
|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|
|**1**|X|X|X|X|X|X|X|

- Type : bit 7
- N°Table : bits 6 et 5
- Requête/Réponse : bits 4 à 0

### Numéro de tables

- `00` : Table 1
- `01` : Table 2
- `10` : Table 3
- `11` : Table 4

### Couleur des billes

- `00` : Rouge
- `01` : Jaune
- `10` : Blanche
- `11` : Noire

### Numéro des blouses (poches)

- `001` : poche n°1
- `010` : poche n°2
- ...
- `110` : poche n°6

### Requêtes/réponses

- Mobile-pool → Table

  - Commencer une manche : `1xx11100`
  - Arrêter une manche : `1xx00000`

```cpp
#define TRAME_START 0x9c // Commencer une manche (réinitialisation)
#define TRAME_STOP  0x80 // Arrêter une manche (facultatif)
```

- Mobile-pool ← Table

  - Trame de tour : `1xx11111`
  - Trame de faute : `1xx00000`

```cpp
#define TRAME_TIR     0x9f // Trame de début de tour
#define TRAME_FAUTE   0x80 // Trame de faute

#define TRAME_EMPOCHE 0x00 // cf. Protocole
```

### Fonctionnement

Pour l'instant, le simulateur démarre à la réception d'une trame : `1xx11100` (`0x9C` pour la table n°1).

Quand le simulateur affiche : 

- "loupé"   : il faut appuyer sur le bouton TOUR (SW2).
- "faute !" : il faut appuyer sur le bouton FAUTE (SW1).

La partie peut être arrêtée avec une trame `1xx00000` (`0x80` pour la table n°1).

## Écran OLED

Le nom du périphérique bluetooth et son adresse MAC sont affichés sur les deux premières lignes.

Les lignes suivantes affichent successivement :

- le score du joueur en train de tirer
- le tir du joueur qui a les billes rouges
- le tir du joueur qui a les billes jaunes
- la trame reçue et l'état de la partie

_Remarque :_ `>` indique le joueur en train de tirer.

## platform.ini

```ini
[env:lolin32]
platform = espressif32
board = lolin32
framework = arduino
lib_deps =
  thingpulse/ESP8266 and ESP32 OLED driver for SSD1306 displays @ ^4.2.0
upload_port = /dev/ttyUSB0
upload_speed = 115200
monitor_port = /dev/ttyUSB0
monitor_speed = 115200
```

## Exemples d'échange

Pour la table n°1

Démarrage d'une partie :

```
10011100
```

Après appui sur bouton :

```
10011111
```

Puis :

```
00001010
00001110
00001010
10011111
10000000
10011111
10011111
00010101
00010010
10000000
```

A la fin de cette série de trames, le score est le suivant :

- Joueur ROUGE : 4 bille(s)  OOOOXXX
- Joueur JAUNE : 5 bille(s)  OOOOOXX

## Auteur

- Thierry Vaira <<tvaira@free.fr>>
