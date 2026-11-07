import java.util.Random;
import java.util.Scanner;

public class MagicNumber {

    public static void main(String[] args) {

        Random random = new Random();
        Scanner scanner = new Scanner(System.in);

        int secret = random.nextInt(100) + 1;
        int attempts = 0;

        System.out.println("=== MAGIC NUMBER ===");
        System.out.println("Devinez le nombre entre 1 et 100.");

        while (true) {

            System.out.print("> ");

            if (!scanner.hasNextInt()) {
                System.out.println("Veuillez entrer un nombre.");
                scanner.next();
                continue;
            }

            int guess = scanner.nextInt();
            attempts++;

            if (guess < secret) {
                System.out.println("Plus grand !");
            } else if (guess > secret) {
                System.out.println("Plus petit !");
            } else {
                System.out.println(
                        "Bravo ! Vous avez trouvé le nombre "
                                + secret
                                + " en "
                                + attempts
                                + " tentative(s).");
                break;
            }
        }

        scanner.close();
    }
}