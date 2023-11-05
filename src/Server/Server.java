package Server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.google.gson.*;

import Model.Song;

public class Server {
	private ServerSocket server;
	private Socket connection;
	private ObjectOutputStream output;
	private String clientCreds;
	private String authToken;
	private String basicAuth;
	private String aToken;

	/**
	 * Default constructor
	 * Starts server and gets creds	
	 */
	public Server() {
		clientCreds = "e12494e8da0143e0ae4023618e136a49:7de448d349a7466190ad42f5b9734a11";
		basicAuth = new String(Base64.getEncoder().encode(clientCreds.getBytes()));
		getCreds();
//		makeRequest();
//		getSongByID("4LRPiXqCikLlN15c3yImP7");
		startServer();
//		getGenre("Pop");
//		serverTest();
//		getRandom();
//		makeRequest();
//		testSearch("a");
	}

	/**
	 * Initial function that runs on server start Used to get spotify auth tokens
	 * and save them
	 */
	public void getCreds() {
		try {
			String str = "{\"grant_type\":\"client_credentials\"}";
			URL url = new URL("https://accounts.spotify.com/api/token");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);

			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			con.setRequestProperty("Authorization", "Basic " + basicAuth);
			con.setRequestProperty("Content-Length", "0");
			con.setRequestProperty("Accept", "application/json");

//			System.out.println(con.getRequestProperties());

//			byte[] outputInBytes = str.getBytes("UTF-8");

			OutputStream os = con.getOutputStream();
//    attempt 1       
//			os.write(str.getBytes("UTF-8"));
// attempt 2  https://guruparang.blogspot.com/2016/01/example-on-working-with-json-and.html
//			os.write(outputInBytes);

			// working attempt
			String grant = "&" + URLEncoder.encode("grant_type", "UTF-8") + "="
					+ URLEncoder.encode("client_credentials", "UTF-8");
			byte[] postDataBytes = grant.toString().getBytes("UTF-8");

			os.write(postDataBytes);

			os.close();
			con.connect();
			int status = con.getResponseCode();
//			System.out.println(status);
//			
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			StringBuilder stringBuilder = new StringBuilder();

			String line;
			while ((line = bufferedReader.readLine()) != null) {
				stringBuilder.append(line + '\n');
			}

//			System.out.println(stringBuilder.toString());
			aToken = stringBuilder.toString().substring(stringBuilder.indexOf(":") + 2, stringBuilder.indexOf(",") - 1);

			switch (status) {
			case 200:
			case 201:
				BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
				StringBuilder sb = new StringBuilder();
//				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line + "\n");
				}
				br.close();
				System.out.println("-" + sb.toString());
			}

			Map<String, String> parameters = new HashMap<>();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Starts server and begins waiting for requests
	 */
	public void startServer() {
		try {
			server = new ServerSocket(4000);
			System.out.println("ServerSocket awaiting connections...");
			connection = server.accept();
			output = new ObjectOutputStream(connection.getOutputStream());
			Thread taskThread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						System.out.println("ran");
						ObjectInputStream input;
						String message = "";
						input = new ObjectInputStream(connection.getInputStream());
//						message = (String) input.readObject();

						while (true) {
							message = (String) input.readObject();
							message = message.trim();
							System.out.println("recieved");

							if (message.indexOf("getSong") != -1) {
								String[] temp = message.split("\\?=", -1);
								System.out.println(temp[1]);
								JsonObject x;

								if (temp[1].equals("")) {
									x = getRandom();
								} else {
									x = getSearch(temp[1]);
								}
								JsonArray y = x.get("tracks").getAsJsonObject().get("items").getAsJsonArray();
								Song[] created = new Song[y.size()];
								int index = 0;
								for (JsonElement t : y) {
									JsonObject obj = t.getAsJsonObject();
									Song nSong = new Song();
									System.out.println(obj.get("artists"));
									nSong.artist = obj.get("artists").getAsJsonArray().get(0).getAsJsonObject()
											.get("name").getAsString();
									nSong.id = obj.get("id").getAsString();
									nSong.name = obj.get("name").getAsString();
									JsonElement preview = obj.get("preview_url");
									System.out.println(preview);
									if (preview.isJsonNull())
										continue;
									nSong.preview_url = obj.get("preview_url").getAsString();
									nSong.duration = obj.get("duration_ms").getAsInt();
									JsonArray imgArr = obj.get("album").getAsJsonObject().get("images")
											.getAsJsonArray();
									nSong.image = imgArr.get(0).getAsJsonObject().get("url").getAsString();
									created[index] = nSong;
									index++;
								}
								output.writeObject(created);
								continue;
							} else if (message.indexOf("getMP3") != -1) {
								String[] temp = message.split("\\?=", -1);
								String x = getSongByID(temp[1]);
								output.writeObject(x);
							} else if (message.indexOf("getGenre") != -1) {
								System.out.print("__" + message);
								String[] temp = message.split("\\?=", -1);
								Song[] x = getGenre(temp[1]);
								output.writeObject(x);
							}

						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			taskThread.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @return JsonObject with a list of random songs from the spotify api
	 * 
	 */
	public JsonObject getRandom() {
		JsonObject obj = new JsonObject();
		try {
			URL url = new URL("https://api.spotify.com/v1/search?q=a&type=track");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);

			con.setRequestMethod("GET");
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			con.setRequestProperty("Authorization", "Bearer " + aToken);
			con.setRequestProperty("Content-Length", "0");
			con.setRequestProperty("Accept", "application/json");

			con.connect();
			int status = con.getResponseCode();
			System.out.println("ran");
			System.out.println(status);
			System.out.println(con.getResponseMessage());
			switch (status) {
			case 200:
			case 201:
				BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				br.close();
				System.out.println(sb.toString());
				JsonParser parser = new JsonParser();
				obj = parser.parse(sb.toString()).getAsJsonObject();
			}
			return obj;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj;

	}

	/**
	 * @param x text to be searched into the spotify API
	 * @return JSON containing elements from spotify's response
	 */
	public JsonObject getSearch(String x) {
		JsonObject obj = new JsonObject();
		try {
			URL url = new URL("https://api.spotify.com/v1/search?q=" + x + "&type=track");
			System.out.println(url.toString());
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);

			con.setRequestMethod("GET");
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			con.setRequestProperty("Authorization", "Bearer " + aToken);
			con.setRequestProperty("Content-Length", "0");
			con.setRequestProperty("Accept", "application/json");

			con.connect();
			int status = con.getResponseCode();
			System.out.println("ran");
			System.out.println(status);
			System.out.println(con.getResponseMessage());
			switch (status) {
			case 200:
			case 201:
				BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				br.close();
				JsonParser parser = new JsonParser();
				obj = parser.parse(sb.toString()).getAsJsonObject();

			}
			return obj;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj;

	}

	/**
	 * @purpose: to get a list of songs by genre
	 * @param genre: string containing the genre of music you'd like returned
	 * @return a JsonObject as an array of songs
	 */
	public Song[] getGenre(String genre) {
		JsonObject obj = new JsonObject();
		Song[] temp = new Song[1];
		try {
			URL url;
			if (genre.equals("Rap")) {
				url = new URL("https://api.spotify.com/v1/playlists/37i9dQZF1DX0XUsuxWHRQd/tracks?offset=0&limit=100");
			} else if (genre.equals("Pop")) {
				url = new URL("https://api.spotify.com/v1/playlists/3ZgmfR6lsnCwdffZUan8EA/tracks?offset=0&limit=100");
			} else {
				url = new URL("https://api.spotify.com/v1/playlists/37i9dQZF1DWXRqgorJj26U/tracks?offset=0&limit=100");
			}
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);

			con.setRequestMethod("GET");
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			con.setRequestProperty("Authorization", "Bearer " + aToken);
			con.setRequestProperty("Content-Length", "0");
			con.setRequestProperty("Accept", "application/json");

			con.connect();
			int status = con.getResponseCode();
			System.out.println(status);
			System.out.println(con.getResponseMessage());
			switch (status) {
			case 200:
			case 201:
				BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				br.close();
				JsonParser parser = new JsonParser();
				obj = parser.parse(sb.toString()).getAsJsonObject();
				JsonArray arr = obj.get("items").getAsJsonArray();
			}
			JsonArray arr = obj.get("items").getAsJsonArray();
			Song[] x = toSongArray(arr);
			System.out.println(x[0].id);
			return x;
			

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return temp;

	}

	/**
	 * @param id the id of the song that should be downloaded
	 * @return a string containing the path to the downloaded mp34
	 */
	public String getSongByID(String id) {
		JsonObject obj = new JsonObject();
		String x = "";
		try {
			URL url = new URL("https://api.spotify.com/v1/tracks/" + id);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);

			con.setRequestMethod("GET");
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			con.setRequestProperty("Authorization", "Bearer " + aToken);
			con.setRequestProperty("Content-Length", "0");
			con.setRequestProperty("Accept", "application/json");

			con.connect();
			int status = con.getResponseCode();
			System.out.println("ran");
			System.out.println(status);
			System.out.println(con.getResponseMessage());
			switch (status) {
			case 200:
			case 201:
				BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				br.close();
				JsonParser parser = new JsonParser();
				obj = parser.parse(sb.toString()).getAsJsonObject();
				System.out.println(obj);
				x = obj.get("preview_url").getAsString();
				URLConnection conn = new URL(x).openConnection();
				InputStream is = conn.getInputStream();
				System.out.println("");
				String filePath = "./src/Media/" + obj.get("id").getAsString() + ".mp3";
				OutputStream outstream = new FileOutputStream(new File(filePath));
				byte[] buffer = new byte[4096];
				int len;
				while ((len = is.read(buffer)) > 0) {
					outstream.write(buffer, 0, len);
				}
				outstream.close();
				x = filePath;
				return filePath;

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return x;

	}

	/**
	 * Testing function for spotify API
	 */
//	public void makeRequest() {
//		try {
//			URL url = new URL("https://api.spotify.com/v1/search?q=test&type=track");
//			HttpURLConnection con = (HttpURLConnection) url.openConnection();
//			con.setDoOutput(true);
//			con.setDoInput(true);
//
//			con.setRequestMethod("GET");
//			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//			con.setRequestProperty("Authorization", "Bearer " + aToken);
//			con.setRequestProperty("Content-Length", "0");
//			con.setRequestProperty("Accept", "application/json");
//
//			con.connect();
//			int status = con.getResponseCode();
//			System.out.println("ran");
//			System.out.println(status);
//			System.out.println(con.getResponseMessage());
//			switch (status) {
//			case 200:
//			case 201:
//				BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
//				StringBuilder sb = new StringBuilder();
//				String line;
//				while ((line = br.readLine()) != null) {
//					sb.append(line + "\n");
//				}
//				br.close();
//				System.out.println("-" + sb.toString());
//			}
//
//			Map<String, String> parameters = new HashMap<>();
//
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}

	/**
	 * Function to test server's functionality without client side
	 */
//	public void serverTest() {
//		try {
//			server = new ServerSocket(4000);
//			System.out.println("ServerSocket awaiting connections...");
//			connection = server.accept();
//			output = new ObjectOutputStream(connection.getOutputStream());
//			Thread taskThread = new Thread(new Runnable() {
//				@Override
//				public void run() {
//					try {
//						ObjectInputStream input;
//						String message = "";
//						input = new ObjectInputStream(connection.getInputStream());
//						message = (String) input.readObject();
//						message = "getSong?=x";
//						String[] temp = message.split("?=");
//						System.out.println(temp[1]);
//						while (true) {
//							message = (String) input.readObject();
//							break;
////							if (message.indexOf("getSong") != -1) {
//////								String[] temp = message.split("?=");
////								
////								break;
////							}
//
//						}
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			});
//			taskThread.start();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

	/**
	 * @param w String containing the term to be searched in spotify Testing
	 *          function used to test without client
	 */
//	public void testSearch(String w) {
//		JsonObject x = getSearch(w);
//		JsonArray y = x.get("tracks").getAsJsonObject().get("items").getAsJsonArray();
//		Song[] created = new Song[y.size()];
//		int index = 0;
//		for (JsonElement t : y) {
//			JsonObject obj = t.getAsJsonObject();
//			Song nSong = new Song();
//			System.out.println(obj.get("artists"));
//			nSong.artist = obj.get("artists").getAsJsonArray().get(0).getAsJsonObject().get("name").getAsString();
//			nSong.id = obj.get("id").getAsString();
//			nSong.name = obj.get("name").getAsString();
//			JsonElement preview = obj.get("preview_url");
//			System.out.println(preview);
//			if (preview.isJsonNull())
//				continue;
//			nSong.preview_url = obj.get("preview_url").getAsString();
//			nSong.duration = obj.get("duration_ms").getAsInt();
//			JsonArray imgArr = obj.get("album").getAsJsonObject().get("images").getAsJsonArray();
//			nSong.image = imgArr.get(0).getAsJsonObject().get("url").getAsString();
//			created[index] = nSong;
//			index++;
//		}
//	}

	public Song[] toSongArray(JsonArray y) {
		Song[] created = new Song[y.size()];
		int index = 0;
		for (JsonElement t : y) {
			JsonObject obj = t.getAsJsonObject().get("track").getAsJsonObject();
			Song nSong = new Song();
			nSong.artist = obj.get("album").getAsJsonObject().get("artists").getAsJsonArray().get(0).getAsJsonObject().get("name").getAsString();
			nSong.id = obj.get("id").getAsString();
			nSong.name = obj.get("name").getAsString();
			JsonElement preview = obj.get("preview_url");
			if (preview.isJsonNull())
				continue;
			nSong.preview_url = obj.get("preview_url").getAsString();
			nSong.duration = obj.get("duration_ms").getAsInt();
			JsonArray imgArr = obj.get("album").getAsJsonObject().get("images").getAsJsonArray();
			nSong.image = imgArr.get(0).getAsJsonObject().get("url").getAsString();
			created[index] = nSong;
			index++;
		}
		return created;
	}
}
