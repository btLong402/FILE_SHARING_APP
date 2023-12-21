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
		Socket socket = null;
		DataOutputStream out = null;
		DataInputStream in = null;
		try {
			socket = new Socket("localhost", 5555);
			// writing to server
			out = new DataOutputStream(socket.getOutputStream());

			// reading from server
			in = new DataInputStream(socket.getInputStream());
//			boolean isLogin = false;
			if (in.readInt() == 200) {
				System.out.println("Connected!");
			}
			while (true) {
				BufferedReader sc = new BufferedReader(new InputStreamReader(System.in));
				String cmd = sc.readLine();

				StringTokenizer command = new StringTokenizer(cmd, " ");
				String key = command.nextToken().toUpperCase();
				if (key.equals("EXIT"))
					break;
				String groupName;
				String filePath;
				int responseCode;
				byte[] buffer = new byte[4096];
				switch (key) {
				case "CREATE_GROUP":
					out.writeUTF(cmd);
					out.flush();
					responseCode = in.readInt();
					if (responseCode == 403) {
						System.out.println("You do not have permission to create a group. Please log in.");
					} else {
						responseCode = in.readInt();
						if (responseCode == 501) {
							System.out.println("Group existed!");
						} else {
							System.out.println("Create success!");
						}
					}
					break;
				case "UPLOAD_FILE":
					groupName = command.nextToken();
					filePath = command.nextToken();
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
					out.writeUTF(cmd);
					int rpCode = in.readInt();
					if (rpCode != 401) {
						responseCode = in.readInt();
						if (responseCode == 404) {
							System.out.println("Group does not exist!");
						} else {
							out.writeLong(fileSource.length());
							int data;
							long byteSend = 0;

							while ((data = bis.read(buffer)) != -1) {
								out.write(buffer, 0, data);
								byteSend += data;
								trackProgress(fileSource.length(), byteSend);
								out.flush();
							}
							bis.close();
							responseCode = in.readInt();
							if (responseCode == 200) {
								System.out.println();
								System.out.println("Uploaded!");
							}
						}
					}
					break;
				case "DOWNLOAD_FILE":
					groupName = command.nextToken();
					filePath = command.nextToken();
					String fileName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
					out.writeUTF(cmd);
					responseCode = in.readInt();
					if (responseCode != 200) {						
						System.out.println("You do not have permission to download a file. Please log in.");
						break;
					}
					responseCode = in.readInt();
					if (responseCode == 401) {
						System.out.println("Source file name not exists!!!");
					} else {
						long fileSize = in.readLong();
						int bytesRead;
						long byteReaded = 0;
						if (fileSize != 0) {
							System.out.print("Input destination path: ");
							String path = sc.readLine();
							File folder = new File(path);
							if (!folder.exists())
								folder.mkdirs();
							String desPath = Paths.get(path).resolve(fileName).toString();
							File f = new File(desPath);
							BufferedOutputStream bos;
							try {
								bos = new BufferedOutputStream(new FileOutputStream(f));
								long tmp = fileSize;
								while (tmp != 0) {
									bytesRead = in.read(buffer);
									bos.write(buffer, 0, bytesRead);
									byteReaded += bytesRead;
									trackProgress(fileSize, byteReaded);
									tmp = tmp - bytesRead;
									bos.flush();
								}
								bos.close();
								System.out.println();
								System.out.println("Download succeed!");
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
					break;
				case "LOGIN":
					out.writeUTF(cmd);
					out.flush();
					responseCode = in.readInt();
					if (responseCode == 409) {
						System.out.println("You are already login!");
					} else {
						responseCode = in.readInt();
						if (responseCode == 202) {
							System.out.println("Login success!");
						} else {
							System.out.println("Wrong User Name or Password!");
						}
					}
					break;
				case "REGISTER":
					out.writeUTF(cmd);
					out.flush();
					responseCode = in.readInt();
					if (responseCode == 409) {
						System.out.println("You are already login!");
					} else {
						responseCode = in.readInt();
						if (responseCode == 201) {
							System.out.println("Create success! Hello " + command.nextToken());
						} else {
							System.out.println("User Name already created!");
						}
					}
					break;
				default:
					responseCode = in.readInt();
					if (responseCode == 404) {
						System.out.println("Command not recognized!");
					}
					break;
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
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
