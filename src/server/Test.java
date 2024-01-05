package server;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class Test {

    public static void main(String[] args) {
        Path sourcePath = Paths.get("C:\\Users\\Long\\Downloads\\New folder");
        Path destinationPath = Paths.get("C:\\Users\\Long\\Downloads\\a\\New folder");
        
//        copy.mkdirs();
//        copyFolder(sourcePath, destinationPath);
//        File oldFolder = new File(sourcePath.toString());
//        File newFolder = new File("C:\\Users\\Long\\Downloads\\he he he");
//        oldFolder.renameTo(newFolder);
//        deleteFolder(destinationPath);
        try {
			Files.move(sourcePath, destinationPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public static void copyFolder(Path sourcePath, Path destinationPath) {
		try {
			Files.walkFileTree(sourcePath, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
					Path targetDir = destinationPath.resolve(sourcePath.relativize(dir));
					Files.createDirectories(targetDir);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					Files.copy(file, destinationPath.resolve(sourcePath.relativize(file)),
							StandardCopyOption.REPLACE_EXISTING);
					return FileVisitResult.CONTINUE;
				}
			});

			System.out.println("Folder copied successfully.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    public static void deleteFolder(Path sourPath) {
    	try {
            Files.walkFileTree(sourPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });

            System.out.println("Folder deleted successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

