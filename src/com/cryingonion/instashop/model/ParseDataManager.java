package com.cryingonion.instashop.model;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class ParseDataManager {

	// Parse.com
	public String PARSE_APPLICATION_ID = "y6RzPzQ7v2Pz9HimuPoZA3jNyc3N4zQHapASaIf7";
	public String PARSE_CLIENT_KEY = "bTpn9m8Ggu7z21dHaU6CRbmhF3oLxB0y0OvcmEIS";
	
	
	public ParseDataManager(){}
	
	public void signUpToParse (String accessToken, String id, String username, String name, String userImage, String bio)
	{
		ParseObject user = new ParseObject("User");
		user.put("username",username);
		user.put("userId",id);
		user.put("name",name);
		user.put("instagramToken",accessToken);
		user.put("userImage",userImage);
		user.put("userBio",bio);
		  
		user.saveInBackground();
	}
	
	public void addWishlist (String accessToken, String id, String username, String name, String userImage)
	{
		ParseObject user = new ParseObject("User");
		user.put("username",username);
		user.put("userId",id);
		user.put("name",name);
		user.put("instagramToken",accessToken);
		user.put("userImage",userImage);
		  
		user.saveInBackground();
	}
	
	
}
