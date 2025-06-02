# Transport Route Planner

Transport Route Planner je JavaFX aplikacija za pronalaženje optimalnih transportnih ruta između gradova, uz podršku za različite kriterijume optimizacije (vreme, cena, broj presedanja). Aplikacija simulira transportnu mrežu sa autobuskim i železničkim stanicama, omogućava generisanje transportnih podataka i kupovinu karata sa generisanjem računa.

## Funkcionalnosti

- **Pretraga ruta**: Pronalaženje optimalne rute između dva grada na osnovu:
  - Najkraćeg vremena putovanja
  - Najniže cene
  - Najmanjeg broja presedanja
- **Prikaz ruta**: Vizuelizacija mreže i optimalne rute na grafičkom platnu.
- **Generisanje podataka**: Automatsko generisanje gradova, stanica i polazaka u JSON formatu.
- **Kupovina karata**: Generisanje računa za kupljene karte sa detaljima o ruti.
- **Statistika**: Prikaz ukupnog broja prodatih karata i prihoda.

## Tehnologije

- **Jezik**: Java
- **GUI**: JavaFX
- **Biblioteke**: org.json (za obradu JSON fajlova)
- **Algoritmi**: Modifikovana Dijkstra-ova pretraga za optimizaciju ruta
- **Build alat**: Maven

## Instalacija

### Preduslovi

- Java 11 ili novija
- Maven (za upravljanje zavisnostima)
- JavaFX SDK (ako nije integrisan u JDK)

### Koraci

1. Kloniraj repozitorijum:

   ```bash
   git clone https://github.com/MladenGrbic/TransportRoutePlanner
   cd transport-route-planner
   ```
2. Konfiguriši zavisnosti u `pom.xml`:

   ```xml
   <dependencies>
       <dependency>
           <groupId>org.openjfx</groupId>
           <artifactId>javafx-controls</artifactId>
           <version>17</version>
       </dependency>
       <dependency>
           <groupId>org.openjfx</groupId>
           <artifactId>javafx-fxml</artifactId>
           <version>17</version>
       </dependency>
       <dependency>
           <groupId>org.json</groupId>
           <artifactId>json</artifactId>
           <version>20231013</version>
       </dependency>
   </dependencies>
   ```
3. Kompajliraj i pokreni aplikaciju:

   ```bash
   mvn clean javafx:run
   ```
4. (Opcionalno) Ako koristiš IDE (npr. IntelliJ), uključi JavaFX modul i pokreni `TransportMain.java`.

## Korišćenje

1. **Pokretanje aplikacije**:
   - Pokreni `TransportMain` klasu.
   - Unesi dimenzije mreže (broj redova i kolona) u prozoru za unos veličine.
2. **Generisanje podataka**:
   - Klikom na "Kreiraj" generiše se transportna mreža i čuva u `src/main/resources/transport_data.json`.
3. **Pretraga ruta**:
   - Izaberi početni i krajnji grad iz padajućih menija.
   - Odaberi kriterijum (vreme, cena, presedanja) i klikni "Pronađi rute".
   - Optimalna ruta se prikazuje na platnu, uz detalje (vreme, cena, presedanja).
4. **Kupovina karte**:
   - Klikni "Kupi" za generisanje računa, koji se čuva u direktorijumu `racuni`.
5. **Statistika**:
   - Klikom na "Prikaži statistiku" prikazuje se ukupni broj prodatih karata i prihod.

## Struktura projekta

```
transport-route-planner/
├── src/
│   ├── java/
│   │       ├── main/
│   │   │   │   ├── model/         # Klase za modelovanje (City, Ticket...)
│   │   │   │   │   ├── controller/    # JavaFX kontroleri (RoutePlanningController...)
│   │   │   │   │   ├── transport/    # Logika ruta (Network, Route...)
│   │   │   │   │   ├── util/          # Uslužne klase (JsonLoader, TicketUtil...)
│   │   │   │   └── TransportMain.java
│   │   │   ├── dataGenerator/     # Generisanje podataka (TransportDataGenerator)
│   │   └── resources/
│   │       ├── MapSize-fxml-       # FXML za unos dimenzija mreže
│   │       ├── RoutePlanning.fxml # FXML za pretraga ruta
│   │       ├── StatisticsPane.fxml # FXML za statistiku
│   │       └── transport_data.json
│   └── test/                      # Testovi (tren implementirani)
├── racuni/                        # Direktorijum za račune
├── pom.xml                        # Maven konfiguracija
└── README.md
```

## Algoritmi

Aplikacija koristi modifikovanu Dijkstra-ovu pretragu sa heuristikom Manhattan distance za pronalaženje optimalnih ruta. Detaljnije objašnjenje algoritama nalazi se u izveštaju.

## Doprinos

1. Fork-uj repozitorijum.
2. Kreiraj novu granu (`git checkout -b feature/nova-funkcionalnost`).
3. Izvrši izmene i commit-uj (`git commit -m 'Dodaj novu funkcionalnost'`).
4. Push-uj na granu (`git push origin feature/nova-funkcionalnost`).
5. Otvori Pull Request.

## Kontakt

Za pitanja ili sugestije, otvori issue u repozitorijumu ili kontaktiraj autora putem GitHub-a.