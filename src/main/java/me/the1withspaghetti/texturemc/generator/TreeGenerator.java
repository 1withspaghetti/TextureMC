package me.the1withspaghetti.texturemc.generator;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class TreeGenerator {
	
public static Gson gson = new Gson();
	
	public static final String VERSION = "1.21.3";
	public static final List<String> ignoreFolders = Arrays.asList("font","presets","background");
	
	static final String folderLoc = "C:\\Users\\tyler\\Downloads\\Minecraft Assets\\texture_folder_only\\"+VERSION+"\\";
	static final String imgOutput = "C:\\Users\\tyler\\OneDrive\\Documents\\Websites\\standalone\\TextureMC 2\\assets\\"+VERSION+"\\";
	static final String treeOutput1 = "C:\\Users\\tyler\\OneDrive\\Documents\\Websites\\standalone\\TextureMC 2\\assets\\"+VERSION+".json";
	static final String treeOutput2 = "C:\\Users\\tyler\\OneDrive\\Documents\\Websites\\standalone\\TextureMC 2\\src\\main\\resources\\assets\\"+VERSION+".json";
	
	public static void main(String[] args) throws IOException {
		
		JsonObject json = copyFolder("");
		new File(treeOutput1).createNewFile();
		FileWriter fw = new FileWriter(new File(treeOutput1));
		fw.append(json.toString());
		fw.close();
		
		new File(treeOutput2).createNewFile();
		FileWriter fw2 = new FileWriter(new File(treeOutput2));
		fw2.append(json.toString());
		fw2.close();
		
		System.out.println("----- Finished -----");
	}
	
	public static JsonObject copyFolder(String loc) {
		JsonObject folder = new JsonObject();
		JsonArray files = new JsonArray();
		for (File file : new File(folderLoc + loc).listFiles()) {
			if (file.isDirectory()) {
				if (!ignoreFolders.contains(file.getName()))
				folder.add(file.getName(), copyFolder(loc+file.getName()+"\\"));
				System.out.println("Finished "+loc+file.getName());
			} else if (file.getName().endsWith(".png")) {
				files.add(file.getName().replace(".png", ""));
				try {
					JsonObject json = new JsonObject();
					BufferedImage img = ImageIO.read(file);
					json.addProperty("img", encodeImage(img));
					
					File imgFile = new File(imgOutput+loc+file.getName());
					File dataFile = new File(imgOutput+loc+file.getName().replace(".png", ".json"));
					
					File metaFile = new File(folderLoc+loc+file.getName()+".mcmeta");
					if (metaFile.exists()) {
						JsonObject meta = gson.fromJson(new FileReader(metaFile), JsonObject.class);
						json.add("meta", meta);
					}
					if (json.has("meta") && json.get("meta").getAsJsonObject().has("animation")) {
						BufferedImage render = new BufferedImage(img.getWidth(), img.getWidth(), BufferedImage.TRANSLUCENT);
						render.getGraphics().drawImage(img, 0, 0, null);
						ImageIO.write(render, "PNG", imgFile);
					} else {
						FileUtils.copyFile(file, imgFile);
					}
					
					dataFile.createNewFile();
					FileWriter fw = new FileWriter(dataFile);
					fw.append(json.toString());
					fw.close();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		folder.add("files", files);
		return folder;
	}
	
	private static String encodeImage(BufferedImage img) throws IOException {
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		final OutputStream b64os = Base64.getEncoder().wrap(os);
		ImageIO.write(img, "png", b64os);
		//return "data:image/png;base64,"+os.toString("UTF-8");
		return os.toString();
	}
}
