package gui.threads;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import logging.Log;

import com.google.gson.Gson;

import gui.exceptions.ImgurDeniedException;


public class AlbumRunnable implements Runnable {
	public static final String IMGUR_ALBUM_URI = "https://api.imgur.com/3/album/";
	public static final String IMGUR_API_CLIENT = "47b683c5a64388a";
	
	private AlbumCode albumCode;
	
	// TODO: Passing a ImgCode object and add layer height to that to track which image belongs to what place
	public AlbumRunnable(AlbumCode albumCode) {
		this.albumCode = albumCode;
	}

	@Override
	public void run() {
		URL url;
		try {
			url = new URL(IMGUR_ALBUM_URI);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
	
			con.setRequestMethod("POST");
			con.setRequestProperty("Authorization", "Client-ID " + IMGUR_API_CLIENT);
			con.setRequestProperty("title", albumCode.getTitle());
			
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(baos);
			for (String s : albumCode.getIds()) {
			    out.writeUTF(s);
			}
			byte[] ids = baos.toByteArray();
			
			wr.write(ids);
			wr.flush();
			wr.close();
			
			//con.getResponseCode();
	
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
	
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
	
			ImageUploadResponse responseObject = new Gson().fromJson(response.toString(), ImageUploadResponse.class);
			
			if (!responseObject.getSuccess())
				throw new ImgurDeniedException(responseObject.getStatus());
			
			System.out.println(responseObject.toString());
			String s = responseObject.getData().getId(); 
			albumCode.setUrl(s);
			
		} catch (IOException | ImgurDeniedException e) {
			
			Log.e(e.getMessage());
		}
	}

	class ImageUploadResponse {
		private ImageUploadResponseData data;
		private int status;
		private boolean success;

		public ImageUploadResponseData getData() {
			return data;
		}

		public int getStatus() {
			return status;
		}

		public boolean getSuccess() {
			return success;
		}

		public void setData(ImageUploadResponseData data) {
			this.data = data;
		}

		public void setStatus(int status) {
			this.status = status;
		}

		public void setSuccess(boolean success) {
			this.success = success;
		}

		@Override
		public String toString() {
			return "succes=" + success + " status=" + status;
		}
	}

	class ImageUploadResponseData {
		private String id;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		@Override
		public String toString() {
			return id;
		}
	}
}
