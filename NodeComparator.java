import java.util.*;

class NodeComparator implements Comparator<Node> {
  public int compare(Node n1, Node n2) {
    if (n1.cost + n1.level < n2.cost + n2.level)
      return -1;
    else if (n1.cost + n1.level > n2.cost + n2.level) 
      return 1;
    else
      return 0;
  }
}