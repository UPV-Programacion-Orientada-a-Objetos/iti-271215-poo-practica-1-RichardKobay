package edu.upvictoria.fpoo.utils;

import javax.naming.NoPermissionException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NotDirectoryException;

public class IOUtils {
    public File openFolder(String path) throws NotDirectoryException, FileNotFoundException, NoPermissionException {
        File folder = new File(path);

        if (!folder.exists())
            throw new FileNotFoundException("Folder " + path + " does not exist");

        if (!folder.isDirectory())
            throw new NotDirectoryException("Folder " + path + " is not a folder");

        if (!folder.canRead())
            throw new NoPermissionException("Folder " + path + " is not readable");

        if (!folder.canWrite())
            throw new NoPermissionException("Folder " + path + " is not writable");

        return folder;
    }

    public boolean createNewFile(String path) throws FileAlreadyExistsException, IOException {
        File file = new File(path);

        if (file.createNewFile())
            return true;
        else
            throw new FileAlreadyExistsException("File already exists");
    }

    public boolean createNewFile(File file) throws FileAlreadyExistsException, IOException {
        if (file.createNewFile())
            return true;
        else
            throw new FileAlreadyExistsException("File already exists");
    }
}
