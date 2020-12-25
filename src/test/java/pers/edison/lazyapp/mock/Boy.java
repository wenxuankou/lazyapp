/* (C)2020 */
package pers.edison.lazyapp.mock;

import pers.edison.lazyapp.annotation.LazyBean;
import pers.edison.lazyapp.annotation.LazyInject;

@LazyBean
public class Boy implements People {

  private String name;
  private Integer age;
  private String schoolName;

  @LazyInject("pers.edison.lazyapp.mock.MathBook")
  private Book mathBook;

  @LazyInject("pers.edison.lazyapp.mock.EnglishBook")
  private Book englishBook;

  public Boy(String name, Integer age) {
    this.name = name;
    this.age = age;
  }

  public Boy() {}

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getAge() {
    return age;
  }

  public void setAge(Integer age) {
    this.age = age;
  }

  public String getSchoolName() {
    return schoolName;
  }

  public void setSchoolName(String schoolName) {
    this.schoolName = schoolName;
  }

  public Book getMathBook() {
    return mathBook;
  }

  public void setMathBook(Book mathBook) {
    this.mathBook = mathBook;
  }

  public Book getEnglishBook() {
    return englishBook;
  }

  public void setEnglishBook(Book englishBook) {
    this.englishBook = englishBook;
  }

  @Override
  public String toString() {
    return "Boy [age=" + age + ", name=" + name + "]";
  }
}
