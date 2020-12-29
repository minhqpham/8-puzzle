import java.util.*;
import java.io.*;

class Puzzle {
  
  static int[][] goal = {{0,1,2},{3,4,5},{6,7,8}};
  
  static int[] dirX = {1,0,-1,0};
  static int[] dirY = {0,1,0,-1};
  
  static int[][] setState(String state) {
    int[][] board = new int[3][3];
    
    int row = 0;
    int col = 0;
    
    for (int i = 0; i < state.length(); i++) {
      char ch = state.charAt(i);
      
      if (ch == '-') {
        row++;
        col = 0;
      }
      
      else {
        if (ch == 'b')
          board[row][col] = 0;
        else
          board[row][col] = Character.getNumericValue(ch);
        col++;
      }
    }
    return board;
  }
  
  static int[] findBlank(int[][] board) {
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        if (board[i][j] == 0) {
          return new int[] {i, j};
        }
      }
    }
    return new int[] {-1, -1};
  }
    
  static void printState(int[][] board) {
    for (int row = 0; row < 3; row++)
      System.out.println(board[row][0] + " " + board[row][1] + " " + board[row][2]);
  }
  
  static boolean isValid(int x, int y, int moveX, int moveY) {
    return ((x+moveX) >= 0 && (x+moveX) < 3 && (y+moveY) >= 0 && (y+moveY) < 3);
  }
  
  static int[][] move(Node node, int moveX, int moveY) {
    int x = node.row + moveX;
    int y = node.col + moveY;
    
    Node temp = new Node(node.board, node.parent, node.cost, node.level, node.row, node.col);
    
    int[][] board = temp.board;
    board[temp.row][temp.col] = board[x][y];
    board[x][y] = 0;
    return board;
  }
  
  static int[][] randomizeState(int n) {
    Random rand = new Random();
    
    int[][] board = {{0,1,2},{3,4,5},{6,7,8}};
    Node node = new Node(board, null, 0, 0, 0, 0);
    
    int i = 0;
    while (i < n) {
      int ind = rand.nextInt(4);
      
      if (isValid(node.row, node.col, dirX[ind], dirY[ind])) {
        i++;
        board = move(node, dirX[ind], dirY[ind]);
        node = new Node(board, null, 0, 0, node.row+dirX[ind], node.col+dirY[ind]);
      }
    }
    return board;
  }
  
  static int calculateCost(String str, int[][] board) {
    int count = 0;
    
    if (str == "h1") {
      for (int row = 0; row < board.length; row++) {
        for (int col = 0; col < board[0].length; col++) {
          if (board[row][col] != 0 && board[row][col] != goal[row][col]) {
            count++;
          }
        }
      }
      return count;
    }
    
    else {
      for (int row = 0; row < 3; row++) {
        for (int col = 0; col < 3; col++) {
          if (board[row][col] != 0) {
            int hor = board[row][col] % 3;
            int ver = board[row][col] / 3;
            count += Math.abs(hor-col) + Math.abs(ver-row);
          }
        }
      }
      return count;
    }
  }
  
  static boolean contains(List<int[][]> list, int[][] board) {
    boolean b;
    
    for (int i = 0; i < list.size(); i++) {
      int[][] matrix = list.get(i);
      b = true;
      
      for (int row = 0; row < 3; row++) {
        for (int col = 0; col < 3; col++) {
          if (matrix[row][col] != board[row][col]) 
            b = false;
        }
      }
      if (b == true)
        return true;
    }
    return false;
  }
  
  static boolean compare(int[][] matrix, int[][] board) {
    for (int row = 0; row < 3; row++) {
      for (int col = 0; col < 3; col++) {
        if (matrix[row][col] != board[row][col]) 
          return false;
      }
    }
      
    return true;
  }
  
  static void solveAstar(String str, int[][] board, int capacity) {
    PriorityQueue<Node> queue = new PriorityQueue<Node>(new NodeComparator());
    
    List<int[][]> list = new ArrayList<int[][]>();
    list.add(board);
    
    int[] arr = findBlank(board);
    int cost = calculateCost(str, board);
    Node root = new Node(board, null, cost, 0, arr[0], arr[1]);
    queue.add(root);
    
    while (!queue.isEmpty()) {
      Node min = queue.poll();
      
      if (min.cost == 0) {
        System.out.println("Number of moves: " + min.level);
        printPath(min);
        return;
      }
      
      else {
        for (int i = 0; i < 4; i++) {
          if (isValid(min.row, min.col, dirX[i], dirY[i])) {
            int[][] matrix = move(min, dirX[i], dirY[i]);
            
            if ((min.parent != null && !compare(min.parent.board, matrix)) || !contains(list, matrix)) {
              list.add(matrix);
              
              cost = calculateCost(str, matrix);
              Node child = new Node(matrix, min, cost, min.level+1, min.row+dirX[i], min.col+dirY[i]);
              queue.add(child);
              
              if (list.size() > capacity) {
                System.out.println("The puzzle either is unsolvable or takes too long to be solved.");
                return;
              }
            }
          }
        }
      }
    }
  }
  
  static void solveBeam(int k, int[][] board, int capacity) {
    PriorityQueue<Node> queue = new PriorityQueue<Node>(new NodeComparator2());
    
    List<int[][]> list = new ArrayList<int[][]>();
    list.add(board);
    
    int[] arr = findBlank(board);
    int cost = calculateCost("h1", board) + calculateCost("h2", board);
    Node root = new Node(board, null, cost, 0, arr[0], arr[1]);
    queue.add(root);
    
    boolean reachGoal = false;
    while (!reachGoal) {
      PriorityQueue<Node> neighbor = new PriorityQueue<Node>(new NodeComparator2());
      
      while (!queue.isEmpty()) {
        Node min = queue.poll();
        
        if (min.cost == 0) {
          System.out.println("Solved! Number of moves: " + min.level);
          printPath(min);
          reachGoal = true;
          return;
        }
        
        else {
          for (int i = 0; i < 4; i++) {
            if (isValid(min.row, min.col, dirX[i], dirY[i])) {
              
              int[][] matrix = move(min, dirX[i], dirY[i]);
              if (!contains(list, matrix)) {
                list.add(matrix);
                
                cost = calculateCost("h1", matrix) + calculateCost("h2", matrix);
                Node child = new Node(matrix, min, cost, min.level+1, min.row+dirX[i], min.col+dirY[i]);
                neighbor.add(child);
                
                if (list.size() > capacity) {
                  System.out.println("The puzzle either is unsolvable or takes too long to be solved.");
                  return;
                }
              }
            }
          }
        }
      }
      for (int j = 0; j < k; j++) {
        if (!neighbor.isEmpty()) {
          Node toAdd = neighbor.poll();
          queue.add(toAdd);
        }
      }
    }
  }
  
  static void printPath(Node node) {
    if (node == null)
      return;
    printPath(node.parent);
    printState(node.board);
    //System.out.println("h-score : " + node.cost);
    //System.out.println("f-score: " + (node.cost + node.level));
    System.out.println();
  }
  
  static void execute(String name) {
    try {
      File file = new File(name);
      Scanner scan = new Scanner(file);
      int[][] board = {};
      
      while (scan.hasNext()) {
        String str = scan.next();
        
        if (str.equals("randomizeState")) {
          System.out.println("Current state being randomized...");
          int n = Integer.parseInt(scan.next());
          board = randomizeState(n);
        }
        
        if (str.equals("setState")) {
          String state = scan.next();
          System.out.println("Input state: " + state);
          board = setState(state);
        }
        
        if (str.equals("move")) {
          int[] arr = findBlank(board);
          Node node = new Node(board, null, 0, 0, arr[0], arr[1]);
          
          String dir = scan.next();
          System.out.println("Move performed: " + dir);
          
          if (dir.equals("down")) {
            if (isValid(arr[0], arr[1], dirX[0], dirY[0])) {
              System.out.println("Performed!");
              System.out.println();
              board = move(node, dirX[0], dirY[0]);
            }
            else {
              System.out.println("Unable to perform this move.");
              System.out.println();
            }
          }
          
          if (dir.equals("right")) {
            if (isValid(arr[0], arr[1], dirX[1], dirY[1])) {
              System.out.println("Performed!");
              System.out.println();
              board = move(node, dirX[1], dirY[1]);
            }
            else {
              System.out.println("Unable to perform this move.");
              System.out.println();
            }
          }
          
          if (dir.equals("up")) {
            if (isValid(arr[0], arr[1], dirX[2], dirY[2])) {
              System.out.println("Performed!");
              System.out.println();
              board = move(node, dirX[2], dirY[2]);
            }
            else {
              System.out.println("Unable to perform this move.");
              System.out.println();
            }
          }
          
          if (dir.equals("left")) {
            if (isValid(arr[0], arr[1], dirX[3], dirY[3])) {
              System.out.println("Performed!");
              System.out.println();
              board = move(node, dirX[3], dirY[3]);
            }
            else {
              System.out.println("Unable to perform this move.");
              System.out.println();
            }
          }
        }
        
        if (str.equals("printState")) {
          System.out.println("Current state:");
          printState(board);
          System.out.println();
        }
        
        if (str.equals("solveAstar")) {
          String heu = scan.next();
          int cap = Integer.parseInt(scan.next());
          if (heu.equals("h1"))
            solveAstar("h1", board, cap);
          else if (heu.equals("h2"))
            solveAstar("h2", board, cap);
          else
            System.out.println("Sorry, there is no such heuristic.");
        }
        
        if (str.equals("solveBeam")) {
          int k = Integer.parseInt(scan.next());
          int cap = Integer.parseInt(scan.next());
          solveBeam(k, board, cap);
        }
      }
    } catch (Exception e) {
      System.out.println("Sorry, command is invalid.");
    }
  }
    
  public static void main(String[] args) {
    execute("test.txt");
  }
}