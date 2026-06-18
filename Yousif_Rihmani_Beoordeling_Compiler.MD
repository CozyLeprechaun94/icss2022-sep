# Eigen beoordeling compiler opdracht APP
 - Student: Yousif Rihmani
 - Versie: 1
 - Klas: ITA-CNI-B-f
 - Docent: Chris van Uffelen
 - Datum: 1-4-2026
 - Studentennummer: 2121923

## 4.1 Algemene eisen (0 punten)
De code die je oplevert is een uitbreiding op de bij de opdracht beschikbaar gestelde startcode. Voor de code gelden de volgende algemene eisen.

| ID | Omschrijving | Prio | Punten | Comp. VT |
| :--- | :--- | :--- | :--- | :--- |
| **AL01** | De code behoudt de packagestructuur van de aangeleverde startcode. Toegevoegde code bevindt zich in de relevante packages. | Must | 0 | APP-6 |
| **AL02** | Alle code compileert en is te bouwen met Maven 3.6+ onder OpenJDK 13. Tip: voer `mvn clean` uit voor het inleveren. Gebruik van Oracle Java is niet toegestaan. | Must | 0 | n.v.t. |
| **AL03** | De code is goed geformatteerd, voorzien van commentaar en correcte variabelenamen. Geen onnodig ingewikkelde constructies (onderhoudbaar). | Must | 0 | n.v.t. |
| **AL04** | De docent stelt vast dat de compiler eigen werk is en voldoet aan APP-6 (kennis van architectuur en basisbegrippen zoals syntaxis/semantiek). | Must | 0 | APP-6 |

---

## 4.2 Parseren (40 punten) (40 punten behaald)

| ID | Omschrijving | Prio | Punten | Comp. VT | Behaalde punten |
| :--- | :--- | :--- | :--- | :--- | :--- | 
| **PA00** | De parser maakt zinvol gebruik van een eigen stack generic voor `ASTNode` (bijv. `IHANStack<ASTNode>`). | Must | 0 | APP-1, 9 | 0 |
| **PA01** | Implementeer grammatica + listener voor "eenvoudige opmaak" (zie `level0.icss`). `testParseLevel0()` slaagt. | Must | 10 | APP-6, 7 | 10 | 
| **PA02** | Breid uit met assignments en gebruik van variabelen (zie `level1.icss`). `testParseLevel1()` slaagt. | Must | 10 | APP-6, 7 | 10 |
| **PA03** | Breid uit met rekenoperaties (+, -, *) inclusief voorrangsregels en associativiteit (zie `level2.icss`). `testParseLevel2()` slaagt. | Must | 10 | APP-6, 7 | 10 |
| **PA04** | Breid uit met if/else-statements (zie `level3.icss`). `testParseLevel3()` slaagt. | Must | 10 | APP-6, 7 | 10 |
| **PA05** | PA01 t/m PA04 leveren samen minimaal 30 punten op. | Must | 0 | nvt | 0 |

---

## 4.3 Checken (30 punten) (25 punten behaald)

| ID | Omschrijving | Prio | Punten | Comp. VT | Behaalde punten |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **CH00** | Minimaal vier van onderstaande checks moeten zijn geïmplementeerd. | Must | 0 | APP-2,6,7 | 0 |
| **CH01** | Controleer op het gebruik van ongedefinieerde variabelen. | Should | 5 | | 5 |
| **CH02** | Controleer of operanden van + en - van gelijk type zijn. Bij * moet minimaal één operand scalair zijn (geen `2px * 3px`). | Should | 5 | | 5 |
| **CH03** | Controleer dat er geen kleuren worden gebruikt in rekenkundige operaties (+, -, *). | Should | 5 | | 5 |
| **CH04** | Controleer of het type van de waarde klopt bij de property (bijv. geen `width: #ff0000`). | Should | 5 | | 0 |
| **CH05** | Controleer of de conditie bij een if-statement van het type boolean is. | Should | 5 | | 5 |
| **CH06** | Controleer of variabelen enkel binnen hun scope gebruikt worden. | Must | 5 | | 5 |

---

## 4.4 Transformeren (20 punten) (20 punten behaald)

| ID | Omschrijving | Prio | Punten | Comp. VT | Behaalde punten |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **TR01** | **Evaluator:** Vervang alle `Expression` knopen door een `Literal` knoop met de berekende waarde. | Must | 10 | APP-2,6,7 | 10 |
| **TR02** | **Evaluator:** Verwerk `IfClause`. Bij `TRUE` vervangen door de body; bij `FALSE` door de `ElseClause` (of verwijderen indien geen else). | Must | 10 | APP-2,6,7 | 10 |

---

## 4.5 Genereren (10 punten) (10 punten behaald)

| ID | Omschrijving | Prio | Punten | Comp. VT | Behaalde punten |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **GE01** | Implementeer de `Generator` die de AST omzet naar een CSS2-compliant string. | Must | 5 | APP-2,6,7 | 5 |
| **GE02** | Genereer CSS met exact twee spaties inspringing per scopeniveau. | Must | 5 | APP-2,6,7 | 5 |

---

## 4.6 Eigen uitbreidingen (20 punten) (0 punten behaald)
* **Niet Gedaan:** 0 punten