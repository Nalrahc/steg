# Steg

Utilitaire CLI de stéganographie écrit en Java permettant de cacher des fichiers binaires dans une image PNG à l'aide de la technique des bits de poids faible (LSB - Least Significant Bit).

Le projet a été développé dans un but pédagogique afin d'explorer :

- la manipulation binaire en Java ;
- le fonctionnement des fichiers `.class` ;
- la stéganographie LSB ;
- les flux d'entrée/sortie ;
- la conception d'outils en ligne de commande de type Unix.

## Fonctionnalités

- Injection d'un fichier dans une image PNG
- Extraction d'un fichier caché
- Support de différents types de payloads
    - CLASS
    - JAR
    - BINARY
- En-tête versionné
- Signature magique `STEG`
- Restauration automatique du nom du fichier lors du décodage
- Vérification de la validité du conteneur avant extraction

---

## Format du conteneur

Les données cachées dans l'image suivent le format suivant :

```text
+---------------------+
| MAGIC   (4 bytes)   |
+---------------------+
| VERSION (1 byte)    |
+---------------------+
| TYPE    (1 byte)    |
+---------------------+
| NAME_LEN (2 bytes)  |
+---------------------+
| NAME    (n bytes)   |
+---------------------+
| SIZE    (4 bytes)   |
+---------------------+
| PAYLOAD             |
+---------------------+
```

### Signature

```text
53 54 45 47
```

soit :

```text
STEG
```

### Version

Version actuelle :

```text
1
```

### Types supportés

| Valeur | Type |
|---------|---------|
| 1 | CLASS |
| 2 | JAR |
| 3 | BINARY |

---

## Principe de la stéganographie

Chaque pixel RGB contient 3 composantes :

```text
R G B
```

Le projet remplace uniquement le bit de poids faible de chaque composante :

```text
RRRRRRRx
GGGGGGGx
BBBBBBBx
```

On obtient ainsi :

```text
3 bits stockés par pixel
```

La modification est généralement invisible à l'œil nu.

---

## Capacité maximale

Capacité théorique :

```text
(width × height × 3) / 8
```

Exemple :

```text
1920 × 1080
```

```text
(1920 × 1080 × 3) / 8
=
777 600 octets
```

Soit environ :

```text
760 Ko
```

de données cachées.

---

## Compilation

Depuis la racine du projet :

```bash
mkdir -p target/classes

javac -d target/classes $(find src/main/java -name "*.java")
```

Création du JAR :

```bash
jar -cfe steg.jar fr.nalrahc.steg.Main -C target/classes .
```

---

## Utilisation

### Encodage

Cacher un fichier dans une image :

```bash
java -jar steg.jar encode HelloWorld.class Boston.png Hidden.png
```

Exemple avec un JAR :

```bash
java -jar steg.jar encode application.jar Boston.png Hidden.png
```

---

### Décodage

Extraction :

```bash
java -jar steg.jar decode Hidden.png
```

Exemple de sortie :

```text
Version : 1
Type    : CLASS
Fichier : HelloWorld.class
Taille  : 430

Extraction terminée
```

Le fichier extrait est automatiquement recréé avec son nom d'origine.

---

## Exemple complet

Création d'une classe :

```java
public class HelloWorld {

    public static void main(String[] args) {
        System.out.println("Hello World");
    }
}
```

Compilation :

```bash
javac HelloWorld.java
```

Encodage :

```bash
java -jar steg.jar encode HelloWorld.class Boston.png Hidden.png
```

Décodage :

```bash
java -jar steg.jar decode Hidden.png
```

Exécution :

```bash
java HelloWorld
```

Résultat :

```text
Hello World
```

---

## Structure du projet

```text
src/
├── main/
│   └── java/
│       └── fr/
│           └── nalrahc/
│               └── steg/
│                   ├── Main.java
│                   ├── BitReader.java
│                   ├── BitWriter.java
│                   ├── LsbEncoder.java
│                   ├── LsbDecoder.java
│                   ├── PayloadType.java
│                   └── StegHeader.java
│
└── test/
    └── java/
```

---

## Limites actuelles

- Un seul fichier par image
- Pas de compression des données
- Pas de chiffrement
- Pas de contrôle d'intégrité (CRC ou SHA)
- PNG uniquement

---

## Évolutions possibles

- Ajout d'un CRC32
- Vérification SHA-256
- Compression GZIP
- Chiffrement AES
- Support de plusieurs fichiers
- Commande `info`
- Commande `verify`
- Support du canal alpha
- Support d'autres formats d'images

---

## Objectif du projet

Ce projet est avant tout un exercice pédagogique autour de :

- Java
- les formats binaires
- la manipulation des bits
- les images PNG
- la stéganographie
- la conception de petits utilitaires CLI
- l'organisation d'un projet Java sans framework

Aucune dépendance externe n'est nécessaire.

## Packaging

Dans un repertoire qui contient une image encodée et steg.jar :
````bash
jpackage --type app-image --name MyApp --input . --main-jar steg.jar --arguments "run app/Encoded.png"
````