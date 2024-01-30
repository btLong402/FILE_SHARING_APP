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
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import controllers.file_controller.FileController;
import controllers.folder_controller.FolderController;
import controllers.group_controller.GroupController;
import controllers.user_controller.UserController;

import helper.response.FactoryResponse;
import helper.response._response.Response;
import helper.response.payload.EmptyPayload;
import models.join_model.JoinRequestList;
import models.join_model.JoinRequestStatus;
import models.join_model.ListOfInvitation;

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
					if (new GroupController().isMember(userController.getUserName(),
							data.get("groupName").getAsString())) {
						File folder = new File(this.currentPath.resolve(data.get("groupName").getAsString())
								.resolve(data.get("folderName").getAsString()).toString());
						if (folder.exists()) {
							if (new FileController().createFile(data.get("fileName").getAsString(),
									data.get("fileSize").getAsLong(), data.get("groupName").getAsString(),
									data.get("folderName").getAsString())) {
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
									System.out.println("Upload start. Please wait!");
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
									System.out.println("Upload successfully!");
								} catch (IOException e) {
									e.printStackTrace();
								}
							} else {
								responseObj.setResponseCode(409);
								out.writeUTF(gson.toJson(responseObj));
								out.flush();
							}
						} else {
							responseObj.setResponseCode(404);
							out.writeUTF(gson.toJson(responseObj));
							out.flush();
						}
					} else {
						responseObj.setResponseCode(403);
						out.writeUTF(gson.toJson(responseObj));
						out.flush();
					}

					break;
				case "DOWNLOAD_FILE":
					if (new GroupController().isMember(userController.getUserName(),
							data.get("groupName").getAsString())) {
						File file = new File(this.currentPath.resolve(data.get("groupName").getAsString())
								.resolve(data.get("folderName").getAsString())
								.resolve(data.get("fileName").getAsString()).toString());
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
							responseObj.setPayload(new EmptyPayload());
							out.writeUTF(gson.toJson(responseObj));
							out.flush();
						}
					} else {
						responseObj.setResponseCode(403);
						responseObj.setPayload(new EmptyPayload());
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
					if (new GroupController().isMember(userController.getUserName(),
							data.get("groupName").getAsString())) {
						File folder = new File(this.currentPath.resolve(data.get("groupName").getAsString())
								.resolve(data.get("folderName").getAsString()).toString());
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
				case "FOLDER_RENAME":
					if (new GroupController().isMember(userController.getUserName(),
							data.get("groupName").getAsString())) {
						Path folder = Paths.get(this.currentPath.resolve(data.get("groupName").getAsString())
								.resolve(data.get("folderName").getAsString()).toString());
						if (Files.exists(folder)) {
							if (new FolderController().rename(data.get("groupName").getAsString(),
									data.get("folderName").getAsString(), data.get("newFolderName").getAsString())) {
								Path newFolder = Paths.get(this.currentPath.resolve(data.get("groupName").getAsString())
										.resolve(data.get("newFolderName").getAsString()).toString());
								if (Files.move(folder, newFolder) != null) {
									responseObj.setResponseCode(200);
								} else {
									new FolderController().rename(data.get("groupName").getAsString(),
											data.get("newFolderName").getAsString(),
											data.get("folderName").getAsString());
									responseObj.setResponseCode(501);
								}
							} else {
								responseObj.setResponseCode(501);
							}
						} else {
							responseObj.setResponseCode(404);
						}
					} else {
						responseObj.setResponseCode(403);
					}
					out.writeUTF(gson.toJson(responseObj));
					out.flush();
					break;
				case "FOLDER_COPY":
					if (new GroupController().isMember(userController.getUserName(),
							data.get("fromGroup").getAsString())
							&& new GroupController().isMember(userController.getUserName(),
									data.get("toGroup").getAsString())) {
						File folder = new File(this.currentPath.resolve(data.get("fromGroup").getAsString())
								.resolve(data.get("folderName").getAsString()).toString());
						if (folder.exists()) {
							if (new FolderController().copy(data.get("fromGroup").getAsString(),
									data.get("toGroup").getAsString(), data.get("folderName").getAsString())) {
								Path fromGroup = Paths.get(this.currentPath.resolve(data.get("fromGroup").getAsString())
										.resolve(data.get("folderName").getAsString()).toString());
								Path toGroup = Paths.get(this.currentPath.resolve(data.get("toGroup").getAsString())
										.resolve(data.get("folderName").getAsString()).toString());
								try {
									copyFolder(fromGroup, toGroup);
									// Check if the copy operation was successful
									if (Files.exists(toGroup)) {
										responseObj.setResponseCode(200);
									} else {
										new FolderController().delete(data.get("toGroup").getAsString(),
												data.get("folderName").getAsString());
										responseObj.setResponseCode(501);
									}
								} catch (Exception e) {
									// TODO: handle exception
									e.printStackTrace();
								}
							} else {
								responseObj.setResponseCode(501);
							}
						} else {
							responseObj.setResponseCode(404);
						}
					} else {
						responseObj.setResponseCode(403);
					}
					out.writeUTF(gson.toJson(responseObj));
					out.flush();
					break;
				case "FOLDER_MOVE":
					if (new GroupController().isMember(userController.getUserName(),
							data.get("fromGroup").getAsString())
							&& new GroupController().isMember(userController.getUserName(),
									data.get("toGroup").getAsString())) {
						Path sourcePath = Paths.get(this.currentPath.resolve(data.get("fromGroup").getAsString())
								.resolve(data.get("folderName").getAsString()).toString());
						if (Files.exists(sourcePath)) {
							Path destinationPath = Paths.get(this.currentPath.resolve(data.get("toGroup").getAsString())
									.resolve(data.get("folderName").getAsString()).toString());
							if (new FolderController().move(data.get("toGroup").getAsString(),
									data.get("fromGroup").getAsString(), data.get("folderName").getAsString())) {
								if (Files.move(sourcePath, destinationPath) != null) {
									responseObj.setResponseCode(200);
								} else {
									new FolderController().move(data.get("toGroup").getAsString(),
											data.get("fromGroup").getAsString(), data.get("folderName").getAsString());
									responseObj.setResponseCode(501);
								}
							} else {
								responseObj.setResponseCode(501);
							}
						} else {
							responseObj.setResponseCode(404);
						}
					} else {
						responseObj.setResponseCode(403);
					}
					out.writeUTF(gson.toJson(responseObj));
					out.flush();
					break;
				case "FOLDER_DELETE":
					if (new GroupController().isAdmin(userController.getUserName(),
							data.get("groupName").getAsString())) {
						Path desPath = Paths.get(this.currentPath.resolve(data.get("groupName").getAsString())
								.resolve(data.get("folderName").getAsString()).toString());

						if (Files.exists(desPath)) {
							if (new FolderController().delete(data.get("groupName").getAsString(),
									data.get("folderName").getAsString())) {
								deleteFolder(desPath);
								responseObj.setResponseCode(200);

							} else {
								responseObj.setResponseCode(501);
							}
						} else {
							responseObj.setResponseCode(404);
						}
					} else {
						responseObj.setResponseCode(403);
					}
					out.writeUTF(gson.toJson(responseObj));
					out.flush();
					break;
				case "FILE_RENAME":
					if (new GroupController().isMember(userController.getUserName(),
							data.get("groupName").getAsString())) {
						File file = new File(this.currentPath.resolve(data.get("groupName").getAsString())
								.resolve(data.get("folderName").getAsString())
								.resolve(data.get("fileName").getAsString()).toString());
						if (file.exists()) {
							if (new FileController().rename(data.get("fileName").getAsString(),
									data.get("newFileName").getAsString(), data.get("groupName").getAsString(),
									data.get("folderName").getAsString())) {
								File newFile = new File(this.currentPath.resolve(data.get("groupName").getAsString())
										.resolve(data.get("folderName").getAsString())
										.resolve(data.get("newFileName").getAsString()).toString());
								if (file.renameTo(newFile))
									responseObj.setResponseCode(200);
								else {
									new FileController().rename(data.get("newNameFile").getAsString(),
											data.get("fileName").getAsString(), data.get("groupName").getAsString(),
											data.get("folderName").getAsString());
									responseObj.setResponseCode(501);
								}
							} else {
								responseObj.setResponseCode(501);
							}
						} else {
							responseObj.setResponseCode(404);
						}
					} else {
						responseObj.setResponseCode(403);
					}
					out.writeUTF(gson.toJson(responseObj));
					out.flush();
					break;
				case "FILE_DELETE":
					if (new GroupController().isMember(userController.getUserName(),
							data.get("groupName").getAsString())) {
						File file = new File(this.currentPath.resolve(data.get("groupName").getAsString())
								.resolve(data.get("folderName").getAsString())
								.resolve(data.get("fileName").getAsString()).toString());
						if (file.exists()) {
							long fileSize = file.length();
							if (new FileController().delete(data.get("fileName").getAsString(),
									data.get("groupName").getAsString(), data.get("folderName").getAsString())) {
								if (file.delete()) {
									responseObj.setResponseCode(200);
									System.out.println("File deleted successfully.");
								} else {
									new FileController().createFile(data.get("fileName").getAsString(), fileSize,
											data.get("groupName").getAsString(), data.get("folderName").getAsString());
									responseObj.setResponseCode(501);
								}
							} else {

								responseObj.setResponseCode(501);
							}
						} else {
							responseObj.setResponseCode(404);
						}
					} else {
						responseObj.setResponseCode(403);
					}
					out.writeUTF(gson.toJson(responseObj));
					out.flush();
					break;
				case "FILE_COPY":
					if (new GroupController().isMember(userController.getUserName(),
							data.get("fromGroup").getAsString())
							&& new GroupController().isMember(userController.getUserName(),
									data.get("toGroup").getAsString())) {
						Path fromFolder = Paths.get(this.currentPath.resolve(data.get("fromGroup").getAsString())
								.resolve(data.get("fromFolder").getAsString())
								.resolve(data.get("fileName").getAsString()).toString());
						if (Files.exists(fromFolder)) {
							if (new FileController().copy(data.get("fileName").getAsString(), Files.size(fromFolder),
									data.get("fromGroup").getAsString(), data.get("toGroup").getAsString(),
									data.get("fromFolder").getAsString(), data.get("toFolder").getAsString())) {

								Path toFolder = Paths.get(this.currentPath.resolve(data.get("toGroup").getAsString())
										.resolve(data.get("toFolder").getAsString())
										.resolve(data.get("fileName").getAsString()).toString());
								try {
									Files.copy(fromFolder, toFolder);
									// Check if the copy operation was successful
									if (Files.exists(toFolder)) {
										responseObj.setResponseCode(200);
									} else {
										new FileController().delete(data.get("fileName").getAsString(),
												data.get("toGroup").getAsString(), data.get("toFolder").getAsString());
										responseObj.setResponseCode(501);
									}
								} catch (Exception e) {
									// TODO: handle exception
									e.printStackTrace();
								}
							} else {
								responseObj.setResponseCode(501);
							}
						} else {
							responseObj.setResponseCode(404);
						}
					} else {
						responseObj.setResponseCode(403);
					}
					out.writeUTF(gson.toJson(responseObj));
					out.flush();
					break;
				case "FILE_MOVE":
					if (new GroupController().isMember(userController.getUserName(),
							data.get("fromGroup").getAsString())
							&& new GroupController().isMember(userController.getUserName(),
									data.get("toGroup").getAsString())) {
						Path sourcePath = Paths.get(this.currentPath.resolve(data.get("fromGroup").getAsString())
								.resolve(data.get("fromFolder").getAsString())
								.resolve(data.get("fileName").getAsString()).toString());
						if (Files.exists(sourcePath)) {
							Path destinationPath = Paths.get(this.currentPath.resolve(data.get("toGroup").getAsString())
									.resolve(data.get("toFolder").getAsString())
									.resolve(data.get("fileName").getAsString()).toString());
							if (new FileController().move(data.get("fileName").getAsString(), Files.size(sourcePath),
									data.get("fromGroup").getAsString(), data.get("toGroup").getAsString(),
									data.get("fromFolder").getAsString(), data.get("toFolder").getAsString())) {
								if (Files.move(sourcePath, destinationPath) != null) {
									responseObj.setResponseCode(200);
								} else {
									new FileController().move(data.get("fileName").getAsString(),
											Files.size(sourcePath), data.get("toGroup").getAsString(),
											data.get("fromGroup").getAsString(), data.get("toFolder").getAsString(),
											data.get("fromFolder").getAsString());
									responseObj.setResponseCode(501);
								}
							} else {
								responseObj.setResponseCode(501);
							}
						} else {
							responseObj.setResponseCode(404);
						}
					} else {
						responseObj.setResponseCode(403);
					}
					out.writeUTF(gson.toJson(responseObj));
					out.flush();
					break;
				case "LIST_GROUP_MEMBERS":
					if (new GroupController().isMember(userController.getUserName(),
							data.get("groupName").getAsString())) {
						responseObj.setResponseCode(200);
						responseObj.payload.setListOfMembers(
								new GroupController().listMember(data.get("groupName").getAsString()));
					} else {
						responseObj.setResponseCode(403);
						responseObj.setPayload(new EmptyPayload());
					}
					out.writeUTF(gson.toJson(responseObj));
					out.flush();
					break;
				case "FOLDER_CONTENT":
					if (new GroupController().isMember(userController.getUserName(),
							data.get("groupName").getAsString())) {
						Path folder = Paths.get(this.currentPath.resolve(data.get("groupName").getAsString())
								.resolve(data.get("folderName").getAsString()).toString());
						if (Files.exists(folder)) {
							responseObj.setResponseCode(200);
							responseObj.payload.setFolderContents(new FolderController().folderContent(
									data.get("groupName").getAsString(), data.get("folderName").getAsString()));
						} else {
							responseObj.setResponseCode(404);
							responseObj.setPayload(new EmptyPayload());
						}
					} else {
						responseObj.setResponseCode(403);
						responseObj.setPayload(new EmptyPayload());
					}
					out.writeUTF(gson.toJson(responseObj));
					out.flush();
					break;
				case "REMOVE_MEMBER":
					if (new GroupController().isAdmin(userController.getUserName(),
							data.get("groupName").getAsString())) {
						if (new GroupController().isAdmin(data.get("memberName").getAsString(),
								data.get("groupName").getAsString()) == false) { // Can't understand
							if (new GroupController().isMember(data.get("memberName").getAsString(),
									data.get("groupName").getAsString())) {
								if (new GroupController().removeMember(data.get("memberName").getAsString(),
										data.get("groupName").getAsString()))
									responseObj.setResponseCode(200);
								else
									responseObj.setResponseCode(501);
							} else
								responseObj.setResponseCode(404);
						} else
							responseObj.setResponseCode(400);
					} else
						responseObj.setResponseCode(403);
					out.writeUTF(gson.toJson(responseObj));
					out.flush();
					break;
				case "LEAVE_GROUP":
					if (new GroupController().isAdmin(userController.getUserName(),
							data.get("groupName").getAsString())) {
						responseObj.setResponseCode(403);
					} else {
						if (new GroupController().isMember(userController.getUserName(),
								data.get("groupName").getAsString())) {
							if (new GroupController().leaveGroup(userController.getUserName(),
									data.get("groupName").getAsString()))
								responseObj.setResponseCode(200);
							else
								responseObj.setResponseCode(501);
						} else
							responseObj.setResponseCode(404);
					}
					out.writeUTF(gson.toJson(responseObj));
					out.flush();
					break;
				case "LIST_OF_INVITATION":
					
					List<ListOfInvitation> list = new GroupController().listInviteStatus(userController.getUserName());
					if(list.isEmpty()) {
						responseObj.setResponseCode(201);
					}
					else {
						responseObj.setResponseCode(200);
						responseObj.payload
						.setListOfInvitation(list);
					}
					out.writeUTF(gson.toJson(responseObj));
					out.flush();
					break;
				case "APPROVAL":
					if (new GroupController().isAdmin(userController.getUserName(),
							data.get("groupName").getAsString())) {
						if (data.get("decision").getAsString().equals("ACCEPT")) {
							if (new GroupController().accept(data.get("requester").getAsString(),
									data.get("groupName").getAsString()))
								responseObj.setResponseCode(200);
							else
								responseObj.setResponseCode(400);
						} else {
							if (new GroupController().denied(data.get("requester").getAsString(),
									data.get("groupName").getAsString()))
								responseObj.setResponseCode(200);
							else
								responseObj.setResponseCode(400);
						}
					} else
						responseObj.setResponseCode(403);
					out.writeUTF(gson.toJson(responseObj));
					out.flush();
					break;
				case "INVITE_TO_GROUP":
					if (new GroupController().isMember(userController.getUserName(),
							data.get("groupName").getAsString())) {
						if (new GroupController().isMember(data.get("invitedUserName").getAsString(),
								data.get("groupName").getAsString())) {
							responseObj.setResponseCode(409);
						} else {
							if (new GroupController().inviteGroup(data.get("invitedUserName").getAsString(),
									data.get("groupName").getAsString())) {
								responseObj.setResponseCode(200);
							} else {
								responseObj.setResponseCode(400); // 400 la gi???
							}
						}
					} else {
						responseObj.setResponseCode(403);
					}
					out.writeUTF(gson.toJson(responseObj));
					out.flush();
					break;
				case "JOIN_REQUEST_LIST":
					if (new GroupController().isAdmin(userController.getUserName(),
							data.get("groupName").getAsString())) {
						List<JoinRequestList> listRq = new GroupController().listRequestList(data.get("groupName").getAsString());
						if(listRq.isEmpty()) {
							responseObj.setResponseCode(201);
						}
						else {
							responseObj.setResponseCode(200);
							responseObj.payload.setJoinRequestList(
									listRq);
						}
					} else
						responseObj.setResponseCode(403);
					out.writeUTF(gson.toJson(responseObj));
					out.flush();
					break;
				case "JOIN_REQUEST_STATUS":
					List<JoinRequestStatus> listRqStatus = new GroupController().listRequestStatus(userController.getUserName());
					if(listRqStatus.isEmpty()) {
						responseObj.setResponseCode(201);
					}
					else {
						responseObj.setResponseCode(200);
						responseObj.payload.setJoinRequestStatus(
								listRqStatus);
					}
					out.writeUTF(gson.toJson(responseObj));
					out.flush();
					break;
				case "JOIN_GROUP":
					if (new GroupController().isMember(userController.getUserName(),
							data.get("groupName").getAsString())) {
						responseObj.setResponseCode(409);
					} else {
						if (new GroupController().requestJoin(userController.getUserName(),
								data.get("groupName").getAsString())) {
							responseObj.setResponseCode(200);
						} else {
							responseObj.setResponseCode(429);
						}
					}
					out.writeUTF(gson.toJson(responseObj));
					out.flush();
					break;
				default:
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
			System.out.println("Folder copy successfully.");
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

	public static void moveFolder(Path sourcePath, Path destinationPath) {
		try {
			Files.move(sourcePath, destinationPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Folder moved successfully.");
	}

	public static void clearLine() {
		System.out.print("\r");
		System.out.flush();
	}
}
