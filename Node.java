class Node {
    int[][] board;
    Node parent;
    int cost, level;
    int row, col;
    
    Node(int[][] board, Node parent, int cost, int level, int row, int col) {
      this.board = new int[3][3];
      for (int i = 0; i < 3; i++) 
        for (int j = 0; j < 3; j++) 
          this.board[i][j] = board[i][j];
      this.parent = parent;
      this.cost = cost;
      this.level = level;
      this.row = row;
      this.col = col;
    }
  }

