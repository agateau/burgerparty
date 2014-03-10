

public class Messages_fr extends com.agateau.burgerparty.utils.Messages {
    public Messages_fr() {
        plainEntries.put("Burger Master", "Maître Burger");
        plainEntries.put("Serve 50 burgers", "Servir 50 burgers");
        plainEntries.put("Burger God", "Dieu des Burgers");
        plainEntries.put("Serve 100 burgers", "Servir 100 burgers");
        plainEntries.put("Close Call", "C'était juste");
        plainEntries.put("Finish a level with 3 seconds left", "Finir un niveau avec 3 secondes restantes");
        pluralEntries.put(
            new PluralId("1 remaining.", "%n remaining."),
            new String[] {
                "1 restant.",
                "%n restants.",
            }
        );
    }

    @Override
    public int plural(int n) {
        return (n > 1) ? 1 : 0;
    }
}