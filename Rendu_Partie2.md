## NOM_Prenom : ZELLEG_Massyl / Groupe 6

## Remarques sur le travail :
- J'ai essayé ici de corriger le plus de code smells possibles dans le projet JUnit4, quand j'avais la possibilité d'utiliser sonarQube qui m'a été d'une grande aide.
- Je n'ai malheureusement pas pu compléter la couverture des tests qui manquait au projet, (ce dernier étant couvert à 84.9%). A cause de problèmes techniques, je n'ai plus la possibilité d'utiliser sonarQube, mon pc s'arrête de marcher dés que je lance dans mon navigateur sur localhost:9000

## Correction de Code Smells
### Les classes qui ont été touchées
- src/main/java/junit/extensions/ActiveTestSuite.java : (suppression de commentaires compliquants la lisibilité)

- src/main/java/junit/framework/Assert.java : suppression de quelques code smells (utilisation de l'operateur > au lieu du (<=) entre autres)

- src/main/java/junit/framework/ComparisonFailure.java : passage des attributs en mode final

- src/main/java/junit/framework/JUnit4TestAdapterCache.java : suppression de paramétre non utilisé dans la méthode getNotifier

- src/main/java/junit/framework/TestCase.java : passage de constructeurs en mode protected au lieu de public

- src/main/java/junit/framework/TestSuite.java :
* changement de declaration de méthodes
* création d'un attribut final static CLASS, pour ne pas avoir à réécrire * plusieurs fois la même chose.
* utilisation de isEmpty au lieu de vérifier si la longueur d'une liste est 0

- src/main/java/junit/runner/BaseTestRunner.java :
* Création de loggers, afin de sauvgarder les messages renvoyer par la méthode processArguments plutôt que d'utiliser System.out.println
* méthodes catchs vide réctifiées
* catch (Exception IOException) --> catch (IOException e)
* return fgFilterStack == false; --> return !fgFilterStack
* dans la condition : if (line.indexOf(patterns[i]) > 0) // 0 est un indice valide, mais ignoré

- src/main/java/junit/runner/Version.java :
* création de variable statique
* création de loggers pour corriger le code smells renvoyé par la méthode sysout
- src/main/java/junit/textui/ResultPrinter.java
* ajout de commentaires explicites sur méthodes vides

- src/main/java/junit/textui/TestRunner.java :
* modification de déclaration de méthode void run
* modification de déclaration de méthode runAndWait
* modification de déclaration de méthode TestResult run
* ajout de messages lors de captures d'exceptions
* remplacement des méthodes system.out.println par des loggers dans les méthodes main et start

- src/main/java/org/junit/Assert.java :
* Refactoring de méthodes : doubleIsDifferent et floatIsDifferent (il y'avait plusieurs conditions if, else inutiles)

- src/main/java/org/junit/ComparisonFailure.java :
* passage d'attributs en mode finals

- src/main/java/org/junit/experimental/theories/Theories.java :
* changement d'opérateurs dans la méthode validateParameterSupplier

- src/main/java/org/junit/experimental/theories/internal/AllMembersSupplier.java
* suppression de paramétres non utilisés dans les méthodes : getDataPointsMethods, getSingleDataPointFields, getDataPointsFields et getSingleDataPointMethods

- src/main/java/org/junit/experimental/theories/internal/Assignments :
* quelques variables avaient la même appellation que certains attributs
* changement de la valeur de retour dans la méthode getConstructorParameterCount
* suppression de paramétres non utilisés dans la méthode getArgumentStrings

- src/main/java/org/junit/internal/InexactComparisonCriteria.java :
* changement de déclaration d'attributs

- src/main/java/org/junit/internal/runners/ClassRoadie.java :
* extraction d'une partie de la méthode runBefores dans une méthode helper, pour faciliter la lisibilité du code, et enlever les code smells.

- src/main/java/org/junit/internal/runners/JUnit38ClassRunner.java :
* changement de noms de variables dans la méthode filter

- src/main/java/org/junit/internal/runners/MethodValidator.java :
* Utilisation de la méthode isEmpty() plutot que vérification de la taille manuellement

- src/main/java/org/junit/internal/runners/TestMethod.java :
* Changement de la valeur de retour dans la methode getTimeout()
