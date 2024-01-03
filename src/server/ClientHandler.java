package server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import controllers.folder_controller.FolderController;
import controllers.group_controller.GroupController;
import controllers.user_controller.UserController;
import helper.response.FactoryResponse;
import helper.response._response.Response;

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
				System.out.println(cmd);
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
				Response responseObj = FactoryResponse.intialResponse(key);
				switch (key) {
				case "CREATE_GROUP":
					String path = currentPath.resolve(data.get("groupName").getAsString()).toString();
					File group = new File(path);
					if (!group.exists()) {

						boolean result = group.mkdirs(); // Tạo thư mục và tất cả các thư mục cha nếu chưa tồn tại
						if (result) {
							System.out.println("Create group success!");
							new GroupController().createGroup(userController.getUserName(),
									data.get("groupName").getAsString());
							responseObj.setResponseCode(201);
						} else {
							System.out.println("Can not create!");
							responseObj.setResponseCode(501);
						}
						out.writeUTF(gson.toJson(responseObj));
						out.flush();

					} else {
						responseObj.setResponseCode(409);
						out.writeUTF(gson.toJson(responseObj));
						out.flush();
					}
					break;
				case "UPLOAD_FILE":
					File Group = new File(this.currentPath.resolve(data.get("groupName").getAsString()).toString());
					if (Group.exists()) {
						responseObj.setResponseCode(200);
						out.writeUTF(gson.toJson(responseObj));
						out.flush();
						File folder = new File(this.currentPath.resolve(data.get("groupName").getAsString())
								.resolve(data.get("folderName").getAsString()).toString());
						if (folder.exists()) {
							responseObj.setResponseCode(200);
							out.writeUTF(gson.toJson(responseObj));
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
								responseObj.setResponseCode(200);
								out.writeUTF(gson.toJson(responseObj));
								out.flush();
							} catch (IOException e) {
								e.printStackTrace();
							}
						} else {
							responseObj.setResponseCode(404);
							out.writeUTF(gson.toJson(responseObj));
							out.flush();
						}
					} else {
						responseObj.setResponseCode(404);
						out.writeUTF(gson.toJson(responseObj));
						out.flush();
					}
					break;
				case "DOWNLOAD_FILE":
					File file = new File(this.currentPath.resolve(data.get("groupName").getAsString())
							.resolve(data.get("folderName").getAsString()).resolve(data.get("fileName").getAsString())
							.toString());
					if (file.exists()) {
						responseObj.setResponseCode(200);
						responseObj.payload.setFileName(data.get("fileName").getAsString());
						responseObj.payload.setFileSize(file.length());
						out.writeUTF(gson.toJson(responseObj));
						out.flush();
						BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
						int byteRead;
						long byteReaded = 0;
						while ((byteRead = bis.read(buffer)) != -1) {
							out.write(buffer, 0, byteRead);
							byteReaded += byteRead;
							trackProgress(file.length(), byteReaded);
							out.flush();
						}
						bis.close();
						System.out.println();
						System.out.println("Sent file successfully!");
					} else {
						responseObj.setResponseCode(404);
						out.writeUTF(gson.toJson(responseObj));
						out.flush();
					}
					break;
				case "LOGIN":
					if (userController.signIn(data.get("userName").getAsString(), data.get("password").getAsString())) {
						responseObj.setResponseCode(200);
						out.writeUTF(gson.toJson(responseObj));
						out.flush();
					} else {
						responseObj.setResponseCode(404);
						out.writeUTF(gson.toJson(responseObj));
						out.flush();
					}
					break;
				case "REGISTER":
					if (userController.signUp(data.get("userName").getAsString(), data.get("password").getAsString())) {
						responseObj.setResponseCode(201);
						out.writeUTF(gson.toJson(responseObj));
						out.flush();
					} else {
						responseObj.setResponseCode(409);
						out.writeUTF(gson.toJson(responseObj));
						out.flush();
					}
					break;
				case "LIST_ALL_GROUPS":
					responseObj.setResponseCode(200);
					responseObj.payload.setListGroups(new GroupController().listAllGroup());
					out.writeUTF(gson.toJson(responseObj));
					out.flush();
					break;
				case "CREATE_FOLDER":
					File folder = new File(this.currentPath.resolve(data.get("groupName").getAsString())
							.resolve(data.get("folderName").getAsString()).toString());
					if (new GroupController().isMember(userController.getUserName(),
							data.get("groupName").getAsString())) {
						if (folder.exists()) {
							responseObj.setResponseCode(409);
							out.writeUTF(gson.toJson(responseObj));
							out.flush();
						} else {
							if (folder.mkdir()) {
								System.out.println("Create folder success!");
								new FolderController().createFolder(data.get("folderName").getAsString(),
										data.get("groupName").getAsString());

								responseObj.setResponseCode(201);
							} else {
								System.out.println("Can not create!");
								responseObj.setResponseCode(501);
							}
							out.writeUTF(gson.toJson(responseObj));
							out.flush();
						}
					} else {
						responseObj.setResponseCode(403);
						out.writeUTF(gson.toJson(responseObj));
						out.flush();
					}
					break;
				default:
					responseObj.setResponseCode(400);
					out.writeUTF(gson.toJson(responseObj));
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
