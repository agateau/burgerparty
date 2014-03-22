public class Messages_fr extends com.greenyetilab.linguaj.Messages {
    public Messages_fr() {
        plainEntries.put("New High Score!", "Nouveau record !");
        plainEntries.put("Congratulations, you finished level %d-%d!", "Félicitations, vous avez terminé le niveau %d-%d !");
        plainEntries.put("Congratulations, you finished world %d!", "Félicitations, vous avez terminé le monde %d !");
        plainEntries.put("Congratulations, you finished the game!", "Félicitations, vous avez terminé le jeu !");
        plainEntries.put("Level %d-%d", "Niveau %d-%d");
        plainEntries.put("High score: %d", "Record : %d");
        plainEntries.put("No high score yet", "Pas encore de record");
        plainEntries.put("Paused", "Pause");
        plainEntries.put("Game Over", "Game Over");
        plainEntries.put("Burger Master", "Maître Burger");
        plainEntries.put("Burger God", "Dieu des Burgers");
        plainEntries.put("Practice Area", "Laboratoire");
        plainEntries.put("Star Collector", "Collectionneur d'étoiles");
        plainEntries.put("Close Call", "C'était juste");
        plainEntries.put("Morning Gamer", "Joueur du matin");
        plainEntries.put("Evening Gamer", "Joueur du soir");
        plainEntries.put("%dx combo!", "%dx combo !");
        plainEntries.put("Happy customer!", "Client satisfait !");
        plainEntries.put("New item unlocked!", "Nouvel ingrédient débloqué !");
        plainEntries.put("Version %s", "Version %s");
        plainEntries.put("Code & Design", "Code & Design");
        plainEntries.put("Aurélien Gâteau", "Aurélien Gâteau");
        plainEntries.put("Music", "Musique");
        plainEntries.put("Thomas Tripon", "Thomas Tripon");
        plainEntries.put("Testers", "Testeurs");
        plainEntries.put("Clara Gâteau\\nAntonin Gâteau\\nGwenaëlle Gâteau\\nMathieu Maret\\nAnd many others!", "Clara Gâteau\\nAntonin Gâteau\\nGwenaëlle Gâteau\\nMathieu Maret\\nEt de nombreux autres !");
        pluralEntries.put(
            new PluralId("1 remaining.", "%n remaining."),
            new String[] {
                "Encore 1.",
                "Encore %n.",
            }
        );
        pluralEntries.put(
            new PluralId("ignore-n-burgers", "Serve %n burgers."),
            new String[] {
                "-",
                "Servir %n burgers.",
            }
        );
        pluralEntries.put(
            new PluralId("ignore-practice", "Collect %n stars to unlock the practice area."),
            new String[] {
                "-",
                "Récupérer %n étoiles pour débloquer le laboratoire.",
            }
        );
        pluralEntries.put(
            new PluralId("ignore-collect", "Collect %n stars."),
            new String[] {
                "-",
                "Récupérer %n étoiles.",
            }
        );
        pluralEntries.put(
            new PluralId("ignore-close-call", "Finish a level with less than %n seconds left."),
            new String[] {
                "-",
                "Finir un niveau avec moins de %n secondes restantes.",
            }
        );
        pluralEntries.put(
            new PluralId("ignore-morning", "Start a game between 7AM and 10AM for %n days."),
            new String[] {
                "-",
                "Jouer entre 7:00 et 10:00 pendant %n jours.",
            }
        );
        pluralEntries.put(
            new PluralId("ignore-evening", "Start a game between 7PM and 11PM for %n days."),
            new String[] {
                "-",
                "Jouer entre 19:00 et 23:00 pendant %n jours.",
            }
        );
    }

    @Override
    public int plural(int n) {
        return (n > 1) ? 1 : 0;
    }
}