package server;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import controllers.user_controller.UserController;

public class ClientHandler implements Runnable {
	private final Socket clientSocket;
	Gson gson;
	Path currentPath;

	public ClientHandler(Socket clientSocket) throws IOException {
		this.clientSocket = clientSocket;
		this.gson = new GsonBuilder().setPrettyPrinting().create();
		this.currentPath = Paths.get("").toAbsolutePath().resolve("root");
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			DataInputStream in = new DataInputStream(this.clientSocket.getInputStream());
			DataOutputStream out = new DataOutputStream(this.clientSocket.getOutputStream());
			out.writeInt(200);
			String cmd;
			UserController userController = new UserController();
			while (true) {
				byte[] buffer = new byte[4096];
				cmd = in.readUTF();
//				StringTokenizer command = new StringTokenizer(cmd, " ");
				JsonObject request = gson.fromJson(cmd, JsonObject.class);
				System.out.println("Request from client: " + this.clientSocket.getInetAddress().getHostAddress());
				System.out.println(request.getAsJsonObject());
				String response = "";
				String key = request.get("messageType").getAsString();
				if (key.equals("EXIT")) {
					out.close();
					in.close();
					this.clientSocket.close();
					System.out.println(
							"Client " + this.clientSocket.getInetAddress().getHostAddress() + " is disconnected!");
					break;
				}
				JsonObject data = request.getAsJsonObject("payload");
				switch (key) {
				case "CREATE_GROUP":

					break;
				case "UPLOAD_FILE":
					File Group = new File(this.currentPath.resolve(data.get("groupName").getAsString()).toString());
					if (Group.exists()) {
						response = "{'statusCode': 200}";
						out.writeUTF(response);
						out.flush();
						File folder = new File(this.currentPath.resolve(data.get("groupName").getAsString())
								.resolve(data.get("folderName").getAsString()).toString());
						if (folder.exists()) {
							response = "{'statusCode': 200}";
							out.writeUTF(response);
							out.flush();

							long fileSize = data.get("fileSize").getAsLong();
							int bytesRead;
							long byteReaded = 0;
							String destinationPath = this.currentPath.resolve(data.get("groupName").getAsString())
									.resolve(data.get("folderName").getAsString())
									.resolve(data.get("fileName").getAsString()).toString();
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
								System.out.println("Upload successfully!");
								response = "{'statusCode': 200}";
								out.writeUTF(response);
								out.flush();
							} catch (IOException e) {
								e.printStackTrace();
							}
						} else {
							response = "{'statusCode': 404}";
							out.writeUTF(response);
							out.flush();
						}
					} else {
						response = "{'statusCode': 404}";
						out.writeUTF(response);
						out.flush();
					}
					break;
				case "DOWNLOAD_FILE":
					break;
				case "LOGIN":
					if (userController.signIn(data.get("userName").getAsString(), data.get("password").getAsString())) {
						response = "{'statusCode': 200}";
					} else {
						response = "{'statusCode': 404}";
					}
					out.writeUTF(response);
					out.flush();
					break;
				case "REGISTER":
					if (userController.signUp(data.get("userName").getAsString(), data.get("passwrod").getAsString())) {
						response = "{'statusCode': 201}";
					} else {
						response = "{'statusCode': 409}";
					}
					out.writeUTF(response);
					out.flush();
					break;

				default:
					response = "{'statusCode': 400}";
					out.writeUTF(response);
					out.flush();
					break;
				}

			}
		} catch (Exception e) {
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
