package us.screenshottr.imagecompressor;

import com.gej.util.ImageTool;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import org.apache.commons.io.FilenameUtils;
import us.screenshottr.imagecompressor.IC_Util.Format;

public class IC_Listener implements ActionListener {

    JFrame JFrame;
    IC_Util IC_Util = new IC_Util();

    public IC_Listener(JFrame frame) {
        this.JFrame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();
        List<String> formats = Arrays.asList("jpg", "jpeg", "png");
        String ext = null;

        if (source == ImageCompressor.compress) {
            String imgLoc = IC_Util.promptForFolder(JFrame);

            if (!(formats.contains(FilenameUtils.getExtension(imgLoc)))) {
                JOptionPane.showMessageDialog(JFrame, "Please only use a format that Image Compressor supports.");
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

            if (FilenameUtils.getExtension(imgLoc).equals("jpg") || FilenameUtils.getExtension(imgLoc).equals("jpeg")) {
                IC_Util.convert(image, imgLoc, Format.JPEG);
                ext = "jpg";
            }

            if (FilenameUtils.getExtension(imgLoc).contains("png")) {
                IC_Util.convert(image, imgLoc, Format.PNG);
                ext = "png";
            }

            JLabel filename = new JLabel("<html>Congratulations, your image <u><bold><b1>" + FilenameUtils.removeExtension(IC_Util.fn) + "</u></bold></b1> has now been compressed!</html>");
            JOptionPane.showMessageDialog(JFrame, filename);

            int upload = JOptionPane.showConfirmDialog(null, "Would you like to upload this to ScreenShottr?");

            if (upload == JOptionPane.YES_OPTION) {
                int encrypted = JOptionPane.showConfirmDialog(null, "Would you like this image to be encrypted?");

                if (encrypted == JOptionPane.YES_OPTION) {
                    try {
                        IC_Util.upload(new File(FilenameUtils.removeExtension(imgLoc) + " - Converted." + ext), true);
                    }
                    catch (Exception ex) {
                        Logger.getLogger(IC_Listener.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                if (encrypted == JOptionPane.NO_OPTION) {
                    try {
                        IC_Util.upload(new File(FilenameUtils.removeExtension(imgLoc) + " - Converted." + ext), false);
                    }
                    catch (Exception ex) {
                        Logger.getLogger(IC_Listener.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                if (encrypted == JOptionPane.CANCEL_OPTION) {
                }

                if (encrypted == JOptionPane.CLOSED_OPTION) {
                }
            }

            if (upload == JOptionPane.NO_OPTION) {
            }

            if (upload == JOptionPane.CANCEL_OPTION) {
            }

            if (upload == JOptionPane.CLOSED_OPTION) {
            }
        }
    }
}
