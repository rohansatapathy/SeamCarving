import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class SeamCarverGUI extends JFrame implements ActionListener {

    private final SeamCarver carver;

    public static void main(String[] args) {
        String filename = "landscape2.jpg";
        SeamCarverGUI gui = new SeamCarverGUI(filename);
        gui.setVisible(true);
        gui.repaint();
    }

    public SeamCarverGUI(String filename) {
        super();

        this.carver = new SeamCarver(new Picture(filename));

        // Set up menu item for image saving
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        menuBar.add(menu);
        JMenuItem menuItem1 = new JMenuItem("Save");
        menuItem1.addActionListener(this);
        // Add keyboard shortcut for save action
        menuItem1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        menu.add(menuItem1);
        this.setJMenuBar(menuBar);

        this.setContentPane(carver.picture().getJLabel());
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                Dimension d = e.getComponent().getBounds().getSize();
                resizeImage(d);
            }
        });  // To respond to resize events
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setTitle(filename);
        this.setResizable(true);
        this.pack();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        FileDialog chooser = new FileDialog(this,
                "Use a .png or .jpg extension", FileDialog.SAVE);
        chooser.setVisible(true);
        if (chooser.getFile() != null) {
            carver.picture().save(chooser.getDirectory() + File.separator + chooser.getFile());
        }
    }

    public void resizeImage(Dimension newSize) {

        if (newSize.width < carver.width()) {
            // Remove vertical seams to compensate
            System.out.println("Need to remove vertical seams.");
            for (int i = 0; i < carver.width() - newSize.width; i++) {
                carver.removeVerticalSeam(carver.findVerticalSeam());
            }
        }
        if (newSize.height < carver.height()) {
            System.out.println("Need to remove horizontal seams.");
            // Remove horizontal seams to compensate
            for (int i = 0; i < carver.height() - newSize.height; i++) {
                carver.removeHorizontalSeam(carver.findHorizontalSeam());
            }
        }

        // Reset the image shown in the frame
        this.setContentPane(carver.picture().getJLabel());
        this.validate();
        this.repaint();
    }

}
