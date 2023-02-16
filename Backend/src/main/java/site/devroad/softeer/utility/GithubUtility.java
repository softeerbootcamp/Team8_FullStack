package site.devroad.softeer.utility;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import site.devroad.softeer.exceptions.CustomException;
import site.devroad.softeer.exceptions.ExceptionType;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.*;

@Component
public class GithubUtility {

    Logger logger = LoggerFactory.getLogger(GithubUtility.class);

    @Value("${github.apiKey}")
    private String gitApiKey;

    @Value("${github.apiKey}")
    private String token;


    class Directory{
        public List<String> files;
        public List<String> dirs;
    }

    public Map<String, String> getAllCodeFromRepo(String owner, String repo, String fileType) throws IOException {
        String path = "https://api.github.com/repos/" + owner + "/" + repo + "/contents";
        URL url = new URL(path);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", "Bearer " + token);
        con.setRequestProperty("Accept", "application/vnd.github.v3+json");

        int status = con.getResponseCode();
        if (status != 200) {
            throw new RuntimeException("Failed to get repository contents: " + status);
        }

        InputStream in = con.getInputStream();
        byte[] responseBytes = in.readAllBytes();
        String response = new String(responseBytes);

        JSONArray jsonArray = new JSONArray(response);

        Map<String, String> javaFiles = new HashMap<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = (JSONObject) jsonArray.get(i);
            String name = (String) obj.get("name");
            String type = (String) obj.get("type");
            String pathUrl = (String) obj.get("url");
            if (type.equals("dir")) {
                javaFiles.putAll(getFilesFromDirectory(pathUrl, fileType));
            } else if (name.endsWith(fileType)) {
                String downloadUrl = (String) obj.get("download_url");
                String content = getFileContentFromUrl(downloadUrl);
                javaFiles.put(getFileName(pathUrl), content);
            }
        }

        return javaFiles;
    }

    //create new Issue from originGitUrl and returns new Issue url;
    public String createIssue(String owner, String repo, String title, String content){
        logger.info("createIssue {}, {}, {}", owner+"/"+repo, title, content);

        try {
            URL url = new URL("https://api.github.com/repos/" + owner+"/"+repo + "/issues");

            logger.info(url.toString());
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("POST");
            String encodedString = "Bearer " + gitApiKey;
            logger.info("auth {}", encodedString);
            con.setRequestProperty("Authorization", encodedString);
            con.setRequestProperty("Accept", "application/vnd.github+json");
            con.setDoOutput(true);

            Map<String, String> data = new HashMap<>();
            data.put("title", title);
            data.put("body", content);
            JSONObject json = new JSONObject(data);

            OutputStream os = con.getOutputStream();
            os.write(json.toString().getBytes());
            os.flush();
            os.close();

            logger.info("Git Message successfully sended");

            int responseCode = con.getResponseCode();

            InputStream in = null;
            if (responseCode >= 200 && responseCode < 300) {
                in = con.getInputStream();
            } else {
                in = con.getErrorStream();
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            StringBuffer response = new StringBuffer();

            while ((line = reader.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            reader.close();

            logger.info("response message is {}", response.toString());
            JSONObject jsons = new JSONObject(response.toString());
            String issueURL = jsons.get("html_url").toString();

            logger.info("issue url : {}", issueURL);

            if(responseCode!=201){
                logger.warn("Error code {}", response);
                throw new CustomException(ExceptionType.GITHUB_API_ERROR_RESPONSE);
            }
            return issueURL;
        }catch (IOException e){
            e.printStackTrace();
            logger.warn("IOExcetion {}", "error occurs while getting result from toss server");
            throw new CustomException(ExceptionType.GITHUB_API_IO_ERROR);
        }
    }

    public Directory getPaths(String originRepoUrl){
        logger.info("createIssue {}", originRepoUrl);
        try {
            Directory directory = new Directory();
            directory.dirs = new ArrayList<>();
            directory.files = new ArrayList<>();

            String repoURL = originRepoUrl.substring("https://github.com/".length());
            logger.info(repoURL);

            URL url = new URL("https://api.github.com/repos/"+repoURL+"/contents/");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            String encodedString = "Bearer " + gitApiKey;
            logger.info("auth {}", encodedString);
            con.setRequestProperty("Authorization", encodedString);
            con.setRequestProperty("Accept", "application/vnd.github+json");
            con.setDoOutput(true);

            int status = con.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();

            List<String> results = new ArrayList<>();

            JSONArray obj = new JSONArray(content.toString());
            for(int i =0; i<obj.length(); i++){
                JSONObject conv = (JSONObject) obj.get(i);
                logger.info(conv.toString());
                String type = conv.get("type").toString();
                String title = conv.get("path").toString();
                if(type.equals("file"))
                    directory.files.add(title);
                else
                    directory.dirs.add(type);
                results.add(title);
            }
            return directory;
        }catch (IOException e){
            e.printStackTrace();
            logger.warn("IOExcetion {}", "error occurs while getting result from toss server");
            throw new CustomException(ExceptionType.GITHUB_API_IO_ERROR);
        }
    }

    private  String getFileName(String originalUrl) {
        originalUrl = URLDecoder.decode(originalUrl, Charset.defaultCharset());
        originalUrl = originalUrl.replace("?ref=main", "");
        return originalUrl.substring(originalUrl.indexOf("contents")+"contents".length());
    }

    private Map<String, String> getFilesFromDirectory(String urlStr, String fileType) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", "Bearer " + token);
        con.setRequestProperty("Accept", "application/vnd.github.v3+json");

        int status = con.getResponseCode();
        if (status != 200) {
            throw new RuntimeException("Failed to get directory contents: " + status);
        }

        InputStream in = con.getInputStream();
        byte[] responseBytes = in.readAllBytes();
        String response = new String(responseBytes);

        JSONArray jsonArray = new JSONArray(response.toString());
        Map<String, String> javaFiles = new HashMap<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = (JSONObject) jsonArray.get(i);
            String name = (String) obj.get("name");
            String type = (String) obj.get("type");
            String pathUrl = (String) obj.get("url");
            if (type.equals("dir")) {
                javaFiles.putAll(getFilesFromDirectory(pathUrl, fileType));
            } else if (name.endsWith(fileType)) {
                String downloadUrl = (String) obj.get("download_url");
                String content = getFileContentFromUrl(downloadUrl);
                logger.info("download_url : {}", downloadUrl);
                javaFiles.put(getFileName(pathUrl), content);
            }
        }

        return javaFiles;
    }

    private String getFileContentFromUrl(String downloadUrl) throws IOException {
        logger.info("get file content from {",downloadUrl);

        URL url = new URL(downloadUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", "Bearer " + token);
        con.setRequestProperty("Accept", "application/vnd.github.v3.raw");

        int status = con.getResponseCode();
        if (status != 200) {
            throw new RuntimeException("Failed to get file content: " + status);
        }

        InputStream in = con.getInputStream();
        byte[] responseBytes = in.readAllBytes();
        String content = new String(responseBytes);

        // Decode Base64-encoded files
        if (content.contains("base64")) {
            content = content.split(",")[1];
            content = new String(Base64.getDecoder().decode(content));
        }

        return content;
    }



}