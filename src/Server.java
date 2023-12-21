import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.StringTokenizer;
import db_access.user.User_DAL;

// Server class
class Server {

	public static void main(String[] args) {
		ServerSocket server = null;

		try {
			// server is listening on port 1234
			server = new ServerSocket(5555);
			server.setReuseAddress(true);

			// running infinite loop for getting
			// client request
			while (true) {
				// socket object to receive incoming client
				// requests
				Socket client = server.accept();

				// Displaying that new client is connected
				// to server
				System.out.println("New client connected" + client.getInetAddress().getHostAddress());

				// create a new thread object
				ClientHandler clientSock = new ClientHandler(client);

				// This thread will handle the client
				// separately
				new Thread(clientSock).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (server != null) {
				try {
					server.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// ClientHandler class
	private static class ClientHandler implements Runnable {

		private final Socket clientSocket;
		DataInputStream in;
		DataOutputStream out;
		Path currentPath = Paths.get("").toAbsolutePath().resolve("root");

		// Constructor
		public ClientHandler(Socket socket) throws IOException {
			this.clientSocket = socket;
			in = new DataInputStream(socket.getInputStream());

			// get the inputstream of client
			out = new DataOutputStream(socket.getOutputStream());
		}

		public void run() {
			try {
				// get the outputstream of client
				out.writeInt(200);
				String line;
				String userId = null;
				while ((line = in.readUTF()) != null) {
//					System.out.println(line);
					// writing the received message from
					// client
					StringTokenizer command = new StringTokenizer(line, " ");
					String key = command.nextToken().toUpperCase();
					if(key == "EXIT") break;
					String userName;
					String password;
					String groupName;
					String filePath;
					byte[] buffer = new byte[4096];
					switch (key) {
					case "CREATE_GROUP":
						if (userId == null) {
							out.writeInt(403);
							out.flush();
							break;
						} else {
							out.writeInt(200);
							out.flush();
						}
						userName = command.nextToken();
						groupName = command.nextToken();
						String path = currentPath.resolve(groupName).toString();
						// System.out.println(path);
						File folder = new File(path);

						// Kiểm tra xem thư mục đã tồn tại chưa, nếu không thì tạo mới
						if (!folder.exists()) {
							boolean result = folder.mkdirs(); // Tạo thư mục và tất cả các thư mục cha nếu chưa tồn tại
							if (result) {
								System.out.println("Create success!");
								out.writeInt(201);
								out.flush();
							} else {
								System.out.println("Can not create!");
							}
						} else {
							out.writeInt(501);
							out.flush();
						}
						break;
					case "UPLOAD_FILE":
						if (userId == null) {
							out.writeInt(401);
							out.flush();
							break;
						} else {
							out.writeInt(200);
							out.flush();
						}
						groupName = command.nextToken();
						filePath = command.nextToken();
						File group = new File(currentPath.resolve(groupName).toString());
						if (!group.exists()) {
							out.writeInt(404);
							out.flush();
							break;
						} else {
							out.writeInt(202);
							out.flush();

							// Extract the file name from the file path
							String fileName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
							System.out.println(fileName);
							long fileSize = in.readLong();
							int bytesRead;
							long byteReaded = 0;
							String destinationPath = currentPath.resolve(groupName).resolve(fileName).toString();
							File f = new File(destinationPath);
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
								System.out.println();
								System.out.println("Upload succeed!");
								bos.close();
								out.writeInt(200);
								out.flush();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						break;
					case "DOWNLOAD_FILE":
						if (userId == null) {
							out.writeInt(401);
							out.flush();
							break;
						} else {
							out.writeInt(200);
							out.flush();
						}
						groupName = command.nextToken();
						filePath = currentPath.resolve(groupName).resolve(command.nextToken()).toString();
						File fileSource = new File(filePath);
						if (!fileSource.exists()) {
							out.writeInt(401);
							out.flush();
							break;
						}
						else {
							out.writeInt(201);
							out.flush();
						}
						BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fileSource));
						out.writeLong(fileSource.length());
						int data;
						long byteSend = 0;

						while ((data = bis.read(buffer)) != -1) {
							out.write(buffer, 0, data);
							byteSend += data;
							trackProgress(fileSource.length(), byteSend);
							out.flush();
						}
						System.out.println();
						System.out.println("Send succeed!");
						bis.close();
//						out.writeInt(200);
//						out.flush();
						break;
					case "LOGIN":
						if (userId != null) {
							out.writeInt(409);
							out.flush();
						} else {
							out.writeInt(200);
							out.flush();
							User_DAL db = new User_DAL();
							userName = command.nextToken();
							password = command.nextToken();
							userId = db.login(userName, password);
							if (userId == null) {
								out.writeInt(404);
								out.flush();
							} else {
								out.writeInt(202);
								out.flush();
							}
						}
						break;
					case "REGISTER":
						if (userId != null) {
							out.writeInt(409);
							out.flush();
						} else {
							out.writeInt(200);
							out.flush();
							User_DAL db = new User_DAL();
							userName = command.nextToken();
							password = command.nextToken();
							userId = db.create(userName, password);
							if (userId == null) {
								out.writeInt(409);
								out.flush();
							} else {
								out.writeInt(201);
								out.flush();
							}
						}
						break;
					default:
						out.writeInt(404);
						out.flush();
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
						clientSocket.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
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
