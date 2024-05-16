package soften_exp;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.List;
import java.util.*;

/**
 * @author tjh && hyb
 */
public class App {

    // ------------------ Main ------------------
    public static void main(String[] args) {
        App app = new App();

        // create the initial GUI
        JFrame frame = new JFrame("SoftEng Exp App");
        app.createInitialGUI(frame);

        // if the file_path are set, then load the file
        if (app.filePath == null) {
            // create a dialog box to get the path and filename
            // app.createGetFileGUI(frame);
        }
    }

// ------------------ Class ------------------

    String filePath = null;
    HashMap<Tuple<String, String>, Integer> graph = new HashMap<Tuple<String, String>, Integer>();
//    HashSet<String> words = new HashSet<String>();

    List<String> words = new ArrayList<String>();

    private void createInitialGUI(JFrame frame) {
        // create the welcome screen
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 200);
        JPanel panel = new JPanel();

        // set the layout of the panel
        panel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.gridx = 0;

        // print welcome message
        JLabel welcome_label = new JLabel();
        welcome_label.setText("Welcome to the Software Engineering Experiment App!");
        constraints.gridy = 0;
        panel.add(welcome_label, constraints);

        // add blank space
        constraints.gridy = 1;
        panel.add(Box.createVerticalStrut(10), constraints);

        // print file path
        JLabel file_path_label = new JLabel();
        if (this.filePath == null) {
            file_path_label.setText("No file selected.");
        } else {
            file_path_label.setText("File: " + this.filePath);
        }
        constraints.gridy = 2;
        panel.add(file_path_label, constraints);

        // add blank space
        constraints.gridy = 3;
        panel.add(Box.createVerticalStrut(10), constraints);

        // add a button to select a file
        JButton select_file_button = new JButton("Select File");
        select_file_button.addActionListener(e -> {
            createGetFileGUI(frame);
            file_path_label.setText("File: " + this.filePath);
        });
        constraints.gridy = 4;
        panel.add(select_file_button, constraints);

        // add blank space
        constraints.gridy = 5;
        panel.add(Box.createVerticalStrut(10), constraints);

        // add a button to load the file
        JButton load_file_button = new JButton("Load File");
        load_file_button.addActionListener(e -> {
            loadFile();
        });
        constraints.gridy = 6;
        panel.add(load_file_button, constraints);

        // add blank space
        constraints.gridy = 7;
        panel.add(Box.createVerticalStrut(10), constraints);

        // add quit button
        JButton quit_button = new JButton("Quit");
        quit_button.addActionListener(e -> {
            System.exit(0);
        });
        constraints.gridy = 8;
        panel.add(quit_button, constraints);

        frame.add(panel);
        frame.setVisible(true);
    }

    private void createGetFileGUI(JFrame frame) {
        // create a file choooser
        JFileChooser fileChooser = new JFileChooser();

        // set the current directory to the current run directory
        fileChooser.setCurrentDirectory(new File("."));

        int returnValue = fileChooser.showOpenDialog(frame);

        // if the user selects a file, then set the file_path
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            this.filePath = fileChooser.getSelectedFile().getAbsolutePath();
        } else {
            JOptionPane.showMessageDialog(frame, "No file selected. Exiting.");
            System.exit(0);
        }
    }

    public void loadFile() {
        // create a list of words to store the words

        // read the file line by line
        try {
//            BufferedReader reader = new BufferedReader(new FileReader(this.file_path));

            // 用于测试
            InputStream inputStream = App.class.getClassLoader().getResourceAsStream("in.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line = reader.readLine();
            while (line != null) {
                // System.out.println(line);

                // split the line by the non-word characters, and add to words
                String[] temp_words = line.split("\\W+");
                for (String word : temp_words) {
                    words.add(word.toLowerCase());
                }

                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        for (String word : words) {
//            // System.out.println(word);
//            this.words.add(word);
//        }

        // use graph to store the word pairs
        for (int i = 0; i < words.size() - 1; i++) {
            Tuple<String, String> pair = new Tuple<String, String>(words.get(i), words.get(i + 1));
            if (this.graph.containsKey(pair)) {
                this.graph.put(pair, this.graph.get(pair) + 1);
            } else {
                this.graph.put(pair, 1);
            }
        }

        // 去重
        words = removeDuplicates(words);

        // print the graph and words
//        for (Tuple<String, String> pair : this.graph.keySet()) {
//            System.out.println(pair.x + " " + pair.y + " " + this.graph.get(pair));
//        }
//        for (String word : this.words) {
//            System.out.println(word);
//        }
    }

    public List<String> removeDuplicates(List<String> list) {
        // 使用 LinkedHashSet 去重并保持顺序
        Set<String> set = new LinkedHashSet<>(list);

        // 将 Set 转换回 List
        return new ArrayList<>(set);
    }

    public String queryBridgeWords(String word1, String word2) {
        String output = null;
        boolean flag = false;
        // 查找输入词汇是否在图中出现
        if (!this.words.contains(word1)) {
            output = "\"" + word1 + "\"";
            flag = true;
        }
        if (!this.words.contains(word2)) {
            if (flag) {
                output = output + " and \"" + word2 + "\"";
            } else {
                output = "\"" + word2 + "\"";
            }
            flag = true;
        }

        if (flag) {
            output = "No " + output + " in the graph!";
            return output;
        }

        // 如果存在桥接词，即中间有一个词相连
        for (String word : this.words) {
            if (this.graph.get(new Tuple<String, String>(word1, word)) != null && this.graph.get(new Tuple<String, String>(word, word2)) != null) {
                output = "The bridge word between " + word1 + " and " + word2 + " is: " + word;
                return output;
            }
        }

        // 如果不存在桥借词 即相连在一起
        output = "No bridge word from \"" + word1 + "\" to \"" + word2 + "\"!";
        return output;
    }

    String generateNewText(String inputtext) {
        List<String> newWordList = new ArrayList<String>();
        String[] temp_words = inputtext.split("\\W+");
        List<String> output = new ArrayList<String>();
        for (String word : temp_words) {
            newWordList.add(word.toLowerCase());
        }
        for (int i = 0; i < newWordList.size() - 1; i++) {
            String result = queryBridgeWords(newWordList.get(i), newWordList.get(i + 1));
            String firstWord = getFirstWord(result);
            output.add(newWordList.get(i));
            if (!"No".equals(firstWord)) {
                String lastWord = getLastWord(result);
                output.add(lastWord);
            }
        }
        output.add(newWordList.get(newWordList.size() - 1));
        String finalText = String.join(" ", output);
        return finalText;
    }

    public String getFirstWord(String str) {
        // 通过空格分割字符串并返回第一个单词
        if (str == null || str.isEmpty()) {
            return "";
        }
        String[] words = str.split("\\s+");
        return words[0];
    }

    public String getLastWord(String str) {
        // 通过空格分割字符串并返回最后一个单词
        if (str == null || str.isEmpty()) {
            return "";
        }
        String[] words = str.split("\\s+");
        return words[words.length - 1];
    }

    String calcShortestPath(String word1, String word2) {
        final int MAXN = 0x3f3f3f3f;
        // 将graph转化为距离矩阵
        int[][] distance = new int[this.words.size()][this.words.size()];
        for (int i = 0; i < this.words.size(); i++) {
            for (int j = 0; j < this.words.size(); j++) {
                distance[i][j] = MAXN;
            }
        }

        for (String firstWord : this.words) {
            int index = words.indexOf(firstWord);
            for (String scecondWord : this.words) {
                if (firstWord.equals(scecondWord)) {
                    distance[index][index] = 0;
                    continue;
                }
                int index2 = words.indexOf(scecondWord);
                if (this.graph.get(new Tuple<String, String>(firstWord, scecondWord)) != null) {
                    distance[index][index2] = 1;
                }
            }
        }
//        printMatrix(wordList, distance);
        int firstIndex = words.indexOf(word1);
        List<List<List<String>>> result = solvePath(firstIndex, distance);
//        System.out.println(result);
//        String output = "\"" + word1 + " cann't reach to " + "\"" + word2 + "\"";
        String output = "";
        if (word2 == null || word2.isEmpty()) {
            // 将result转化为字符串
            StringBuilder sb = new StringBuilder();
            for (List<List<String>> paths : result) {
                for (List<String> path : paths) {
                    sb.append(String.join(" -> ", path)).append("\n");
                }
            }
            output = sb.toString();
        } else {
            int secondIndex = words.indexOf(word2);
            if (secondIndex == -1) {
                throw new IllegalArgumentException("Word2 not found in the word list.");
            }

            if (secondIndex >= 0 && secondIndex < result.size() && result.get(secondIndex) != null) {
                List<List<String>> word2Result = result.get(secondIndex);
                // 将word2Result转化为字符串
                StringBuilder sb = new StringBuilder();
                for (List<String> path : word2Result) {
                    sb.append(String.join(" -> ", path)).append("\n");
                }
                output = sb.toString();
            }
        }
        if ("".equals(output)) {
            output = "\"" + word1 + "\"" + " cann 't reach to " + "\"" + word2 + "\"";
        }
        return output;
    }

    public List<List<List<String>>> solvePath(int startIndex, int[][] distance) {
        final int MAXN = 0x3f3f3f3f;
        int n = words.size();
        int[] dist = new int[n];
        boolean[] visited = new boolean[n];
        List<List<Integer>> predecessors = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            dist[i] = MAXN;
            visited[i] = false;
            predecessors.add(new ArrayList<>());
        }

        PriorityQueue<Tuple<Integer, Integer>> pq = new PriorityQueue<>(Comparator.comparingInt(t -> t.y));
        dist[startIndex] = 0;
        pq.add(new Tuple<>(startIndex, 0));

        while (!pq.isEmpty()) {
            Tuple<Integer, Integer> current = pq.poll();
            int u = current.x;
            if (visited[u]) {
                continue;
            }
            visited[u] = true;

            for (int v = 0; v < n; v++) {
                if (!visited[v] && distance[u][v] != MAXN) {
                    int newDist = dist[u] + distance[u][v];
                    if (newDist < dist[v]) {
                        dist[v] = newDist;
                        pq.add(new Tuple<>(v, newDist));
                        predecessors.get(v).clear();
                        predecessors.get(v).add(u);
                    } else if (newDist == dist[v]) {
                        predecessors.get(v).add(u);
                    }
                }
            }
        }

        // 构建从起点到所有可达点的路径
        List<List<List<String>>> result = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            result.add(new ArrayList<>());
        }

        for (int endIndex = 0; endIndex < n; endIndex++) {
            if (endIndex != startIndex && dist[endIndex] != MAXN) {
                List<String> currentPath = new ArrayList<>();
                findPaths(startIndex, endIndex, predecessors, words, currentPath, result.get(endIndex));
            }
        }
        return result;
    }

    public void findPaths(int start, int end, List<List<Integer>> predecessors, List<String> wordList, List<String> currentPath, List<List<String>> resultPaths) {
        if (end == start) {
            currentPath.add(wordList.get(end));
            Collections.reverse(currentPath);
            resultPaths.add(new ArrayList<>(currentPath));
            Collections.reverse(currentPath);
            currentPath.remove(currentPath.size() - 1);
            return;
        }

        currentPath.add(wordList.get(end));
        for (int pred : predecessors.get(end)) {
            findPaths(start, pred, predecessors, wordList, currentPath, resultPaths);
        }
        currentPath.remove(currentPath.size() - 1);
    }

    public void printMatrix(List<String> wordList, int[][] distance) {
        StringBuilder formattedOutput = new StringBuilder();
        formattedOutput.append(String.format("%-15s", ""));
        for (String word : wordList) {
            formattedOutput.append(String.format("%-15s", word));
        }
        System.out.println(formattedOutput.toString());
        for (int i = 0; i < distance.length; i++) {
            System.out.print(String.format("%-15s", wordList.get(i))); // 打印左侧列标签
            for (int j = 0; j < distance[i].length; j++) {
                System.out.print(String.format("%-15d", distance[i][j]));
            }
            System.out.println();
        }
    }
}

class Tuple<X, Y> {
    public final X x;
    public final Y y;

    public Tuple(X x, Y y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Tuple)) {
            return false;
        }
        Tuple<?, ?> tuple = (Tuple<?, ?>) o;
        return Objects.equals(x, tuple.x) && Objects.equals(y, tuple.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
