package org.lexize.rpautoupdater;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;

import net.minecraft.client.network.ClientCommandSource;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ModMainClass implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LogManager.getLogger();
	public static HashMap<String, String> urlQueue = new HashMap<String, String>();
	public static File respackDir;
	@Override
	public void onInitialize() {
		Command<FabricClientCommandSource> command = context -> {
			update();
			return 0;
		};
		LiteralArgumentBuilder<FabricClientCommandSource> lit = ClientCommandManager.literal("aureload").requires(source -> true).executes(command);
		ClientCommandManager.DISPATCHER.register(lit);
		update();
	}

	public static void update() {
		//Get client instance
		MinecraftClient mc = MinecraftClient.getInstance();

		//Get respacks folder
		respackDir = mc.getResourcePackDir();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		//Get all files in respack dir
		for (File pack: respackDir.listFiles()) {
			//Check, if that is ZIP
			String packName = pack.getName();
			if (packName.matches("(?:.+).zip")) {
				// Starting analyzing pack
				//Try to open ZIP
				try {
					ZipFile zipFile = new ZipFile(pack);
					log(Level.INFO, "Opened \"%s\". Starting analyzing".formatted(packName));
					//Get MCMETA file of pack
					ZipEntry mcmeta = zipFile.getEntry("update_data.json");
					//If not found
					if (mcmeta == null) {
						log(Level.ERROR, "\"update_data.json\" doen't found. Closing %s".formatted(packName));
						zipFile.close();
					}
					//If found
					else {
						InputStream mcmetaStream = zipFile.getInputStream(mcmeta);
						String content = new String(mcmetaStream.readAllBytes(), StandardCharsets.UTF_8);
						mcmetaStream.close();
						mcmeta.clone();
						McMetaClass mcMetaClass = gson.fromJson(content, McMetaClass.class);
						//Checking, is pack updatable
						if (mcMetaClass.checkUrl != null) {
							log(Level.INFO, "%s is updatable. Checking version.".formatted(packName));
							McMetaClass checkMetaClass = gson.fromJson(getFromURL(mcMetaClass.checkUrl), McMetaClass.class);
							//Check is web MCMeta version is bigger then our
							if (mcMetaClass.version < checkMetaClass.version) {
								zipFile.close();
								download(pack, checkMetaClass.downloadUrl);
								log(Level.INFO, "Successfully updated \"%s.".formatted(packName));
							}
							else {
								log(Level.INFO, "You have latest version of \"%s\". Skipping pack.".formatted(packName));
								zipFile.close();
							}
						}
						else {
							log(Level.ERROR, "\"%s\" isn't updatable. Skipping pack.".formatted(packName));
							zipFile.close();
						}
					}
				} catch (IOException e) {
					log(Level.ERROR, "Failed to update %s".formatted(packName));
					log(Level.TRACE, e.getMessage());
				}
			}
		}
	}

	public static void log(Level level, String message){
		LOGGER.log(level, "[RPAU] " + message);
	}
	public static String getFromURL(String urlPath) throws IOException {
		URL url = new URL(urlPath);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		var is = con.getInputStream();
		String outStr = new String(is.readAllBytes(), StandardCharsets.UTF_8);
		is.close();
		con.disconnect();
		return outStr;
	}
	public static void download(File file, String url) throws IOException {
		InputStream inputStream = new URL(url).openStream();
		Files.copy(inputStream, Paths.get(file.getAbsolutePath()), StandardCopyOption.REPLACE_EXISTING);
	}

}
