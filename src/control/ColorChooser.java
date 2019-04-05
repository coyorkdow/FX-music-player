package control;

public class ColorChooser {
    private int[][] list = new int[100][3];
    final private int size = 100;
    private int curIndex;

    public ColorChooser(int n) {
        for (int i = 0; i < size; i++) {
            if (i < size * 2 / 7) {
                list[i][0] = 255;
                list[i][1] = 255 * i * 7 / (size * 2);
                list[i][2] = 0;
            } else if (i < size * 3 / 7) {
                list[i][0] = 255 - 255 * (i - size * 2 / 7) * 7 / size;
                list[i][1] = 255;
                list[i][2] = 0;
            } else if (i < size * 4 / 7) {
                list[i][0] = 0;
                list[i][1] = 255;
                list[i][2] = 255 * (i - size * 3 / 7) * 7 / size;
            } else if (i < size * 5 / 7) {
                list[i][0] = 0;
                list[i][1] = 255 - 255 * (i - size * 4 / 7) * 7 / size;
                list[i][2] = 255;
            } else if (i < size * 6 / 7) {
                list[i][0] = 150 * (i - size * 5 / 7) * 7 / size;
                list[i][1] = 0;
                list[i][2] = 255;
            } else {
                list[i][0] = 150 + 155 * (i - size * 6 / 7) * 7 / size;
                list[i][1] = 0;
                list[i][2] = 255 - 255 * (i - size * 6 / 7) * 7 / size;
            }
        }
        curIndex = n;
    }

    final public void setColor(int n) {
        this.curIndex = n;
    }

    final public String getColor() {
        return "rgb(" + list[this.curIndex][0] + ',' + list[this.curIndex][1] + ',' + list[this.curIndex][2] + ')';
    }

    final public String nextColor() {
        curIndex++;
        if (curIndex == this.size)
            curIndex = 0;
        return "rgb(" + list[this.curIndex][0] + ',' + list[this.curIndex][1] + ',' + list[this.curIndex][2] + ')';
    }
}