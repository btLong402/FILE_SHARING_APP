package client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import helper.request.FactoryRequest;
import helper.request._request.Request;

import java.io.*;
import java.net.*;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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
				System.out.println("Connect to Server successed!");
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
				byte[] buffer = new byte[4096];

				if (isLogin == false) {
					System.out.println("Please login to use program!");
					System.out.print("#CLIENT> ");
				} else {
					System.out.print('#' + userName.toUpperCase() + "> ");
				}
				String cmd = sc.readLine();
				if (cmd.equals("")) {
					System.out.println("Command not recognized!");
					System.out.println("Use command \"Help\" to show the usage!");
					continue;
				}
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
						if (!command.hasMoreTokens()) {
							System.out.println("Miss file path!");
							System.out.println("Use command \"Help\" to show the usage!");
							break;
						}
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
								case 409:
									System.out.println("File existed!");
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
				case "FOLDER_RENAME":
					if (isLogin == true) {
						System.out.print("Enter group-name: ");
						groupName = sc.readLine();
						System.out.print("Enter folder-name: ");
						folderName = sc.readLine();
						System.out.print("Enter new name: ");
						String newName = sc.readLine();
						requestObj.payload.setFolderName(folderName);
						requestObj.payload.setGroupName(groupName);
						requestObj.payload.setNewFolderName(newName);
						rq = gson.toJson(requestObj);
						out.writeUTF(rq);
						out.flush();
						res = in.readUTF();
						response = gson.fromJson(res, JsonObject.class);
						System.out.println("Response form server:");
						System.out.println(res);
						switch (response.get("responseCode").getAsInt()) {
						case 200:
							System.out.println("Rename folder successfully!");
							break;
						case 404:
							System.out.println("Folder does not exist!");
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
				case "FOLDER_COPY":
					if (isLogin == true) {
						System.out.print("From group: ");
						groupName = sc.readLine();
						System.out.print("To group: ");
						String toGroup = sc.readLine();
						System.out.print("Enter folder-name: ");
						folderName = sc.readLine();
						requestObj.payload.setFolderName(folderName);
						requestObj.payload.from(groupName);
						requestObj.payload.to(toGroup);
						rq = gson.toJson(requestObj);
						out.writeUTF(rq);
						out.flush();
						res = in.readUTF();
						response = gson.fromJson(res, JsonObject.class);
						System.out.println("Response form server:");
						System.out.println(res);
						switch (response.get("responseCode").getAsInt()) {
						case 200:
							System.out.println("Copy folder successfully!");
							break;
						case 404:
							System.out.println("Folder does not exist!");
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
				case "FOLDER_MOVE":
					if (isLogin == true) {
						System.out.print("From group: ");
						groupName = sc.readLine();
						System.out.print("To group: ");
						String toGroup = sc.readLine();
						System.out.print("Enter folder-name: ");
						folderName = sc.readLine();
						requestObj.payload.setFolderName(folderName);
						requestObj.payload.from(groupName);
						requestObj.payload.to(toGroup);
						rq = gson.toJson(requestObj);
						out.writeUTF(rq);
						out.flush();
						res = in.readUTF();
						response = gson.fromJson(res, JsonObject.class);
						System.out.println("Response form server:");
						System.out.println(res);
						switch (response.get("responseCode").getAsInt()) {
						case 200:
							System.out.println("Move folder successfully!");
							break;
						case 404:
							System.out.println("Folder does not exist!");
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
				case "FOLDER_DELETE":
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
						case 200:
							System.out.println("Delete folder successfully!");
							break;
						case 404:
							System.out.println("Folder does not exist!");
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
				// “fileName”:”string”, “groupName”:”string”, “folderName”:”string”,
				// “newFileName”:”string”

				case "FILE_RENAME":
					if (isLogin == true) {
						System.out.print("Enter group-name: ");
						groupName = sc.readLine();
						System.out.print("Enter folder-name: ");
						folderName = sc.readLine();
						System.out.print("Enter file name that you want to rename: ");
						String fileName = sc.readLine();
						System.out.print("Enter new name: ");
						String newName = sc.readLine();
						requestObj.payload.setFileName(fileName);
						requestObj.payload.setFolderName(folderName);
						requestObj.payload.setGroupName(groupName);
						requestObj.payload.setNewFileName(newName);
						rq = gson.toJson(requestObj);
						out.writeUTF(rq);
						out.flush();
						res = in.readUTF();
						response = gson.fromJson(res, JsonObject.class);
						System.out.println("Response form server:");
						System.out.println(res);
						switch (response.get("responseCode").getAsInt()) {
						case 200:
							System.out.println("Rename file successfully!");
							break;
						case 404:
							System.out.println("Folder or File does not exist!");
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
						System.out.println("You do not have permission to rename a file. Please log in!");
					}
					break;
				case "FILE_COPY":
					if (isLogin == true) {
						System.out.print("From group: ");
						groupName = sc.readLine();
						System.out.print("To group: ");
						String toGroup = sc.readLine();
						System.out.print("From folder: ");
						folderName = sc.readLine();
						System.out.print("To folder: ");
						String toFolder = sc.readLine();
						System.out.print("Enter file name: ");
						String fileName = sc.readLine();
						requestObj.payload.from(groupName, folderName);
						requestObj.payload.to(toGroup, toFolder);
						requestObj.payload.setFileName(fileName);
						rq = gson.toJson(requestObj);
						out.writeUTF(rq);
						out.flush();
						res = in.readUTF();
						response = gson.fromJson(res, JsonObject.class);
						System.out.println("Response form server:");
						System.out.println(res);
						switch (response.get("responseCode").getAsInt()) {
						case 200:
							System.out.println("Copy file successfully!");
							break;
						case 404:
							System.out.println("Folder or File does not exist!");
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
				case "FILE_MOVE":
					if (isLogin == true) {
						System.out.print("From group: ");
						groupName = sc.readLine();
						System.out.print("To group: ");
						String toGroup = sc.readLine();
						System.out.print("From folder: ");
						folderName = sc.readLine();
						System.out.print("To folder: ");
						String toFolder = sc.readLine();
						System.out.print("Enter file name: ");
						String fileName = sc.readLine();
						requestObj.payload.from(groupName, folderName);
						requestObj.payload.to(toGroup, toFolder);
						requestObj.payload.setFileName(fileName);
						rq = gson.toJson(requestObj);
						out.writeUTF(rq);
						out.flush();
						res = in.readUTF();
						response = gson.fromJson(res, JsonObject.class);
						System.out.println("Response form server:");
						System.out.println(res);
						switch (response.get("responseCode").getAsInt()) {
						case 200:
							System.out.println("Move file successfully!");
							break;
						case 404:
							System.out.println("Folder or File does not exist!");
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
				case "FILE_DELETE":
					if (isLogin == true) {
						System.out.print("Enter group-name: ");
						groupName = sc.readLine();
						System.out.print("Enter folder-name: ");
						folderName = sc.readLine();
						System.out.print("Enter file name that you want to delete: ");
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
						case 200:
							System.out.println("Delete file successfully!");
							break;
						case 404:
							System.out.println("Folder or File does not exist!");
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
						System.out.println("You do not have permission to rename a file. Please log in!");
					}
					break;
				case "JOIN_GROUP":
					if (isLogin == true) {
						System.out.print("Enter group-name: ");
						groupName = sc.readLine();
						requestObj.payload.setGroupName(groupName);
						out.writeUTF(gson.toJson(requestObj));
						out.flush();
						res = in.readUTF();
						response = gson.fromJson(res, JsonObject.class);
						System.out.println("Response form server:");
						System.out.println(res);
						switch (response.get("responseCode").getAsInt()) {
						case 200:
							System.out.println("Reguest successfully!");
							break;
						case 404:
							System.out.println("Group does not exist!");
							break;
						case 409:
							System.out.println("You are a member in group!");
							break;
						case 429:
							System.out.println("Too many request!");
							break;
						case 501:
							System.out.println("Server error!");
							break;
						default:
							break;
						}
					} else {
						System.out.println("You do not have permission to request to join a group. Please log in!");
					}
					break;
				case "JOIN_REQUEST_STATUS":
					if (isLogin == true) {
						rq = gson.toJson(requestObj);
						out.writeUTF(rq);
						out.flush();
						res = in.readUTF();
						response = gson.fromJson(res, JsonObject.class);
						System.out.println("Response form server:");
						System.out.println(res);
						switch (response.get("responseCode").getAsInt()) {
						case 200:
							JsonArray joinRequestStatusArray = response.getAsJsonObject().getAsJsonObject("payload")
							.getAsJsonArray("listOfAppliedGroups");
					printJoinRequestStatus(joinRequestStatusArray);
							break;
						case 403:
							System.out.println("You are not an admin of group!");
							break;
						case 201:
							System.out.println("You do not have any request!");
							break;
						default:
							System.out.println("You are not an admin in group!");
							break;
						}
					} else {
						System.out.println("You do not have permission to see list groups. Please log in!");
					}
					break;
				case "JOIN_REQUEST_LIST":
					if (isLogin == false) {
						System.out.println("You do not have permission to see member in this group. Please log in.");
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
						switch (response.get("responseCode").getAsInt()) {
						case 200:
							JsonArray joinRequestListArray = response.getAsJsonObject().getAsJsonObject("payload")
									.getAsJsonArray("joinRequestList");
							printJoinRequestList(joinRequestListArray, groupName);
							break;
						case 403:
							System.out.println("You are not an admin of group!");
							break;
						case 201:
							System.out.println("Group does not have any request!");
							break;
						default:
							System.out.println("You are not an admin in group!");
							break;
						}
					}
					break;

				case "INVITE_TO_GROUP":
					if (isLogin) {
						System.out.print("Enter group-name: ");
						groupName = sc.readLine();
						System.out.print("Enter user-name who you want to invite: ");
						String invitedName = sc.readLine();
						requestObj.payload.setGroupName(groupName);
						requestObj.payload.setInvitedName(invitedName);
						rq = gson.toJson(requestObj);
						out.writeUTF(rq);
						out.flush();
						res = in.readUTF();
						response = gson.fromJson(res, JsonObject.class);
						System.out.println("Response form server:");
						System.out.println(res);
						switch (response.get("responseCode").getAsInt()) {
						case 200:
							System.out.println("Invite member successfully!");
							break;
						case 409:
							System.out.println("Member is already in group!");
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
						System.out.println("Please login to use this function!");
					}
					break;
				case "APPROVAL":
					if (isLogin) {
						System.out.print("Enter group-name: ");
						groupName = sc.readLine();
						System.out.print("Enter requester: ");
						String requester = sc.readLine();
						System.out.print("Decision[YES/NO]: ");
						String decision = sc.readLine();
						requestObj.payload.setGroupName(groupName);
						requestObj.payload.setRequester(requester);
						boolean isCorrect = true;
						switch (decision.toUpperCase()) {
						case "Y":
						case "YES":
							requestObj.payload.setDecision("ACCEPT");
							break;
						case "N":
						case "NO":
							requestObj.payload.setDecision("DENIAL");
							break;
						default:
							isCorrect = false;
							System.out.println("WRONG DICISION!");
							break;
						}
						if (isCorrect) {
							rq = gson.toJson(requestObj);
							out.writeUTF(rq);
							out.flush();
							res = in.readUTF();
							response = gson.fromJson(res, JsonObject.class);
							System.out.println("Response form server:");
							System.out.println(res);
							switch (response.get("responseCode").getAsInt()) {
							case 200:
								System.out.println("Approval successfully!");
								break;
							case 400:
								System.out.println("Bad request!");
								break;
							case 403:
								System.out.println("You are not admin of group " + groupName + '!');
								break;
							case 501:
								System.out.println("Server error!");
								break;
							default:
								break;
							}
						}
					} else {
						System.out.println("Please login to use this function!");
					}
					break;
				case "LIST_INVITATION":
					if (isLogin == true) {
						rq = gson.toJson(requestObj);
						out.writeUTF(rq);
						out.flush();
						res = in.readUTF();
						response = gson.fromJson(res, JsonObject.class);
						System.out.println("Response form server:");
						System.out.println(res);
						switch (response.get("responseCode").getAsInt()) {
						case 200:
							JsonArray listOfInvitations = response.getAsJsonObject().getAsJsonObject("payload")
							.getAsJsonArray("listOfInvitation");
					printInvitaionList(listOfInvitations);
							break;

						default:
							System.out.println("You do not have any invitation!");
							break;
						}
					} else {
						System.out.println("You do not have permission to see list groups. Please log in!");
					}
					break;
				case "REMOVE_MEMBER":
					if (!isLogin) {
						System.out.println("You do not have permission to create a group. Please log in.");
					} else {
						System.out.print("Enter group-name: ");
						groupName = sc.readLine();
						System.out.print("Enter member-name: ");
						String memberName = sc.readLine();
						requestObj.payload.setGroupName(groupName);
						requestObj.payload.setMemberName(memberName);
						out.writeUTF(gson.toJson(requestObj));
						out.flush();
						res = in.readUTF();
						response = gson.fromJson(res, JsonObject.class);
						System.out.println("Response form server:");
						System.out.println(res);
						switch (response.get("responseCode").getAsInt()) {
						case 200:
							System.out.println("Remove member successfully!");
							break;
						case 404:
							System.out.println("Member does not exist!");
							break;
						case 403:
							System.out.println("You are not admin of this group!");
							break;
						case 501:
							System.out.println("Server error!");
							break;
						default:
							System.out.println("You can not remove yourself!");
							break;
						}
					}
					break;
				case "LEAVE_GROUP":
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
						switch (response.get("responseCode").getAsInt()) {
						case 200:
							System.out.println("Leave group successfully!");
							break;
						case 404:
							System.out.println("You are not a member in group!");
							break;
						case 403:
							System.out.println("You can not leave because you are admin of this group!");
							break;
						case 501:
							System.out.println("Server error!");
							break;
						default:
							break;
						}
					}
					break;
				case "LIST_GROUP_MEMBERS":
					if (isLogin == false) {
						System.out.println("You do not have permission to see member in this group. Please log in.");
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
						switch (response.get("responseCode").getAsInt()) {
						case 200:
							JsonArray listOfMembersArray = response.getAsJsonObject().getAsJsonObject("payload")
									.getAsJsonArray("listOfMembers");
							printTableMember(listOfMembersArray, groupName);
							break;
						default:
							System.out.println("You are not a member in group!");
							break;
						}
					}
					break;
				case "FOLDER_CONTENT":
					if (isLogin) {
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
						case 200:
							JsonArray folderContentArray = response.getAsJsonObject().getAsJsonObject("payload")
									.getAsJsonArray("folderContents");
							printTableFiles(folderContentArray, folderName, groupName);
							break;
						case 404:
							System.out.println("Folder does not exist!");
							break;
						default:
							System.out.println("You are not a member in group!");
							break;
						}
					} else {
						System.out.println(
								"You do not have permission to see folder content in this group. Please log in.");
					}
					break;
				case "HELP":
					printUsage();
					break;
				default:
					System.out.println("Command not recognized!");
					System.out.println("Use command \"Help\" to show the usage!");
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

	// Function to print usage information
	private static void printUsage() {
		System.out.println("Commands:");
		System.out.println("LOGIN - Log in to the system");
		System.out.println("REGISTER - Register a new user");
		System.out.println("CREATE_GROUP - Create a new group");
		System.out.println("UPLOAD_FILE <file_path> - Upload a file to a group");
		System.out.println("DOWNLOAD_FILE - Download a file from a group");
		System.out.println("CREATE_FOLDER - Create a new folder in a group");
		System.out.println("FOLDER_COPY - Copy a folder to another group");
		System.out.println("FOLDER_MOVE - Move a folder to another group");
		System.out.println("FOLDER_RENAME - Rename a folder in a group");
		System.out.println("FOLDER_DELETE - Delete a folder from a group");
		System.out.println("FOLDER_CONTENT - Content of the folder from a group");
		System.out.println("FILE_RENAME - Rename a file");
		System.out.println("FILE_COPY - Copy a file to another folder");
		System.out.println("FILE_MOVE - Move a file to another folder");
		System.out.println("FILE_DELETE - Delete a file");
		System.out.println("LIST_ALL_GROUPS - List all available groups");
		System.out.println("JOIN_REQUEST_LIST - List all Join request from user to groups");
		System.out.println("JOIN_REQUEST_STATUS - List all Join request status");
		System.out.println("HELP - Show usage");
		System.out.println("EXIT - Exit the program");
	}

	private static void printTableMember(JsonArray list, String groupName) {
		System.out.printf("Member of Group `%s`\n", groupName);
		System.out.println("+----------------------+---------------------------+");
		System.out.printf("| %-20s | %-25s |\n", "Member", "Role");
		System.out.println("+----------------------+---------------------------+");
		for (JsonElement memberElement : list) {
			JsonObject memberObject = memberElement.getAsJsonObject();
			String userName = memberObject.get("userName").getAsString();
			String role = memberObject.get("role").getAsString();
			printTableRow(userName, role);
		}
	}

	private static void printTableFiles(JsonArray list, String folderName, String groupName) {
		System.out.printf("Content of `%s` Folder in `%s` Group \n", folderName, groupName);
		System.out.println("+----------------------+---------------------------+");
		System.out.printf("| %-20s | %-25s |\n", "File name", "File size");
		System.out.println("+----------------------+---------------------------+");
		for (JsonElement element : list) {
			JsonObject fileObject = element.getAsJsonObject();
			String fileName = fileObject.get("fileName").getAsString();
			long fileSize = fileObject.get("fileSize").getAsLong();
			String sizeInMB = String.format("%.3f MB", convertBytesToMB(fileSize));
			printTableRow(fileName, sizeInMB);
		}
	}

	private static void printInvitaionList(JsonArray list) {
		System.out.println("List of Invitation");
		System.out.println("+------------+------------+--------------------------------+");
		System.out.printf("| %-10s | %-10s | %-30s |\n", "groupName", "status", "requestAt");
		System.out.println("+------------+------------+--------------------------------+");
		for (JsonElement element : list) {
			JsonObject invite = element.getAsJsonObject();
			String groupName = invite.get("groupName").getAsString();
			String status = invite.get("status").getAsString();
			String date = invite.get("inviteAt").getAsString();
			printTable3Row(groupName, status ,date);
		}
	}

	private static void printJoinRequestList(JsonArray list, String groupName) {
		System.out.printf("List of Requests in `%s` \n", groupName);
		System.out.println("+----------------------+---------------------------+");
		System.out.printf("| %-20s | %-25s |\n", "requestedUserName", "requestAt");
		System.out.println("+----------------------+---------------------------+");
		for (JsonElement requestListElement : list) {
			JsonObject requestListObject = requestListElement.getAsJsonObject();
			String userName = requestListObject.get("userName").getAsString();
			String date = requestListObject.get("requestAt").getAsString();
			printTableRow(userName, date);
		}
	}

	private static void printJoinRequestStatus(JsonArray list) {
		System.out.printf("List of Request status \n");
		System.out.println("+------------+------------+--------------------------------+");
		System.out.printf("| %-10s | %-10s | %-30s |\n", "groupName", "status", "requestAt");
		System.out.println("+------------+------------+--------------------------------+");
		for (JsonElement requestStatusElement : list) {
			JsonObject requestStatusObject = requestStatusElement.getAsJsonObject();
			String groupName = requestStatusObject.get("groupName").getAsString();
			String status = requestStatusObject.get("status").getAsString();
			String date = requestStatusObject.get("requestAt").getAsString();
			printTable3Row(groupName, status, date);
		}
	}

	private static void printTableRow(String column1, String column2) {
		System.out.printf("| %-20s | %-25s |\n", column1, column2);
		System.out.println("+----------------------+---------------------------+");
	}

	private static void printTable3Row(String column1, String column2, String column3) {
		System.out.printf("| %-10s | %-10s | %-30s |\n", column1, column2, column3);
		System.out.println("+------------+------------+--------------------------------+");
	}

//	private static double convertBytesToKB(long bytes) {
//        return bytes / 1024.0; // 1 KB = 1024 bytes
//    }
	private static String convertTimestampToString(Timestamp timestamp) {
		// Create a SimpleDateFormat object with the desired date format
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		// Convert Timestamp to Date
		Date date = new Date(timestamp.getTime());

		// Format the Date object to a string
		return dateFormat.format(date);
	}

	private static double convertBytesToMB(long bytes) {
		return bytes / (1024.0 * 1024.0); // 1 MB = 1024 KB, 1 KB = 1024 bytes
	}
}
