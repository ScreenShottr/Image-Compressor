package us.screenshottr.imagecompressor;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.util.Iterator;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import javax.net.ssl.HttpsURLConnection;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.validator.routines.UrlValidator;

public class IC_Util {

    public enum Format {

        JPEG, PNG;
    }

    public String fn;
    String version = "1.0";
    String ext = null;

    public String promptForFolder(Component parent) {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FileFilter filter = new FileNameExtensionFilter("Images", new String[]{"jpg", "jpeg", "png"});
        fc.setFileFilter(filter);

        if (fc.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            fn = fc.getSelectedFile().getName();
            return fc.getSelectedFile().getAbsolutePath();
        }

        return null;
    }

    public void convert(BufferedImage image, String imgLoc, Format format) {
        if (format == Format.JPEG) {
            ext = "jpeg";
        }

        if (format == Format.PNG) {
            ext = "png";
        }

        Iterator<ImageWriter> i;
        i = ImageIO.getImageWritersByFormatName(ext);

        ImageWriter writer;
        writer = i.next();

        ImageWriteParam param;
        param = writer.getDefaultWriteParam();

        if (format == Format.JPEG) {
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(0.8f);
        }

        try (
                FileImageOutputStream out = new FileImageOutputStream(new File(FilenameUtils.removeExtension(imgLoc) + " - Converted." + ext))) {
            writer.setOutput(out);
            writer.write(null, new IIOImage(image, null, null), param);
            writer.dispose();
        }
        catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error writing the image to disk - do you have enough disk space?", "ScreenShottr - Image Compressor", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void upload(File f, boolean encrypted) throws Exception {
        final String USER_AGENT = "ImageCompressor " + version;
        byte[] encodedBytes = Base64.encodeBase64(Files.readAllBytes(f.toPath()));
        String base64 = new String(encodedBytes);
        String URL;

        if (encrypted == true) {
            URL = "https://www.screenshottr.us/upload.php?encrypt=true&base64=true";
        }
        else {
            URL = "https://screenshottr.us/upload.php?base64=true";
        }

        URL obj = new URL(URL);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        con.setDoOutput(true);
        try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
            wr.writeBytes("base64data=" + base64);
            wr.flush();
        }

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + URL);
        System.out.println("Post parameters : " + base64);
        System.out.println("Response Code : " + responseCode);

        StringBuilder response;
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        }

        System.out.println(response.toString());

        UrlValidator validator = new UrlValidator();

        if (validator.isValid(response.toString())) {
            Desktop.getDesktop().browse(new URI(response.toString()));
        }
    }
}
