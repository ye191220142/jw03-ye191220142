package example;

public class QuickSorter implements Sorter {

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
        int low = 0, high = ranks.length - 1;
        quickSort(low, high);
    }

    private void quickSort(int low, int high){
        if (low >= high) return;
        int curlow = low + 1, curhigh = high;
        while(curlow < curhigh){
            while(curlow < curhigh && ranks[curlow] <= ranks[low]){
                curlow++;
            }
            while(curlow < curhigh && ranks[curhigh] >= ranks[low]){
                curhigh--;
            }
            if (curlow == curhigh) break;
            swap(curlow, curhigh);
        }
        while(ranks[curhigh] > ranks[low]){
            curhigh--;
        }
        swap(low, curhigh);
        quickSort(low, curhigh - 1);
        quickSort(curhigh + 1, high);
    }

    @Override
    public String getPlan() {
        return this.plan;
    }

}
