package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;

import models.file_model.FileModel;

public class Test {

  public static void main(String[] args) {
    // TODO Auto-generated method stub
	  Gson gson = new GsonBuilder().setPrettyPrinting().create();
    Path currentPath = Paths.get("").toAbsolutePath().resolve("src").resolve("test").resolve("test.json");
    try {
      FileModel file = gson.fromJson(new FileReader(currentPath.toString()), FileModel.class);
      JsonObject js = gson.fromJson(new FileReader(currentPath.toString()), JsonObject.class);
      JsonObject data = js.getAsJsonObject("data");
      System.out.println(js.get("messageType"));
      System.out.println(data.getAsJsonObject());
      System.out.println(js.getAsJsonObject());
    } catch (JsonSyntaxException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (JsonIOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
	catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }
}
