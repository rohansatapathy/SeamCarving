import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        Picture p = new Picture("landscape.jpg");
        SeamCarver carver = new SeamCarver(p);

        System.out.println(carver.width() * carver.height());

        System.out.println(Arrays.toString(carver.findVerticalSeam()));
        carver.removeVerticalSeam(carver.findVerticalSeam());
        carver.picture().show();

    }
}
