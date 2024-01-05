package server;

import java.io.*;
import java.net.*;

// Server class
class Server implements Runnable {

	private int serverPort;
	private ServerSocket serverSocket;
	private boolean isStopped = false;

	public Server(int port) {
		this.serverPort = port;
	}

	private synchronized boolean isStopped() {
		return this.isStopped;
	}

	public synchronized void stop() {
		this.isStopped = true;
		try {
			this.serverSocket.close();

		} catch (IOException e) {
			throw new RuntimeException("Error on stop server", e);
		}
	}

	private void openServerSocket() {
		try {
			this.serverSocket = new ServerSocket(this.serverPort);
			this.serverSocket.setReuseAddress(true);
			System.out.println("Server is running on port " + this.serverPort);

		} catch (IOException e) {
			throw new RuntimeException("Cannot open port " + this.serverPort, e);
		}
	}
//
	@Override
	public void run() {
		// TODO Auto-generated method stub
		openServerSocket();

		while (!isStopped()) {
			Socket clientSocket = null;
			try {
				clientSocket = this.serverSocket.accept();
				System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());

				new Thread(new ClientHandler(clientSocket)).start();
			} catch (IOException e) {
				if (isStopped()) {
					System.out.println("Server stopped");
					return;
				}
				throw new RuntimeException("Error on accept client connection", e);

			}
		}

		System.out.println("Server stopped");
	}

	public static void main(String[] args) {
		Server server = new Server(5555);
		new Thread(server).start();
	}

}
