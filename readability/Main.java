package readability;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    static Scanner scanner = new Scanner(System.in);

    static int sentences;
    static int words;
    static int chars;
    static int[] syllables;
    static double l;
    static double s;

    static int avgAge;
    static int ageCounter;

    public static void main(String[] args) {
        String text;

        // read text from file
        try (Scanner scanner = new Scanner(new File("." + File.separator + args[0]))) {
            text = scanner.nextLine();
        } catch (IOException e) {
            System.out.println("There was an error reading the file: " + e.getMessage());
            return;
        }

        System.out.println("The text is:\n" + text + "\n");

        sentences = text.split("[?.!]").length;
        words = text.split("\\s+").length;
        chars = text.replaceAll("\\s+", "").toCharArray().length;
        syllables = syllables(text);
        l = (double) chars / words *  100;
        s = (double) sentences / words * 100;

        System.out.println("Words: " + words + "\n" +
                "Sentences: " + sentences + "\n" +
                "Characters: " + chars + "\n" +
                "Syllables: " + syllables[0] + "\n" +
                "Polysyllables: " + syllables[1] + "\n");

        System.out.println("Enter the score you want to calculate (ARI, FK, SMOG, CL, all):");
        double score;
        switch (scanner.nextLine().toUpperCase()) {
            case "ARI":
                ari();
                break;
            case "FK":
                fk();
                break;
            case "SMOG":
                smog();
                break;
            case "CL":
                cl();
                break;
            case "ALL":
                ari();
                fk();
                smog();
                cl();
                break;
            default:
                return;
        }

        System.out.println("\nThis text should be understood in average by " + (double) avgAge / ageCounter + " year olds.");
    }

    public static void ari() {
        print(4.71 * chars / words + 0.5 * words / sentences - 21.43, "Automated Readability Index");
    }

    public static void fk() {
        print(0.39 * words / sentences + 11.8 * syllables[0] / words - 15.59, "Flesch–Kincaid readability tests");
    }

    public static void smog() {
        print(1.043 * Math.sqrt((double) syllables[1] * 30 / sentences) + 3.1291, "Simple Measure of Gobbledygook");
    }

    public static void cl() {
        print(0.0588 * l - 0.296 * s - 15.8, "Coleman–Liau index");
    }

    public static void print(double score, String type) {
        final int[] AGE_RANGE = {5, 6, 7, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 24};
        System.out.printf(type + ": %.2f (about %d year olds).\n", score, AGE_RANGE[Math.min((int) Math.round(score), 13)]);
        avgAge += AGE_RANGE[Math.min((int) Math.round(score), 13)];
        ageCounter++;
    }

    public static int[] syllables(String text) {
        int totalSyllables = 0;
        int polySyllables = 0;

        for (String word : text.replaceAll("[.?!]", "").split("\\s+")) {
            int syllables = 0;
            boolean lastWasSyllable = false;

            for (int i = 0; i < word.length(); i++) {
                if (word.toLowerCase().charAt(i) == 'a' || word.toLowerCase().charAt(i) == 'e' || word.toLowerCase().charAt(i) == 'i' || word.toLowerCase().charAt(i) == 'o' ||
                        word.toLowerCase().charAt(i) == 'u' || word.toLowerCase().charAt(i) == 'y') {
                    // e at end of word does not count
                    if (!(i == word.length() - 1 && word.charAt(i) == 'e') && !lastWasSyllable) {
                        lastWasSyllable = true;
                        syllables++;
                    }
                } else {
                    lastWasSyllable = false;
                }
            }

            if (syllables == 0) {
                syllables = 1;
            }

            if (syllables > 2) {
                polySyllables++;
            }
            totalSyllables += syllables;
        }

        return new int[]{totalSyllables, polySyllables};
    }
}
