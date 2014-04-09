package us.screenshottr.imagecompressor;

import com.gej.util.ImageTool;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.commons.io.FilenameUtils;

public class ImageCompressor extends JFrame implements ActionListener {

    JPanel pane = new JPanel();
    JButton compress = new JButton("Compress Image");

    double Version = 1.0;
    String Author = "ScreenShottr";

    ImageCompressor() {
        super("ScreenShottr - Image Compressor");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(screenSize.width / 2, screenSize.height / 2, 400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container con = this.getContentPane();
        con.add(pane);
        compress.setMnemonic('M');
        compress.addActionListener(this);
        pane.add(compress);
        compress.requestFocus();
        setVisible(true);
        URL icon = ClassLoader.getSystemResource("us/screenshottr/imagecompressor/resources/logo.png");
        Toolkit kit = Toolkit.getDefaultToolkit();
        Image icon_img = kit.createImage(icon);
        setIconImage(icon_img);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();
        List<String> formats = Arrays.asList("jpg", "jpeg");

        if (source == compress) {
            String imgLoc = promptForFolder(rootPane);

            if (!(formats.contains(FilenameUtils.getExtension(imgLoc)))) {
                JOptionPane.showMessageDialog(rootPane, "Please only use a format that Image Compressor supports.");
                return;
            }

            Image img = null;
            try {
                img = ImageIO.read(new File(imgLoc));
            }
            catch (IOException ex) {
                Logger.getLogger(ImageCompressor.class.getName()).log(Level.SEVERE, null, ex);
            }
            BufferedImage image;
            image = ImageTool.toBufferedImage(img);

            Iterator<ImageWriter> i;
            i = ImageIO.getImageWritersByFormatName("jpeg");
            
            ImageWriter jpegWriter;
            jpegWriter = i.next();
            
            ImageWriteParam param;
            param = jpegWriter.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(0.8f);

            try (
                    FileImageOutputStream out = new FileImageOutputStream(new File(FilenameUtils.removeExtension(imgLoc) + " - Converted.jpg"))) {
                jpegWriter.setOutput(out);
                jpegWriter.write(null, new IIOImage(image, null, null), param);
                jpegWriter.dispose();
            }
            catch (IOException ex) {
                Logger.getLogger(ImageCompressor.class.getName()).log(Level.SEVERE, null, ex);
            }

            JOptionPane.showMessageDialog(rootPane, "Congratulations, your image " + FilenameUtils.removeExtension(imgLoc) + " has now been converted!");
        }
    }

    public String promptForFolder(Component parent) {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FileFilter filter = new FileNameExtensionFilter("Images", new String[]{"jpg", "jpeg"});
        fc.setFileFilter(filter);

        if (fc.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            return fc.getSelectedFile().getAbsolutePath();
        }

        return null;
    }

    public static void main(String args[]) {
        new ImageCompressor();
    }
}
