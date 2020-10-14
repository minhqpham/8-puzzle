import java.util.*;

class NodeComparator2 implements Comparator<Node> {
  public int compare(Node n1, Node n2) {
    if (n1.cost < n2.cost)
      return -1;
    else if (n1.cost > n2.cost) 
      return 1;
    else
      return 0;
  }
}

