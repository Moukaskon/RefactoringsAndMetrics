package main.java;

public class JavaFile {
    private String path;
    private Double rank= 0.0;
    private Double DSC= -1.0;
    private Double WMC= -1.0;
    private int DIT= -1;
    private Double CC= -1.0;
    private Double LCOM= -1.0;
    private Double MPC= -1.0;
    private Double NOM= -1.0;
    private Double RFC= -1.0;
    private int DAC= -1;
    private Double NOCC= -1.0;
    private Double CBO= -1.0;
    private Double SIZE1= -1.0;
    private Double SIZE2= -1.0;

    public JavaFile(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }

    public String getPathSonarQube() {
        return getPath().replaceAll("\\\\", "/").substring(1);
    }

    public Double getDSC() {
        return DSC;
    }
    public void setDSC(Double DSC) {
        this.DSC = DSC;
    }

    public Double getWMC() {
        return WMC;
    }
    public void setWMC(Double WMC) {
        this.WMC = WMC;
    }

    public int getDIT() {
        return DIT;
    }
    public void setDIT(int DIT) {
        this.DIT = DIT;
    }

    public Double getCC() {
        return CC;
    }
    public void setCC(Double CC) {
        this.CC = CC;
    }

    public Double getLCOM() {
        return LCOM;
    }
    public void setLCOM(Double LCOM) {
        this.LCOM = LCOM;
    }

    public Double getMPC() {
        return MPC;
    }
    public void setMPC(Double MPC) {
        this.MPC = MPC;
    }

    public Double getNOM() {
        return NOM;
    }
    public void setNOM(Double NOM) {
        this.NOM = NOM;
    }

    public Double getRFC() {
        return RFC;
    }
    public void setRFC(Double RFC) {
        this.RFC = RFC;
    }

    public int getDAC() {
        return DAC;
    }
    public void setDAC(int DAC) {
        this.DAC = DAC;
    }

    public Double getNOCC() {
        return NOCC;
    }
    public void setNOCC(Double NOCC) {
        this.NOCC = NOCC;
    }

    public Double getCBO() {
        return CBO;
    }

    public void setCBO(Double CBO) {
        this.CBO = CBO;
    }

    public Double getSIZE1() {
        return SIZE1;
    }

    public void setSIZE1(Double SIZE1) {
        this.SIZE1 = SIZE1;
    }

    public Double getSIZE2() {
        return SIZE2;
    }

    public void setSIZE2(Double SIZE2) {
        this.SIZE2 = SIZE2;
    }

    public Double getRank() {
        return rank;
    }
    public void setRank(Double rank) {
        this.rank = rank;
    }

    @Override
    public String toString() {
        return "JavaFile{" +
                "path='" + path + '\'' +
                ", rank=" + rank +
                ", DSC=" + DSC +
                ", WMC=" + WMC +
                ", DIT=" + DIT +
                ", CC=" + CC +
                ", LCOM=" + LCOM +
                ", MPC=" + MPC +
                ", NOM=" + NOM +
                ", RFC=" + RFC +
                ", DAC=" + DAC +
                ", NOCC=" + NOCC +
                ", CBO=" + CBO +
                ", SIZE1=" + SIZE1 +
                ", SIZE2=" + SIZE2 +
                '}';
    }
}
