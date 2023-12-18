import java.io.*; 
import java.net.*; 
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.StringTokenizer;
// Server class 
class Server { 
	public static void main(String[] args) 
	{ 
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
				System.out.println("New client connected"
								+ client.getInetAddress() 
										.getHostAddress()); 

				// create a new thread object 
				ClientHandler clientSock 
					= new ClientHandler(client); 

				// This thread will handle the client 
				// separately 
				new Thread(clientSock).start(); 
			} 
		} 
		catch (IOException e) { 
			e.printStackTrace(); 
		} 
		finally { 
			if (server != null) { 
				try { 
					server.close(); 
				} 
				catch (IOException e) { 
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
		Path currentPath = Paths.get("").toAbsolutePath().getParent().resolve("root");
		// Constructor 
		public ClientHandler(Socket socket) throws IOException 
		{ 
			this.clientSocket = socket; 
			in = new DataInputStream(socket.getInputStream()); 
			
			// get the inputstream of client 
			out = new DataOutputStream(socket.getOutputStream()); 
		} 

		public void run() 
		{ 
			try { 
					
				// get the outputstream of client 

				String line; 
				while ((line = in.readUTF()) != null) { 
					System.out.println(line);
					// writing the received message from 
					// client 
					StringTokenizer command = new StringTokenizer(line, " ");
					String key = command.nextToken().toUpperCase();
					switch (key) {
					case "CREATE_GROUP":
						String userName = command.nextToken();
						String groupName = command.nextToken();
						String path = currentPath.resolve(groupName).toString();
//						System.out.println(path);
						File folder = new File(path);

				        // Kiểm tra xem thư mục đã tồn tại chưa, nếu không thì tạo mới
				        if (!folder.exists()) {
				            boolean result = folder.mkdirs(); // Tạo thư mục và tất cả các thư mục cha nếu chưa tồn tại
				            if (result) {
				                System.out.println("Create success!");
				            } else {
				                System.out.println("Can not create!");
				            }
				        } else {
				            System.out.println("Group existed!");
				        }
						break;
					case "UPLOAD_FILE":
						String groupNameUpload = command.nextToken();
						String filePath = command.nextToken();

					    // Extract the file name from the file path
					    String fileName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
					    System.out.println(fileName);
						long fileSize = in.readLong();
						String destinationPath = currentPath.resolve(groupNameUpload).resolve(fileName).toString();
						File f = new File(destinationPath);
						BufferedOutputStream bos;
						try {
							bos = new BufferedOutputStream(new FileOutputStream(f));
							for (int i = 0; i < fileSize; i++) {
								bos.write(in.read());
								bos.flush();
							}
							System.out.println("Upload successed!");
							bos.close();
						} catch (IOException e) {
						}
						break;
					default:
						break;
					}
				} 
			} 
			catch (IOException e) { 
				e.printStackTrace(); 
			} 
			finally { 
				try { 
					if (out != null) { 
						out.close(); 
					} 
					if (in != null) { 
						in.close(); 
						clientSocket.close(); 
					} 
				} 
				catch (IOException e) { 
					e.printStackTrace(); 
				} 
			} 
		} 
	} 
}
