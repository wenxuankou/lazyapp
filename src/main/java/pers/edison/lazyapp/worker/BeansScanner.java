/* (C)2020 */
package pers.edison.lazyapp.worker;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import pers.edison.lazyapp.annotation.LazyBean;
import pers.edison.lazyapp.entity.YmlBeanEntity;

public class BeansScanner {

  private BeansFactory beansFactory;

  private String[] classPathAry;

  public BeansScanner(BeansFactory beansFactory) {
    this.beansFactory = beansFactory;
    this.classPathAry = System.getProperty("java.class.path").split(File.pathSeparator);
  }

  public void scanBeans(String[] pkgs, YmlBeanEntity[] beanEntities)
      throws ClassNotFoundException, IOException {
    scanPackages(pkgs);

    if (Objects.nonNull(beanEntities)) {
      for (YmlBeanEntity entity : beanEntities) {
        entity.setKlass(Class.forName(entity.getClassName()));
        entity.setManually(true);

        beansFactory.getBeans().put(entity.getId(), entity);
      }
    }
  }

  public void scanPackages(String[] pkgs) throws ClassNotFoundException, IOException {
    for (String pkg : pkgs) {
      boolean foundFromDir = false;

      for (String classPath : classPathAry) {
        File f = new File(classPath);

        if (f.isDirectory()) {
          boolean found = scanByFileSystem(classPath, pkg);
          if (found) {
            foundFromDir = true;
          }
        }

        if (f.isFile() && classPath.endsWith(".jar")) {
          if (foundFromDir || scanByJar(classPath, pkg)) break;
        }
      }
    }
  }

  private boolean scanByFileSystem(String classPath, String pkg) throws ClassNotFoundException {
    File dir =
        new File(
            classPath
                + File.separator
                + pkg.replaceAll("\\.", Matcher.quoteReplacement(File.separator)));

    boolean flag = false;

    if (dir.exists() && dir.isDirectory()) {
      flag = true;

      File[] files = dir.listFiles();

      for (File file : files) {
        if (file.isHidden()) {
          continue;
        }

        if (file.isFile() && file.getName().endsWith(".class")) {
          loadClass(file.getName(), pkg);
        }

        if (file.isDirectory()) {
          scanByFileSystem(classPath, pkg + "." + file.getName());
        }
      }
    }

    return flag;
  }

  private boolean scanByJar(String classPath, String pkg)
      throws IOException, ClassNotFoundException {
    String pkgPath = pkg.replaceAll("\\.", "/");

    boolean flag = false;

    try (JarFile jarFile = new JarFile(classPath)) {
      Enumeration<JarEntry> e = jarFile.entries();

      while (e.hasMoreElements()) {
        String filePath = e.nextElement().getName();
        String[] filePathSlices = filePath.split("/");
        String fileName = filePathSlices[filePathSlices.length - 1];

        if (filePath.contains(pkgPath) && filePath.endsWith(".class")) {
          loadClass(fileName, pkg);
        }
      }
    }

    return flag;
  }

  private void loadClass(String fileName, String pkg) throws ClassNotFoundException {
    String klassName = pkg + "." + fileName.replace(".class", "");
    Class<?> klass = Class.forName(klassName);

    if (isValidClass(klass)) {
      YmlBeanEntity entity = new YmlBeanEntity();

      entity.setId(klassName);
      entity.setClassName(klassName);
      entity.setKlass(klass);

      beansFactory.getBeans().put(entity.getId(), entity);
    }
  }

  private boolean isValidClass(Class<?> klass) throws ClassNotFoundException {
    if (klass.isAnnotation()) {
      return false;
    }

    if (klass.isInterface()) {
      return false;
    }

    if (klass.isEnum()) {
      return false;
    }

    if (Objects.isNull(klass.getAnnotation(LazyBean.class))) {
      return false;
    }

    return true;
  }
}
