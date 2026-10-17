package com.api.gateway.igint.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class InstagramUrlExtractor {

    public static String getRawMp4Url(String reelUrl) {
        List<String> command = new ArrayList<>();
        command.add("C:\\Users\\daniel\\AppData\\Local\\Microsoft\\WinGet\\Packages\\yt-dlp.yt-dlp_Microsoft.Winget.Source_8wekyb3d8bbwe\\yt-dlp");
        //command.add("--get-url");                       // Instructs yt-dlp to print only the CDN file URL
        command.add("-f"); command.add("b");            // Extracts best pre-merged format (video+audio)
        command.add("--extractor-args"); 
        command.add("youtube:player_client=default");   // Throttling protection bypass
        // 3. Precise Chrome User-Agent signature to mirror browser extraction handshakes
       // command.add("--user-agent");
        //command.add("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36");
        
        command.add("--merge-output-format");
        command.add("mp4");
        command.add(reelUrl);

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        StringBuilder output = new StringBuilder();

        try {
            Process process = processBuilder.start();
            
            // Read output stream from yt-dlp execution
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line);
                }
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                return output.toString().trim();
            } else {
                System.err.println("yt-dlp process failed with exit code: " + exitCode);
            }
        } catch (Exception e) {
            System.err.println("Error executing yt-dlp payload: " + e.getMessage());
        }
        return null;
    }

    public static void main(String[] args) {
        String testReel = "https://www.instagram.com/p/DeDbh2nODYq/";
        // String testReel = "https://www.youtube.com/watch?v=dQw4w9WgXcQ";
        String directMp4Url = getRawMp4Url(testReel);
        
        if (directMp4Url != null) {
            System.out.println("Direct Playable MP4 URL:\n" + directMp4Url);
        } else {
            System.out.println("Failed to extract raw video URL.");
        }
    }
}