package me.the1withspaghetti.texturemc.backend.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.RandomStringUtils;

public class ConsoleCommands {
	private static ExecutorService executor;
	private static BufferedReader stream;
	
	public static void init(InputStream in) {
		executor = Executors.newSingleThreadExecutor();
		stream = new BufferedReader(new InputStreamReader(in));
		executor.execute(start);
	}
	
	static Runnable start = new Runnable() {
		@Override
		public void run() {
			while (true) {
				try {
					String cmd = stream.readLine();
					List<String> args = parseString(cmd);
					switch (args.get(0)) {
						case "help":
							System.out.println("----- All commands: -----");
							System.out.println("addkey \"key\"  -  Adds a beta-tester key");
							System.out.println("randomkey  -  Adds a random beta-tester key");
							System.out.println("listkeys  -  Lists all beta-tester keys");
							System.out.println("removekey \"key\"  -  Removes a beta-tester key");
							System.out.println("--------------------------");
							break;
						case "addkey":
							if (args.size() > 3 || args.size() < 2) {
								System.out.println("Usage: addkey \"key\" [number]");
								break;
							}
							if (args.size() == 2) {
								BetaTesters.addKey(args.get(1));
								System.out.println("Added key: "+args.get(1));
							} else {
								for (int i = 0; i < Integer.parseInt(args.get(2)); i++)
									BetaTesters.addKey(args.get(1));
								System.out.println("Added key: "+args.get(1)+" "+args.get(2)+" times");
							}
							break;
						case "randomkey":
							String key = RandomStringUtils.randomAlphanumeric(20);
							BetaTesters.addKey(key);
							System.out.println("Added key: "+key);
							break;
						case "listkeys":
							BetaTesters.listKeys();
							break;
						case "removekey":
							if (args.size() == 2) {
								BetaTesters.removeKey(args.get(1));
								System.out.println("Removed key: "+args.get(1));
							} else {
								for (int i = 0; i < Integer.parseInt(args.get(2)); i++)
									BetaTesters.removeKey(args.get(1));
								System.out.println("Removed key: "+args.get(1)+" "+args.get(2)+" times");
							}
							break;
						default:
							System.out.println("Unknown command! Use \"help\" to see the list of commands");
							break;
						
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	};
	
	
	public static List<String> parseString(String s)
            throws IOException {
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
 
        boolean inEscape = false;
        boolean inQuotes = false;
        for (char c : s.toCharArray()) {
            if (inEscape) {
                inEscape = false;
            } else if (c == '\\') {
                inEscape = true;
                continue;
            } else if (c == '"' && !inEscape) {
            	inQuotes = !inQuotes;
            	continue;
            } else if (c == ' ' && !inQuotes) {
                tokens.add(sb.toString());
                sb.setLength(0);
                continue;
            }
            sb.append(c);
        }
        if (inEscape || inQuotes)
            throw new IOException("Invalid terminal escape");
 
        tokens.add(sb.toString());
 
        return tokens;
    } 
	
}

