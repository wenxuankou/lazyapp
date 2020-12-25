/* (C)2020 */
package pers.edison.lazyapp.worker;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import pers.edison.lazyapp.entity.YmlContextEntity;
import pers.edison.lazyapp.exception.AppContextConfigException;

public class ConfigFileParser {

  private String[] classPathAry = System.getProperty("java.class.path").split(File.pathSeparator);

  private HashMap<String, String> configFileAndClassPathType = new HashMap<String, String>();

  public ConfigFileParser(String inputConfigUrl) {
    parseInputConfigUrl(inputConfigUrl);
  }

  public ConfigFileParser(String[] inputConfigUrls) {
    for (String url : inputConfigUrls) {
      parseInputConfigUrl(url);
    }
  }

  public YmlContextEntity parse()
      throws JsonParseException, JsonMappingException, IOException, AppContextConfigException {
    if (configFileAndClassPathType.isEmpty()) {
      throw new AppContextConfigException();
    }

    ArrayList<YmlContextEntity> contextEntityList = new ArrayList<YmlContextEntity>();

    for (Entry<String, String> entry : configFileAndClassPathType.entrySet()) {
      String filePath = entry.getKey();
      String urlType = entry.getValue();

      YmlContextEntity contextEntity = null;

      switch (urlType) {
        case "file":
          contextEntity = parseByFileSystem(filePath);
          contextEntityList.add(contextEntity);
          break;
        case "classpath":
          contextEntity = parseByClassPath(filePath);
          contextEntityList.add(contextEntity);
          break;
        case "classpath*":
          contextEntity = parseByClassPath(filePath, true);
          contextEntityList.add(contextEntity);
          break;
        default:
      }
    }

    return combineContextEntities(contextEntityList);
  }

  private YmlContextEntity combineContextEntities(ArrayList<YmlContextEntity> contextEntityList) {
    return (new YmlContextEntity(contextEntityList.toArray(new YmlContextEntity[] {})));
  }

  private YmlContextEntity parseByFileSystem(String filePath)
      throws JsonParseException, JsonMappingException, IOException {
    File config = new File(filePath);

    YmlContextEntity contextEntity = null;

    if (config.exists()) {
      ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
      mapper.findAndRegisterModules();
      contextEntity = mapper.readValue(config, YmlContextEntity.class);
    }

    return contextEntity;
  }

  private YmlContextEntity parseByJar(String classPath, String filePath)
      throws JsonParseException, JsonMappingException, IOException {

    filePath = filePath.replaceAll(Matcher.quoteReplacement(File.separator), "/");

    YmlContextEntity contextEntity = null;

    try (JarFile jarFile = new JarFile(classPath)) {
      Enumeration<JarEntry> e = jarFile.entries();

      while (e.hasMoreElements()) {
        JarEntry jarEntry = e.nextElement();

        if (filePath.equals(jarEntry.getName())) {
          ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
          mapper.findAndRegisterModules();
          contextEntity =
              mapper.readValue(jarFile.getInputStream(jarEntry), YmlContextEntity.class);
        }
      }
    }

    return contextEntity;
  }

  private YmlContextEntity parseByClassPath(String filePath)
      throws JsonParseException, JsonMappingException, IOException {
    return parseByClassPath(filePath, false);
  }

  private YmlContextEntity parseByClassPath(String filePath, boolean allClassPath)
      throws JsonParseException, JsonMappingException, IOException {
    ArrayList<YmlContextEntity> contextEntityList = new ArrayList<YmlContextEntity>();

    for (String classPath : classPathAry) {
      File f = new File(classPath);

      if (f.isDirectory()) {
        YmlContextEntity contextEntity = parseByFileSystem(classPath + File.separator + filePath);

        if (contextEntity != null) {
          contextEntityList.add(contextEntity);
        }
      }

      if (f.isFile() && classPath.endsWith(".jar")) {
        if (!allClassPath && !contextEntityList.isEmpty()) {
          break;
        }

        YmlContextEntity contextEntity = parseByJar(classPath, filePath);

        if (contextEntity != null) {
          contextEntityList.add(contextEntity);
        }
      }
    }

    return combineContextEntities(contextEntityList);
  }

  private void parseInputConfigUrl(String inputConfigUrl) {
    String[] pairPath = inputConfigUrl.split(":");
    String urlType = pairPath[0];
    String configFilePath = pairPath[pairPath.length - 1];

    if (urlType.equals(configFilePath)) {
      urlType = "";
    }

    switch (urlType) {
      case "":
        configFileAndClassPathType.put(configFilePath, "classpath");
        break;
      case "classpath":
        configFileAndClassPathType.put(configFilePath, "classpath");
        break;
      case "classpath*":
        configFileAndClassPathType.put(configFilePath, "classpath*");
        break;
      case "file":
        configFileAndClassPathType.put(configFilePath, "file");
        break;
      default:
    }
  }
}
