import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        Picture p = new Picture("landscape.jpg");
        SeamCarver carver = new SeamCarver(p);

        System.out.println("landscape.jpg (" + carver.width() + "x" + carver.height() + ")");

        System.out.println(Arrays.toString(carver.findHorizontalSeam()));
        carver.removeHorizontalSeam(carver.findHorizontalSeam());
        System.out.println("landscape.jpg (" + carver.width() + "x" + carver.height() + ")");
        carver.picture().show();

    }
}
