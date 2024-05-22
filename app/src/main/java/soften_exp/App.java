package soften_exp;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

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

    }

    // ------------------ Class ------------------

    String filePath = "in.txt";
    Map<String, Map<String, Integer>> graph = new HashMap<String, Map<String, Integer>>();
    Set<String> words = new LinkedHashSet<String>();

    Set<String> visitedEdges = new HashSet<String>();
    Set<String> visitedWords = new LinkedHashSet<String>();

    /**
     * 创建初始 GUI
     * 
     * @param frame
     */
    private void createInitialGUI(JFrame frame) {
        // create the welcome screen
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        JPanel panel = new JPanel();

        // set the layout of the panel
        panel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.gridx = 0;

        int i = 0;

        // print welcome message
        JLabel welcome_label = new JLabel();
        welcome_label.setText("Welcome to the Software Engineering Experiment App!");
        constraints.gridy = i++;
        panel.add(welcome_label, constraints);

        // add blank space
        constraints.gridy = i++;
        panel.add(Box.createVerticalStrut(10), constraints);

        // print file path
        JLabel file_path_label = new JLabel();
        if (this.filePath == null) {
            file_path_label.setText("No file selected.");
        } else {
            file_path_label.setText("File: " + this.filePath);
        }
        constraints.gridy = i++;
        panel.add(file_path_label, constraints);

        // add blank space
        constraints.gridy = i++;
        panel.add(Box.createVerticalStrut(10), constraints);

        // add a button to select a file
        JButton select_file_button = new JButton("Select File");
        select_file_button.addActionListener(e -> {
            createGetFileGUI(frame);
            file_path_label.setText("File: " + this.filePath);
        });
        constraints.gridy = i++;
        panel.add(select_file_button, constraints);

        // add blank space
        constraints.gridy = i++;
        panel.add(Box.createVerticalStrut(10), constraints);

        // add a button to load the file
        JButton load_file_button = new JButton("Load File");
        load_file_button.addActionListener(e -> {
            loadFile(true);
        });
        constraints.gridy = i++;
        panel.add(load_file_button, constraints);

        // add blank space
        constraints.gridy = i++;
        panel.add(Box.createVerticalStrut(10), constraints);

        // add paint graph button
        JButton paint_graph_button = new JButton("Paint Graph");
        paint_graph_button.addActionListener(e -> {
            printGraph();
        });
        constraints.gridy = i++;
        panel.add(paint_graph_button, constraints);
        
        // add blank space
        constraints.gridy = i++;
        panel.add(Box.createVerticalStrut(10), constraints);

        // add button to search bridge words
        JButton search_bridge_words_button = new JButton("Search Bridge Words");
        search_bridge_words_button.addActionListener(e -> {
            String word1 = JOptionPane.showInputDialog(frame, "Enter the first word:");
            String word2 = JOptionPane.showInputDialog(frame, "Enter the second word:");
            String output = queryBridgeWords(word1, word2);
            // create a new frame to display the output, vercital layout
            JFrame output_frame = new JFrame("Bridge Words Output");
            JPanel output_panel = new JPanel();
            JLabel output_label = new JLabel();
            JButton exiButton = new JButton("Exit");
            exiButton.addActionListener(e1 -> {
                output_frame.dispose();
            });
            output_label.setText(output);
            output_panel.add(output_label);
            output_panel.add(exiButton);
            output_frame.add(output_panel);
            output_frame.setSize(500, 200);
            output_frame.setVisible(true);
        });
        constraints.gridy = i++;
        panel.add(search_bridge_words_button, constraints);
        
        // add blank space
        constraints.gridy = i++;
        panel.add(Box.createVerticalStrut(10), constraints);

        // add button to generate new text
        JButton generate_new_text_button = new JButton("Generate New Text");
        generate_new_text_button.addActionListener(e -> {
            String inputText = JOptionPane.showInputDialog(frame, "Enter the input text:");
            String output = generateNewText(inputText);
            // create a new frame to display the output, vercital layout
            JFrame output_frame = new JFrame("New Text Output");
            JPanel output_panel = new JPanel();
            JLabel output_label = new JLabel();
            JButton exiButton = new JButton("Exit");
            exiButton.addActionListener(e1 -> {
                output_frame.dispose();
            });
            output_label.setText(output);
            output_panel.add(output_label);
            output_panel.add(exiButton);
            output_frame.add(output_panel);
            output_frame.setSize(500, 200);
            output_frame.setVisible(true);
        });
        constraints.gridy = i++;
        panel.add(generate_new_text_button, constraints);

        // add blank space
        constraints.gridy = i++;
        panel.add(Box.createVerticalStrut(10), constraints);

        // add button to calculate shortest path
        JButton calc_shortest_path_button = new JButton("Calculate Shortest Path");
        calc_shortest_path_button.addActionListener(e -> {
            String word1 = JOptionPane.showInputDialog(frame, "Enter the first word:");
            String word2 = JOptionPane.showInputDialog(frame, "Enter the second word: (can be empty to calculate all paths from the first word)");
            String output = calcShortestPath(word1, word2);
            System.out.println(output);
            // create a new frame to display the output
            if (word2 == null || word2.isEmpty()) {
                // for each path, add a button to paint the path
                JFrame output_frame = new JFrame("Shortest Path Output");
                JPanel output_panel = new JPanel();
                String[] paths = output.split("\n");
                for (String path : paths) {
                    String[] path_words = path.split(" -> ");
                    String lastWord = path_words[path_words.length - 1];
                    JButton paint_path_button = new JButton("Paint Path to " + lastWord);
                    paint_path_button.addActionListener(e1 -> {
                        visitedEdges.clear();
                        visitedWords.clear();
                        for (String word : path_words) {
                            visitedWords.add(word);
                        }
                        for (int j = 0; j < path_words.length - 1; j++) {
                            visitedEdges.add(path_words[j] + " " + path_words[j + 1]);
                        }
                        printGraph();
                    });
                    output_panel.add(paint_path_button);
                }
                JButton exiButton = new JButton("Exit");
                exiButton.addActionListener(e1 -> {
                    output_frame.dispose();
                });
                output_panel.add(exiButton);
                output_frame.add(output_panel);
                output_frame.setSize(500, 400);
                output_frame.setVisible(true);
            } else {
                // print the path
                // layout is 2xn grid
                JFrame output_frame = new JFrame("Shortest Path Output");
                JPanel output_panel = new JPanel();
                JButton exiButton = new JButton("Exit");
                // for each path, there is a "\n" to separate
                String[] paths = output.split("\n");
                int j = 0;
                for (String path : paths) {
                    JLabel output_label = new JLabel();
                    output_label.setText(path);
                    output_panel.add(output_label);
                    // add a button to paint the path
                    JButton paint_path_button = new JButton("Paint Path " + j++);
                    paint_path_button.addActionListener(e1 -> {
                        String[] path_words = path.split(" -> ");
                        visitedEdges.clear();
                        visitedWords.clear();
                        for (String word : path_words) {
                            visitedWords.add(word);
                        }
                        for (int k = 0; k < path_words.length - 1; k++) {
                            visitedEdges.add(path_words[k] + " " + path_words[k + 1]);
                        }
                        printGraph();
                    });
                    output_panel.add(paint_path_button);
                }
                // add a button to exit
                exiButton.addActionListener(e1 -> {
                    output_frame.dispose();
                });
                output_panel.add(exiButton);
                output_frame.add(output_panel);
                output_frame.setSize(500, 400);
                output_frame.setVisible(true);
            }
        });
        constraints.gridy = i++;
        panel.add(calc_shortest_path_button, constraints);

        // add blank space
        constraints.gridy = i++;
        panel.add(Box.createVerticalStrut(10), constraints);

        // add random walk button
        JButton random_walk_button = new JButton("Random Walk");
        random_walk_button.addActionListener(e -> {
            String output = randomWalk();
            // create a new frame to display the output, vercital layout
            JFrame output_frame = new JFrame("Random Walk Output");
            JPanel output_panel = new JPanel();
            JLabel output_label = new JLabel();
            JButton exiButton = new JButton("Exit");
            exiButton.addActionListener(e1 -> {
                output_frame.dispose();
            });
            output_label.setText(output);
            output_panel.add(output_label);
            output_panel.add(exiButton);
            output_frame.add(output_panel);
            output_frame.setSize(500, 200);
            output_frame.setVisible(true);
            // set the visited edges and visited words
            visitedEdges.clear();
            visitedWords.clear();
            for (String word : output.split(" ")) {
                visitedWords.add(word);
            }
            for (int j = 0; j < output.split(" ").length - 1; j++) {
                visitedEdges.add(output.split(" ")[j] + " " + output.split(" ")[j + 1]);
            }
            printGraph();
            // add a button to save the output text
            JButton save_button = new JButton("Save");
            save_button.addActionListener(e1 -> {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showSaveDialog(output_frame);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    try {
                        file.createNewFile();
                        // write the output to the file
                        FileWriter fileWriter = new FileWriter(file);
                        fileWriter.write(output);
                        fileWriter.close();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                }
            });
            output_panel.add(save_button);
        });
        constraints.gridy = i++;
        panel.add(random_walk_button, constraints);
        
        // add blank space
        constraints.gridy = i++;
        panel.add(Box.createVerticalStrut(10), constraints);

        // add quit button
        JButton quit_button = new JButton("Quit");
        quit_button.addActionListener(e -> {
            System.exit(0);
        });
        constraints.gridy = i++;
        panel.add(quit_button, constraints);

        frame.add(panel);
        frame.setVisible(true);
    }

    /**
     * 创建获取文件 GUI
     * 
     * @param frame
     */
    private void createGetFileGUI(JFrame frame) {
        // create a file choooser
        JFileChooser fileChooser = new JFileChooser();

        // set the current project directory to the current run directory
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir") + "/src/main/resources"));

        int returnValue = fileChooser.showOpenDialog(frame);

        // if the user selects a file, then set the file_path
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            this.filePath = fileChooser.getSelectedFile().getAbsolutePath();
        } else {
            JOptionPane.showMessageDialog(frame, "No file selected. Exiting.");
            // System.exit(0);
        }
    }

    /**
     * 加载文件
     */
    public void loadFile(boolean newLoad) {
        // read the file line by line
        List<String> temp_words = new ArrayList<String>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(this.filePath));

            String line = reader.readLine();
            while (line != null) {
                System.out.println(line);

                // split the line by the non-word characters, and add to words
                String[] line_words = line.split("\\W+");
                for (String word : line_words) {
                    temp_words.add(word.toLowerCase());
                }
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (newLoad) {
            this.graph = new HashMap<String, Map<String, Integer>>();
            this.words = new LinkedHashSet<String>();
        }

        // add the words to the graph
        for (int i = 0; i < temp_words.size() - 1; i++) {
            String word1 = temp_words.get(i);
            String word2 = temp_words.get(i + 1);
            this.words.add(word1);
            this.words.add(word2);
            if (this.graph.get(word1) == null) {
                Map<String, Integer> temp = new HashMap<String, Integer>();
                temp.put(word2, 1);
                this.graph.put(word1, temp);
            } else {
                Map<String, Integer> temp = this.graph.get(word1);
                if (temp.get(word2) == null) {
                    temp.put(word2, 1);
                } else {
                    temp.put(word2, temp.get(word2) + 1);
                }
                this.graph.put(word1, temp);
            }
        }
    }

    /**
     * 查询桥接词
     * 
     * @param word1
     * @param word2
     * @return 桥接词
     * 
     */
    public String queryBridgeWords(String word1, String word2) {
        String output = null;
        /* 查找输入词汇是否在图中出现 */
        if (!this.words.contains(word1) || !this.words.contains(word2)) {
            output = "No " + (!this.words.contains(word1) ? "\"" + word1 + "\"" : "") + (!this.words.contains(word1) && !this.words.contains(word2) ? " and " : "")
                    + (!this.words.contains(word2) ? "\"" + word2 + "\"" : "") + " in the graph!";
            return output;
        }

        // 如果存在桥接词，即中间有一个词相连
        Set<String> bridgeWords = new HashSet<String>();
        Map<String, Integer> firstWord_map = this.graph.get(word1);
        for (String word : this.words) {
            if (firstWord_map.get(word) != null && this.graph.get(word).get(word2) != null) {
                bridgeWords.add(word);
            }
        }
        if (bridgeWords.size() > 0) {
            output = "The bridge words from \"" + word1 + "\" to \"" + word2 + "\" are: ";
            for (String word : bridgeWords) {
                output = output + word + " ";
            }
            return output;
        }

        // 如果不存在桥借词 即相连在一起
        output = "No bridge words from \"" + word1 + "\" to \"" + word2 + "\"!";
        return output;
    }

    /**
     * 生成新文本, 插入随机桥接词
     * 
     * @param inputText
     * @return 新文本
     */
    String generateNewText(String inputText) {
        List<String> newWordList = new ArrayList<String>();
        String[] temp_words = inputText.split("\\W+");
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

    /**
     * 获取第一个单词
     * 
     * @param str
     * @return 第一个单词
     */
    public String getFirstWord(String str) {
        // 通过空格分割字符串并返回第一个单词
        if (str == null || str.isEmpty()) {
            return "";
        }
        String[] words = str.split("\\s+");
        return words[0];
    }

    /**
     * 获取最后一个单词
     * 
     * @param str
     * @return 最后一个单词
     */
    public String getLastWord(String str) {
        // 通过空格分割字符串并返回最后一个单词
        if (str == null || str.isEmpty()) {
            return "";
        }
        String[] words = str.split("\\s+");
        return words[words.length - 1];
    }

    /**
     * 计算最短路径
     * 
     * @param word1
     * @param word2
     * @return 最短路径
     */
    String calcShortestPath(String word1, String word2) {
        final int MAXN = 0x3f3f3f3f;
        // 将graph转化为距离矩阵
        int[][] distance = new int[this.words.size()][this.words.size()];
        for (int i = 0; i < this.words.size(); i++) {
            for (int j = 0; j < this.words.size(); j++) {
                distance[i][j] = MAXN;
            }
        }

        List<String> wordList = new ArrayList<>(this.words);
        for (int i = 0; i < wordList.size(); i++) {
            String firstWord = wordList.get(i);
            for (int j = 0; j < wordList.size(); j++) {
                String secondWord = wordList.get(j);
                if (firstWord.equals(secondWord)) {
                    distance[i][j] = 0;
                    continue;
                }
                if (this.graph.get(firstWord) != null && this.graph.get(firstWord).get(secondWord) != null) {
                    distance[i][j] = this.graph.get(firstWord).get(secondWord);
                }
            }
        }

        int firstIndex = wordList.indexOf(word1);
        List<List<List<String>>> result = solvePath(firstIndex, distance);
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
            int secondIndex = wordList.indexOf(word2);
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

    /**
     * 计算输入单词到其他所有词的最短路径
     * 
     * @param startIndex
     * @param distance
     * @return 最短路径
     */
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
                findPaths(startIndex, endIndex, predecessors, new ArrayList<String>(this.words), currentPath,
                        result.get(endIndex));
            }
        }
        return result;
    }

    /**
     * 递归查找路径
     * 
     * @param start
     * @param end
     * @param predecessors
     * @param wordList
     * @param currentPath
     * @param resultPaths
     */
    public void findPaths(int start, int end, List<List<Integer>> predecessors, List<String> wordList,
            List<String> currentPath, List<List<String>> resultPaths) {
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

    public String randomWalk() {
        String output = "";

        Set<String> visited = new HashSet<>();

        // 随机选择一个起始词
        List<String> wordList = new ArrayList<>(this.words);
        int startIndex = (int) (Math.random() * wordList.size());
        String currentWord = wordList.get(startIndex);
        output += currentWord;
        visited.add(currentWord);

        // 随机选择下一个词
        while (true) {
            Map<String, Integer> neighbors = this.graph.get(currentWord);
            if (neighbors == null) {
                break;
            }

            List<String> neighborList = new ArrayList<>(neighbors.keySet());
            int nextIndex = (int) (Math.random() * neighborList.size());
            String nextWord = neighborList.get(nextIndex);
            output += " " + nextWord;

            // 如果当前词和下一个词都在 visited 中，结束
            if (visited.contains(nextWord)) {
                break;
            }

            currentWord = nextWord;
            visited.add(currentWord);
        }

        return output;
    }

    /**
     * 打印图
     *
     */
    public void printGraph() {
        // /* test the visited edges and visited words */
        // visitedEdges.clear();
        // visitedWords.clear();
        // for (String firstWord : words) {
        //     for (String secondWord : words) {
        //         if (graph.get(firstWord) != null && graph.get(firstWord).get(secondWord) != null) {
        //             /* randomly set some edges to be visited, random rate is 0.1 */
        //             if (Math.random() < 0.1) {
        //                 visitedEdges.add(firstWord + " " + secondWord);
        //                 visitedWords.add(firstWord);
        //                 visitedWords.add(secondWord);
        //             }
        //         }
        //     }
        // }

        DirectedGraphSwing graphPanel = new DirectedGraphSwing(words, graph, visitedEdges, visitedWords);
        JFrame frame = new JFrame("Directed Graph with Weights");
        frame.add(graphPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);

        // add a button to quit
        JPanel panel = new JPanel();
        JButton quit_button = new JButton("Quit");
        quit_button.addActionListener(e -> {
            frame.dispose();
        });
        panel.add(quit_button);
        
        // add a save button
        JButton save_button = new JButton("Save");
        save_button.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showSaveDialog(frame);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                graphPanel.saveImage(file);
            }
        });
        panel.add(save_button);

        frame.add(panel);

        frame.setVisible(true);
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

class DirectedGraphSwing extends JPanel {

    private static final int WIDTH = 1000;
    private static final int HEIGHT = 800;
    private static final int RADIUS = 20;
    private static final double REPULSION_CONSTANT = 4000;
    private static final double SPRING_CONSTANT = 0.32;
    private static final double DAMPING = 0.32;
    private static final int ITERATIONS = 5000;

    private Set<String> words;
    private Map<String, Map<String, Integer>> connections;

    private Set<String> visitedEdges;
    private Set<String> visitedWords;

    private Map<String, Point> positions;

    /* how to create a panel */
    // JFrame frame = new JFrame("Directed Graph with Weights");
    // DirectedGraphSwing graphPanel = new DirectedGraphSwing(words, connections);
    // frame.add(graphPanel);
    // frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    // frame.pack();
    // frame.setLocationRelativeTo(null);
    // frame.setVisible(true)

    public DirectedGraphSwing(Set<String> words, Map<String, Map<String, Integer>> connections,
            Set<String> visitedEdges, Set<String> visitedWords) {
        this.words = words;
        this.connections = connections;
        this.visitedEdges = visitedEdges;
        this.visitedWords = visitedWords;
        this.positions = calculateLayout(words, connections);
        normalizePositions();
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
    }

    private Map<String, Point> calculateLayout(Set<String> words, Map<String, Map<String, Integer>> connections) {
        Map<String, Point> positions = new HashMap<>();
        Map<String, double[]> velocities = new HashMap<>();

        int gridSize = (int) Math.ceil(Math.sqrt(words.size()));
        int cellSize = Math.min(WIDTH, HEIGHT) / gridSize;

        // Initialize positions and velocities on a grid
        int index = 0;
        for (String word : words) {
            int row = index / gridSize;
            int col = index % gridSize;
            positions.put(word, new Point(cellSize * col + cellSize / 2, cellSize * row + cellSize / 2));
            velocities.put(word, new double[] { 0, 0 });
            index++;
        }

        // Run the force-directed algorithm
        for (int i = 0; i < ITERATIONS; i++) {
            // Calculate repulsion forces
            for (String word1 : words) {
                Point pos1 = positions.get(word1);
                double[] force = new double[] { 0, 0 };

                for (String word2 : words) {
                    if (!word1.equals(word2)) {
                        Point pos2 = positions.get(word2);
                        double dx = pos1.x - pos2.x;
                        double dy = pos1.y - pos2.y;
                        double distance = Math.sqrt(dx * dx + dy * dy);
                        if (distance < 0.1) {
                            distance = 0.1;
                        }
                        double repulsion = REPULSION_CONSTANT / Math.pow(distance, 3);
                        force[0] += repulsion * (dx / distance);
                        force[1] += repulsion * (dy / distance);
                    }
                }

                // Calculate attraction forces
                Map<String, Integer> neighbors = connections.get(word1);
                if (neighbors != null) {
                    for (Map.Entry<String, Integer> entry : neighbors.entrySet()) {
                        String word2 = entry.getKey();
                        Point pos2 = positions.get(word2);
                        double dx = pos1.x - pos2.x;
                        double dy = pos1.y - pos2.y;
                        double distance = Math.sqrt(dx * dx + dy * dy);
                        if (distance < 0.1) {
                            distance = 0.1;
                        }
                        double attraction = SPRING_CONSTANT * entry.getValue() * Math.pow(distance, 1);
                        force[0] -= attraction * (dx / distance);
                        force[1] -= attraction * (dy / distance);
                    }
                }

                // Update velocities and positions
                double[] velocity = velocities.get(word1);
                velocity[0] = (velocity[0] + force[0]) * DAMPING;
                velocity[1] = (velocity[1] + force[1]) * DAMPING;
                pos1.x += velocity[0];
                pos1.y += velocity[1];
            }
        }

        return positions;
    }

    private void normalizePositions() {
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;

        // Find the bounding box of the current positions
        for (Point p : positions.values()) {
            if (p.x < minX) {
                minX = p.x;
            }
            if (p.x > maxX) {
                maxX = p.x;
            }
            if (p.y < minY) {
                minY = p.y;
            }
            if (p.y > maxY) {
                maxY = p.y;
            }
        }

        // Calculate scale and translation to fit the positions in the panel
        int panelWidth = WIDTH - 2 * RADIUS;
        int panelHeight = HEIGHT - 2 * RADIUS;
        double scaleX = panelWidth / (double) (maxX - minX);
        double scaleY = panelHeight / (double) (maxY - minY);

        // Apply scaling and translation
        for (Map.Entry<String, Point> entry : positions.entrySet()) {
            Point p = entry.getValue();
            p.x = (int) (RADIUS + scaleX * (p.x - minX));
            p.y = (int) (RADIUS + scaleY * (p.y - minY));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Draw edges
        for (Map.Entry<String, Map<String, Integer>> entry : connections.entrySet()) {
            String from = entry.getKey();
            for (Map.Entry<String, Integer> edge : entry.getValue().entrySet()) {
                String to = edge.getKey();
                int weight = edge.getValue();
                Point fromPos = positions.get(from);
                Point toPos = positions.get(to);

                // Draw edge
                if (visitedEdges.contains(from + " " + to)) {
                    g2d.setColor(Color.RED);
                } else {
                    g2d.setColor(Color.BLACK);
                }
                g2d.draw(new Line2D.Double(fromPos.x, fromPos.y, toPos.x, toPos.y));
                // Draw weight
                float rate = 0.2f;
                int midX = fromPos.x + (int) ((toPos.x - fromPos.x) * rate);
                int midY = fromPos.y + (int) ((toPos.y - fromPos.y) * rate);
                g2d.setColor(Color.RED);
                g2d.drawString(String.valueOf(weight), midX, midY);
            }
        }

        // Draw nodes
        for (String word : words) {
            Point pos = positions.get(word);
            if (visitedWords.contains(word)) {
                g2d.setColor(Color.RED);
            } else {
                g2d.setColor(Color.BLUE);
            }
            g2d.fillOval(pos.x - RADIUS / 2, pos.y - RADIUS / 2, RADIUS, RADIUS);
            g2d.setColor(Color.BLACK);
            g2d.drawString(word, pos.x - RADIUS / 2, pos.y - RADIUS / 2);
        }
    }
    
    public void saveImage(File file) {
        try {
            BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();
            paintComponent(g2d);
            g2d.dispose();
            ImageIO.write(image, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
