import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class TextSimilarityChecker {
    public static String readFile(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString().toLowerCase();
    }

    public static double calculateSimilarity(String text1, String text2) {
        int[][] dp = new int[text1.length() + 1][text2.length() + 1];
        for (int i = 1; i <= text1.length(); i++) {
            for (int j = 1; j <= text2.length(); j++) {
                if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }
        return (double) dp[text1.length()][text2.length()] / text1.length() * 100;
    }

    public static void identifyMisspelledWords(String text1, String text2) {
        String[] sentences1 = text1.split("\\.\\s*");
        String[] sentences2 = text2.split("\\.\\s*");

        for (String sentence2 : sentences2) {
            for (String sentence1 : sentences1) {
                double similarity = calculateSimilarity(sentence1, sentence2);
                if (similarity >= 55) {
                    identifyWords(sentence1, sentence2);
                    break;
                }
            }
        }
    }

    private static void identifyWords(String sentence1, String sentence2) {
        String[] words1 = sentence1.split("\\s+");
        String[] words2 = sentence2.split("\\s+");

        for (String word2 : words2) {
            boolean foundMatch = false;
            for (String word1 : words1) {
                if (word1.equals(word2)) continue;
                if (calculateLevenshteinDistance(word1, word2) >= 55) {
                    System.out.println(word2 + " - " + word1);
                    foundMatch = true;
                    break;
                }
            }
        }
    }

    private static int calculateLevenshteinDistance(String word1, String word2) {
        int[][] dp = new int[word1.length() + 1][word2.length() + 1];
        for (int i = 0; i <= word1.length(); i++) {
            for (int j = 0; j <= word2.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j], Math.min(dp[i][j - 1], dp[i - 1][j - 1]));
                }
            }
        }
        int maxLen = Math.max(word1.length(), word2.length());
        return (maxLen - dp[word1.length()][word2.length()]) * 100 / maxLen;
    }

    public static void main(String[] args) {
        try {
            String text1 = readFile("/home/isaverse/IdeaProjects/ProjectAlgoI/file1.txt");
            String text2 = readFile("/home/isaverse/IdeaProjects/ProjectAlgoI/file2.txt");

            double similarityPercentage = calculateSimilarity(text2, text1);
            System.out.println("Similarity Percentage: " + similarityPercentage + "%");

            identifyMisspelledWords(text1, text2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
