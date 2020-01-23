package design.pixelw.utils;

import design.pixelw.exception.MUErrors;
import design.pixelw.exception.MUException;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.*;
import java.util.List;

/**
 * @author Carl Su
 * @date 2019.1.10
 */
public class FileIO {
    private static final String defaultPath = FileSystemView.getFileSystemView().getHomeDirectory().getPath();


    //JavaFX
    public static File chooseFile(FileType fileType, Stage stage) {
        FileChooser fileChooser = new FileChooser();
        if (fileType == FileType.supportedEncrypted) {
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("所有支持的加密格式","*.ncm","*.qmc0","*.qmc3")
            );
        } else {
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(fileType.getDescription(),"*."+fileType.getFileExt()));
        }
        fileChooser.setTitle("Open " + fileType.getFileExt());
        return fileChooser.showOpenDialog(stage);
    }

    public static List<File> chooseFiles(FileType fileType, Stage stage) {
        FileChooser fileChooser = new FileChooser();
        if (fileType == FileType.supportedEncrypted) {
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("所有支持的加密格式","*.ncm","*.qmc0","*.qmc3")
            );
        } else {
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(fileType.getDescription(),"*."+fileType.getFileExt()));
        }
        fileChooser.setTitle("Open " + fileType.getFileExt());
        return fileChooser.showOpenMultipleDialog(stage);
    }

    //Swing
    public static File chooseFile(FileType fileType, Component parent){
        JFileChooser chooser = new JFileChooser(defaultPath);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setMultiSelectionEnabled(false);
        if (fileType == FileType.supportedEncrypted) {
            chooser.setFileFilter(new SupportedEncryptedFormatFilter());
        } else {
            FileNameExtensionFilter extensionFilter = new FileNameExtensionFilter(fileType.getDescription(), fileType.getFileExt());
            chooser.setFileFilter(extensionFilter);
        }
        chooser.setDialogTitle("Open " + fileType.getFileExt());
        //选择并判断成功选择
        if (chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        }
        return null;
    }

    public static File[] chooseFiles(FileType fileType, Component parent){
        JFileChooser chooser = new JFileChooser(defaultPath);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setMultiSelectionEnabled(true);
        if (fileType == FileType.supportedEncrypted) {
            chooser.setFileFilter(new SupportedEncryptedFormatFilter());
        } else {
            FileNameExtensionFilter extensionFilter = new FileNameExtensionFilter(fileType.getDescription(), fileType.getFileExt());
            chooser.setFileFilter(extensionFilter);
        }
        chooser.setDialogTitle("Open " + fileType.getFileExt());
        //选择并判断成功选择
        if (chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFiles();
        }
        return null;
    }


    public static BufferedInputStream getBufferedInputStream(File file) throws FileNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(file);
        return new BufferedInputStream(fileInputStream);
    }


    public static boolean outputBesideInput(File originalFile, byte[] bytes, FileType fileType) {
        String pathWithoutExt = originalFile.getAbsolutePath().substring(0,
                originalFile.getAbsolutePath().lastIndexOf("."));
        String newPath = pathWithoutExt + "." + fileType.getFileExt();
        File file = new File(newPath);
        if (file.exists()) {
            System.out.println("file exists");
//            return;
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(bytes);
            fileOutputStream.flush();
            fileOutputStream.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static byte[] loadBytes(File file) throws IOException, MUException {
        BufferedInputStream bfi = getBufferedInputStream(file);
        int intLength;
        if (file.length() <= Integer.MAX_VALUE) {
            intLength = (int) file.length();
        } else {
            throw new MUException(MUErrors.FILE_TOO_LARGE);
        }

        byte[] bytes = new byte[intLength];

        int read = bfi.read(bytes);
        System.out.println("read " + read + " bytes");
        bfi.close();
        return bytes;
    }

    public static String getExtension(File file){
        String fileName = file.getName().toLowerCase();
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
}


class SupportedEncryptedFormatFilter extends FileFilter {

    @Override
    public boolean accept(File f) {
        String name = f.getName().toLowerCase();
        return name.endsWith(".ncm") || name.endsWith(".qmc0") || name.endsWith(".qmc3") || f.isDirectory();
    }

    @Override
    public String getDescription() {
        return "所有支持的加密格式(ncm, qmc)";
    }
}
