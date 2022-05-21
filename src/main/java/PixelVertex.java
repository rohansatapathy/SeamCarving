public class PixelVertex {

    private int x;
    private int y;
    private double weight;
    private int inDegree;

    public PixelVertex(int x, int y, double energy) {
        this.x = x;
        this.y = y;
        this.weight = energy;
    }

    public PixelVertex(PixelVertex v) {
        this.x = v.getX();
        this.y = v.getY();
        this.weight = v.getWeight();
        this.inDegree = 0;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public double getWeight() {
        return weight;
    }

    public int getInDegree() { return inDegree; }

    public void setInDegree(int n) {
        this.inDegree = n;
    }

    public void incrInDegree() {
        inDegree++;
    }

    public void decrInDegree() {
        inDegree--;
    }

    @Override
    public String toString() {
        return "PixelVertex<coords=(" + x + ", " + y + "), weight=" + weight + ">";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PixelVertex that = (PixelVertex) o;

        if (getX() != that.getX()) return false;
        if (getY() != that.getY()) return false;
        return Double.compare(that.getWeight(), getWeight()) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = getX();
        result = 31 * result + getY();
        temp = Double.doubleToLongBits(getWeight());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
