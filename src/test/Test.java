package test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import helper.request.FactoryRequest;
import helper.request._request.Request;



public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Request rq = FactoryRequest.intialRequest("LOGIN");
		rq.payload.setUserName("long");
		rq.payload.setPassword("123456");
		rq.payload.setFileSize(128);
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String request = gson.toJson(rq);
		System.out.println(request);
	}

}
