import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class SeamCarver {

    private Picture picture;

    public SeamCarver(Picture picture) {
        this.picture = picture;
    }

    public Picture picture() {
        return new Picture(this.picture);
    }

    public int width() {
        return this.picture.width();
    }

    public int height() {
        return this.picture.height();
    }

    public double energy(int x, int y) {
        validateCoords(x, y);
        return Math.pow(xGradient(x, y), 2) + Math.pow(yGradient(x, y), 2);
    }

    private void validateCoords(int x, int y) {
        if (!(x >= 0 && x < width())) {
            throw new IndexOutOfBoundsException(x + " is not a valid x value");
        }
        if (!(y >= 0 && y < height())) {
            throw new IndexOutOfBoundsException(y + " is not a valid y value");
        }
    }

    private double xGradient(int x, int y) {
        // Use x-adjacent pixels for gradient calculation
        // If pixel is on border use pixel from opposite side of image
        int leftX = (x == 0) ? width() - 1 : x - 1;
        int rightX = (x == width() - 1) ? 0 : x + 1;

        Color leftPixel = picture.get(leftX, y);
        Color rightPixel = picture.get(rightX, y);

        return gradient(leftPixel, rightPixel);
    }

    private double yGradient(int x, int y) {
        // Use x-adjacent pixels for gradient calculation
        // If pixel is on border use pixel from opposite side of image
        int topY = (y == 0) ? height() - 1 : y - 1;
        int bottomY = (y == height() - 1) ? 0 : y + 1;

        Color topPixel = picture.get(x, topY);
        Color bottomPixel = picture.get(x, bottomY);

        return gradient(topPixel, bottomPixel);
    }

    private double gradient(Color p1, Color p2) {
        return (
                Math.pow(p1.getRed() - p2.getRed(), 2) +
                Math.pow(p1.getGreen() - p2.getGreen(), 2) +
                Math.pow(p1.getBlue() - p2.getBlue(), 2)
        );
    }

    public int[] findHorizontalSeam() {

        int[] horizontalSeam = new int[width()];

        double[][] dpArr = new double[height()][width()];
        for (double[] row : dpArr) {
            Arrays.fill(row, Double.MAX_VALUE);
        }

        // Keep count of energy paths by adding minimum of left three connecting pixels to
        // current pixel energy
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                if (x == 0) {
                    // Fill with pre-existing energy values
                    dpArr[y][x] = energy(x, y);
                    continue;
                }

                // Default from energy to left, since we know left pixel exists
                dpArr[y][x] = dpArr[y][x-1];
                if (y > 0) {
                    // Upper left pixel exists, so replace current value if it has smaller energy
                    dpArr[y][x] = Math.min(dpArr[y][x], dpArr[y-1][x-1]);
                }
                if (y < height() - 1) {
                    // Bottom left pixel exists, so replace current value if it has smaller energy
                    dpArr[y][x] = Math.min(dpArr[y][x], dpArr[y+1][x-1]);
                }
                // Add energy of current pixel to keep track of total seam weight
                dpArr[y][x] += energy(x, y);
            }
        }

        // Find y value where minimum seam ends (in right column)
        int minSeamY = 0;
        double minEnergy = dpArr[0][width() - 1];
        for (int y = 1; y < height(); y++) {
            if (dpArr[y][width() - 1] < minEnergy) {
                minSeamY = y;
                minEnergy = dpArr[y][width()-1];
            }
        }

        // Traverse back up the array to find the indices of the pixels to remove
        int y = minSeamY;
        boolean hitTop = false, hitBottom = false;
        for (int x = width() - 1; x >= 0; x--) {
            // Check if edges have been reached
            hitTop = hitTop || y == 0;
            hitBottom = hitBottom || y == height() - 1;

            // Add entry to vertical seam
            horizontalSeam[x] = y;

            // Update seam column counter
            if (x > 0) {
                // Use pixel directly to left as default
                double currentEnergy = dpArr[y][x-1];
                if (y > 0 && dpArr[y-1][x-1] < currentEnergy) {
                    currentEnergy = dpArr[y-1][x-1];
                    if (y < height() - 1 && dpArr[y+1][x-1] < currentEnergy) {
                        // Lower left is the minimum
                        y++;
                    } else {
                        // Upper left is the minimum
                        y--;
                    }
                }
            }
        }

        if (hitTop & hitBottom) {
            // Seam is invalid since it spans vertically
            throw new IllegalArgumentException("Invalid horizontal seam.");
        }

        return horizontalSeam;
    }

    public int[] findVerticalSeam() {

        int[] verticalSeam = new int[height()];

        double[][] dpArr = new double[height()][width()];
        for (double[] row : dpArr) {
            Arrays.fill(row, Double.MAX_VALUE);
        }

        // Keep count of energy paths by adding minimum of upper three connecting pixels to
        // current pixel energy
        for (int y = 0; y < height(); y++) {
            for (int x = 0; x < width(); x++) {
                if (y == 0) {
                    // Fill with pre-existing energy values
                    dpArr[y][x] = energy(x, y);
                    continue;
                }

                // Default from energy above, since we know above pixel exists
                dpArr[y][x] = dpArr[y-1][x];
                if (x > 0) {
                    // Upper left pixel exists, so replace current value if it has smaller energy
                    dpArr[y][x] = Math.min(dpArr[y][x], dpArr[y-1][x-1]);
                }
                if (x < width() - 1) {
                    // Upper right pixel exists, so replace current value if it has smaller energy
                    dpArr[y][x] = Math.min(dpArr[y][x], dpArr[y-1][x+1]);
                }
                // Add energy of current pixel to keep track of total seam weight
                dpArr[y][x] += energy(x, y);
            }
        }

        // Find x value where minimum seam ends (in bottom row)
        int minSeamX = 0;
        double minEnergy = dpArr[height() - 1][0];
        for (int x = 1; x < width(); x++) {
            if (dpArr[height() - 1][x] < minEnergy) {
                minSeamX = x;
                minEnergy = dpArr[height() - 1][x];
            }
        }

        // Traverse back up the array to find the indices of the pixels to remove
        int x = minSeamX;
        boolean hitRight = false, hitLeft = false;
        for (int y = height() - 1; y >= 0; y--) {
            // Check if edges have been reached
            hitRight = hitRight || x == width() - 1;
            hitLeft = hitLeft || x == 0;

            // Add entry to vertical seam
            verticalSeam[y] = x;

            // Update seam column counter
            if (y > 0) {
                // Use pixel directly above as default
                double currentEnergy = dpArr[y-1][x];
                if (x > 0 && dpArr[y-1][x-1] < currentEnergy) {
                    currentEnergy = dpArr[y-1][x-1];
                    if (x < width() - 1 && dpArr[y-1][x+1] < currentEnergy) {
                        // Upper right is the minimum
                        x++;
                    } else {
                        // Upper left is the minimum
                        x--;
                    }
                }
            }
        }

        if (hitRight & hitLeft) {
            // Seam is invalid since it spans horizontally
            throw new IllegalArgumentException("Invalid vertical seam.");
        }

        return verticalSeam;
    }

    public void removeHorizontalSeam(int[] seam) {
        Picture newPicture = new Picture(width(), height() - 1);
        for (int x = 0; x < width(); x++) {
            boolean seamReached = false;
            for (int y = 0; y < height(); y++) {
                if (seam[x] == y) {
                    seamReached = true;
                    continue;
                }
                if (seamReached) {
                    newPicture.setRGB(x, y-1, picture.getRGB(x, y));
                } else {
                    newPicture.setRGB(x, y, picture.getRGB(x, y));
                }

            }
        }

        picture = newPicture;
    }

    public void removeVerticalSeam(int[] seam) {
        Picture newPicture = new Picture(width() - 1, height());
        for (int y = 0; y < height(); y++) {
            boolean seamReached = false;
            for (int x = 0; x < width(); x++) {
                if (seam[y] == x) {
                    seamReached = true;
                    continue;
                }
                if (seamReached) {
                    newPicture.setRGB(x-1, y, picture.getRGB(x, y));
                } else {
                    newPicture.setRGB(x, y, picture.getRGB(x, y));
                }
            }
        }

        picture = newPicture;
    }




}
