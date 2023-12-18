import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

// Client class 
class Client {

	// driver code
	public static void main(String[] args) {
		// establish a connection by providing host and port
		// number

		try (Socket socket = new Socket("192.168.21.113", 5555)) {

			// writing to server
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());

			// reading from server
			DataInputStream in = new DataInputStream(socket.getInputStream());
			while (true) {
				BufferedReader sc = new BufferedReader(new InputStreamReader(System.in));
				String cmd = sc.readLine();
				if (cmd.equals("Exit"))
					break;
				StringTokenizer command = new StringTokenizer(cmd, " ");
				String key = command.nextToken().toUpperCase();
				switch (key) {
				case "CREATE_GROUP":
					out.writeUTF(cmd);
					out.flush();
					break;
				case "UPLOAD_FILE":
					String groupName = command.nextToken();
					String filePath = command.nextToken();
					File fileSource = new File(filePath);

					if (!fileSource.exists()) {
						System.out.println("Source file name not exists!!!");
						break;
					} else {
						try {
							// Sử dụng Files.probeContentType() để lấy loại của file
							Path path = Paths.get(filePath);
							String fileType = Files.probeContentType(path);

							if (fileType != null) {
								System.out.println("File Type: " + fileType);
							} else {
								System.out.println("Unable to determine the file type.");
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fileSource));
//                    byte[] fileContent = Files.readAllBytes(Paths.get(filePath));
					out.writeUTF(cmd);
					out.writeLong(fileSource.length());
					int data;
					long byteSend = 0;
					byte[] buffer = new byte[4096];
					while ((data = bis.read(buffer)) != -1) {
						out.write(buffer, 0, data);
						byteSend += data;
						trackProgress(fileSource.length(), byteSend);
						out.flush();
					}
					System.out.println();
					System.out.println("Uploaded!");
					bis.close();
					break;
				default:
					break;
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void trackProgress(long totalFileSize, long byteSend) {
		double progress = (double) byteSend / totalFileSize * 100;
		System.out.printf("Progress: %.2f%%", progress);
		try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
		clearLine();
	}

	public static void clearLine() {
		System.out.print("\r");
		System.out.flush();
	}
}
