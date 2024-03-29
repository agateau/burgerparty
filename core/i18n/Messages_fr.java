// This file has been dynamically generated by po-compile.
// Do not modify it, all changes will be lost.

public class Messages_fr extends com.greenyetilab.linguaj.Messages {
    public Messages_fr() {
        plainEntries.put("%dx combo!", "%dx combo !");
        plainEntries.put("About", "A propos");
        plainEntries.put("All Stars #%d", "Toutes les étoiles #%d");
        plainEntries.put("Burger Apprentice", "Apprenti");
        plainEntries.put("Burger God", "Dieu des Burgers");
        plainEntries.put("Burger Master", "Maître Burger");
        plainEntries.put("Buy Burger Party goodies!", "Achetez des goodies\nBurger Party !");
        plainEntries.put("Close Call", "C'était juste");
        plainEntries.put("Code & Design", "Code & Design");
        plainEntries.put("Congratulations, you finished level %d-%d!", "Félicitations, vous avez terminé le niveau %d-%d !");
        plainEntries.put("Congratulations, you finished the game!", "Félicitations, vous avez terminé le jeu !");
        plainEntries.put("Congratulations, you finished world %d!", "Félicitations, vous avez terminé le monde %d !");
        plainEntries.put("Creative", "Créatif");
        plainEntries.put("Evening Gamer", "Joueur du soir");
        plainEntries.put("Fan", "Fan");
        plainEntries.put("Finish all levels of world %d with 3 stars.", "Finir tous les niveaux du monde %d avec 3 étoiles.");
        plainEntries.put("Follow me on Mastodon", "Suivez-moi sur Mastodon");
        plainEntries.put("Fonts", "Fontes");
        plainEntries.put("Game Over", "Game Over");
        plainEntries.put("Get a perfect in all levels of world %d.", "Obtenir un Perfect dans tous les niveaux du monde %d.");
        plainEntries.put("Goodies", "Goodies");
        plainEntries.put("Happy customer!", "Client satisfait !");
        plainEntries.put("High score: %d", "Record : %d");
        plainEntries.put("Level %d-%d", "Niveau %d-%d");
        plainEntries.put("Like the game? Give it a good rate!", "Vous aimez le jeu ? Donnez-lui une bonne note !");
        plainEntries.put("Like the game? Support my work!", "Vous aimez ce jeu ? Soutenez mon travail !");
        plainEntries.put("Mastodon", "Mastodon");
        plainEntries.put("Morning Gamer", "Joueur du matin");
        plainEntries.put("Music", "Musique");
        plainEntries.put("New High Score!", "Nouveau record !");
        plainEntries.put("New item unlocked!", "Nouvel ingrédient débloqué !");
        plainEntries.put("No high score yet", "Pas encore de record");
        plainEntries.put("Paused", "Pause");
        plainEntries.put("Perfect #%d", "Perfect #%d");
        plainEntries.put("Practice Area", "Laboratoire");
        plainEntries.put("Rate Burger Party", "Noter Burger Party");
        plainEntries.put("Sound", "Effets sonores");
        plainEntries.put("Sound is OFF", "Effets sonores désactivés");
        plainEntries.put("Sound is ON", "Effets sonores activés");
        plainEntries.put("Sounds", "Effets sonores");
        plainEntries.put("Star Collector", "Collectionneur d'étoiles");
        plainEntries.put("Support Burger Party", "Soutenir Burger Party");
        plainEntries.put("Testers", "Testeurs");
        plainEntries.put("Thank you for playing!", "Merci d'avoir joué !");
        plainEntries.put("Thomas Tripon", "Thomas Tripon");
        plainEntries.put("Version %s", "Version %s");
        plainEntries.put("Who made this?", "Qui a fait ce jeu ?");
        pluralEntries.put(
            new PluralId("+%# sec", "+%# sec"),
            new String[] {
                "+%# seconde",
                "+%# secondes",
            }
        );
        pluralEntries.put(
            new PluralId("1 remaining.", "%# remaining."),
            new String[] {
                "Encore 1.",
                "Encore %#.",
            }
        );
        pluralEntries.put(
            new PluralId("ignore-close-call", "Finish a level with less than %# seconds left."),
            new String[] {
                "-",
                "Finir un niveau avec moins de %# secondes restantes.",
            }
        );
        pluralEntries.put(
            new PluralId("ignore-collect", "Collect %# stars."),
            new String[] {
                "-",
                "Récupérer %# étoiles.",
            }
        );
        pluralEntries.put(
            new PluralId("ignore-creative", "Create %# different burgers in the practice area."),
            new String[] {
                "-",
                "Créer %# burgers différents dans le laboratoire.",
            }
        );
        pluralEntries.put(
            new PluralId("ignore-evening", "Start a game between 7PM and 11PM for %# days."),
            new String[] {
                "-",
                "Jouer entre 19:00 et 23:00 pendant %# jours.",
            }
        );
        pluralEntries.put(
            new PluralId("ignore-fan", "Play %# levels."),
            new String[] {
                "-",
                "Jouer à %# niveaux.",
            }
        );
        pluralEntries.put(
            new PluralId("ignore-morning", "Start a game between 7AM and 10AM for %# days."),
            new String[] {
                "-",
                "Jouer entre 7:00 et 10:00 pendant %# jours.",
            }
        );
        pluralEntries.put(
            new PluralId("ignore-n-burgers", "Serve %# burgers."),
            new String[] {
                "-",
                "Servir %# burgers.",
            }
        );
        pluralEntries.put(
            new PluralId("ignore-practice", "Play %# levels to unlock the practice area."),
            new String[] {
                "-",
                "Jouer %# niveaux pour débloquer le laboratoire.",
            }
        );
    }

    @Override
    public int plural(int n) {
        return (n > 1) ? 1 : 0;
    }
}