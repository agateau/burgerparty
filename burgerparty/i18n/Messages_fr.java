

public class Messages_fr extends com.agateau.burgerparty.utils.Messages {
    public Messages_fr() {
        plainEntries.put("Burger Master", "Maître Burger");
        plainEntries.put("Burger God", "Dieu des Burgers");
        plainEntries.put("Practice Area", "Laboratoire");
        plainEntries.put("Star Collector", "Collectionneur d'étoiles");
        plainEntries.put("Close Call", "C'était juste");
        plainEntries.put("Morning Gamer", "Joueur du matin");
        plainEntries.put("Evening Gamer", "Joueur du soir");
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