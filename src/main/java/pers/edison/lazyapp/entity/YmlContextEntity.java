/* (C)2020 */
package pers.edison.lazyapp.entity;

import java.util.ArrayList;
import java.util.Arrays;

public class YmlContextEntity {

  private String[] scanners;
  private YmlSettingEntity[] settings;
  private YmlBeanEntity[] beans;

  public YmlContextEntity() {}

  public YmlContextEntity(YmlContextEntity[] entities) {
    ArrayList<String> scannersList = new ArrayList<String>();
    ArrayList<YmlSettingEntity> settingsList = new ArrayList<YmlSettingEntity>();
    ArrayList<YmlBeanEntity> beansList = new ArrayList<YmlBeanEntity>();

    for (YmlContextEntity entity : entities) {
      if (entity.getScanners() != null) {
        scannersList.addAll(Arrays.asList(entity.getScanners()));
      }

      if (entity.getSettings() != null) {
        settingsList.addAll(Arrays.asList(entity.getSettings()));
      }

      if (entity.getBeans() != null) {
        beansList.addAll(Arrays.asList(entity.getBeans()));
      }
    }

    this.setScanners(scannersList.toArray(new String[] {}));
    this.setSettings(settingsList.toArray(new YmlSettingEntity[] {}));
    this.setBeans(beansList.toArray(new YmlBeanEntity[] {}));
  }

  public YmlBeanEntity[] getBeans() {
    return beans;
  }

  public void setBeans(YmlBeanEntity[] beans) {
    this.beans = beans;
  }

  public String[] getScanners() {
    return scanners;
  }

  public void setScanners(String[] scanners) {
    this.scanners = scanners;
  }

  public YmlSettingEntity[] getSettings() {
    return settings;
  }

  public void setSettings(YmlSettingEntity[] settings) {
    this.settings = settings;
  }
}
