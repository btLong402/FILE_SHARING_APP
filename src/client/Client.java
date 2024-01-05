package client;

import java.io.*;
import java.net.*;
import java.nio.file.Paths;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import helper.request.FactoryRequest;
import helper.request._request.Request;

// Client class
class Client {

	// driver code
	public static void main(String[] args) {
		// establish a connection by providing host and port
		// number
		Socket socket = null;
		DataOutputStream out = null;
		DataInputStream in = null;
		boolean isLogin = false;
		String userName = "";
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		try {
			socket = new Socket("localhost", 5555);
			// writing to server
			out = new DataOutputStream(socket.getOutputStream());

			// reading from server
			in = new DataInputStream(socket.getInputStream());
			BufferedReader sc = new BufferedReader(new InputStreamReader(System.in));
			// boolean isLogin = false;
			if (in.readInt() == 200) {
				System.out.println("Connect to Server successedS!");
			}
			while (true) {
				String rq;
				String res;
				JsonObject response;
				JsonObject payload;
				String groupName;
				String folderName;
				String filePath;
				String uName;
				String ps;
				int responseCode;
				byte[] buffer = new byte[4096];

				if (isLogin == false) {
					System.out.println("Please login to use program!");
					System.out.print("#CLIENT> ");
				} else {
					System.out.print('#' + userName.toUpperCase() + "> ");
				}
				String cmd = sc.readLine();

				StringTokenizer command = new StringTokenizer(cmd, " ");
				String key = command.nextToken().toUpperCase();
				if (key.equals("EXIT")) {
					rq = "{'messageType': 'EXIT'}";
					out.writeUTF(rq);
					out.flush();
					break;
				}
				Request requestObj = FactoryRequest.intialRequest(key);
				switch (key) {
				case "CREATE_GROUP":
					if (isLogin == false) {
						System.out.println("You do not have permission to create a group. Please log in.");
					} else {
						System.out.print("Enter group-name: ");
						groupName = sc.readLine();
						requestObj.payload.setGroupName(groupName);
						out.writeUTF(gson.toJson(requestObj));
						out.flush();
						res = in.readUTF();
						response = gson.fromJson(res, JsonObject.class);
						System.out.println("Response form server:");
						System.out.println(res);
						if (response.get("responseCode").getAsInt() == 409) {
							System.out.println("Group existed!");
						} else if (response.get("responseCode").getAsInt() == 501) {
							System.out.println("Server error!");
						} else {
							System.out.println("Create success!");
						}
					}
					break;
				case "UPLOAD_FILE":
					if (isLogin == true) {
						filePath = command.nextToken();
						File fileSource = new File(filePath);
						if (!fileSource.exists()) {
							System.out.println("Source file path does not exists!!!");
							break;
						} else {
							System.out.print("Upload at group: ");
							groupName = sc.readLine();
							System.out.print("Upload at folder: ");
							folderName = sc.readLine();
							try {
								// Sử dụng Files.probeContentType() để lấy loại của file
								requestObj.payload.setGroupName(groupName);
								requestObj.payload.setFileName(fileSource.getName());
								requestObj.payload.setFileSize(fileSource.length());
								requestObj.payload.setFolderName(folderName);
								rq = gson.toJson(requestObj);
								BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fileSource));
								out.writeUTF(rq);
								out.flush();
								res = in.readUTF();
								response = gson.fromJson(res, JsonObject.class);
								System.out.println("Response form server:");
								System.out.println(res);
								switch (response.get("responseCode").getAsInt()) {
								case 200:
									System.out.println("Upload start. Please wait!");
									int data;
									long byteSend = 0;
									while ((data = bis.read(buffer)) != -1) {
										out.write(buffer, 0, data);
										byteSend += data;
										trackProgress(fileSource.length(), byteSend);
										out.flush();
									}
									bis.close();
									System.out.println();
									System.out.println("Uploaded!");
									break;
								case 404:
									System.out.println("Folder or Group does not existed!");
									break;
								case 403:
									System.out.println("You are not member of this group!");
									break;
								default:
									break;
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					} else {
						System.out.println("You do not have permission to upload a file. Please log in.");
					}
					break;
				case "DOWNLOAD_FILE":
					if (isLogin) {
						System.out.print("Download from group: ");
						groupName = sc.readLine();
						System.out.print("Download from folder: ");
						folderName = sc.readLine();
						System.out.print("Enter File name: ");
						String fileName = sc.readLine();
						requestObj.payload.setFileName(fileName);
						requestObj.payload.setFolderName(folderName);
						requestObj.payload.setGroupName(groupName);
						rq = gson.toJson(requestObj);
						out.writeUTF(rq);
						out.flush();
						res = in.readUTF();
						response = gson.fromJson(res, JsonObject.class);
						System.out.println("Response form server:");
						System.out.println(res);
						switch (response.get("responseCode").getAsInt()) {
						case 404:
							System.out.println("Group or Folder or File does not exists!!!");
							break;
						case 403:
							System.out.println("You are not member of this group!");
							break;
						case 200:
							payload = response.get("payload").getAsJsonObject();
							long fileSize = payload.get("fileSize").getAsLong();
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
							break;
						default:
							break;
						}
					} else {
						System.out.println("You do not have permission to download a file. Please log in.");
					}
					break;
				case "LOGIN":
					if (isLogin == true) {
						System.out.println("You are already login!");
					} else {
						System.out.print("Enter user-name: ");
						uName = sc.readLine();
						System.out.print("Enter password: ");
						ps = sc.readLine();
						requestObj.payload.setUserName(uName);
						requestObj.payload.setPassword(ps);
						rq = gson.toJson(requestObj);
						out.writeUTF(rq);
						out.flush();
						res = in.readUTF();
						response = gson.fromJson(res, JsonObject.class);
						System.out.println("Response form server:");
						System.out.println(res);
						if (response.get("responseCode").getAsInt() == 200) {
							userName = uName;
							isLogin = true;
							System.out.println("Login success!");
						} else {
							System.out.println("Wrong User Name or Password!");
						}
					}
					break;
				case "REGISTER":
					if (isLogin == true) {
						System.out.println("You are already login!");
					} else {
						System.out.print("Enter user-name: ");
						uName = sc.readLine();
						System.out.print("Enter password: ");
						ps = sc.readLine();
						requestObj.payload.setUserName(uName);
						requestObj.payload.setPassword(ps);
						rq = gson.toJson(requestObj);
						out.writeUTF(rq);
						out.flush();
						res = in.readUTF();
						response = gson.fromJson(res, JsonObject.class);
						System.out.println("Response form server:");
						System.out.println(res);
						if (response.get("responseCode").getAsInt() == 201) {
							userName = uName;
							isLogin = true;
							System.out.println("Create success! Hello " + userName);
						} else {
							System.out.println("User already created!");
						}
					}
					break;
				case "LIST_ALL_GROUPS":
					if (isLogin == true) {
						rq = gson.toJson(requestObj);
						out.writeUTF(rq);
						out.flush();
						res = in.readUTF();
						response = gson.fromJson(res, JsonObject.class);
						System.out.println("Response form server:");
						System.out.println(res);
					} else {
						System.out.println("You do not have permission to see list groups. Please log in!");
					}
					break;
				case "CREATE_FOLDER":
					if (isLogin == true) {
						System.out.print("Enter group-name: ");
						groupName = sc.readLine();
						System.out.print("Enter folder-name: ");
						folderName = sc.readLine();
						requestObj.payload.setFolderName(folderName);
						requestObj.payload.setGroupName(groupName);
						rq = gson.toJson(requestObj);
						out.writeUTF(rq);
						out.flush();
						res = in.readUTF();
						response = gson.fromJson(res, JsonObject.class);
						System.out.println("Response form server:");
						System.out.println(res);
						switch (response.get("responseCode").getAsInt()) {
						case 201:
							System.out.println("Create folder success!");
							break;
						case 409:
							System.out.println("Folder existed!");
							break;
						case 403:
							System.out.println("You are not a member in group!");
							break;
						case 501:
							System.out.println("Server error!");
							break;
						default:
							break;
						}
					} else {
						System.out.println("You do not have permission to create a folder. Please log in!");
					}
					break;
				default:
					System.out.println("Command not recognized!");
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
		System.out.printf("\r");
		System.out.flush();
	}
}
