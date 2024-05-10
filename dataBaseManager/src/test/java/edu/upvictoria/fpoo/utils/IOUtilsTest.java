package edu.upvictoria.fpoo.utils;

import org.junit.jupiter.api.Test;

import javax.naming.NoPermissionException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.attribute.PosixFilePermissions;

import static org.junit.jupiter.api.Assertions.*;

class IOUtilsTest {

    IOUtils ioUtils = new IOUtils();

    @Test
    public void testOpenFolder_1() {
        File folder = null;
        try {
            folder = Files.createTempDirectory("testFolder").toFile();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        File result = null;
        try {
            assert folder != null;
            result = ioUtils.openFolder(folder.getAbsolutePath());
        } catch (NotDirectoryException | FileNotFoundException | NoPermissionException e) {
            System.err.println(e.getMessage());
        }
        assertEquals(folder, result);

        assertTrue(folder.delete());
    }

    @Test
    public void testOpenFolder_2() {
        File folder = null;
        try {
            folder = Files.createTempDirectory("otherFolder").toFile();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        File result = null;
        try {
            assert folder != null;
            result = ioUtils.openFolder(folder.getAbsolutePath());
        } catch (NotDirectoryException | FileNotFoundException | NoPermissionException e) {
            System.err.println(e.getMessage());
        }
        assertEquals(folder, result);

        assertTrue(folder.delete());
    }

    @Test
    public void testOpenFolder_3() {
        File folder = null;
        try {
            folder = Files.createTempDirectory("yesFolder").toFile();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        File result = null;
        try {
            assert folder != null;
            result = ioUtils.openFolder(folder.getAbsolutePath());
        } catch (NotDirectoryException | FileNotFoundException | NoPermissionException e) {
            System.err.println(e.getMessage());
        }
        assertEquals(folder, result);

        assertTrue(folder.delete());
    }

    @Test
    public void testOpenFolder_4() {
        File folder = null;
        try {
            folder = Files.createTempDirectory("ivanFolder").toFile();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        File result = null;
        try {
            assert folder != null;
            result = ioUtils.openFolder(folder.getAbsolutePath());
        } catch (NotDirectoryException | FileNotFoundException | NoPermissionException e) {
            System.err.println(e.getMessage());
        }
        assertEquals(folder, result);

        assertTrue(folder.delete());
    }

    @Test
    public void testOpenFolder_5() {
        File folder = null;
        try {
            folder = Files.createTempDirectory("aaafolder").toFile();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        File result = null;
        try {
            assert folder != null;
            result = ioUtils.openFolder(folder.getAbsolutePath());
        } catch (NotDirectoryException | FileNotFoundException | NoPermissionException e) {
            System.err.println(e.getMessage());
        }
        assertEquals(folder, result);

        assertTrue(folder.delete());
    }

    @Test
    public void testOpenFolder_6() {
        File folder = null;
        try {
            folder = Files.createTempDirectory("finalFolder").toFile();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        File result = null;
        try {
            assert folder != null;
            result = ioUtils.openFolder(folder.getAbsolutePath());
        } catch (NotDirectoryException | FileNotFoundException | NoPermissionException e) {
            System.err.println(e.getMessage());
        }
        assertEquals(folder, result);

        assertTrue(folder.delete());
    }

    @Test
    public void testOpenNonExistingFolder_1() throws Exception {
        assertThrows(FileNotFoundException.class, () -> {
            ioUtils.openFolder("/path/to/nonexistent/folder");
        });
    }

    @Test
    public void testOpenNonExistingFolder_2() throws Exception {
        assertThrows(FileNotFoundException.class, () -> {
            ioUtils.openFolder("invalid path");
        });
    }

    @Test
    public void testOpenNonExistingFolder_3() throws Exception {
        assertThrows(FileNotFoundException.class, () -> {
            ioUtils.openFolder("this is not a path, or is?");
        });
    }

    @Test
    public void testOpenNonExistingFolder_4() throws Exception {
        assertThrows(FileNotFoundException.class, () -> {
            ioUtils.openFolder("/path/to/ivan/folder");
        });
    }

    @Test
    public void testOpenNonExistingFolder_5() throws Exception {
        assertThrows(FileNotFoundException.class, () -> {
            ioUtils.openFolder("/si?");
        });
    }

    @Test
    public void testOpenNonExistingFolder_6() throws Exception {
        assertThrows(FileNotFoundException.class, () -> {
            ioUtils.openFolder("apoco si tilín?");
        });
    }

    @Test
    public void testOpenFileInsteadOfFolder_1() throws Exception {
        File file = Files.createTempFile("testFile", ".txt").toFile();

        assertThrows(NotDirectoryException.class, () -> {
            ioUtils.openFolder(file.getAbsolutePath());
        });
    }

    @Test
    public void testOpenFileInsteadOfFolder_2() throws Exception {
        File file = Files.createTempFile("testFile", ".csv").toFile();

        assertThrows(NotDirectoryException.class, () -> {
            ioUtils.openFolder(file.getAbsolutePath());
        });
    }

    @Test
    public void testOpenFileInsteadOfFolder_3() throws Exception {
        File file = Files.createTempFile("testFile", ".txt").toFile();

        assertThrows(NotDirectoryException.class, () -> {
            ioUtils.openFolder(file.getAbsolutePath());
        });
    }

    @Test
    public void testOpenFileInsteadOfFolder_4() throws Exception {
        File file = Files.createTempFile("testFile", ".ivan").toFile();

        assertThrows(NotDirectoryException.class, () -> {
            ioUtils.openFolder(file.getAbsolutePath());
        });
    }

    @Test
    public void testOpenFileInsteadOfFolder_5() throws Exception {
        File file = Files.createTempFile("anotherTestFile", ".java").toFile();

        assertThrows(NotDirectoryException.class, () -> {
            ioUtils.openFolder(file.getAbsolutePath());
        });
    }

    @Test
    public void testOpenFileInsteadOfFolder_6() throws Exception {
        File file = Files.createTempFile("ivan", ".ivan").toFile();

        assertThrows(NotDirectoryException.class, () -> {
            ioUtils.openFolder(file.getAbsolutePath());
        });
    }

    @Test
    public void testOpenUnreadableFolder_1() throws Exception {
        File folder = Files.createTempDirectory("testUnreadableFolder").toFile();
        Files.setPosixFilePermissions(folder.toPath(), PosixFilePermissions.fromString("---------"));

        assertThrows(NoPermissionException.class, () -> {
            ioUtils.openFolder(folder.getAbsolutePath());
        });
    }

    @Test
    public void testOpenUnreadableFolder_2() throws Exception {
        File folder = Files.createTempDirectory("a_path").toFile();
        Files.setPosixFilePermissions(folder.toPath(), PosixFilePermissions.fromString("---------"));

        assertThrows(NoPermissionException.class, () -> {
            ioUtils.openFolder(folder.getAbsolutePath());
        });
    }

    @Test
    public void testOpenUnreadableFolder_3() throws Exception {
        File folder = Files.createTempDirectory("otherPath").toFile();
        Files.setPosixFilePermissions(folder.toPath(), PosixFilePermissions.fromString("---------"));

        assertThrows(NoPermissionException.class, () -> {
            ioUtils.openFolder(folder.getAbsolutePath());
        });
    }

    @Test
    public void testOpenUnreadableFolder_4() throws Exception {
        File folder = Files.createTempDirectory("IvanPath").toFile();
        Files.setPosixFilePermissions(folder.toPath(), PosixFilePermissions.fromString("---------"));

        assertThrows(NoPermissionException.class, () -> {
            ioUtils.openFolder(folder.getAbsolutePath());
        });
    }

    @Test
    public void testOpenUnreadableFolder_5() throws Exception {
        File folder = Files.createTempDirectory("uis").toFile();
        Files.setPosixFilePermissions(folder.toPath(), PosixFilePermissions.fromString("---------"));

        assertThrows(NoPermissionException.class, () -> {
            ioUtils.openFolder(folder.getAbsolutePath());
        });
    }

    @Test
    public void testOpenUnreadableFolder_6() throws Exception {
        File folder = Files.createTempDirectory("anotherIvan").toFile();
        Files.setPosixFilePermissions(folder.toPath(), PosixFilePermissions.fromString("---------"));

        assertThrows(NoPermissionException.class, () -> {
            ioUtils.openFolder(folder.getAbsolutePath());
        });
    }

    @Test
    public void testOpenUnWritableFolder_1() throws Exception {
        File folder = Files.createTempDirectory("testUnWritableFolder").toFile();
        Files.setPosixFilePermissions(folder.toPath(), PosixFilePermissions.fromString("r--------"));

        assertThrows(NoPermissionException.class, () -> {
            ioUtils.openFolder(folder.getAbsolutePath());
        });
    }

    @Test
    public void testOpenUnWritableFolder_2() throws Exception {
        File folder = Files.createTempDirectory("testUnWritableIvan").toFile();
        Files.setPosixFilePermissions(folder.toPath(), PosixFilePermissions.fromString("r--------"));

        assertThrows(NoPermissionException.class, () -> {
            ioUtils.openFolder(folder.getAbsolutePath());
        });
    }

    @Test
    public void testOpenUnWritableFolder_3() throws Exception {
        File folder = Files.createTempDirectory("No se pq no puedo agregar una ruta a esta función").toFile();
        Files.setPosixFilePermissions(folder.toPath(), PosixFilePermissions.fromString("r--------"));

        assertThrows(NoPermissionException.class, () -> {
            ioUtils.openFolder(folder.getAbsolutePath());
        });
    }

    @Test
    public void testOpenUnWritableFolder_4() throws Exception {
        File folder = Files.createTempDirectory("No me gusta eso pero bueno").toFile();
        Files.setPosixFilePermissions(folder.toPath(), PosixFilePermissions.fromString("r--------"));

        assertThrows(NoPermissionException.class, () -> {
            ioUtils.openFolder(folder.getAbsolutePath());
        });
    }

    @Test
    public void testOpenUnWritableFolder_5() throws Exception {
        File folder = Files.createTempDirectory("Es lo que hay").toFile();
        Files.setPosixFilePermissions(folder.toPath(), PosixFilePermissions.fromString("r--------"));

        assertThrows(NoPermissionException.class, () -> {
            ioUtils.openFolder(folder.getAbsolutePath());
        });
    }

    @Test
    public void testOpenUnWritableFolder_6() throws Exception {
        File folder = Files.createTempDirectory("Hola mamá estoy en youtube").toFile();
        Files.setPosixFilePermissions(folder.toPath(), PosixFilePermissions.fromString("r--------"));

        assertThrows(NoPermissionException.class, () -> {
            ioUtils.openFolder(folder.getAbsolutePath());
        });
    }

    @Test
    public void testCreateNewFile_1() throws IOException {
        String filePath = "testFile_" + System.currentTimeMillis() + ".txt";

        try {
            assertTrue(ioUtils.createNewFile(filePath));
            File file = new File(filePath);
            assertTrue(file.exists());
        } finally {
            File file = new File(filePath);
            if (file.exists()) {
                assertTrue(file.delete());
            }
        }
    }

    @Test
    public void testCreateNewFile_2() throws IOException {
        String filePath = "testFile_ivan.csv";

        try {
            assertTrue(ioUtils.createNewFile(filePath));
            File file = new File(filePath);
            assertTrue(file.exists());
        } finally {
            File file = new File(filePath);
            if (file.exists()) {
                assertTrue(file.delete());
            }
        }
    }

    @Test
    public void testCreateNewFile_3() throws IOException {
        String filePath = "testFile_another_ivan_but_now_in_txt.txt";

        try {
            assertTrue(ioUtils.createNewFile(filePath));
            File file = new File(filePath);
            assertTrue(file.exists());
        } finally {
            File file = new File(filePath);
            if (file.exists()) {
                assertTrue(file.delete());
            }
        }
    }

    @Test
    public void testCreateNewFile_4() throws IOException {
        String filePath = "hola soy un archivo aca bien maniaco.svg";

        try {
            assertTrue(ioUtils.createNewFile(filePath));
            File file = new File(filePath);
            assertTrue(file.exists());
        } finally {
            File file = new File(filePath);
            if (file.exists()) {
                assertTrue(file.delete());
            }
        }
    }

    @Test
    public void testCreateNewFile_5() throws IOException {
        String filePath = "ultimo w .ivan";

        try {
            assertTrue(ioUtils.createNewFile(filePath));
            File file = new File(filePath);
            assertTrue(file.exists());
        } finally {
            File file = new File(filePath);
            if (file.exists()) {
                assertTrue(file.delete());
            }
        }
    }

    @Test
    public void testCreateNewFile_6() throws IOException {
        String filePath = "ivan.ivan";

        try {
            assertTrue(ioUtils.createNewFile(filePath));
            File file = new File(filePath);
            assertTrue(file.exists());
        } finally {
            File file = new File(filePath);
            if (file.exists()) {
                assertTrue(file.delete());
            }
        }
    }

    @Test
    public void testCreateNewFileAlreadyExists_1() throws IOException {
        File tempFile = File.createTempFile("testFile", ".ivan");

        try {
            assertThrows(FileAlreadyExistsException.class, () -> {
                ioUtils.createNewFile(tempFile.getAbsolutePath());
            });
        } finally {
            if (tempFile.exists()) {
                assertTrue(tempFile.delete());
            }
        }
    }

    @Test
    public void testCreateNewFileAlreadyExists_2() throws IOException {
        File tempFile = File.createTempFile("ivan", ".csv");

        try {
            assertThrows(FileAlreadyExistsException.class, () -> {
                ioUtils.createNewFile(tempFile.getAbsolutePath());
            });
        } finally {
            if (tempFile.exists()) {
                assertTrue(tempFile.delete());
            }
        }
    }

    @Test
    public void testCreateNewFileAlreadyExists_3() throws IOException {
        File tempFile = File.createTempFile("ivan", ".ivan");

        try {
            assertThrows(FileAlreadyExistsException.class, () -> {
                ioUtils.createNewFile(tempFile.getAbsolutePath());
            });
        } finally {
            if (tempFile.exists()) {
                assertTrue(tempFile.delete());
            }
        }
    }

    @Test
    public void testCreateNewFileAlreadyExists_4() throws IOException {
        File tempFile = File.createTempFile("ya casi acabo de hacer los test w", ".si");

        try {
            assertThrows(FileAlreadyExistsException.class, () -> {
                ioUtils.createNewFile(tempFile.getAbsolutePath());
            });
        } finally {
            if (tempFile.exists()) {
                assertTrue(tempFile.delete());
            }
        }
    }

    @Test
    public void testCreateNewFileAlreadyExists_5() throws IOException {
        File tempFile = File.createTempFile("como q no", ".ai");

        try {
            assertThrows(FileAlreadyExistsException.class, () -> {
                ioUtils.createNewFile(tempFile.getAbsolutePath());
            });
        } finally {
            if (tempFile.exists()) {
                assertTrue(tempFile.delete());
            }
        }
    }

    @Test
    public void testTestCreateNewFile() {}
}