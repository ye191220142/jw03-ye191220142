package example;

public class SelectSorter implements Sorter {
    
    private int[] ranks;
    private String plan = "";

    @Override
    public void load(int[] ranks) {
        this.ranks = ranks;
    }

    private void swap(int i, int j) {
        int temp;
        temp = ranks[i];
        ranks[i] = ranks[j];
        ranks[j] = temp;
        plan += "" + ranks[i] + "<->" + ranks[j] + "\n";
    }

    @Override
    public void sort() {
        for (int i = 0; i < ranks.length - 1; i++) {
            int maxIndex = 0;
            for (int j = 1; j <= ranks.length - 1 - i; j++) {
                if (ranks[maxIndex] < ranks[j]) {
                    maxIndex = j;
                }
            }
            if (maxIndex != ranks.length - 1 - i) {
                swap(maxIndex, ranks.length - 1 - i);
            }
        }
    }

    @Override
    public String getPlan() {
        return this.plan;
    }

}