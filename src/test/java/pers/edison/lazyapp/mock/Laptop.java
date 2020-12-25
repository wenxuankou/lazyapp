/* (C)2020 */
package pers.edison.lazyapp.mock;

public class Laptop {

  private String cpu;
  private String hardDisk;
  private String ram;

  private People belongsTo;

  public Laptop() {}

  public Laptop(String cpu, String hardDisk, String ram, Boy belongsTo) {
    this.cpu = cpu;
    this.hardDisk = hardDisk;
    this.ram = ram;
    this.belongsTo = belongsTo;
  }

  public String getCpu() {
    return cpu;
  }

  public void setCpu(String cpu) {
    this.cpu = cpu;
  }

  public String getHardDisk() {
    return hardDisk;
  }

  public void setHardDisk(String hardDisk) {
    this.hardDisk = hardDisk;
  }

  public String getRam() {
    return ram;
  }

  public void setRam(String ram) {
    this.ram = ram;
  }

  public People getBelongsTo() {
    return belongsTo;
  }

  public void setBelongsTo(People belongsTo) {
    this.belongsTo = belongsTo;
  }
}
