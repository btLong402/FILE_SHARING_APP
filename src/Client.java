import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import javax.imageio.ImageIO;
// Client class 
class Client {

	// driver code
	public static void main(String[] args) {
		// establish a connection by providing host and port
		// number

		try (Socket socket = new Socket("localhost", 5555)) {

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
//					String userName = command.nextToken();
//					String groupName = command.nextToken();
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
                    }
                    else {
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
                    while ((data = bis.read()) != -1) {
                        out.write(data);
                        System.out.print('.');
                        out.flush();
                    }
                    System.out.println();
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
}
