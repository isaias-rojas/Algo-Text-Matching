import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TextSimilarityChecker {

    private static final int SIMILARITY_THRESHOLD = 55;
    private static final int MIN_WORD_LENGTH_FOR_LEVENSHTEIN = 3;

    public static String readFile(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    public static double calculateSimilarity(String text1, String text2) {
        String[] words1 = text1.split("\\s+");
        String[] words2 = text2.split("\\s+");

        int[][] dp = new int[words1.length + 1][words2.length + 1];

        for (int i = 1; i <= words1.length; i++) {
            for (int j = 1; j <= words2.length; j++) {
                dp[i][j] = calculateCommonWords(dp, words1, words2, i, j);
            }
        }

        int commonWords = dp[words1.length][words2.length];
        return (double) (commonWords * 100) / words2.length;
    }

    private static int calculateCommonWords(int[][] dp, String[] words1, String[] words2, int i, int j) {
        if (words1[i - 1].equalsIgnoreCase(words2[j - 1])) {
            return dp[i - 1][j - 1] + 1;
        } else if (words1[i - 1].length() > words2[j - 1].length() &&
                calculateLevenshteinDistance(words1[i - 1].toLowerCase(), words2[j - 1].toLowerCase()) >= SIMILARITY_THRESHOLD) {
            return dp[i - 1][j - 1] + 1;
        } else {
            return Math.max(dp[i - 1][j], dp[i][j - 1]);
        }
    }

    public static void identifyMisspelledWords(String text1, String text2) {
        String[] sentences1 = text1.split("\\.\\s*");
        String[] sentences2 = text2.split("\\.\\s*");

        for (String sentence2 : sentences2) {
            for (String sentence1 : sentences1) {
                double similarity = calculateSimilarity(sentence1, sentence2);
                if (similarity >= SIMILARITY_THRESHOLD) {
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
            for (String word1 : words1) {
                if (word1.equalsIgnoreCase(word2)) {
                    break;
                }
                // Check if the second word is longer than a threshold before calculating Levenshtein distance
                if (word2.length() > MIN_WORD_LENGTH_FOR_LEVENSHTEIN &&
                        calculateLevenshteinDistance(word1.toLowerCase(), word2.toLowerCase()) >= SIMILARITY_THRESHOLD) {
                    System.out.println(word2 + " - " + word1);
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

            double similarityPercentage = calculateSimilarity(text1, text2);
            System.out.println("Similarity Percentage: " + similarityPercentage + "%");

            identifyMisspelledWords(text1, text2);
        } catch (IOException e) {
            System.err.println("Error reading files: " + e.getMessage());
        }
    }
}
