package us.screenshottr.imagecompressor;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.net.URL;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ImageCompressor extends JFrame {

    JPanel pane = new JPanel();
    ActionListener actionListener = new IC_Listener(this);
    public static JButton compress = new JButton("Compress Image");

    String author = "ScreenShottr";

    ImageCompressor() {
        super("ScreenShottr - Image Compressor");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(screenSize.width / 2, screenSize.height / 2, 400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container con = this.getContentPane();
        con.add(pane);
        compress.setMnemonic('M');
        compress.addActionListener(actionListener);
        pane.add(compress);
        compress.requestFocus();
        setVisible(true);
        URL icon = ClassLoader.getSystemResource("us/screenshottr/imagecompressor/resources/logo.png");
        Toolkit kit = Toolkit.getDefaultToolkit();
        Image icon_img = kit.createImage(icon);
        setIconImage(icon_img);
    }

    public static void main(String args[]) {
        new ImageCompressor();
    }
}
