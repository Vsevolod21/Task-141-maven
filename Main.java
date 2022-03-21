package task132;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Main {
    public static void main(String[] args) throws IOException {
        GameProgress[] gameProgress = {
                new GameProgress(3, 4, 5, 2.2),
                new GameProgress(8, 25, 36, 5.2),
                new GameProgress(1, 87, 55, 2.52)
        };

        String archiveName = "Games\\savegames\\zip_output.zip";
        String dirName = "Games\\savegames";

        saveGame("Games\\savegames\\save1.dat", gameProgress[0]);
        saveGame("Games\\savegames\\save2.dat", gameProgress[1]);
        saveGame("Games\\savegames\\save3.dat", gameProgress[2]);

        zipFiles(archiveName, dirName);

        openZip(archiveName, dirName);

        System.out.println(openProgress("Games\\savegames\\save1.dat"));
        System.out.println(openProgress("Games\\savegames\\save2.dat"));
        System.out.println(openProgress("Games\\savegames\\save3.dat"));

    }

    public static void saveGame(String path, GameProgress gp) {
        try (FileOutputStream fos = new FileOutputStream(path);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(gp);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void zipFiles(String arhName, String zippedDir) throws IOException {
        File zipDir = new File(zippedDir);
        String[] fileList = zipDir.list();
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(arhName));
        for (String value : fileList) {
            File f = new File(value);
            String filePath = f.getPath();
            try (FileInputStream fis = new FileInputStream(zippedDir + "\\" + filePath)) {
                ZipEntry ze = new ZipEntry(filePath);
                zos.putNextEntry(ze);
                byte[] bytes = new byte[fis.available()];
                fis.read(bytes);
                zos.write(bytes);
                zos.closeEntry();
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
        zos.close();
        for (String s : fileList) {
            if (new File(zippedDir + "\\" + s).delete()) {
                System.out.println("Файл " + s + " добавлен в архив " + arhName);
                System.out.println("Файл " + s + " удален из папки: " + zippedDir);
            }
        }
    }

    public static void openZip(String arhName, String dirName) {
        try (ZipInputStream zin = new ZipInputStream(new FileInputStream(arhName))) {
            ZipEntry entry;
            String name;
            long size;
            while ((entry = zin.getNextEntry()) != null) {
                name = entry.getName(); // получим название файла
                size = entry.getSize(); // получим его размер в байтах
                System.out.printf("File name: %s \t File size: %d \n", name, size);
                // распаковка
                FileOutputStream fout = new FileOutputStream(dirName + "\\" + name);
                for (int c = zin.read(); c != -1; c = zin.read()) {
                    fout.write(c);
                }
                fout.flush();
                zin.closeEntry();
                fout.close();
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static GameProgress openProgress(String path) {
        GameProgress gameProgress = null;
        // откроем входной поток для чтения файла
        try (FileInputStream fis = new FileInputStream(path);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            // десериализуем объект и скастим его в класс
            gameProgress = (GameProgress) ois.readObject();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return gameProgress;
    }
}
