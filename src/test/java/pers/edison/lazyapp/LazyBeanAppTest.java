/* (C)2020 */
package pers.edison.lazyapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.BeforeClass;
import org.junit.Test;
import pers.edison.lazyapp.exception.AppContextConfigException;
import pers.edison.lazyapp.exception.CircularDependenciesException;
import pers.edison.lazyapp.exception.FailToNewInstanceException;
import pers.edison.lazyapp.exception.RequiredBeanException;
import pers.edison.lazyapp.mock.Boy;
import pers.edison.lazyapp.mock.Laptop;
import pers.edison.lazyapp.mock.Man;
import pers.edison.lazyapp.mock.People;

public class LazyBeanAppTest {

  private static LazyBeanApp app;

  @BeforeClass
  public static void before()
      throws AppContextConfigException, ClassNotFoundException, CircularDependenciesException,
          RequiredBeanException {
    app = new LazyBeanApp("lazyAppContext.yml");
  }

  @Test
  public void testGetInstanceBySetterConfig()
      throws RequiredBeanException, FailToNewInstanceException {
    Boy boy = (Boy) app.getBean("boy", true);

    assertEquals("fucking kids", boy.getName());
    assert (boy.getAge() == 3);
    assertEquals("XXX school", boy.getSchoolName());
  }

  @Test
  public void testGetInstanceByConstructorConfig()
      throws RequiredBeanException, FailToNewInstanceException {
    Laptop laptop = (Laptop) app.getBean("laptop");

    assertEquals("intel-i7-9700k", laptop.getCpu());
    assertEquals("1TB", laptop.getHardDisk());
    assertEquals("16GB", laptop.getRam());

    People people = laptop.getBelongsTo();

    assertNotNull(people);
  }

  @Test
  public void testGetInstanceBySetterInject()
      throws RequiredBeanException, FailToNewInstanceException {
    Man man = (Man) app.getBean(People.class, Man.class, true);

    assertNotNull(man);

    man.setName("Edison");
    man.setAge(35);

    assertEquals("Edison", man.getName());
    assert (35 == man.getAge());
  }
}
