/* (C)2020 */
package pers.edison.lazyapp.setting;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;
import pers.edison.lazyapp.entity.YmlContextEntity;
import pers.edison.lazyapp.entity.YmlSettingEntity;

public class SettingsCenter {

  private YmlContextEntity contextEntity;

  private HashMap<String, String> settings = new HashMap<String, String>();

  public SettingsCenter(YmlContextEntity contextEntity) {
    this.contextEntity = contextEntity;
  }

  public SettingsCenter scanSettings() throws FileNotFoundException, IOException {
    YmlSettingEntity[] settingEntities = contextEntity.getSettings();

    if (Objects.nonNull(settingEntities)) {
      for (YmlSettingEntity entity : settingEntities) {
        if (entity.getValue() != null) {
          settings.put(entity.getName(), entity.getValue());
        } else if (entity.getFile() != null) {
          scanFile(entity);
        }
      }
    }

    return this;
  }

  public String getSetting(String key) {
    return this.settings.get(key);
  }

  public HashMap<String, String> getSettings() {
    return settings;
  }

  public void setSettings(HashMap<String, String> settings) {
    this.settings = settings;
  }

  private void scanFile(YmlSettingEntity fileEntity) throws FileNotFoundException, IOException {
    if (!fileEntity.getFile().endsWith(".properties")) {
      return;
    }

    settings.put(fileEntity.getName(), fileEntity.getFile());

    InputStream in = this.getClass().getResourceAsStream("/" + fileEntity.getFile());

    if (in != null) {
      scanPropsFile(fileEntity.getName(), in);
    }
  }

  private void scanPropsFile(String key, InputStream in) throws FileNotFoundException, IOException {
    Properties pps = new Properties();

    pps.load(in);

    for (Entry<Object, Object> entry : pps.entrySet()) {
      settings.put(key + "." + entry.getKey().toString(), entry.getValue().toString());
    }
  }
}
